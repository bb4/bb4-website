package com.becker.ui;

import javax.swing.*;

/**
 * Base class for programs that you want to be
 * run as applications or resizable applets.
 *
 * @author Barry Becker  Date: Dec 2008
 */
public abstract class ApplicationApplet extends JApplet
{
    protected ResizableAppletPanel resizablePanel_;

    static {
        GUIUtil.setStandAlone((GUIUtil.getBasicService() != null));
    }
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

        resizablePanel_ =
                new ResizableAppletPanel(createMainPanel());

        getContentPane().add(resizablePanel_);
    }

    /**
     * create and initialize the puzzle
     * (init required for applet)
     */
    protected abstract JPanel createMainPanel();

    /**
     * This method allow javascript to resize the applet from the browser.
     */
    @Override
    public void setSize( int width, int height )
    {
        super.setSize(width, height);
        if (resizablePanel_ != null) {
            resizablePanel_.setSize( width, height );
        }
    }


    /**
     * called by the browser after init(), if running as an applet
     */
    @Override
    public void start() {}

}

