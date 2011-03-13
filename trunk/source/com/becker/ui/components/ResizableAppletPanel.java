package com.becker.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 *  Create a specialized panel that will allow applets to be resizable within a browser
 *  Usually it is not possible to have applets resizable.
 *
 * The basic idea is this:
 *   - We represent a really huge mainPanel.
 *   - The viewable area in the browser will just be a portion of this and be represented by mainPanel.
 *   - The horizontal and vertical resize panel represent the unused margin areas to the right and bottom.
 *     These margin area will not be visible in the browser.
 *   - When you resize the browser window, java script will call the setSize method on the applet
 *     This tells the mainWindow (and margin areas) to resize appropriately.
 *
 *  @author Barry Becker
 */
public class ResizableAppletPanel extends JPanel {

    /** holds what would normally be put in the applet content pane   */
    private JPanel mainPanel_;
    private static final long serialVersionUID = 0L;

    // these buffer panels allows the applet to be resized.
    private JPanel resizeHorizontalPanel_ = new JPanel();
    private JPanel resizeVerticalPanel_ = new JPanel();

    /** Constructor */
    public ResizableAppletPanel( JPanel content ) {

        mainPanel_ = content;
        this.setLayout( new BorderLayout() );

        resizeHorizontalPanel_.setPreferredSize( new Dimension( 1, 200 ) );
        resizeVerticalPanel_.setPreferredSize( new Dimension( 200, 1 ) );
        this.add( mainPanel_, BorderLayout.CENTER );
        this.add( resizeHorizontalPanel_, BorderLayout.EAST );
        this.add( resizeVerticalPanel_, BorderLayout.SOUTH );
    }

    /**
     * This resizes the mainPanel inside the large global applet window.
     */
    @Override
    public void setSize( int width, int height )  {

        int totalWidth = this.getWidth();
        int w = width;
        int h = height;
        if ( w > totalWidth ) {
            w = totalWidth;
        }
        Dimension sizeH = new Dimension( totalWidth - w, h );
        resizeHorizontalPanel_.setPreferredSize(sizeH);

        int totalHeight = this.getHeight();

        if ( h > totalHeight ) {
            h = totalHeight;
        }
        Dimension sizeV = new Dimension( totalWidth, totalHeight - h );
        resizeVerticalPanel_.setPreferredSize(sizeV);

        Dimension mainSize = new Dimension(w, h);
        mainPanel_.setPreferredSize(mainSize);

        resizeHorizontalPanel_.invalidate();
        resizeVerticalPanel_.invalidate();
        this.validate();
    }
}