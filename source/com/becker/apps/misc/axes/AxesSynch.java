package com.becker.apps.misc.axes;

import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AxesSynch extends JApplet
{

    ResizableAppletPanel resizablePanel_ = null;

    private AxesPanel axesPanel_;


    public boolean isStandalone_ = false;


    // constructor
    public AxesSynch()
    {
        commonInit();
    }

    // constructor
    public void commonInit()
    {
        GUIUtil.setCustomLookAndFeel();

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        setFont( new Font( "Serif", Font.PLAIN, 14 ) );

        JPanel mainPanel = createMainPanel();

        resizablePanel_ = new ResizableAppletPanel( mainPanel );
        this.getContentPane().add( resizablePanel_ );

        axesPanel_.addComponentListener( new ComponentAdapter()
        {
            @Override
            public void componentResized( ComponentEvent ce )
            {
                    //resized();
            }
        } );
    }

    /**
     *  Overrides the applet init() method
     */
    @Override
    public void init()
    {
        //resized();
    }

    private JPanel createMainPanel()
    {
        axesPanel_ = new AxesPanel();
        axesPanel_.setBorder(BorderFactory.createEtchedBorder());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));

        JPanel axesPanel = new JPanel(new BorderLayout());
        axesPanel.add( controlsPanel, BorderLayout.NORTH );
        axesPanel.add( axesPanel_, BorderLayout.CENTER );

        mainPanel.add( axesPanel, BorderLayout.NORTH );

        return mainPanel;
    }


    /**
     * This method allow javascript to resize the applet from the browser.
     */
    public void setSize( int width, int height )
    {
        resizablePanel_.setSize( width, height );
    }


    public void start()
    {
        //resized();
    }

    //------ Main method --------------------------------------------------------

    public static void main( String[] args )
    {
        AxesSynch simulator = new AxesSynch();
        GUIUtil.showApplet( simulator, "Axes Synchronizer" );
    }
}