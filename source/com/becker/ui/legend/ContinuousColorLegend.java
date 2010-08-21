package com.becker.ui.legend;

import com.becker.common.ColorMap;
import com.becker.common.math.NiceNumbers;
import com.becker.common.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.event.*;

/**
 * shows a continuous color legend given a list of colors and corresponding values.
 * It may be editable if isEditable is set.
 *
 * @author Barry Becker
 */
public class ContinuousColorLegend extends JPanel {

    private static final int MARGIN = 5;
     private String title_;
    private ColorMap colormap_;

    private static final Font LABEL_FONT = new Font("Sanserif", Font.PLAIN, 10);
    private static final int LABEL_SPACING = 110;
    private static final int MARKER_SIZE = 6;
    private static final int MARKER_HALF_SIZE = 3;
    private static final Color EDIT_BAR_BG = new Color(255, 255, 255, 180);
    private static final BasicStroke MARKER_STROKE = new BasicStroke(0.5f);


    /**
     * By default the min and max come from the colormap min and max
     * in some cases, such as synchronizing with another map, you may want to adjust them.
     */
    private double min_;
    private double max_;

    private LegendEditBar legendEditBar_;
    private boolean isEditable_ = false;

    public ContinuousColorLegend(ColorMap colormap) {
        this(null, colormap, false);
    }

    public ContinuousColorLegend(String title, ColorMap colormap) {
        this(title, colormap, false);
    }

