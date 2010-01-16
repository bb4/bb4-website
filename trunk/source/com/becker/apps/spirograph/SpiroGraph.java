package com.becker.apps.spirograph;

import com.becker.ui.ApplicationApplet;
import com.becker.ui.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * That old spirograph game from the 70's brought into the computer age.
 * Based on work originially done by David Little.
 * http://www.math.psu.edu/dlittle/java/parametricequations/index.html
 *
 * @author Barry Becker
 */
public class SpiroGraph extends ApplicationApplet
{

    public SpiroGraph() {}

    @Override
    public JPanel createMainPanel()
    {
        GraphState state_ = ControlSliderGroup.createGraphState();
        GraphRenderer graphRenderer_ = new GraphRenderer(state_);

        ControlPanel cp = new ControlPanel(graphRenderer_, state_);

        JPanel mainPanel = new JPanel( new BorderLayout() );

        mainPanel.add( "Center", graphRenderer_);
        mainPanel.add( "East", cp );

        return mainPanel;
    }


    //------ Main method - to allow running as an application ---------------------
    public static void main( String[] args )
    {
        SpiroGraph applet = new SpiroGraph();
        GUIUtil.showApplet( applet, "SpiroGraph" );
    }
}

