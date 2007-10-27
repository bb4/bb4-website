package ca.dj.util.encoder;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;

/**
 * PngEncoder takes a Java Image object and creates a byte string which can
 * be saved as a PNG file.
 *
 * <P>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or( at your option ) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * A copy of the GNU LGPL may be found at
 * <A HREF=http://www.gnu.org/copyleft/lesser.html>
 * http://www.gnu.org/copyleft/lesser.html</A>
 *
 * @author J. David Eisenberg (david@catcode.com)
 * @version 1.4, 31 March 2000
 * <P>
 * Modified: January 25, 2001<BR>
 * Author: Dave Jarvis<BR>
 * <UL>
 * <LI>Removed unused variables
 * <LI>Removed unused set/get accessor methods (to simplify API)
 * <LI>Replaced modulous with bitwise-AND where appropriate
 * <LI>Added temporary variables to replace repeated calculations
 * <LI>Reordered variables to take advantage of a bytecode trick
 * <LI>Renamed some variables to follow conventional OO
 * <LI>Made all source lines fit in 80 columns
 * </UL>
 *
 * Notice that in order to encode multiple images, you'll need to create
 * multiple instances of PngEncoder.  This shouldn't be a problem as I
 * don't envision many applications that would need to convert Images to
 * PNG byte-arrays many times.  This minor spec. change allows the code
 * to be simplified in a few places.
 *
 * <P>
 * A comment (or two) that touch on what filters do would be useful.
 */
public final class PngEncoder
{
  /** The alpha channel should be encoded. */
  public static final boolean ENCODE_ALPHA = true;

  /** The alpha channel should not be encoded. */
  public static final boolean NO_ALPHA = false;

  private final static int COLOUR_DEPTH = 8;

  /** Default. */
  public static final int NO_COMPRESSION = 0;

  /** Maximum compression -- smallest amount of data produced. */
  public static final int FULL_COMPRESSION = 9;

  /** Don't use any filters (default). */
  public static final int FILTER_NONE = 0;

  /** Use the sub(stitute?) filter. */
  public static final int FILTER_SUB = 1;

  /** Use the up filter. */
  public static final int FILTER_UP = 2;

  /** Use the last filter. */
  public static final int FILTER_LAST = 2;

  // Since these are used most often, move them up for a better bytecode.
  //
  private int bytePos, maxPos;

  private byte[] pngBytes;
  private byte[] priorRow;
  private byte[] leftBytes;
  private Image myImage;
  private int width, height;

  // hdrPos, dataPos, and endPos are not used.
  // int hdrPos, dataPos, endPos
  
  private CRC32 crc = new CRC32();

  // crcValue is never used, either (value was substituted immediately).
  //private long crcValue;

  private boolean myEncodeAlpha;
  private int myFilter = FILTER_NONE;

  private int bytesPerPixel;
  private int compressionLevel = NO_COMPRESSION;

  /**
   * Class constructor specifying Image to encode, with no alpha channel
   * encoding.  It isn't very useful to create a PngEncoder without an
   * image to encode, thus you cannot create an instance of PngEncoder
   * without an image.
   *
   * @param image A Java Image object which uses the DirectColorModel
   */
  public PngEncoder( Image image )
  {
    this( image, NO_ALPHA );
  }

  /**
   * Class constructor specifying Image to encode, and whether to encode
   * alpha.
   *
   * @param image A Java Image object which uses the DirectColorModel
   * @param encodeAlpha Encode the alpha channel?
   */
  public PngEncoder( Image image, boolean encodeAlpha )
  {
    this( image, encodeAlpha, FILTER_NONE );
  }

  /**
   * Class constructor specifying Image to encode, whether to encode alpha,
   * and filter to use.
   *
   * @param image A Java Image object which uses the DirectColorModel
   * @param encodeAlpha Encode the alpha channel? false=no; true=yes
   * @param whichFilter 0=none, 1=sub, 2=up
   * @see java.awt.Image
   */
  public PngEncoder( Image image, boolean encodeAlpha, int whichFilter )
  {
    this( image, encodeAlpha, whichFilter, NO_COMPRESSION );
  }

