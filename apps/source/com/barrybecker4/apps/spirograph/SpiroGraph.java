/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph;

import com.barrybecker4.apps.spirograph.model.GraphState;
import com.barrybecker4.ui.application.ApplicationApplet;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * That old spirograph game from the 70's brought into the computer age.
 * Based on work originialy done by David Little.
 * http://www.math.psu.edu/dlittle/java/parametricequations/index.html
 *
 * @author Barry Becker
 */
public class SpiroGraph extends ApplicationApplet {

    @Override
    public JPanel createMainPanel()  {

        GraphState state = ControlSliderGroup.createGraphState();

        GraphPanel graphPanel = new GraphPanel(state);
        ControlPanel controlPanel = new ControlPanel(graphPanel, state);

        JPanel mainPanel = new JPanel( new BorderLayout() );

        mainPanel.add( BorderLayout.CENTER, graphPanel);
        mainPanel.add( BorderLayout.EAST, controlPanel );

        return mainPanel;
    }

    @Override
    public String getName() {
        return "Spirograph";
    }


    /**
     *  Main method - to allow running as an application instead of applet.
     */
    public static void main( String[] args ) {

        SpiroGraph applet = new SpiroGraph();
        GUIUtil.showApplet( applet);
    }
}

