package com.becker.java2d;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.media.jai.codec.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * A utility class for generating image files and manipulating images
 *
 * @author Barry Becker
 */
public final class ImageUtil
{

    // print no more than this
    protected static int MAX_TO_PRINT = 50;
    // print quality for JPGs 1 is no compression
    protected static float JPG_QUALITY = .9f;

    // Note: if you add a static var here, rememeber to null it out in dispose() !
    protected static JFileChooser fileDialog_ = null;


    /**
     * create a BufferedImage from an Image
     */
    public static BufferedImage makeBufferedImage( Image image )
    {
        BufferedImage bImg = new BufferedImage( image.getWidth( null ), image.getHeight( null ),
                BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = bImg.createGraphics();
        g2.drawImage( image, null, null );
        g2.dispose();
        return bImg;
    }

    /**
     * create an image that is compatible with your hardware
     */
    public static BufferedImage createCompatibleImage( int width, int height )
    {
        GraphicsEnvironment local = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screen = local.getDefaultScreenDevice();
        GraphicsConfiguration configuration = screen.getDefaultConfiguration();
        return configuration.createCompatibleImage( width, height );
    }

    /**
     * return a byte array given an image
     * @param img the image to convert
     * @param type the type of image to create ("jpg" or "png")
     */
    public static byte[] getImageAsByteArray( Image img, String type )
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedOutputStream os = new BufferedOutputStream( bos );
        ImageUtil.writeImage( img, os, type );

        return bos.toByteArray();
    }

    /**
     *  write an image to the given output stream
     *  @param img
     *  @param os output stream to write to
     *  @param type the type of image to create ("jpg" or "png")
     */
    public static void writeImage( Image img, BufferedOutputStream os, String type )
    {
        //long time = System.currentTimeMillis();
        BufferedImage bi = ImageUtil.makeBufferedImage( img );

        if ( "jpg".equals( type ) ) {
            com.sun.image.codec.jpeg.JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder( os );
            com.sun.image.codec.jpeg.JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam( bi );
            // this makes the images near perfect - very little compression
            param.setQuality( JPG_QUALITY, false );
            encoder.setJPEGEncodeParam( param );

            try {
                encoder.encode( bi );  // this writes it to a file as a .jpg
            } catch (IOException fne) {
                System.out.println( "IOException error" );
            }
        }
        else { // PNG is the default
            // Create the ParameterBlock.
            // we can play with this for better compression/quality
            PNGEncodeParam param = PNGEncodeParam.getDefaultEncodeParam( bi );

            //Create the PNG image encoder.
            ImageEncoder encoder = ImageCodec.createImageEncoder( "PNG", os, param );
            try {
                encoder.encode( bi );  // this writes it to a file as a .png
            } catch (IOException fne) {
                System.out.println( "IOException error" );
            }
        }

        try {
            os.flush();
            os.close();
        } catch (IOException fne) {
            System.out.println( "IOException error" );
        }
        //System.out.println("VizUtil: createImage time = "+(System.currentTimeMillis()-time));
    }

    /**
     * Saves an image to a file using the format specified by the type
     * note the filename should not include the extension.
     * this will be added as appropriate.
     * @param fileName the fileName should not have an extension because it gets added based on VizContext.imageFormat
     * @param img the image to save
     * @param type of image ("jpg" or "png" (default)
     */
    public static void saveAsImage( String fileName, Image img, String type )
    {
        BufferedOutputStream os = null;
        try {
            String extension = null;
            if ( "jpg".equals( type ) )
                extension = ".jpg";
            else   // png
                extension = ".png";

            os = new BufferedOutputStream( new FileOutputStream( fileName + extension ) );
        } catch (FileNotFoundException fne) {
            System.out.println( "File " + fileName + " not found" );
        }
        writeImage( img, os, type );
    }

}