  /**
   * Class constructor specifying Image source to encode, whether to encode
   * alpha, filter to use, and compression level.
   *
   * @param image A Java Image object
   * @param encodeAlpha Encode the alpha channel? false=no; true=yes
   * @param whichFilter 0=none, 1=sub, 2=up
   * @param compLevel 0..9
   * @see java.awt.Image
   */
  public PngEncoder( Image image, boolean encodeAlpha, int whichFilter,
     int compLevel )
  {
    myImage = image;
    myEncodeAlpha = encodeAlpha;
    setFilter( whichFilter );

    // Already coded, so why not use it?
    //
    setCompressionLevel( compLevel );
  }

  /**
   * Creates an array of bytes that is the PNG equivalent of the
   * current image, specifying whether to encode alpha or not.
   *
   * @param encodeAlpha boolean false=no alpha, true=encode alpha
   * @return an array of bytes, or null if there was a problem
   */
  public byte[] pngEncode( boolean encodeAlpha )
  {
    byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };

    if( myImage == null )
      return null;

    width = myImage.getWidth( null );
    height = myImage.getHeight( null );

    // Erm ...?
    //this.image = image;

    /*
     * start with an array that is big enough to hold all the pixels
     *( plus filter bytes ), and an extra 200 bytes for header info
     */
    pngBytes = new byte[ ((width + 1) * height * 3) + 200];

    /*
     * keep track of largest byte written to the array
     */
    maxPos = 0;

    bytePos = writeBytes( pngIdBytes, 0 );
    writeHeader();

    if( writeImageData() )
    {
      writeEnd();
      pngBytes = resizeByteArray( pngBytes, maxPos );
    }
    else
      return null;

