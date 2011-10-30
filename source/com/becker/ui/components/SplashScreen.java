/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Given an image show a splash screen to fill the time until the Application starts.
 * The user can click on it to make it go away.
 * Source is mostly copied from http://www.javaworld.com/javaworld/javatips/jw-javatip104.html
 *
 * @author Barry Becker
 */
public final class SplashScreen extends JWindow
{

    /** Constructor
     * @param image image to show in a borderless window
     * @param f owning frame (may be null)
     * @param waitTime time to wait in milliseconds before closing the splash screen
     */
    public SplashScreen( Icon image, Frame f, int waitTime )
    {
        super( f );
        JLabel label = new JLabel( image );
        label.setBorder( BorderFactory.createRaisedBevelBorder() );
        getContentPane().add( label, BorderLayout.CENTER );
        pack();
        Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = label.getPreferredSize();
        setLocation( (screenSize.width >> 1) - (labelSize.width >> 1),
                (screenSize.height >> 1) - (labelSize.height >> 1) );
        addMouseListener( new MouseAdapter()
        {
            @Override
            public void mousePressed( MouseEvent e )
            {
                setVisible( false );
                dispose();
            }
        } );
        final int pause = waitTime;
        final Runnable closerRunner = new Runnable()
        {
            public void run()
            {
                setVisible( false );
                dispose();
            }
        };
        Runnable waitRunner = new Runnable()
        {
            public void run()
            {
                try {
                    Thread.sleep( pause );
                    SwingUtilities.invokeAndWait( closerRunner );
                } catch (Exception e) {
                    e.printStackTrace();
                    // can catch InvocationTargetException
                    // can catch InterruptedException
                }
            }
        };
        setVisible( true );
        Thread splashThread = new Thread( waitRunner, "SplashThread" );
        splashThread.start();
    }
}

