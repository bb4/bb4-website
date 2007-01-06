package com.becker.simulation.reactiondiffusion;

import com.becker.common.*;

import javax.vecmath.*;
import java.awt.*;

/**
 * @author Barry Becker Date: Nov 5, 2006
 */
public final class RDRenderer {

    private GrayScott gs_;

    private boolean isShowingU_ = false;
    private boolean isShowingV_ = true;

    // used for scaling the bump height. if 0, then no bumpiness.
    private double heightScale_ = 0;


    // the bigger this is the smaller the specular highlight will be.
    private static final double SPECULAR_HIGHLIGHT_EXP = 4.0;
    private static final Vector3d LIGHT_SOURCE_DIR = new Vector3d(1.0, 1.0, 1.0);
    private static final Vector3d HALF_ANGLE;
    private static final Color LIGHT_SOURCE_COLOR = Color.WHITE;
    static {
        LIGHT_SOURCE_DIR.normalize();
        HALF_ANGLE = new Vector3d(0,0,1);
        HALF_ANGLE.add(LIGHT_SOURCE_DIR);
        HALF_ANGLE.normalize();
    }
    private double specularConst_ = 0;



    private static final int MARGIN = 20;

    private ColorMap cmap_;

    double minC_ = 0;
    double maxC_ = 1.0;

    public RDRenderer(GrayScott gs) {
        gs_ = gs;
        cmap_ = createColorMap();
    }


    private ColorMap createColorMap() {

        double range = maxC_ - minC_;

        double[] values = {
              minC_,
              minC_ + 0.04 * range,
              minC_ + 0.1 * range,
              minC_ + 0.3 * range,
              minC_ + 0.5 * range,
              minC_ + 0.7 * range,
              minC_ + 0.94 * range,
              minC_ + range};
        Color[] colors = {
            new Color(0, 0, 0),
            new Color(0, 0, 255),   // .04
            new Color(100, 0, 250),   // .1
            new Color(0, 255, 255),   // .3
            new Color(0, 255, 0),     // .5
            new Color(255, 255, 0),   // .7
            new Color(255, 0, 0),     // .94
            new Color(0, 0, 0)
        };
        return new ColorMap(values, colors);
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
     * Draw the model representing the current state of the GrayScott rd implementation.
     */
    public void render(Graphics2D g2) {

        int cn;
        int xmax = gs_.getWidth();
        int ymax = gs_.getHeight();
        //cmap_ = createColorMap();
        //minC_ = 1000.0;
        //maxC_= -1000.0;

        for (int x = 0; x < xmax; x++) {
            for (int y = 0; y < ymax; y++) {

                double concentration = getConcentration(x, y);

                /*
                if (concentration > maxC_) {
                    maxC_ = concentration;
                }
                if ( concentration < minC_)  {
                    minC_ = concentration;
                }
                */
                Color c = cmap_.getColorForValue(concentration);
                if (heightScale_ != 0) {
                    c = adjustForLighting(c, concentration, x, y, xmax, ymax);
                }

                g2.setColor(c);
                g2.drawLine(MARGIN + x, MARGIN + y, MARGIN + x, MARGIN + y);
            }
        }
    }

    public double getConcentration(int x, int y) {
        double concentration = isShowingU() ? gs_.u_[x][y] : 0.0;
        if (isShowingV()) {
            concentration += gs_.v_[x][y];
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
     *
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

    private Color computeColor(Color c, Vector3d surfaceNormal) {

        // duffuse the surface notrmal with the light source direction, to determine the shading effect.
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
