package com.becker.misc.colormixer.colormixer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Barry Becker
 * Date: Feb 18, 2005
 * Time: 7:38:16 AM
 */
public class MixedColorsScrollPane extends JPanel {


    JPanel mainPanel_;
    JScrollPane scrollPane_;
    ArrayList mixPanels_;

    public MixedColorsScrollPane(Color colorA, Color colorB) {
        mainPanel_ = new JPanel();
        mainPanel_.setLayout(new BoxLayout(mainPanel_, BoxLayout.Y_AXIS));
        //mainPanel_.setPreferredSize(new Dimension(500, 900));
        scrollPane_ = new JScrollPane(mainPanel_, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mixPanels_ = new ArrayList();


        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, MixPanel.ADDITIVE_MIX));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, MixPanel.SUBTRACTIVE_MIX));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, MixPanel.OVER_MIX));
        mixPanels_.add(new MixPanel(colorA, 1.0f, colorB, 1.0f, MixPanel.UNDER_MIX));


        for (int i=0; i<mixPanels_.size(); i++) {
            MixPanel p = (MixPanel)mixPanels_.get(i);
            p.setPreferredSize(new Dimension(200, 100));
            mainPanel_.add(p);
        }

        this.setLayout(new BorderLayout());
        this.add(scrollPane_, BorderLayout.CENTER);
        mainPanel_.invalidate();
    }

    public void setColorsToMix(Color colorA, float opacityA, Color colorB, float opacityB) {
       for (int i=0; i<mixPanels_.size(); i++) {
            MixPanel p = (MixPanel)mixPanels_.get(i);
            p.setColors(colorA, opacityA, colorB, opacityB);
       }
       mainPanel_.invalidate();
    }

}
