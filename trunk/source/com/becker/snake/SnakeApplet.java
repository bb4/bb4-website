package com.becker.snake;

/**
 * @author Barry Becker
 */

import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;

public class SnakeApplet extends JApplet
{

    SnakeSimulator simulator_ = null;
    private ResizableAppletPanel resizablePanel_ = null;
    private static final boolean RUN_OPTIMIZATION = false;

    /**
     * the applet's init method
     */
    public void init()
    {
        simulator_ = new SnakeSimulator();
        JPanel animPanel = new AnimationPanel( simulator_ );
        animPanel.add( simulator_.createTopControls(), BorderLayout.NORTH );

        resizablePanel_ = new ResizableAppletPanel( animPanel );
        this.getContentPane().add( resizablePanel_ );
    }

    /**
     * the applets start method
     */
    public void start()
    {
        if (RUN_OPTIMIZATION)
            simulator_.doOptimization();
    }

    /**
     * This method allow javascript to resize the applet from the browser.
     */
    public void setSize( int width, int height )
    {
        resizablePanel_.setSize( width, height );
    }

    //------ Main method - to allow running as an application ---------------------
    public static void main( String[] args )
    {
        SnakeApplet applet = new SnakeApplet();
        JFrame baseFrame = GUIUtil.showApplet( applet, "Snake Applet" );
    }
}

