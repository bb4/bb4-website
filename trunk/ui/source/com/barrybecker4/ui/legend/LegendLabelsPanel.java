// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.legend;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.math.cutpoints.CutPointGenerator;
import com.barrybecker4.common.math.Range;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * Draw labels underneath the legend line.
 *
 * @author Barry Becker
 */
class LegendLabelsPanel extends JPanel {

    private static final Font LABEL_FONT = new Font("Sanserif", Font.PLAIN, 10); //NON-NLS
    private static final int LABEL_SPACING = 110;

    /**
     * By default the min and max come from the colormap min and max
     * in some cases, such as synchronizing with another map, you may want to adjust them.
     */
    private Range range_;

    private CutPointGenerator cutPointGenerator;


    LegendLabelsPanel(ColorMap colormap) {
        range_ = new Range(colormap.getMinValue(), colormap.getMaxValue());
        cutPointGenerator = new CutPointGenerator();
    }

    public double getMin() {
        return range_.getMin();
    }

    public void setMin(double min) {
        assert(min < range_.getMax()) : "Min=" + min + " cannot be greater than the max=" + range_.getMax();
        range_ = new Range(min, range_.getMax());
    }

    public double getMax() {
        return range_.getMax();
    }

    public void setMax(double max) {
        assert(max > range_.getMin()) :"Max=" + max + " cannot be less than the min=" + range_.getMin();
        range_ = new Range(range_.getMin(), max);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2 = (Graphics2D) g;
        FontRenderContext frc = g2.getFontRenderContext();

        int desiredTicks = this.getWidth() / LABEL_SPACING;

        double[] values = cutPointGenerator.getCutPoints(range_, 2 + desiredTicks);

        g2.setColor(this.getBackground());  // was white
        int width = this.getWidth();
        g2.fillRect(0, 0, width, 25);
        int numVals = values.length;

        double rat = (double) (width - 20) / range_.getExtent();

        g2.setColor(Color.black);
        g2.setFont(LABEL_FONT);
        g2.drawString(FormatUtil.formatNumber(range_.getMin()), 2, 10);
        for (int i = 1; i < numVals - 2; i++) {
            double xpos = rat * (values[i] - range_.getMin());
            String label = FormatUtil.formatNumber(values[i]);
            g2.drawString(label, (int) xpos, 10);
        }
        String maxLabel = FormatUtil.formatNumber(range_.getMax());
        Rectangle2D bounds = g2.getFont().getStringBounds(maxLabel, frc);
        double maxLabelWidth = bounds.getWidth();
        if (values.length > 2) {
            double xpos = rat * (values[numVals - 2] - range_.getMin());
            String label = FormatUtil.formatNumber(values[numVals - 2]);
            if ((width - xpos) > (maxLabelWidth + (LABEL_SPACING >> 1))) {
                g2.drawString(label, (int) xpos, 10);
            }
        }

        g2.drawString(maxLabel, (int) (width - bounds.getWidth() - 5), 10);
    }
}
