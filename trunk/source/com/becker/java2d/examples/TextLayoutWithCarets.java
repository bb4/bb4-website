package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.*;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class TextLayoutWithCarets
        extends ApplicationFrame
{
    public static void main( String[] args )
    {
        TextLayoutWithCarets f = new TextLayoutWithCarets();
        f.setVisible( true );
    }

    private TextHitInfo mHit;
    private TextLayout mLayout;
    private boolean mInitialized = false;

    public TextLayoutWithCarets()
    {
        super( "TextLayoutWithCarets v1.0" );
    }

    private void initialize( Graphics2D g2 )
    {
        String s = "Please \u062e\u0644\u0639 slowly.";
        // Create a plain and italic font.
        int fontSize = 32;
        Font font = new Font( "Lucida Sans Regular", Font.PLAIN, fontSize );
        Font italicFont = new Font( "Lucida Sans Oblique", Font.ITALIC, fontSize );
        // Create an Attributed String
        AttributedString as = new AttributedString( s );
        as.addAttribute( TextAttribute.FONT, font );
        as.addAttribute( TextAttribute.FONT, italicFont, 2, 5 );
        // Get the iterator.
        AttributedCharacterIterator iterator = as.getIterator();
        // Create a TextLayout.
        FontRenderContext frc = g2.getFontRenderContext();
        mLayout = new TextLayout( iterator, frc );

        mHit = mLayout.getNextLeftHit( 1 );

        // Respond to left and right arrow keys.
        addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent ke )
            {
                if ( ke.getKeyCode() == KeyEvent.VK_RIGHT ) {
                    mHit = mLayout.getNextRightHit( mHit.getInsertionIndex() );
                    if ( mHit == null ) mHit = mLayout.getNextLeftHit( 1 );
                    repaint();
                }
                else if ( ke.getKeyCode() == KeyEvent.VK_LEFT ) {
                    mHit = mLayout.getNextLeftHit( mHit.getInsertionIndex() );
                    if ( mHit == null )
                        mHit = mLayout.getNextRightHit( mLayout.getCharacterCount() - 1 );
                    repaint();
                }
            }
        } );

        mInitialized = true;
    }

    public void paint( Graphics g )
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON );

        if ( mInitialized == false ) initialize( g2 );

        float x = 20, y = 80;
        mLayout.draw( g2, x, y );

        // Create a plain stroke and a dashed stroke.
        Stroke[] caretStrokes = new Stroke[2];
        caretStrokes[0] = new BasicStroke();
        caretStrokes[1] = new BasicStroke( 1,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0,
                new float[]{4, 4}, 0 );

        // Now draw the carets
        Shape[] carets = mLayout.getCaretShapes( mHit.getInsertionIndex() );
        for ( int i = 0; i < carets.length; i++ ) {
            if ( carets[i] != null ) {
                AffineTransform at = AffineTransform.getTranslateInstance( x, y );
                Shape shape = at.createTransformedShape( carets[i] );
                g2.setStroke( caretStrokes[i] );
                g2.draw( shape );
            }
        }
    }
}