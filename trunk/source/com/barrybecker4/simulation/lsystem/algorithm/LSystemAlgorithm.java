// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.algorithm;

import com.barrybecker4.simulation.common.Profiler;
import java.awt.image.BufferedImage;

/**
 *
 * @author Barry Becker
 */
public class LSystemAlgorithm {

    public static final int DEFAULT_ITERATIONS = 1;
    public static final double DEFAULT_ANGLE = 90.0;
    public static final double DEFAULT_SCALE = 1.0;
    public static final int DEFAULT_SIZE = 256;

    private LSystemModel model;

    private int numIterations;
    private double angle;
    private double scale;

    private boolean restartRequested = false;


    public LSystemAlgorithm() {
        reset();
    }

    public void setSize(int width, int height)  {

        if (width != model.getWidth() || height != model.getHeight())   {
            requestRestart(width, height);
        }
    }

    public void reset() {

        numIterations = DEFAULT_ITERATIONS;
        angle = DEFAULT_ANGLE;
        scale = DEFAULT_SCALE;

        model = new LSystemModel(DEFAULT_SIZE, DEFAULT_SIZE, numIterations, angle, scale);
    }

    public void setNumIterations(int num) {
        if (num != this.numIterations) {
            numIterations = num;
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    public void setAngle(double ang) {
        if (ang != angle)  {
            angle = ang;
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    public void setScale(double value) {
        if (value != scale)  {
            scale = value;
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    private void requestRestart(int width, int height) {
        model = new LSystemModel(width, height, numIterations, angle, scale);
        restartRequested = true;
    }

    public BufferedImage getImage() {
        return model.getImage();
    }

    /**
     * @param timeStep number of rows to compute on this timestep.
     * @return true when done computing whole model.
     */
    public boolean timeStep(double timeStep) {

        if (restartRequested) {
            restartRequested = false;
            model.reset();
            Profiler.getInstance().startCalculationTime();
        }
        model.render();

        return false;
    }
}
