// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.legend;

import com.barrybecker4.ui.util.ColorMap;

import javax.swing.*;
import java.awt.*;


/**
 * The bar that is the continuous color legend.
 *
 * @author Barry Becker
 */
class ColoredLegendLine extends JPanel {

    private static final int MARGIN = LegendEditBar.MARGIN;

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
        g2.fillRect(MARGIN, 0, getWidth() - 2 * MARGIN, HEIGHT);

        double firstVal = cmap_.getValue(0);
        double rat = (double) (getWidth() - 2 * MARGIN) / cmap_.getValueRange();

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

