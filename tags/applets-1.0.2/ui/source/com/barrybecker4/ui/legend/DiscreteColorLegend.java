/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.legend;

import javax.swing.*;
import java.awt.*;

/**
 * shows a discrete color legend given a list of colors and corresponding values.
 * @author Barry Becker
 */
public class DiscreteColorLegend extends JPanel {

    String title_;
    Color[] colors_;
    String[] values_;


    public DiscreteColorLegend(String title, Color[] colors, String[] values)  {
        title_ = title;
        colors_ = colors;
        values_ = values;
        assert (colors_.length == values_.length);
        initUI();
    }

    private void initUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);

        if (title_ != null) {
            JPanel titlePanel = new JPanel();
            titlePanel.setOpaque(false);
            JLabel title = new JLabel(title_, JLabel.CENTER);
            title.setOpaque(false);
            title.setBorder(BorderFactory.createEtchedBorder());
            titlePanel.add(title);
            add(titlePanel );
            add(Box.createRigidArea(new Dimension(4, 4)));
        }

        for (int i=0; i < values_.length; i++) {
             add(createLegendEntry(colors_[i], values_[i]));
        }
    }


    private JPanel createLegendEntry(Color color, String value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        JPanel swatch = new JPanel();
        swatch.setMaximumSize(new Dimension(10, 10));
        swatch.setBackground(color);
        JLabel label = new JLabel(value);
        label.setOpaque(false);

        p.add(fill());
        p.add(swatch);
        p.add(fill());
        p.add(label);
        p.add(Box.createHorizontalGlue());
        return p;
    }

    private static Component fill() {
        return Box.createRigidArea(new Dimension(4,4));
    }

}
