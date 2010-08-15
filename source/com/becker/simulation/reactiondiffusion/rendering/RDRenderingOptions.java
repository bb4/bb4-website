package com.becker.simulation.reactiondiffusion.rendering;

import com.becker.common.ColorMap;
import com.becker.simulation.reactiondiffusion.algorithm.GrayScottModel;

import javax.vecmath.Vector3d;
import java.awt.*;

/**
 * Date: Aug 15, 2010
 *
 * @author Barry Becker
 */
public class RDRenderingOptions {

    private boolean isShowingU_ = false;
    private boolean isShowingV_ = true;

    /** used for scaling the bump height. if 0, then no bumpiness. */
    private double heightScale_ = 0;

    /** Specular highlight degree. */
    private double specularConst_ = 0;


    /** the bigger this is the smaller the specular highlight will be. */
    public static final Vector3d LIGHT_SOURCE_DIR = new Vector3d(1.0, 1.0, 1.0);
    private static final double SPECULAR_HIGHLIGHT_EXP = 4.0;
    private static final Vector3d HALF_ANGLE;

    static {
        LIGHT_SOURCE_DIR.normalize();
        HALF_ANGLE = new Vector3d(0, 0, 1);
        HALF_ANGLE.add(LIGHT_SOURCE_DIR);
        HALF_ANGLE.normalize();
    }


    /**
     * Constructor
     */
    public RDRenderingOptions() {
    }

    public void setHeightScale(double h) {
        heightScale_ = h;
    }

    protected double getHeightScale() {
        return heightScale_;
    }


    public void setSpecular(double s) {
        specularConst_ = s;
    }

   public double getSpecular() {
        return specularConst_;
    }

    public double getSpecularExponent(Vector3d surfaceNormal) {
        double specular = 0;
        if (specularConst_ > 0)  {
           specular = specularConst_ * Math.pow(Math.abs(surfaceNormal.dot(HALF_ANGLE)), SPECULAR_HIGHLIGHT_EXP);
        }
        return specular;
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
}
