/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.colormixer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Barry Becker
 * Date: Feb 18, 2005
 * Time: 7:38:16 AM
 */
public class MixedColorsScrollPane extends JPanel {


    JPanel mainPanel;
    JScrollPane scrollPane;
    List<MixPanel> mixPanels;

    public MixedColorsScrollPane(Color colorA, Color colorB) {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        //mainPanel.setPreferredSize(new Dimension(500, 900));
        scrollPane =
                new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mixPanels = new ArrayList<MixPanel>();

        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.DST_ATOP, "Dest Atop"));
        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.DST_IN, "Dest in"));
        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.DST_OUT, "Dest out"));
        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.DST_OVER, "Dest Over"));
        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.SRC_ATOP, "Source Atop"));
        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.SRC_IN, "Source In"));
        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.SRC_OUT, "Source in"));
        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.SRC_OVER, "Source over"));
        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.CLEAR, "Clear"));
        mixPanels.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.XOR, "XOR"));

        for (MixPanel p : mixPanels) {
            p.setPreferredSize(new Dimension(200, 60));
            mainPanel.add(p);
        }

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        mainPanel.invalidate();
    }

    public void setColorsToMix(Color colorA, float opacityA, Color colorB, float opacityB) {
        for (MixPanel p : mixPanels) {
            p.setColors(colorA, opacityA, colorB, opacityB);
        }
       mainPanel.invalidate();
    }

    public void setOpacity(float opacity) {
        for (MixPanel p : mixPanels) {
            p.setOpacity(opacity);
        }
       mainPanel.invalidate();
    }

}
