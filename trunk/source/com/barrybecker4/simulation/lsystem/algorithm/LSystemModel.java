// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.algorithm;

import com.barrybecker4.simulation.common.Profiler;
import java.awt.image.BufferedImage;

/**
 *
 * @author Barry Becker
 */
public class LSystemModel {

    public static final String DEFAULT_EXPRESSION = "F(+F)F(-F)F";
    public static final int DEFAULT_ITERATIONS = 1;
    public static final double DEFAULT_ANGLE = 90.0;
    public static final double DEFAULT_SCALE = 0.5;
    public static final double DEFAULT_SCALE_FACTOR = 0.9;
    public static final int DEFAULT_SIZE = 256;

    private LSystemRenderer model;

    private int numIterations;
    private double angle;
    private double scale;
    private double scaleFactor;
    private String expression;

    private boolean restartRequested = false;


    public LSystemModel() {
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
        scaleFactor = DEFAULT_SCALE_FACTOR;
        expression = DEFAULT_EXPRESSION;

        model = new LSystemRenderer(DEFAULT_SIZE, DEFAULT_SIZE, expression, numIterations, angle,
                                    scale, scaleFactor);
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

    public void setScaleFactor(double value) {
        if (value != scaleFactor)  {
            scaleFactor = value;
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    public void setExpression(String exp) {
        if (!exp.equals(expression))  {
            expression = exp;
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    private void requestRestart(int width, int height) {
        model = new LSystemRenderer(width, height, expression, numIterations, angle, scale, scaleFactor);
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
            model.render();
        }

        return false;
    }
}
