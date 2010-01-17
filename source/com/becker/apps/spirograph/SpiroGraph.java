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

    @Override
    public JPanel createMainPanel()
    {
        GraphState state = ControlSliderGroup.createGraphState();

        GraphRenderer graphRenderer = new GraphRenderer(state);
        ControlPanel cp = new ControlPanel(graphRenderer, state);

        JPanel mainPanel = new JPanel( new BorderLayout() );

        mainPanel.add( BorderLayout.CENTER, graphRenderer);
        mainPanel.add( BorderLayout.EAST, cp );

        return mainPanel;
    }


    //------ Main method - to allow running as an application --------
    public static void main( String[] args )
    {
        SpiroGraph applet = new SpiroGraph();
        GUIUtil.showApplet( applet, "SpiroGraph" );
    }
}

