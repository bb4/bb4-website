package com.barrybecker4.java2d.print;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class PatchworkComponent extends JComponent
                                implements Printable {
    private float mSide = 36;
    private float mOffset = 36;
    private int mColumns = 8;
    private int mRows = 4;
    private String mString = "Captivated";
    private Font mFont = new Font( "Serif", Font.PLAIN, 64 );

    private Paint mHorizontalGradient, mVerticalGradient;

    public PatchworkComponent() {
        float x = mOffset;
        float y = mOffset;
        float halfSide = mSide / 2;
        float x0 = x + halfSide;
        float y0 = y;
        float x1 = x + halfSide;
        float y1 = y + (mRows * mSide);
        mVerticalGradient = new GradientPaint(
                x0, y0, Color.darkGray, x1, y1, Color.lightGray, true );
        x0 = x;
        y0 = y + halfSide;
        x1 = x + (mColumns * mSide);
        y1 = y + halfSide;
        mHorizontalGradient = new GradientPaint(
                x0, y0, Color.darkGray, x1, y1, Color.lightGray, true );
    }

    public PatchworkComponent( String s ) {
        this();
        mString = s;
    }

    @Override
    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;

        g2.rotate( Math.PI / 24, mOffset, mOffset );

        for ( int row = 0; row < mRows; row++ ) {
            for ( int column = 0; column < mColumns; column++ ) {
                float x = column * mSide + mOffset;
                float y = row * mSide + mOffset;

                if ( ((column + row) % 2) == 0 )
                    g2.setPaint( mVerticalGradient );
                else
                    g2.setPaint( mHorizontalGradient );

                Rectangle2D r = new Rectangle2D.Float( x, y, mSide, mSide );
                g2.fill( r );
            }
        }

        FontRenderContext frc = g2.getFontRenderContext();
        float width = (float) mFont.getStringBounds( mString, frc ).getWidth();
        LineMetrics lm = mFont.getLineMetrics( mString, frc );
        float x = ((mColumns * mSide) - width) / 2 + mOffset;
        float y = ((mRows * mSide) + lm.getAscent()) / 2 + mOffset;
        g2.setFont( mFont );
        g2.setPaint( Color.white );
        g2.drawString( mString, x, y );
    }

    @Override
    public int print( Graphics g, PageFormat pageFormat, int pageIndex ) {
        if ( pageIndex != 0 ) return NO_SUCH_PAGE;
        paintComponent( g );
        return PAGE_EXISTS;
    }
}