/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Create a specialized panel that will allow applets to be resizable within a browser.
 * Usually it is not possible to have applets resizable.
 *
 * The basic idea is this:
 *   - We represent a really huge mainPanel.
 *   - The viewable area in the browser will just be a portion of this and be represented by mainPanel.
 *   - The horizontal and vertical resize panel represent the unused margin areas to the right and bottom.
 *     These margin area will not be visible in the browser.
 *   - When you resize the browser window, javascript will call the setSize method on the applet
 *     This tells the mainWindow (and margin areas) to resize appropriately.
 *
 *  @author Barry Becker
 */
public class ResizableAppletPanel extends JPanel {

    /** holds what would normally be put in the applet content pane   */
    private JPanel mainPanel;

    /** Horizontal buffer panel allowing the applet to be resized horizontally. */
    private JPanel resizeHorizontalPanel = new JPanel();
    /** Vertical buffer panel allowing the applet to be resized vertically. */
    private JPanel resizeVerticalPanel = new JPanel();

    /** Constructor */
    public ResizableAppletPanel( JPanel content ) {

        mainPanel = content;
        this.setLayout( new BorderLayout() );

        resizeHorizontalPanel.setPreferredSize(new Dimension(1, 200));
        resizeVerticalPanel.setPreferredSize(new Dimension(200, 1));
        this.add(mainPanel, BorderLayout.CENTER );
        this.add(resizeHorizontalPanel, BorderLayout.EAST );
        this.add(resizeVerticalPanel, BorderLayout.SOUTH );
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
        resizeHorizontalPanel.setPreferredSize(sizeH);

        int totalHeight = getHeight();

        if ( h > totalHeight ) {
            h = totalHeight;
        }
        Dimension sizeV = new Dimension( totalWidth, totalHeight - h );
        resizeVerticalPanel.setPreferredSize(sizeV);

        Dimension mainSize = new Dimension(w, h);
        mainPanel.setPreferredSize(mainSize);

        resizeHorizontalPanel.invalidate();
        resizeVerticalPanel.invalidate();
        validate();
    }
}