package com.becker.simulation.reactiondiffusion.rendering;

import java.awt.*;

/**
 * Renders one of the rectangular strips.
 * @author Barry Becker
 */
public class RenderWorker implements Runnable {
    
    private int minX_, maxX_;
    private Graphics2D g2_;
    private RDRenderer renderer_;
    private boolean synchronizedRendering_;

    public RenderWorker(int minX, int maxX, RDRenderer renderer, boolean synch, Graphics2D g2) {
        minX_ = minX;
        maxX_ = maxX;
        renderer_ = renderer;
        g2_ = g2;
        synchronizedRendering_ = synch;
    }

    public void run() {
        
        if (synchronizedRendering_)  {
            renderer_.renderStripSynchronized(minX_, maxX_, g2_);
        }
        else {
            renderer_.renderStrip(minX_, maxX_, g2_); 
        }
    }
}
