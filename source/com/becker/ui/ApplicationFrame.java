package com.becker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
            public void windowClosing( WindowEvent e )
            {
                dispose();
                System.exit( 0 );
            }
        } );
    }

    public void center()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;
        setLocation( x, y );
    }
}