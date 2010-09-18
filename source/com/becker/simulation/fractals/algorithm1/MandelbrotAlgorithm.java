package com.becker.simulation.fractals.algorithm1;

import com.becker.common.math.ComplexNumber;

/**
 * Populates the FractalModel using the iterative Mandelbrot algorithm1..
 *
 * @author Barry Becker
 */
public class MandelbrotAlgorithm extends FractalAlgorithm  {

    public static final int MAX_ITERATIONS = 500;

    public MandelbrotAlgorithm(FractalModel model) {
        super(model, new ComplexNumber(-2.1, -1.5), new ComplexNumber(1.1, 1.5));
        model.setCurrentRow(0);
    }

    @Override
    public int getFractalValue(ComplexNumber initialValue) {

        ComplexNumber z = initialValue;
        int numIterations = 0;

        while (z.getMagnitude() < 2.0 && numIterations <  MAX_ITERATIONS) {
            z = z.power(2).add(initialValue);
            numIterations++;
        }
        return numIterations;
    }
    
}
