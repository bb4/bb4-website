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

    /** the bigger this is the smaller the specular highlight will be. */
    private static final Color LIGHT_SOURCE_COLOR = Color.WHITE;

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
     * Draw the model representing the current state of the GrayScottController rd implementation.
     */
    public void render(Graphics2D g2) {

        int maxX = model_.getWidth();
        int ymax = model_.getHeight();

        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < ymax; y++) {

                Color c = getColorForValue(model_.getFractalValue(x, y));

                g2.setColor(c);
                g2.drawLine(x, y, x, y);
            }
        }

    }

    protected Color getColorForValue(int value) {
        Color c = getColorMap().getColorForValue(value);
        //if (options_.getHeightScale() != 0) {
        //    c = adjustForLighting(c, value, x, y);
        //}
        return c;
    }


    /**
     * @param c color of surface
     * @return new color based on old, but accounting for lighting effects using the Phong reflection model.
     *
    protected Color adjustForLighting(Color c, double concentration, int x, int y) {
        double xdelta = 0;
        double ydelta = 0;
        if (x < model_.getWidth() - 1) {
            xdelta = getConcentration(x+1, y) - concentration;
        }
        if (y < model_.getHeight() - 1) {
            ydelta = getConcentration(x, y+1) - concentration;
        }
        double htScale = options_.getHeightScale();
        Vector3d xVec = new Vector3d(1.0, 0.0, htScale * xdelta);
        Vector3d yVec = new Vector3d(0.0, 1.0, htScale * ydelta);
        Vector3d surfaceNormal = new Vector3d();
        surfaceNormal.cross(xVec, yVec);
        surfaceNormal.normalize(); 

        return computeColor(c, surfaceNormal);
    }*/

    /**
     * Diffuse the surface normal with the light source direction, to determine the shading effect.
     * @param c base color
     * @param surfaceNormal surface normal for lighting calculations.
     * @return color adjusted for lighting.
     *
    protected Color computeColor(Color c, Vector3d surfaceNormal) {

        double diffuse = Math.abs(surfaceNormal.dot(RDRenderingOptions.LIGHT_SOURCE_DIR));
        double specular = options_.getSpecularExponent(surfaceNormal);

        Color cc = c.brighter();
        return new Color(
                (int)Math.min(255, cc.getRed() * diffuse + LIGHT_SOURCE_COLOR.getRed() * specular),
                (int)Math.min(255, cc.getGreen() * diffuse + LIGHT_SOURCE_COLOR.getGreen() * specular),
                (int)Math.min(255, cc.getBlue() * diffuse + LIGHT_SOURCE_COLOR.getBlue() * specular));
    }*/
}
