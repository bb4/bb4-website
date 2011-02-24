package com.becker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Boiler plate code for a java application.
 * @author Barry Becker
 */
public class ApplicationFrame extends JFrame
{

    public static final String DEFAULT_TITLE = "ApplicationFrame v1.0";

    public ApplicationFrame()
    {
        this( DEFAULT_TITLE );
    }

    public ApplicationFrame( String title )
    {
        super( (title == null) ? DEFAULT_TITLE : title );
        createUI();
    }

    protected void createUI()
    {
        setSize( 500, 400 );
        center();

        addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent e )
            {
                dispose();
                System.exit( 0 );
            }
        } );
        
        this.setVisible(true);
    }

    public void center()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        int x = (screenSize.width - frameSize.width) >> 1;
        int y = (screenSize.height - frameSize.height) >> 1;
        setLocation( x, y );
 
    }
}