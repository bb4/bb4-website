package com.becker.simulation.reactiondiffusion.rendering;

import com.becker.common.ColorMap;
import com.becker.simulation.common.rendering.ColorRect;
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


    /**
     * Renders a rectangular strip of pixels.
     */
    @Override
    public void renderStrip(int minX, ColorRect rect, Graphics2D g2) {

        Image img = rect.getAsImage();
        g2.drawImage(img, minX, 0, null);
    }

    @Override
    protected void postRender(Graphics2D g2) {}
}
