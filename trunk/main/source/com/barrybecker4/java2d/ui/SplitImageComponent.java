package com.barrybecker4.java2d.ui;

import com.barrybecker4.java2d.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 * Shows an image that can be split dow the middle according to where the user clicks.
 * There are left and right images shown on either side of the split.
 * @author Barry Becker
 */
public class SplitImageComponent extends JPanel {

    private BufferedImage mImage;
    private BufferedImage mSecondImage;
    private int mSplitX;

    public SplitImageComponent( String path ) {
        setImage( path );
        init();
    }

    public SplitImageComponent( BufferedImage image ) {
        setImage( image );
        init();
    }

    public void setImage( String path ) {
        Image image = Utilities.blockingLoad( path );
        mImage = Utilities.makeBufferedImage( image );
    }

    public void setImage( BufferedImage image ) {
        mImage = image;
    }

    public void setSecondImage( BufferedImage image ) {
        mSecondImage = image;
        repaint();
    }

    public BufferedImage getImage() {
        return mImage;
    }

    public BufferedImage getSecondImage() {
        return mSecondImage;
    }

    private void init() {
        setBackground( Color.white );
        addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent me ) {
                setSplitX(me.getX());
            }
        } );
        addMouseMotionListener( new MouseMotionAdapter() {
            @Override
            public void mouseDragged( MouseEvent me ) {
                setSplitX(me.getX());
            }
        } );
    }

    public void setSplitX(int pos)
    {
        mSplitX = pos;
        repaint();
    }

    public int getSplitX() {
        return mSplitX;
    }


    @Override
    public void paint( Graphics g ) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        int width = getSize().width;
        int height = getSize().height;
        int splitX = getSplitX();
        clear(g2);

        // Clip the first image, if appropriate,
        //   to be on the right side of the split.
        if ( splitX != 0 && mSecondImage != null ) {
            Rectangle firstClip = new Rectangle( splitX, 0,
                    width - splitX, height );
            g2.setClip( firstClip );
        }
        g2.drawImage( getImage(), 0, 0, null );

        if ( splitX == 0 || mSecondImage == null )
            return;

        Rectangle secondClip = new Rectangle( 0, 0, splitX, height );
        g2.setClip( secondClip );
        g2.drawImage( mSecondImage, 0, 0, null );

        Line2D splitLine = new Line2D.Float( splitX, 0, splitX, height );
        g2.setClip( null );
        g2.setColor( Color.white );
        g2.draw( splitLine );
    }

    /** Explicitly clear the window.  */
    private void clear(Graphics2D g2) {
        int width = getSize().width;
        int height = getSize().height;

        Rectangle clear = new Rectangle( 0, 0, width, height );
        g2.setPaint( getBackground() );
        g2.fill( clear );
    }

    @Override
    public Dimension getPreferredSize() {
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