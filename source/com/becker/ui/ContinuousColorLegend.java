package com.becker.ui;

import com.becker.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * shows a continuous color legend given a list of colors and corresponding values.
 * @author Barry Becker
 */
public class ContinuousColorLegend extends JPanel {

    private String title_;
    private ColorMap colormap_;

    private static final Font LABEL_FONT = new Font("Sanserif", Font.PLAIN, 10);

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

        add(createLegendPanel());
        add(createLegendLabelsPanel());

        this.addComponentListener( new ComponentAdapter()  {
            public void componentResized( ComponentEvent ce )
            {}
        } );
    }


    private JPanel createLegendPanel() {
        return new ColoredLegendLine(colormap_);
    }

    private JPanel createLegendLabelsPanel() {
        return new LegendLabelsPanel(colormap_);
    }

    public double getMin() {
        return min_;
    }

    public void setMin(double min) {
        assert(min < max_) : "Min=\"+min+\" cannot be greater than the max=\"+max_;";
        min_ = min;
    }

    public double getMax() {
        return max_;
    }

    public void setMax(double max) {
        assert(max > min_) :"Max="+max+" cannot be less than the min="+min_;
        max_ = max;
    }

    public double getRangeExtent() {
        return max_ - min_;
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

        double leg1Min = legend1.getMin();
        double leg2Min = legend2.getMin();
        double leg1Length = Math.abs(legend1.getMax()) + Math.abs(leg1Min);
        double leg2Length = Math.abs(legend2.getMax()) + Math.abs(leg2Min);
        double leg1Prop = Math.abs(leg1Min) / leg1Length;
        double leg2Prop = Math.abs(leg2Min) / leg2Length;
        double meanProp =(leg1Prop + leg2Prop) / 2.0;
        //System.out.println("leg1Prop="+leg1Prop+" leg2Prop="+leg2Prop+" meanProp="+meanProp);

        if (leg1Prop < meanProp)  {
            // double newMin = legend1.getMin() -(leg2Prop * leg1Length - Math.abs(legend1.getMin())) / (1.0 - leg2Prop);
            legend1.setMin( -meanProp * legend1.getMax() / (1.0 - meanProp));
            legend2.setMax( -leg2Min * ( 1.0 - meanProp) / meanProp);
        } else {
            legend1.setMax( -leg1Min * ( 1.0 - meanProp) / meanProp);
            legend2.setMin( -meanProp * legend2.getMax() / (1.0 - meanProp));
        }
        //leg1Length = Math.abs(legend1.getMax()) + Math.abs(legend1.getMin());
        //leg2Length = Math.abs(legend2.getMax()) + Math.abs(legend2.getMin());
        //System.out.println("leg1Prop="+ legend1.getMin()/leg1Length);
        //System.out.println("leg2Prop="+ legend2.getMin()/leg2Length);
    }


    private static class ColoredLegendLine extends JPanel {

        ColorMap cmap_;

        ColoredLegendLine(ColorMap colormap) {
            cmap_ = colormap;
        }

        public void paintComponent(Graphics g) {
            super.paintComponents(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(Color.white);
            g2.fillRect(0, 0, this.getWidth(), 20);

            double[] vals = cmap_.getValues();
            Color[] colors = cmap_.getColors();
            double firstVal = vals[0];
            double rat = (double) this.getWidth() / cmap_.getValueRange();

            for (int i = 1; i < vals.length; i++) {
                double xstart = rat * (vals[i - 1] - firstVal);
                float width = (float) (rat * (vals[i] - vals[i - 1]));
                GradientPaint paint =
                        new GradientPaint((int) xstart, 0.0f, colors[i - 1], (int) (xstart + width), 0.0f, colors[i]);
                g2.setPaint(paint);
                g2.fillRect((int) xstart, 0, (int) width, 20);
            }
        }
    }

    private static final int LABEL_SPACING = 110;

    private class LegendLabelsPanel extends JPanel {

        ColorMap cmap_;

        LegendLabelsPanel(ColorMap colormap) {
            cmap_ = colormap;
        }

        public void paintComponent(Graphics g) {
            super.paintComponents(g);
            Graphics2D g2 = (Graphics2D) g;
            FontRenderContext frc = g2.getFontRenderContext();

            int desiredTicks = this.getWidth() / LABEL_SPACING;
            double[] values =
                      NiceNumbers.getCutPoints(getMin(), getMax(), 2 + desiredTicks, true);


            g2.setColor(Color.white);
            int width = this.getWidth();
            g2.fillRect(0, 0, width, 25);
            int numVals = values.length;

            double rat = (double) (width - 20) / getRangeExtent();
            //System.out.println("rat="+rat+ "cmap_.getValueRange()="+cmap_.getValueRange());

            g2.setColor(Color.black);
            g2.setFont(LABEL_FONT);
            g2.drawString(Util.formatNumber(getMin()), 2, 10);
            for (int i = 1; i < numVals - 2; i++) {
                double xpos = rat * (values[i] - getMin());
                String label = Util.formatNumber(values[i]);
                g2.drawString(label, (int) xpos, 10);
            }
            String maxLabel = Util.formatNumber(getMax());
            Rectangle2D bounds = g2.getFont().getStringBounds(maxLabel, frc);
            double maxLabelWidth = bounds.getWidth();
            if (values.length > 2) {
                double xpos = rat * (values[numVals - 2] - getMin());
                String label = Util.formatNumber(values[numVals - 2]);
                if ((width - xpos) > (maxLabelWidth + (LABEL_SPACING >> 1))) {
                    g2.drawString(label, (int) xpos, 10);
                }
            }

            g2.drawString(maxLabel, (int) (width - bounds.getWidth() - 5), 10);
        }
    }


    /*
    private void refreshLegendLabels() {

        int desiredTicks = this.getWidth() / 90;
        double[] values =
                NiceNumbers.getCutPoints(getMin(), getMax(), 2 + desiredTicks, true);


        // bug: inaccurate spacing for labels when wight labeling is used.
        JLabel labels[] = new JLabel[values.length];
        for (int i=0; i<values.length; i++) {
            labels[i] = new JLabel(Util.formatNumber(values[i]));
            legendLabels_.add(labels[i]);
            if (i < values.length-1)
                legendLabels_.add(Box.createHorizontalGlue());
        }
    }   */



}
