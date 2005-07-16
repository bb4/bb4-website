package com.becker.ui;

import com.becker.common.ColorMap;
import com.becker.common.Util;
import com.becker.common.NiceCutPoints;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * shows a discrete color legend given a list of colors and corresponding values.
 * @author Barry Becker
 */
public class ContinuousColorLegend extends JPanel {

    private String title_;
    private ColorMap colormap_;
    private JPanel legendLabels_;



    public ContinuousColorLegend(String title, ColorMap colormap)  {
        title_ = title;
        colormap_ = colormap;
        initUI();
    }

    private void initUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);

        if (title_ != null) {
            JPanel titlePanel = new JPanel();
            titlePanel.setOpaque(false);
            JLabel title = new JLabel(title_, JLabel.LEFT);
            title.setOpaque(false);
            titlePanel.add(title, Component.LEFT_ALIGNMENT);
            add(titlePanel);
            add(Box.createRigidArea(new Dimension(4, 4)));
            this.setBorder(BorderFactory.createEtchedBorder());
        }

        legendLabels_ = new JPanel();
        refreshLegendLabels();

        add(createLegend());
        add(legendLabels_);

        this.addComponentListener( new ComponentAdapter()
        {
            public void componentResized( ComponentEvent ce )
            {
                    refreshLegendLabels();
            }
        } );
    }


    private JPanel createLegend() {
        return new ColoredLegendLine(colormap_);
    }


    private void refreshLegendLabels() {

        legendLabels_.removeAll();
        legendLabels_.setOpaque(false);
        legendLabels_.setLayout(new BoxLayout(legendLabels_, BoxLayout.X_AXIS));

        int desiredTicks = this.getWidth() / 90;
        double[] values =
                NiceCutPoints.cutpoints(colormap_.getMinValue(), colormap_.getMaxValue(), 2+desiredTicks, true);


        JLabel labels[] = new JLabel[values.length];
        for (int i=0; i<values.length; i++) {
            labels[i] = new JLabel(Util.formatNumber(values[i]));
            legendLabels_.add(labels[i]);
            if (i < values.length-1)
                legendLabels_.add(Box.createHorizontalGlue());
        }
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
           double rat = (double) this.getWidth() / colormap_.getValueRange();

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
