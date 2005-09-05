package com.becker.common;

import sun.misc.*;

import java.io.*;
import java.util.zip.*;
import static com.becker.common.EncodingConstants.*;

/**
 * Utility methods for Base64 compression and decompression.
 *
 * @author Barry Becker
 */
public final class Base64Codec {


    private Base64Codec() {}

    // for character codec
    private static BASE64Encoder charEncoder_ = null;
    private static BASE64Decoder charDecoder_ = null;

    /**
     * take a String and compress it.
     * See @decompress for reversing the compression.
     * @param data a string to compress.
     * @return compressed string representation.
     */
    public static String compress( String data )
    {
        if (charEncoder_==null)
            charEncoder_ = new BASE64Encoder();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream( 512 );
        Deflater deflater = new Deflater();
        DeflaterOutputStream oStream = new DeflaterOutputStream( byteOut, deflater );

        try {
            oStream.write( data.getBytes( CONVERTER_UTF8 ) );
            oStream.flush();
            oStream.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println( "Unsupported encoding exceptin :" + e.getMessage() );
        } catch (IOException e) {
            System.out.println( "io error :" + e.getMessage() );
            e.printStackTrace();
        }


        return charEncoder_.encode( byteOut.toByteArray() );
    }

    /**
     * take a String and decompress it.
     * @param data the compressed string to decompress.
     */
    public static String decompress( String data )
    {
        if (charDecoder_==null)
            charDecoder_ = new BASE64Decoder();
        // convert from string to bytes for uncompressing
        byte[] compressedDat = null;
        try {
            compressedDat = charDecoder_.decodeBuffer( data );
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream in = new ByteArrayInputStream( compressedDat );
        Inflater inflater = new Inflater();
        InflaterInputStream iStream = new InflaterInputStream( in, inflater );
        char cBuffer[] = new char[4096];
        StringBuffer sBuf = new StringBuffer();
        try {
            InputStreamReader iReader =
                    new InputStreamReader( iStream, CONVERTER_UTF8 );
            while ( true ) {
                int numRead = iReader.read( cBuffer );
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

        String uncompressedString = sBuf.toString();
        return uncompressedString;
    }

}
