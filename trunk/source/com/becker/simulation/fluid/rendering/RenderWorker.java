package com.becker.simulation.fluid.rendering;

import com.becker.simulation.common.ColorRect;

import java.awt.*;

/**
 * Renders one of the rectangular strips.
 * @author Barry Becker
 */
public class RenderWorker implements Runnable {
    
    private int minX_, maxX_;
    private Graphics2D g2_;
    private EnvironmentRenderer renderer_;

    public RenderWorker(int minX, int maxX, EnvironmentRenderer renderer, Graphics2D g2) {
        minX_ = minX;
        maxX_ = maxX;
        renderer_ = renderer;
        g2_ = g2;
    }

    public void run() {

        ColorRect colorRect = renderer_.getColorRect(minX_, maxX_);
        renderer_.renderPressureStrip(minX_, colorRect, g2_);
    }
}
