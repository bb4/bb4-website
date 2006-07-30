package com.becker.misc.eatest.maze;

import javax.swing.*;
import java.awt.*;

/**
 *  Create a specialized panel that will allow applets to be resizable within a browser
 *  Usually it is not possible to have applets resizable.
 *
 *  @author Barry Becker
 */
public class ResizableAppletPanel extends JPanel
{

    // holds what would normally be put in the applet contentpane
    private JPanel mainPanel_ = null;
    private static final long serialVersionUID = 0L;

    // these buffer panels allows the applet to be resized
    private JPanel resizeHorizontalPanel_ = new JPanel();
    private JPanel resizeVerticalPanel_ = new JPanel();

    //Constructor
    public ResizableAppletPanel( JPanel content )
    {
        mainPanel_ = content;
        this.setLayout( new BorderLayout() );

        resizeHorizontalPanel_.setPreferredSize( new Dimension( 1, 400 ) );
        resizeVerticalPanel_.setPreferredSize( new Dimension( 400, 1 ) );
        this.add( mainPanel_, BorderLayout.CENTER );
        this.add( resizeHorizontalPanel_, BorderLayout.EAST );
        this.add( resizeVerticalPanel_, BorderLayout.SOUTH );
    }

    /**
     *  This resizes the mainPanel inside the large global applet window
     */
    public void setSize( int width, int height )
    {
        int totalWidth = this.getWidth();
        if ( width > totalWidth )
            width = totalWidth;
        Dimension sizeH = new Dimension( totalWidth - width, height );
        resizeHorizontalPanel_.setPreferredSize( sizeH );

        int totalHeight = this.getHeight();
        if ( height > totalHeight )
            height = totalHeight;
        Dimension sizeV = new Dimension( totalWidth, totalHeight - height );
        resizeVerticalPanel_.setPreferredSize( sizeV );

        Dimension size1 = new Dimension( width, height );
        mainPanel_.setPreferredSize( size1 );

        resizeHorizontalPanel_.invalidate();
        resizeVerticalPanel_.invalidate();
        validate();
        repaint();
    }

}