    return pngBytes;
  }

  /**
   * Creates an array of bytes that is the PNG equivalent of the current image.
   * Alpha encoding is determined by its setting in the constructor.
   *
   * @return an array of bytes, or null if there was a problem
   */
  public byte[] pngEncode()
  {
    return pngEncode( myEncodeAlpha );
  }

  /**
   * Set the filter to use
   *
   * @param whichFilter from constant list
   */
  public void setFilter( int whichFilter )
  {
    myFilter = FILTER_NONE;

    if( whichFilter <= FILTER_LAST )
      myFilter = whichFilter;
  }

  /**
   * Set the compression level to use
   *
   * @param level 0 through 9
   */
  public void setCompressionLevel( int level )
  {
    if( (level >= 0) && (level <= 9) )
      compressionLevel = level;
  }

  /**
   * Increase or decrease the length of a byte array.
   *
   * @param array The original array.
   * @param newLength The length you wish the new array to have.
   * @return Array of newly desired length. If shorter than the
   * original, the trailing elements are truncated.
   */
  private byte[] resizeByteArray( byte[] array, int newLength )
  {
    byte[]  newArray = new byte[newLength];

    System.arraycopy( array, 0,
                      newArray, 0,
                      Math.min( array.length, newLength ) );

    return newArray;
  }

  /**
   * Write an array of bytes into the pngBytes array.
   * Note: This routine has the side effect of updating
   * maxPos, the largest element written in the array.
   * The array is resized by 1000 bytes or the length
   * of the data to be written, whichever is larger.
   *
   * @param data The data to be written into pngBytes.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   */
  private int writeBytes( byte[] data, int offset )
  {
    maxPos = Math.max( maxPos, offset + data.length );

    if( data.length + offset > pngBytes.length )
      pngBytes = resizeByteArray(
        pngBytes, pngBytes.length + Math.max( 1000, data.length ) );

    System.arraycopy( data, 0, pngBytes, offset, data.length );
    return offset + data.length;
  }

  /**
   * Write an array of bytes into the pngBytes array, specifying number of
   * bytes to write.
   * Note: This routine has the side effect of updating
   * maxPos, the largest element written in the array.
   * The array is resized by 1000 bytes or the length
   * of the data to be written, whichever is larger.
   *
   * @param data The data to be written into pngBytes.
   * @param nBytes The number of bytes to be written.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   */
  private int writeBytes( byte[] data, int nBytes, int offset )
  {
    maxPos = Math.max( maxPos, offset + nBytes );

    if( nBytes + offset > pngBytes.length )
      pngBytes = resizeByteArray(
        pngBytes, pngBytes.length + Math.max( 1000, nBytes ) );

    System.arraycopy( data, 0, pngBytes, offset, nBytes );
    return offset + nBytes;
  }

  /**
   * Write a four-byte integer into the pngBytes array at a given position.
   *
   * @param n The integer to be written into pngBytes.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   */
  private int writeInt4( int n, int offset )
  {
    byte[] temp = { (byte)((n >> 24) & 0xFF),
                    (byte)((n >> 16) & 0xFF),
                    (byte)((n >> 8 ) & 0xFF),
                    (byte)(n & 0xFF) };
    return writeBytes( temp, offset );
  }

  /**
   * Write a single byte into the pngBytes array at a given position.
   *
   * @param n The integer to be written into pngBytes.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   */
  private int writeByte( int b, int offset )
  {
    byte[] temp = { (byte) b };
    return writeBytes( temp, offset );
  }

  /**
   * Write a string into the pngBytes array at a given position.
   * This uses the getBytes method, so the encoding used will
   * be its default.
   *
   * @param n The integer to be written into pngBytes.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   * @see java.lang.String#getBytes()
   */
  private int writeString( String s, int offset )
  {
    return writeBytes( s.getBytes(), offset );
  }

  /**
   * Write a PNG "IHDR" chunk into the pngBytes array.
   */
  private void writeHeader()
  {
    int startPos;

    startPos = bytePos = writeInt4( 13, bytePos );
    bytePos = writeString( "IHDR", bytePos );

    // width and height were already assigned, and they cannot change, since
    // the API has been simplified.
    //
    //width = myImage.getWidth( null );
    //height = myImage.getHeight( null );

    bytePos = writeInt4( width, bytePos );
    bytePos = writeInt4( height, bytePos );
    bytePos = writeByte( COLOUR_DEPTH, bytePos ); // bit depth
    bytePos = writeByte(( myEncodeAlpha ) ? 6 : 2, bytePos ); // direct model
    bytePos = writeByte( 0, bytePos ); // compression method
    bytePos = writeByte( 0, bytePos ); // filter method
    bytePos = writeByte( 0, bytePos ); // no interlace
    crc.reset();
    crc.update( pngBytes, startPos, bytePos - startPos );
    bytePos = writeInt4( (int)(crc.getValue()), bytePos );
  }

  /**
   * Perform "sub" filtering on the given row.
   * Uses temporary array leftBytes to store the original values
   * of the previous pixels.  The array is 16 bytes long, which
   * will easily hold two-byte samples plus two-byte alpha.
   *
   * @param pixels The array holding the scan lines being built
   * @param startPos Starting position within pixels of bytes to be filtered.
   */
  private void filterSub( byte[] pixels, int startPos )
  {
    int endPos = startPos + (width * bytesPerPixel),
        leftInsert = bytesPerPixel,
        leftExtract = 0;

    // The actual start is equal to "startPos + bytePerPixel"
    //
    for( int i = (startPos + bytesPerPixel); i < endPos; i++ )
    {
      leftBytes[leftInsert] =  pixels[i];

      // & 255 = % 256
      //
      pixels[i] = (byte)((pixels[i] - leftBytes[leftExtract]) & 255);

      /*leftInsert = (leftInsert + 1) % 0x0F;
      leftExtract = (leftExtract + 1) % 0x0F;*/

      leftInsert = ++leftInsert % 0x0F;
      leftExtract = ++leftExtract % 0x0F;
    }
  }

  /**
   * Perform "up" filtering on the given row.
   * Side effect: refills the prior row with current row
   *
   * @param pixels The array holding the scan lines being built
   * @param startPos Starting position within pixels of bytes to be filtered.
   */
  private void filterUp( byte[] pixels, int startPos )
  {
    int nBytes = width * bytesPerPixel;
    byte current_byte;

    // Instead of using "startPos + i" everywhere, just increment it along
    // with "i".
    //
    for( int i = 0; i < nBytes; i++, startPos++ )
    {
      current_byte = pixels[startPos];

      // & 255 = % 256
      //
      pixels[startPos] = (byte)((pixels[startPos] - priorRow[i]) & 255);
      priorRow[i] = current_byte;
    }
  }

  /**
   * Write the image data into the pngBytes array.
   * This will write one or more PNG "IDAT" chunks. In order
   * to conserve memory, this method grabs as many rows as will
   * fit into 32K bytes, or the whole image; whichever is less.
   *
   *
   * @return true if no errors; false if error grabbing pixels
   */
  private boolean writeImageData()
  {
    int rowsLeft = height;  // number of rows remaining to write
    int startRow = 0;       // starting row to process this time through
    int nRows;              // how many rows to grab at a time

    byte[] scanLines;       // the scan lines to be compressed
    int scanPos;            // where we are in the scan lines
    int startPos;           // where this line's actual pixels start

    byte[] compressedLines; // the resultant compressed lines
    int nCompressed;        // how big is the compressed area?

    PixelGrabber pg;

    bytesPerPixel = myEncodeAlpha ? 4 : 3;

    Deflater scrunch = new Deflater( compressionLevel );
    ByteArrayOutputStream outBytes = new ByteArrayOutputStream( 1024 );

    DeflaterOutputStream compBytes =
      new DeflaterOutputStream( outBytes, scrunch );

    try
    {
      while( rowsLeft > 0 )
      {
        nRows = Math.min( 32767 / (width * (bytesPerPixel + 1)), rowsLeft );

        // Instead of having to calculate this several times, do it once.
        //
        int rowWidth = width * nRows;

        int[] pixels = new int[ rowWidth ];

        pg = new PixelGrabber(
          myImage, 0, startRow, width, nRows, pixels, 0, width );

        try
        {
          pg.grabPixels();
        }
        catch( Exception e )
        {
          return false;
        }

        if( (pg.getStatus() & ImageObserver.ABORT) != 0 )
          return false;

        /*
         * Create a data chunk. scanLines adds "nRows" for
         * the filter bytes. 
         */
        scanLines = new byte[ rowWidth * bytesPerPixel +  nRows];

        if( myFilter == FILTER_SUB )
          leftBytes = new byte[16];

        if( myFilter == FILTER_UP )
          priorRow = new byte[width * bytesPerPixel];

        scanPos = 0;
        startPos = 1;

        for( int i = 0; i < rowWidth; i++ )
        {
          // Modulous is a slow operation; do it once, instead of twice.
          //
          int iModWidth = i % width;

          if( iModWidth == 0 )
          {
            scanLines[scanPos++] = (byte)myFilter; 
            startPos = scanPos;
          }

          scanLines[scanPos++] = (byte)(( pixels[i] >> 16 ) & 0xFF );
          scanLines[scanPos++] = (byte)(( pixels[i] >>  8 ) & 0xFF );
          scanLines[scanPos++] = (byte)(( pixels[i]       ) & 0xFF );

          if( myEncodeAlpha )
            scanLines[scanPos++] = (byte)(( pixels[i] >> 24) & 0xFF );

          if( (iModWidth == width - 1) && (myFilter != FILTER_NONE) )
          {
            if( myFilter == FILTER_SUB )
              filterSub( scanLines, startPos );
            if( myFilter == FILTER_UP )
              filterUp( scanLines, startPos );
          }
        }

        /*
         * Write these lines to the output area
         */
        compBytes.write( scanLines, 0, scanPos );

        startRow += nRows;
        rowsLeft -= nRows;
      }

      compBytes.close();

      /*
       * Write the compressed bytes
       */
      compressedLines = outBytes.toByteArray();
      nCompressed = compressedLines.length;

      crc.reset();
      bytePos = writeInt4( nCompressed, bytePos );
      bytePos = writeString( "IDAT", bytePos );
      crc.update( "IDAT".getBytes() );

      bytePos = writeBytes( compressedLines, nCompressed, bytePos );
      crc.update( compressedLines, 0, nCompressed );

      bytePos = writeInt4( (int)(crc.getValue()), bytePos );
      scrunch.finish();
    }
    catch( IOException e )
    {
      return false;
    }

    return true;
  }

  /**
   * Write a PNG "IEND" chunk into the pngBytes array.
   */
  private void writeEnd()
  {
    bytePos = writeInt4( 0, bytePos );
    bytePos = writeString( "IEND", bytePos );
    crc.reset();
    crc.update( "IEND".getBytes() );
    bytePos = writeInt4( (int)(crc.getValue()), bytePos );
  }
}

