package com.becker.java2d.ui;

import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.net.URL;

public class RightSizer extends ApplicationFrame
{
    private Image mImage;

    public static void main( String[] args ) throws Exception
    {
        String url = "http://www.geocities.com/barrybecker4/family_portrait_s.JPG";
        if ( args.length > 0 ) url = args[0];
        new RightSizer( new URL( url ) );
    }

    public RightSizer( URL url )
    {
        super( "RightSizer v1.0" );
        mImage = Toolkit.getDefaultToolkit().getImage( url );
        rightSize();
    }

    private void rightSize()
    {
        int width = mImage.getWidth( this );
        int height = mImage.getHeight( this );
        if ( width == -1 || height == -1 ) return;
        addNotify();
        Insets insets = getInsets();
        setSize( width + insets.left + insets.right,
                height + insets.top + insets.bottom );
        center();
        setVisible( true );
    }

    public boolean imageUpdate( Image img, int infoflags,
                                int x, int y, int width, int height )
    {
        if ( (infoflags & ImageObserver.ERROR) != 0 ) {
            System.out.println( "Error loading image!" );
            System.exit( -1 );
        }
        if ( (infoflags & ImageObserver.WIDTH) != 0 &&
                (infoflags & ImageObserver.HEIGHT) != 0 )
            rightSize();
        if ( (infoflags & ImageObserver.SOMEBITS) != 0 )
            repaint();
        if ( (infoflags & ImageObserver.ALLBITS) != 0 ) {
            rightSize();
            repaint();
            return false;
        }
        return true;
    }

    public void update( Graphics g )
    {
        paint( g );
    }

    public void paint( Graphics g )
    {
        Insets insets = getInsets();
        g.drawImage( mImage, insets.left, insets.top, this );
    }
}