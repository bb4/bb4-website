package com.becker.simulation.fractals.algorithm;

import com.becker.common.math.ComplexNumber;

/**
 * Populates the FractalModel using the iterative Mandelbrot algorithm..
 *
 * @author Barry Becker
 */
public class MandelbrotAlgorithm extends FractalAlgorithm  {

    public static final int MAX_ITERATIONS = 500;

    public MandelbrotAlgorithm(FractalModel model) {
        super(model, new ComplexNumber(-1.3, -1.0), new ComplexNumber(-0.2, -0.2));
        model.setCurrentRow(0);
    }

    @Override
    public boolean timeStep(double timeStep) {

        if (model.isDone())
            return true;  // we are done.

        int computeToRow = model.getCurrentRow() + (int)timeStep;
        for (int y = model.getCurrentRow(); y < computeToRow; y++)   {
            for (int x = 0; x < model.getWidth(); x++)   {
                ComplexNumber z = getComplexPosition(x, y);
                model.setFractalValue(x, y, getFractalValue(z));
            }
        }
        model.setCurrentRow(computeToRow);
        return false;
    }

    @Override
    public int getFractalValue(ComplexNumber initialValue) {

        ComplexNumber z = initialValue;
        int numIterations = 0;

        while (z.getMagnitude() < 2.0 && numIterations <  MAX_ITERATIONS) {
            z = z.power(2).add(z);
            numIterations++;
        }
        return numIterations;
    }

    
}