    public ContinuousColorLegend(String title, ColorMap colormap, boolean editable)  {
        title_ = title;
        colormap_ = colormap;
        min_ = colormap_.getMinValue();
        max_ = colormap_.getMaxValue();
        isEditable_ = editable;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                      BorderFactory.createEtchedBorder(),
                      BorderFactory.createMatteBorder(1, 0, 2, 0, this.getBackground())));

        int height = 40;
        if (title_ != null) {
            JPanel titlePanel = new JPanel();
            titlePanel.setOpaque(false);
            JLabel title = new JLabel(title_, JLabel.LEFT);
            title.setOpaque(false);
            titlePanel.add(title, Component.LEFT_ALIGNMENT);
            add(titlePanel);
            add(Box.createRigidArea(new Dimension(4, 4)));
            height = 55;
        }
        legendEditBar_ = new LegendEditBar(colormap_);
        if (isEditable_)  {
            add(legendEditBar_, BorderLayout.NORTH);
        }
        add(createLegendPanel(), BorderLayout.CENTER);
        add(createLegendLabelsPanel(), BorderLayout.SOUTH);

        setMaximumSize(new Dimension(2000, height));

        this.addComponentListener( new ComponentAdapter()  {
            @Override
            public void componentResized( ComponentEvent ce ) {}
        } );
    }


    private JPanel createLegendPanel() {
        return new ColoredLegendLine(colormap_);
    }

    private JPanel createLegendLabelsPanel() {
        return new LegendLabelsPanel(colormap_);
    }

    public boolean isEditable() {
        return isEditable_;
    }

    public void setEditable(boolean editable) {
        if (isEditable_ == editable) {
            return;
        }
        isEditable_ = editable;
        if (isEditable_) {
            add(legendEditBar_, BorderLayout.NORTH);
        } else {
            remove(legendEditBar_);
        }
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

    private void refresh() {
        repaint();
    }


    /**
     * Make the 0 point fall at the same physical spot if both legends include 0.
     */
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
    }



    /** -------------- these inner classes are used to draw the interactive legend  --------------- */

    /**
     * The bar that is the color legend.
     */
    private static class ColoredLegendLine extends JPanel {

        private static final int HEIGHT = 20;
        ColorMap cmap_;

        ColoredLegendLine(ColorMap colormap) {
            cmap_ = colormap;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponents(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(Color.white);
            g2.fillRect(MARGIN, 0, getWidth() - 2*MARGIN, HEIGHT);

            double firstVal = cmap_.getValue(0);
            double rat = (double) (getWidth() - 2*MARGIN) / cmap_.getValueRange();

            for (int i = 1; i < cmap_.getNumValues(); i++) {
                double xstart = rat * (cmap_.getValue(i - 1) - firstVal);
                double xstop =  rat * (cmap_.getValue(i) - firstVal);
                GradientPaint paint =
                    new GradientPaint((float)xstart, 0.0f, cmap_.getColor(i - 1),
                                      (float)(xstop), 0.0f, cmap_.getColor(i),
                                      true);
                g2.setPaint(paint);
                int w = (int)xstop - (int)xstart;
                g2.fillRect((int) xstart + MARGIN, 0, w, HEIGHT);
            }
        }
    }

    /**
     * use the controls within this edit bar to edit the color legend.
     * Does not show is isEditable is false.
     * Not static so we can call methods in the owning legend class.
     */
    private class LegendEditBar extends JPanel implements MouseListener, MouseMotionListener {

        ColorMap cmap_;
        private double ratio_;
        private int dragIndex_ = -1;
        private int dragPosition_;

        LegendEditBar(ColorMap colormap) {
            cmap_ = colormap;
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponents(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

            g2.setColor(EDIT_BAR_BG);
            g2.fillRect(MARGIN, 0, getWidth() - 2*MARGIN, MARKER_SIZE + 4);

            ratio_ = (double) (getWidth() - 2*MARGIN) / cmap_.getValueRange();

            g2.setStroke(MARKER_STROKE);
            for (int i = 0; i < cmap_.getNumValues(); i++) {
                if (dragIndex_ != i)  {
                    int x = getPositionForValue(cmap_.getValue(i));
                    drawMarker(cmap_.getColor(i), x, g2);
                }
            }
            if (dragIndex_ > 0) {
                drawMarker(cmap_.getColor(dragIndex_), dragPosition_, g2);
            }
        }

        /**
         *  Draw a little triangular marker for the draggable control point.
         */
        private void drawMarker(Color c, int xpos, Graphics2D g2) {
            g2.setColor(c);
            int[] xpoints = {xpos - MARKER_HALF_SIZE, xpos + MARKER_HALF_SIZE, xpos};
            int[] ypoints = {1, 1, MARKER_SIZE + 2};
            Polygon triangle = new Polygon(xpoints, ypoints, 3);
            g2.fillPolygon(triangle);
            g2.setColor(Color.BLACK);
            //g2.drawPolygon(triangle);
            g2.drawLine(xpoints[1], ypoints[1], xpoints[2], ypoints[2]);
        }

        private double getValueForPosition(int x) {
            return ((double)x - MARGIN) / ratio_ + cmap_.getMinValue();
        }

        private int getPositionForValue(double v) {
             return (int) (MARGIN + ratio_ * (v - cmap_.getMinValue()));
        }

        /**
         * @return -1 if no control index under the given x pos
         */
        private int getControlIndex(int xpos) {

            double v = getValueForPosition(xpos);
            int i = cmap_.getClosestIndexForValue(v);
            int diff = Math.abs(xpos - getPositionForValue(cmap_.getValue(i)));
            if (diff <= MARKER_HALF_SIZE + 1)
                return i;
            else
                return -1;
        }

        /**
         * @return the index at or to the left of xpos
         */
        private int getLeftControlIndex(int xpos) {

            double v = getValueForPosition(xpos);
            return cmap_.getLeftIndexForValue(v);
        }

        public void mouseClicked(MouseEvent e) {

            int xpos = e.getX();
            int index = getControlIndex(xpos);

            if (e.getButton() == MouseEvent.BUTTON3) {
                // delete on right click
                if (index != -1) {
                    cmap_.removeControlPoint(index);
                }
            }
            else if (e.getClickCount() > 1) {
                Color oldColor = cmap_.getColorForValue(getValueForPosition(xpos));
                Color newControlColor =
                            JColorChooser.showDialog(this, "New Control Point Color", oldColor);
                if (newControlColor != null) {
                    if (index == -1) {
                        // add a new control point and marker here if no point is double clicked on.
                        cmap_.insertControlPoint(getLeftControlIndex(xpos)+1, getValueForPosition(xpos), newControlColor);
                    }
                    else {
                        // get a new color for this control point  double clicked on
                        cmap_.setColor(index, newControlColor);
                    }
                    refresh();
                }
            }
        }

        public void mousePressed(MouseEvent e) {
            int index = getControlIndex(e.getX());

            if (index > 0 && index < (cmap_.getNumValues() - 1)) {
                // we are dragging the control point.
                // Note: can't drag the first and last control points.
                dragIndex_ = index;
                dragPosition_ = e.getX();
            }
        }

        public void mouseReleased(MouseEvent e) {
            // dropped
            updateDrag(e.getX());
            dragIndex_ = -1;
            refresh();
        }

        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        private void updateDrag(int xpos) {
            if (dragIndex_ > 0) {
                double v = getValueForPosition(xpos);
                if (v < cmap_.getValue(dragIndex_+1) && v > cmap_.getValue(dragIndex_ - 1)) {
                    cmap_.setValue(dragIndex_, v);
                    dragPosition_ = xpos;
                    //repaint();
                    paint( getGraphics() );
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            updateDrag(e.getX());
        }

        public void mouseMoved(MouseEvent e) {}
    }



    /**
     * draw labels  underneath the legend line.
     */
    private class LegendLabelsPanel extends JPanel {

        ColorMap cmap_;

        LegendLabelsPanel(ColorMap colormap) {
            cmap_ = colormap;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponents(g);
            Graphics2D g2 = (Graphics2D) g;
            FontRenderContext frc = g2.getFontRenderContext();

            int desiredTicks = this.getWidth() / LABEL_SPACING;
            double[] values =
                      NiceNumbers.getCutPoints(getMin(), getMax(), 2 + desiredTicks, true);


            g2.setColor(this.getBackground());  // was white
            int width = this.getWidth();
            g2.fillRect(0, 0, width, 25);
            int numVals = values.length;

            double rat = (double) (width - 20) / getRangeExtent();
            
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

}
