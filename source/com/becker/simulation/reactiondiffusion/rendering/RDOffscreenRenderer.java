package com.becker.simulation.reactiondiffusion.rendering;

import com.becker.common.ColorMap;
import com.becker.java2d.OfflineGraphics;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottModel;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Renders the state of the GrayScottController model to an offscreen image,
 * then copies the whole image to the screen.
 * @author Barry Becker
 */
public class RDOffscreenRenderer extends RDRenderer {

    /** offline rendering is fast */
    private OfflineGraphics offlineGraphics_;

    private ImageObserver observer_;

    /**
     * Constructor
     */
    public RDOffscreenRenderer(GrayScottModel model, ColorMap cmap, RDRenderingOptions options, Container imageObserver) {
        super(model, cmap, options);
        observer_ = imageObserver;
        offlineGraphics_ = new OfflineGraphics(imageObserver.getSize(), Color.BLACK);
    }


    @Override
    public void render(Graphics2D g2) {

        int xmax = model_.getWidth();
        int ymax = model_.getHeight();

        for (int x = 0; x < xmax; x++) {
            for (int y = 0; y < ymax; y++) {

                double concentration = getConcentration(x, y);
                Color c = getColorForConcentration(concentration, x, y);
                offlineGraphics_.setColor(c);
                offlineGraphics_.drawPoint(x, y);
            }
        }
        g2.drawImage(offlineGraphics_.getOfflineImage(), 0, 0, observer_);
    }

}
