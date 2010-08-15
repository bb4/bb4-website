package com.becker.simulation.reactiondiffusion;

import com.becker.common.ColorMap;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottController;
import com.becker.simulation.reactiondiffusion.rendering.*;

import javax.swing.*;
import java.awt.*;

/**
 * Reaction diffusion viewer.
 */
public class RDViewer extends JPanel {

    private static final int FIXED_SIZE_DIM = 250;

    private GrayScottController grayScott_;
    private RDRenderer onScreenRenderer_;
    private RDRenderer offScreenRenderer_;

    private boolean useFixedSize_ = false;
    private boolean useOfflineRendering = false;

    private int oldWidth;
    private int oldHeight;

    RDRenderingOptions renderOptions_;
    ColorMap cmap_;


    /**
     * Constructor
     */
    public RDViewer(GrayScottController grayScott) {
        grayScott_ = grayScott;
        oldWidth = getWidth();
        oldHeight = getHeight();
        cmap_ = new RDColorMap();
        renderOptions_ = new RDRenderingOptions();
    }

    public RDRenderingOptions getRenderingOptions() {
        return renderOptions_;
    }

    /**
     * @param fixed if true then the render area does not resize automatically.
     */
    public void setUseFixedSize(boolean fixed) {
        useFixedSize_ = fixed;
    }

    public boolean getUseFixedSize() {
        return useFixedSize_;
    }

    public void setUseOffscreenRendering(boolean use) {
        useOfflineRendering = use; 
    }

    public boolean getUseOffScreenRendering() {
        return useOfflineRendering;
    }

    public ColorMap getColorMap() {
        return cmap_;
    }

    @Override
    public void paint( Graphics g )
    {
        checkDimensions();

        Graphics2D g2 = (Graphics2D) g;
        getRenderer().render(g2);
    }

    /**
     * Sets to new size if needed.
     */
    private void checkDimensions() {
        int w = FIXED_SIZE_DIM;
        int h = FIXED_SIZE_DIM;
        if (!useFixedSize_) {
            w = getWidth();
            h = getHeight();
        }
        initRenderers(w, h);
    }

    private void initRenderers(int w, int h) {
        if (w != oldWidth || h != oldHeight) {
            grayScott_.setSize(w, h);
            onScreenRenderer_ = new RDOnscreenRenderer(grayScott_.getModel(), cmap_, renderOptions_);
            offScreenRenderer_ = new RDOffscreenRenderer(grayScott_.getModel(), cmap_, renderOptions_, this);
            oldWidth = w;
            oldHeight = h;
        }
    }


    private RDRenderer getRenderer() {
        return  (useOfflineRendering) ? offScreenRenderer_: onScreenRenderer_;
    }
}
