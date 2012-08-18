/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.application;

import com.barrybecker4.ui.components.ResizableAppletPanel;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;

/**
 * Base class for programs that you want to be
 * run as applications or resizable applets.
 *
 * @author Barry Becker
 */
public abstract class ApplicationApplet extends JApplet{

    protected ResizableAppletPanel resizablePanel_;

    /**
     * Construct the application.
     */
    public ApplicationApplet() {
        GUIUtil.setCustomLookAndFeel();
    }

    /**
     * initialize. Called by the browser.
     */
    @Override
    public void init() {
        resizablePanel_ = new ResizableAppletPanel(createMainPanel());
        getContentPane().add(resizablePanel_);
    }

    /**
     * create and initialize the application
     * (init required for applet)
     */
    protected abstract JPanel createMainPanel();

    /**
     * This method allow javascript to resize the applet from the browser.
     */
    @Override
    public void setSize( int width, int height ) {

        getContentPane().setSize(width, height);  // this was the key.
        System.out.println("setSize w="+width + " h=" + height);
        if (resizablePanel_ != null) {
            resizablePanel_.setSize( width, height );
        }
    }

    /**
     * called by the browser after init(), if running as an applet
     */
    @Override
    public void start() {
        System.out.println("applet start");
        validate();
    }
}

