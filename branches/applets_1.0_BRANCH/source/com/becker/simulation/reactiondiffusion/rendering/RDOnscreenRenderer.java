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
    protected void renderPoint(int x, int y, Color color, Graphics2D g2) {
        g2.setColor(color);
        g2.drawLine(x, y, x, y);  // a point
    }  
}
