package com.becker.java2d.examples;

import com.becker.java2d.Utilities;
import com.becker.ui.ApplicationFrame;
import com.sun.image.codec.jpeg.*;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ShowOff
        extends Component
{
    private BufferedImage mImage;
    private Font mFont;
    private String mMessage;
    private int mSplit;
    private TextLayout mLayout;

    /**
     *
     * @param filename
     * @param message
     * @param split
     */
    public ShowOff( String filename, String message, int split )
            throws IOException
    {
        
        Image img = Utilities.blockingLoad( filename );
        mImage = Utilities.makeBufferedImage( img );
 
        // Create a font.
        mFont = new Font( "Serif", Font.PLAIN, 116 );
        // Save the message and split.
        mMessage = message;
        mSplit = split;
        // Set our size to match the image's size.
        setSize( (int) mImage.getWidth(), (int) mImage.getHeight() );
    }

    @Override
    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;

        // Turn on anti-aliasing.
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

        drawBackground( g2 );
        drawImageMosaic( g2 );
        drawText( g2 );
    }

    protected void drawBackground( Graphics2D g2 )
    {
        // Draw circles of different colors.
        int side = 45;
        int width = getSize().width;
        int height = getSize().height;
        Color[] colors = {Color.yellow, Color.cyan, Color.orange,
                          Color.pink, Color.magenta, Color.lightGray};
        for ( int y = 0; y < height; y += side ) {
            for ( int x = 0; x < width; x += side ) {
                Ellipse2D ellipse = new Ellipse2D.Float( x, y, side, side );
                int index = (x + y) / side % colors.length;
                g2.setPaint( colors[index] );
                g2.fill( ellipse );
            }
        }
    }

    protected void drawImageMosaic( Graphics2D g2 )
    {
        // Break the image up into tiles. Draw each
        //   tile with its own transparency, allowing
        //   the background to show through to varying
        //   degrees.
        int side = 36;
        int width = mImage.getWidth();
        int height = mImage.getHeight();
        for ( int y = 0; y < height; y += side ) {
            for ( int x = 0; x < width; x += side ) {
                // Calculate an appropriate transparency value.
                float xBias = (float) x / (float) width;
                float yBias = (float) y / (float) height;
                float alpha = 1.0f - Math.abs( xBias - yBias );
                g2.setComposite( AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, alpha ) );
                // Draw the subimage.
                int w = Math.min( side, width - x );
                int h = Math.min( side, height - y );
                BufferedImage tile = mImage.getSubimage( x, y, w, h );
                g2.drawImage( tile, x, y, null );
            }
        }
        // Reset the composite.
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER ) );
    }

    protected void drawText( Graphics2D g2 )
    {
        // Find the bounds of the entire string.
        FontRenderContext frc = g2.getFontRenderContext();
        mLayout = new TextLayout( mMessage, mFont, frc );
        // Find the dimensions of this component.
        int width = getSize().width;
        int height = getSize().height;
        // Place the first full string, horizontally centered,
        //   at the bottom of the component.
        Rectangle2D bounds = mLayout.getBounds();
        double x = (width - bounds.getWidth()) / 2;
        double y = height - bounds.getHeight();
        drawString( g2, x, y, 0 );
        // Now draw a second version, anchored to the right side
        //   of the component and rotated by -PI / 2.
        drawString( g2, width - bounds.getHeight(), y, -Math.PI / 2 );
    }

    protected void drawString( Graphics2D g2,
                               double x, double y, double theta )
    {
        // Transform to the requested location.
        g2.translate( x, y );
        // Rotate by the requested angle.
        g2.rotate( theta );
        // Draw the first part of the string.
        String first = mMessage.substring( 0, mSplit );
        float width = drawBoxedString( g2, first, Color.white, Color.red, 0 );
        // Draw the second part of the string.
        String second = mMessage.substring( mSplit );
        drawBoxedString( g2, second, Color.blue, Color.white, width );
        // Undo the transformations.
        g2.rotate( -theta );
        g2.translate( -x, -y );
    }

    protected float drawBoxedString( Graphics2D g2,
                                     String s, Color c1, Color c2, double x )
    {
        // Calculate the width of the string.
        FontRenderContext frc = g2.getFontRenderContext();
        TextLayout subLayout = new TextLayout( s, mFont, frc );
        float advance = subLayout.getAdvance();
        // Fill the background rectangle with a gradient.
        GradientPaint gradient = new GradientPaint( (float) x, 0, c1,
                (float) (x + advance), 0, c2 );
        g2.setPaint( gradient );
        Rectangle2D bounds = mLayout.getBounds();
        Rectangle2D back = new Rectangle2D.Double( x, 0,
                advance, bounds.getHeight() );
        g2.fill( back );
        // Draw the string over the gradient rectangle.
        g2.setPaint( Color.white );
        g2.setFont( mFont );
        g2.drawString( s, (float) x, (float) -bounds.getY() );
        return advance;
    }

    /**
     * The image is loaded either from this
     * default filename or the first command-
     * line argument.
     * The second command-line argument specifies
     * what string will be displayed. The third
     * specifies at what point in the string the
     * background color will change.
     * @param args
     * @throws IOException
     */
    public static void main( String[] args ) throws IOException
    {
        String filename = Utilities.DEFAULT_IMAGE_DIR +"Raphael.jpg";
        String message = "Java2D";
        int split = 4;
        if ( args.length > 0 ) filename = args[0];
        if ( args.length > 1 ) message = args[1];
        if ( args.length > 2 ) split = Integer.parseInt( args[2] );
        ApplicationFrame f = new ApplicationFrame( "ShowOff v1.0" );
        f.getContentPane().setLayout( new BorderLayout() );
        ShowOff showOff = new ShowOff( filename, message, split );
        f.getContentPane().add( showOff, BorderLayout.CENTER );
        f.center();
        f.setResizable( false );
    }

}