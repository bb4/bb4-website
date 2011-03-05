package com.becker.java2d.examples;

import com.becker.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;

public class HitMe extends ApplicationFrame
{
    
    private TextLayout mTextLayout;
    private int mX = 40, mY = 80;

    
    public HitMe()
    {
        super( "HitMe v1.0" );
        addMouseListener( new MouseAdapter()
        {
            public void mouseClicked( MouseEvent me )
            {
                TextHitInfo hit = mTextLayout.hitTestChar(
                        me.getX() - mX, me.getY() - mY );
                System.out.println( hit );
            }
        } );
    }

    public void paint( Graphics g )
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

        String s = "Camelopardalis";
        Font font = new Font( "Serif", Font.PLAIN, 32 );

        if ( mTextLayout == null ) {
            FontRenderContext frc = g2.getFontRenderContext();
            mTextLayout = new TextLayout( s, font, frc );
        }

        mTextLayout.draw( g2, mX, mY );
    }
    
    public static void main( String[] args )
    {
        new HitMe();         
    }

}