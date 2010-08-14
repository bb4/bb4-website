package com.becker.simulation.reactiondiffusion;

import com.becker.common.*;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottModel;

import javax.vecmath.*;
import java.awt.*;

/**
 * Randers the state of the GrayScottController model to the screen.
 * @author Barry Becker
 */
public final class RDRenderer {

    private GrayScottModel model_;

    private boolean isShowingU_ = false;
    private boolean isShowingV_ = true;

    /** used for scaling the bump height. if 0, then no bumpiness. */
    private double heightScale_ = 0;

    /** the bigger this is the smaller the specular highlight will be. */
    private static final double SPECULAR_HIGHLIGHT_EXP = 4.0;
    private static final Vector3d LIGHT_SOURCE_DIR = new Vector3d(1.0, 1.0, 1.0);
    private static final Vector3d HALF_ANGLE;
    private static final Color LIGHT_SOURCE_COLOR = Color.WHITE;
    static {
        LIGHT_SOURCE_DIR.normalize();
        HALF_ANGLE = new Vector3d(0, 0, 1);
        HALF_ANGLE.add(LIGHT_SOURCE_DIR);
        HALF_ANGLE.normalize();
    }

    private double specularConst_ = 0;

    private ColorMap cmap_;

    /**
     * Constructor
     */
    RDRenderer(GrayScottModel model) {
        model_ = model;
        cmap_ = new RDColorMap();
    }

    public void setHeightScale(double h) {
        heightScale_ = h;
    }

    public void setSpecular(double s) {
        specularConst_ = s;
    }

    public ColorMap getColorMap() {
        return cmap_;
    }

    /**
     * Draw the model representing the current state of the GrayScottController rd implementation.
     */
    public void render(Graphics2D g2) {

        int xmax = model_.getWidth();
        int ymax = model_.getHeight();

        for (int x = 0; x < xmax; x++) {
            for (int y = 0; y < ymax; y++) {

                double concentration = getConcentration(x, y);

                Color c = cmap_.getColorForValue(concentration);
                if (heightScale_ != 0) {
                    c = adjustForLighting(c, concentration, x, y, xmax, ymax);
                }

                g2.setColor(c);

                g2.drawLine(x, y, x, y);  // a point
            }
        }
    }

    public double getConcentration(int x, int y) {
        double concentration = isShowingU() ? model_.getU(x, y): 0.0;
        if (isShowingV()) {
            concentration += model_.getV(x, y);
        }
        return concentration;
    }

    public boolean isShowingU() {
        return isShowingU_;
    }

    public void setShowingU(boolean showingU) {
        isShowingU_ = showingU;
    }

    public boolean isShowingV() {
        return isShowingV_;
    }

    public void setShowingV(boolean showingV) {
        isShowingV_ = showingV;
    }


    /**
     * @param c color of surface
     * @return new color based on old, but accounting for lighting effects using the Phong reflection model.
     */
    private Color adjustForLighting(Color c, double concentration, int x, int y, int xmax, int ymax) {
        double xdelta = 0;
        double ydelta = 0;
        if (x < xmax - 1) {
            xdelta = getConcentration(x+1, y) - concentration;
        }
        if (y < ymax - 1) {
            ydelta = getConcentration(x, y+1) - concentration;
        }
        Vector3d xVec = new Vector3d(1.0, 0.0, heightScale_ * xdelta);
        Vector3d yVec = new Vector3d(0.0, 1.0, heightScale_ * ydelta);
        Vector3d surfaceNormal = new Vector3d();
        surfaceNormal.cross(xVec, yVec);
        surfaceNormal.normalize();

        return computeColor(c, surfaceNormal);
    }

    /**
     * Diffuse the surface normal with the light source direction, to determine the shading effect.
     * @param c base color
     * @param surfaceNormal surface normal for lighting calculations.
     * @return color adjusted for lighting.
     */
    private Color computeColor(Color c, Vector3d surfaceNormal) {

        //
        double duffuse = Math.abs(surfaceNormal.dot(LIGHT_SOURCE_DIR));
        double specular = 0;
        if (specularConst_ > 0)
           specular = specularConst_ *Math.pow(Math.abs(surfaceNormal.dot(HALF_ANGLE)), SPECULAR_HIGHLIGHT_EXP);

        Color cc = c.brighter();
        return new Color(
                (int)Math.min(255, cc.getRed() * duffuse + LIGHT_SOURCE_COLOR.getRed() * specular),
                (int)Math.min(255, cc.getGreen() * duffuse + LIGHT_SOURCE_COLOR.getGreen() * specular),
                (int)Math.min(255, cc.getBlue() * duffuse + LIGHT_SOURCE_COLOR.getBlue() * specular));
    }

}
