package com.becker.simulation.fractals;

import com.becker.common.ColorMap;
import com.becker.simulation.fractals.algorithm.FractalModel;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottModel;
import com.becker.simulation.reactiondiffusion.rendering.RDRenderingOptions;
import com.becker.simulation.reactiondiffusion.rendering.RenderWorker;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the state of the fractal model to the screen.
 * @author Barry Becker
 */
public class FractalRenderer {

    protected FractalModel model_;

    private ColorMap cmap_;


    /**
     * Constructor
     */
    FractalRenderer(FractalModel model, ColorMap cmap) {
        model_ = model;
        cmap_ = cmap;
    }


    public ColorMap getColorMap() {
        return cmap_;
    }

    /**
     * Draw the fractal model.
     */
    public void render(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        int maxX = model_.getWidth();
        int ymax = model_.getCurrentRow();

        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < ymax; y++) {

                Color c = getColorMap().getColorForValue(model_.getFractalValue(x, y));

                g2.setColor(c);
                g2.drawLine(x, y, x, y);
            }
        }

    }
}
