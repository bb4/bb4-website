package com.becker.java2d.png;

import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.zip.CRC32;

public class PNGDecoder
{
    private PNGDecoder() {}

    public static void main( String[] args ) throws Exception
    {
        String name = "basn3p08.png";
        if ( args.length > 0 ) name = args[0];
        InputStream in = PNGDecoder.class.getResourceAsStream( name );
        final BufferedImage image = decode( in );
        in.close();

        // Create a Frame to display the image.
        Frame f = new ApplicationFrame( "PNGDecoder v1.0" )
        {
            public void paint( Graphics g )
            {
                Insets insets = getInsets();
                g.drawImage( image, insets.left, insets.top, null );
            }
        };
        f.setVisible( true );
        Insets insets = f.getInsets();
        f.setSize( image.getWidth() + insets.left + insets.right,
                image.getHeight() + insets.top + insets.bottom );
    }

    public static BufferedImage decode( InputStream in )
            throws IOException
    {
        DataInputStream dataIn = new DataInputStream( in );
        readSignature( dataIn );
        PNGData chunks = readChunks( dataIn );

        long widthLong = chunks.getWidth();
        long heightLong = chunks.getHeight();
        if ( widthLong > Integer.MAX_VALUE || heightLong > Integer.MAX_VALUE )
            throw new IOException( "That image is too wide or tall." );

        ColorModel cm = chunks.getColorModel();
        WritableRaster raster = chunks.getRaster();

        BufferedImage image = new BufferedImage( cm, raster, false, null );

        return image;
    }

    protected static void readSignature( DataInputStream in )
            throws IOException
    {
        long signature = in.readLong();
        if ( signature != 0x89504e470d0a1a0aL )
            throw new IOException( "PNG signature not found!" );
    }

    protected static PNGData readChunks( DataInputStream in )
            throws IOException
    {
        PNGData chunks = new PNGData();

        boolean trucking = true;
        while ( trucking ) {
            try {
                // Read the length.
                int length = in.readInt();
                if ( length < 0 )
                    throw new IOException( "Sorry, that file is too long." );
                // Read the type.
                byte[] typeBytes = new byte[4];
                in.readFully( typeBytes );
                // Read the data.
                byte[] data = new byte[length];
                in.readFully( data );
                // Read the CRC.
                long crc = in.readInt() & 0x00000000ffffffffL; // Make it unsigned.
                if ( !verifyCRC(typeBytes, data, crc) )
                    throw new IOException( "That file appears to be corrupted." );

                PNGChunk chunk = new PNGChunk( typeBytes, data );
                chunks.add( chunk );
            } catch (EOFException eofe) {
                trucking = false;
            }
        }
        return chunks;
    }

    protected static boolean verifyCRC( byte[] typeBytes, byte[] data,
                                        long crc )
    {
        CRC32 crc32 = new CRC32();
        crc32.update( typeBytes );
        crc32.update( data );
        long calculated = crc32.getValue();
        return (calculated == crc);
    }
}