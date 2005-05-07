package com.becker.ui;

import com.becker.common.ColorMap;
import com.becker.common.Util;
import com.becker.common.NiceCutPoints;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * shows a discrete color legend given a list of colors and corresponding values.
 * @author Barry Becker
 */
public class ContinuousColorLegend extends JPanel {

    String title_;
    ColorMap colormap_;



    public ContinuousColorLegend(String title, ColorMap colormap)  {
        title_ = title;
        colormap_ = colormap;
        initUI();
    }

    private void initUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(10000, 60));

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

        add(createLegend());
        add(createLegendLabels());
    }


    private JPanel createLegend() {
        return new ColoredLegendLine(colormap_);
    }


    private JPanel createLegendLabels() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));

        int desiredTicks = this.getWidth() / 50;
        double[] values =
                NiceCutPoints.cutpoints(colormap_.getMinValue(), colormap_.getMaxValue(), 2+desiredTicks, true);


        JLabel labels[] = new JLabel[values.length];
        for (int i=0; i<values.length; i++) {
            labels[i] = new JLabel(Util.formatNumber(values[i]));
            p.add(labels[i]);
            if (i < values.length-1)
                p.add(Box.createHorizontalGlue());
        }

        return p;
    }

   private class ColoredLegendLine extends JPanel {

       ColorMap colormap_;

       public ColoredLegendLine(ColorMap colormap) {
           colormap_ = colormap;
       }

       public void paintComponent(Graphics g) {
           super.paintComponents( g );
           Graphics2D g2 = (Graphics2D)g;

           g2.setColor(Color.white);
           g2.fillRect(0, 0, this.getWidth(), 20);

           double[] vals = colormap_.getValues();
           Color[] colors = colormap_.getColors();
           double firstVal = vals[0];
           double rat = this.getWidth() / colormap_.getValueRange();

           for (int i=1; i<vals.length; i++)  {
               double xstart = rat * (vals[i-1] - firstVal);
               float width = (float) (rat * (vals[i] - vals[i-1]));
               GradientPaint paint = new GradientPaint((int)xstart, 0f, colors[i-1], (int)(xstart+width), 0f, colors[i]);
               g2.setPaint(paint);
               g2.fillRect((int)xstart, 0, (int)width, 20);
           }
       }
   }

}
