package com.becker.common;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static com.becker.common.EncodingConstants.CONVERTER_UTF8;


/**
 * Utility methods for Base64 compression and decompression.
 *
 * @author Barry Becker
 */
public final class Base64Codec {

    private Base64Codec() {}

    // for character codec
    private static Base64 charEncoder_ = null;
   
    /**
     * take a String and compress it.
     * See @decompress for reversing the compression.
     * @param data a string to compress.
     * @return compressed string representation.
     */
    public static synchronized String compress(final String data )
    {
        if (charEncoder_ == null) {            
            charEncoder_ = new Base64();
        }
        
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream( 512 );
        Deflater deflater = new Deflater();
        DeflaterOutputStream oStream = new DeflaterOutputStream( byteOut, deflater );

        try {
            oStream.write( data.getBytes( CONVERTER_UTF8 ) );
            oStream.flush();
            oStream.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println( "Unsupported encoding exception :" + e.getMessage() );
        } catch (IOException e) {
            System.out.println( "io error :" + e.getMessage() );
            e.printStackTrace();
        }

        return new String(charEncoder_.encodeBase64( byteOut.toByteArray() ));
    }

    /**
     * take a String and decompress it.
     * @param data the compressed string to decompress.
     */
    public static synchronized String decompress( final String data )
    {
        if (charEncoder_==null) {
            charEncoder_ = new Base64();
        }
        // convert from string to bytes for decompressing
        byte[] compressedDat = charEncoder_.decodeBase64( data.getBytes() );  

        final ByteArrayInputStream in = new ByteArrayInputStream( compressedDat );
        final Inflater inflater = new Inflater();
        final InflaterInputStream iStream = new InflaterInputStream( in, inflater );
        final char cBuffer[] = new char[4096];
        StringBuilder sBuf = new StringBuilder();
        try {
            InputStreamReader iReader = new InputStreamReader( iStream, CONVERTER_UTF8 );
            while ( true ) {
                final int numRead = iReader.read( cBuffer );
                if ( numRead == -1 ) {
                    break;
                }
                sBuf.append( cBuffer, 0, numRead );
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println( "Unsupported encoding exceptin :" + e.getMessage() );
        } catch (IOException e) {
            System.out.println( "io error :" + e.getMessage() );
            e.printStackTrace();
        }

        return sBuf.toString();
    }
}
