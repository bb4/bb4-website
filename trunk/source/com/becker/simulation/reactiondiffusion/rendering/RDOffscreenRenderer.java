package com.becker.simulation.reactiondiffusion.rendering;

import com.becker.common.ColorMap;
import com.becker.simulation.common.rendering.ColorRect;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottModel;
import com.becker.ui.renderers.OfflineGraphics;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Renders the state of the GrayScottController model to an offscreen image,
 * then copies the whole image to the screen.
 * @author Barry Becker
 */
public class RDOffscreenRenderer extends RDRenderer {

    /** offline rendering is fast  (I wish it was anyway)  */
    private OfflineGraphics offlineGraphics_;

    private ImageObserver observer_;

    /**
     * Constructor
     */
    public RDOffscreenRenderer(GrayScottModel model, ColorMap cmap, RDRenderingOptions options,
                               Container imageObserver) {
        super(model, cmap, options);
        observer_ = imageObserver;
        offlineGraphics_ = new OfflineGraphics(imageObserver.getSize(), Color.BLACK);
    }

    /**
     * Renders a rectangular strip of pixels.
     */
    @Override
    public void renderStrip(int minX, ColorRect rect, Graphics2D g2) {

        Image img = rect.getAsImage();
        offlineGraphics_.drawImage(img, minX, 0, null);
    }


    @Override
    protected void postRender(Graphics2D g2) {
        g2.drawImage(offlineGraphics_.getOfflineImage(), 0, 0, observer_);
    }

}
