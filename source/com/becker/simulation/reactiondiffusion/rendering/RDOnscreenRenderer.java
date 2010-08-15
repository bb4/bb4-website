package com.becker.simulation.reactiondiffusion.rendering;

import com.becker.common.ColorMap;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottModel;

import java.awt.*;

/**
 * Renders the state of the GrayScottController model to the screen.
 * @author Barry Becker
 */
public class RDOnscreenRenderer extends RDRenderer {


    /**
     * Constructor
     */
    public RDOnscreenRenderer(GrayScottModel model, ColorMap cmap, RDRenderingOptions options) {
        super(model, cmap, options);
    }


    @Override
    public void render(Graphics2D g2) {

        int xmax = model_.getWidth();
        int ymax = model_.getHeight();

        for (int x = 0; x < xmax; x++) {
            for (int y = 0; y < ymax; y++) {

                double concentration = getConcentration(x, y);
                Color c = getColorForConcentration(concentration, x, y);

                g2.setColor(c);
                g2.drawLine(x, y, x, y);  // a point
            }
        }
    }

}
