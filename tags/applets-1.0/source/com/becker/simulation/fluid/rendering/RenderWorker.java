/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.fluid.rendering;

import com.becker.simulation.common.rendering.ModelImage;

/**
 * Renders one of the rectangular strips.
 * @author Barry Becker
 */
public class RenderWorker implements Runnable {

    private ModelImage modelImage_;
    private int minY_, maxY_;

    public RenderWorker(ModelImage modelImage, int minY, int maxY) {
        modelImage_ = modelImage;
        minY_ = minY;
        maxY_ = maxY;
    }

    public void run() {

        modelImage_.updateImage(minY_, maxY_);
    }
}
