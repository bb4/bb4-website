/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.components;

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
public final class SplashScreen extends JWindow {

    /**
     * Constructor
     * @param image image to show in a borderless window
     * @param frame owning frame (may be null)
     * @param waitTime time to wait in milliseconds before closing the splash screen
     */
    public SplashScreen( Icon image, Frame frame, int waitTime ) {
        super( frame );

        addLabel(image);

        Thread splashThread = createSplashThread(waitTime);

        setVisible( true );
        splashThread.start();
    }

    /**
     * Shows the splash screen until the user clicks on it.
     * @param waitTime time to wait before auto dismissing if the user has not clicked.
     * @return the thread taht will show the splash
     */
    private Thread createSplashThread(int waitTime) {
        addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent e ) {
                setVisible( false );
                dispose();
            }
        } );

        final int pause = waitTime;
        final Runnable closerRunner = new Runnable()  {
            public void run()
            {
                setVisible( false );
                dispose();
            }
        };

        Runnable waitRunner = new Runnable() {
            public void run() {
                try {
                    Thread.sleep( pause );
                    SwingUtilities.invokeAndWait(closerRunner);
                } catch (Exception e) {
                    e.printStackTrace();
                    // can catch InvocationTargetException
                    // can catch InterruptedException
                }
            }
        };

        return new Thread( waitRunner, "SplashThread" );
    }

    private void addLabel(Icon image) {
        JLabel label = new JLabel( image );
        label.setBorder( BorderFactory.createRaisedBevelBorder() );
        getContentPane().add( label, BorderLayout.CENTER );
        pack();

        Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = label.getPreferredSize();
        setLocation( (screenSize.width >> 1) - (labelSize.width >> 1),
                (screenSize.height >> 1) - (labelSize.height >> 1) );
    }
}

