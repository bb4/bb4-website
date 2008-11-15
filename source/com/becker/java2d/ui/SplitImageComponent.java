package com.becker.java2d.ui;

import com.becker.java2d.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class SplitImageComponent
        extends JPanel
{
    private BufferedImage mImage;
    private BufferedImage mSecondImage;
    private int mSplitX;

    public SplitImageComponent( String path )
    {
        setImage( path );
        init();
    }

    public SplitImageComponent( BufferedImage image )
    {
        setImage( image );
        init();
    }

    public void setImage( String path )
    {
        Image image = Utilities.blockingLoad( path );
        mImage = Utilities.makeBufferedImage( image );
    }

    public void setImage( BufferedImage image )
    {
        mImage = image;
    }

    public void setSecondImage( BufferedImage image )
    {
        mSecondImage = image;
        repaint();
    }

    public BufferedImage getImage()
    {
        return mImage;
    }

    public BufferedImage getSecondImage()
    {
        return mSecondImage;
    }

    private void init()
    {
        setBackground( Color.white );
        addMouseListener( new MouseAdapter()
        {
            public void mousePressed( MouseEvent me )
            {
                mSplitX = me.getX();
                repaint();
            }
        } );
        addMouseMotionListener( new MouseMotionAdapter()
        {
            public void mouseDragged( MouseEvent me )
            {
                mSplitX = me.getX();
                repaint();
            }
        } );
    }

    public void paint( Graphics g )
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        int width = getSize().width;
        int height = getSize().height;

        // Explicitly clear the window.
        Rectangle clear = new Rectangle( 0, 0, width, height );
        g2.setPaint( getBackground() );
        g2.fill( clear );
        // Clip the first image, if appropriate,
        //   to be on the right side of the split.
        if ( mSplitX != 0 && mSecondImage != null ) {
            Rectangle firstClip = new Rectangle( mSplitX, 0,
                    width - mSplitX, height );
            g2.setClip( firstClip );
        }
        g2.drawImage( getImage(), 0, 0, null );

        if ( mSplitX == 0 || mSecondImage == null ) return;

        Rectangle secondClip = new Rectangle( 0, 0, mSplitX, height );
        g2.setClip( secondClip );
        g2.drawImage( mSecondImage, 0, 0, null );

        Line2D splitLine = new Line2D.Float( mSplitX, 0, mSplitX, height );
        g2.setClip( null );
        g2.setColor( Color.white );
        g2.draw( splitLine );
    }

    public Dimension getPreferredSize()
    {
        int width = 100;
        int height = 100;
        if (getImage() != null) {
            width = getImage().getWidth();
            height = getImage().getHeight();
        }
        if ( mSecondImage != null ) {
            width = Math.max( width, mSecondImage.getWidth() );
            height = Math.max( height, mSecondImage.getHeight() );
        }
        return new Dimension( width, height );
    }
}