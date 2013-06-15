/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.axes;

import com.barrybecker4.ui.components.ResizableAppletPanel;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

public class AxesSynch extends JApplet {

    ResizableAppletPanel resizablePanel = null;

    /** constructor*/
    public AxesSynch() {
        commonInit();
    }

    public void commonInit() {
        GUIUtil.setCustomLookAndFeel();

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        setFont( new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 14 ) );

        JPanel mainPanel = createMainPanel();

        resizablePanel = new ResizableAppletPanel( mainPanel );
        this.getContentPane().add(resizablePanel);
    }

    private JPanel createMainPanel() {
        AxesPanel axesPanel = new AxesPanel();
        axesPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));

        JPanel axesPanelContainer = new JPanel(new BorderLayout());
        axesPanelContainer.add(controlsPanel, BorderLayout.NORTH);
        axesPanelContainer.add(axesPanel, BorderLayout.CENTER);

        mainPanel.add( axesPanelContainer, BorderLayout.NORTH );

        return mainPanel;
    }


    /**
     * This method allow javascript to resize the applet from the browser.
     */
    @Override
    public void setSize( int width, int height ) {

        resizablePanel.setSize(width, height);
    }

    @Override
    public String getName() {
        return "Axes Synchronizer";
    }


    //------ Main method --------------------------------------------------------

    public static void main( String[] args ) {
        AxesSynch simulator = new AxesSynch();
        GUIUtil.showApplet( simulator );
    }
}