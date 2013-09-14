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


    JPanel mainPanel_;
    JScrollPane scrollPane_;
    List<MixPanel> mixPanels_;

    public MixedColorsScrollPane(Color colorA, Color colorB) {
        mainPanel_ = new JPanel();
        mainPanel_.setLayout(new BoxLayout(mainPanel_, BoxLayout.Y_AXIS));
        //mainPanel_.setPreferredSize(new Dimension(500, 900));
        scrollPane_ =
                new JScrollPane(mainPanel_, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mixPanels_ = new ArrayList<MixPanel>();

        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.DST_ATOP, "Dest Atop"));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.DST_IN, "Dest in"));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.DST_OUT, "Dest out"));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.DST_OVER, "Dest Over"));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.SRC_ATOP, "Source Atop"));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.SRC_IN, "Source In"));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.SRC_OUT, "Source in"));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.SRC_OVER, "Source over"));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.CLEAR, "Clear"));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, AlphaComposite.XOR, "XOR"));

        for (MixPanel p : mixPanels_) {
            p.setPreferredSize(new Dimension(200, 60));
            mainPanel_.add(p);
        }

        this.setLayout(new BorderLayout());
        this.add(scrollPane_, BorderLayout.CENTER);
        mainPanel_.invalidate();
    }

    public void setColorsToMix(Color colorA, float opacityA, Color colorB, float opacityB) {
        for (MixPanel p : mixPanels_) {
            p.setColors(colorA, opacityA, colorB, opacityB);
        }
       mainPanel_.invalidate();
    }

    public void setOpacity(float opacity) {
        for (MixPanel p : mixPanels_) {
            p.setOpacity(opacity);
        }
       mainPanel_.invalidate();
    }

}