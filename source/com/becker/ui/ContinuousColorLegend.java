package com.becker.ui;

import com.becker.common.ColorMap;
import com.becker.common.Util;
import com.becker.common.RoundCutPoints;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * shows a continuous color legend given a list of colors and corresponding values.
 * @author Barry Becker
 */
public class ContinuousColorLegend extends JPanel {

    private String title_;
    private ColorMap colormap_;
    private JPanel legendLabels_;

    // by default the min and max come from the colormap min and max
    // in some cases, such as synchronizing with another map, you may want to adjust them.
    private double min_;
    private double max_;

    public ContinuousColorLegend(String title, ColorMap colormap)  {
        title_ = title;
        colormap_ = colormap;
        min_ = colormap_.getMinValue();
        max_ = colormap_.getMaxValue();
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

        this.addComponentListener( new ComponentAdapter()  {
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
                RoundCutPoints.cutpoints(getMin(), getMax(), 2 + desiredTicks, true);

        JLabel labels[] = new JLabel[values.length];
        for (int i=0; i<values.length; i++) {
            labels[i] = new JLabel(Util.formatNumber(values[i]));
            legendLabels_.add(labels[i]);
            if (i < values.length-1)
                legendLabels_.add(Box.createHorizontalGlue());
        }
    }


    public double getMin() {
        return min_;
    }

    public void setMin(double min) {
        assert(min < max_);
        min_ = min;
    }

    public double getMax() {
        return max_;
    }

    public void setMax(double max) {
        assert(max > min_);
        max_ = max;
    }


    public static void synchronizeLegends(ContinuousColorLegend legend1, ContinuousColorLegend legend2) {
        // assume pixel widths are the same
        if (!( legend1.getMin()<=0 && legend1.getMax()>=0)) {
            System.out.println("legend1 does not have 0 in its range");
            return;
        }
        if (!( legend2.getMin()<=0 && legend2.getMax()>=0)) {
            System.out.println("legend2 does not have 0 in its range");
            return;
        }

        double leg1Prop = legend1.getMin() / (legend1.getMax() + legend1.getMin());
        double leg2Prop = legend2.getMin() / (legend2.getMax() + legend2.getMin());
        double meanProp = (leg1Prop + leg2Prop) / 2.0;

        if (leg1Prop < meanProp)  {
            legend1.setMin( leg1Prop * legend1.getMax() / (leg1Prop - 1.0));
        } else {
            legend1.setMax(-legend1.getMin() * ( 1.0 - leg1Prop) / leg1Prop);
        }

        if (leg2Prop < meanProp)  {

            legend2.setMin( leg2Prop * legend2.getMax() / (leg2Prop - 1.0));
        } else {
            legend2.setMax(-legend2.getMin() * ( 1.0 - leg2Prop) / leg2Prop);
        }
    }



    private static class ColoredLegendLine extends JPanel {

       ColorMap cmap_;

       ColoredLegendLine(ColorMap colormap) {
           cmap_ = colormap;
       }

       public void paintComponent(Graphics g) {
           super.paintComponents( g );
           Graphics2D g2 = (Graphics2D)g;

           g2.setColor(Color.white);
           g2.fillRect(0, 0, this.getWidth(), 20);

           double[] vals = cmap_.getValues();
           Color[] colors = cmap_.getColors();
           double firstVal = vals[0];
           double rat = (double) this.getWidth() / cmap_.getValueRange();

           for (int i=1; i<vals.length; i++)  {
               double xstart = rat * (vals[i-1] - firstVal);
               float width = (float) (rat * (vals[i] - vals[i-1]));
               GradientPaint paint =
                       new GradientPaint((int)xstart, 0.0f, colors[i-1], (int)(xstart+width), 0.0f, colors[i]);
               g2.setPaint(paint);
               g2.fillRect((int)xstart, 0, (int)width, 20);
           }
       }
   }

}
