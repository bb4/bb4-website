package com.becker.simulation.fractals.algorithm;

import com.becker.common.math.ComplexNumber;

/**
 * Populates the FractalModel using the iterative Mandelbrot algorithm..
 *
 * @author Barry Becker
 */
public class JuliaAlgorithm extends FractalAlgorithm  {

    public static final int MAX_ITERATIONS = 500;

    public static final ComplexNumber INITIAL_VALUE = new ComplexNumber(-0.4, 0.6);

    public JuliaAlgorithm(FractalModel model) {
        super(model, new ComplexNumber(-1.9, -1.8), new ComplexNumber(1.9, 1.8));
        model.setCurrentRow(0);
    }

    @Override
    public int getFractalValue(ComplexNumber initialValue) {

        ComplexNumber z = initialValue;
        int numIterations = 0;

        while (z.getMagnitude() < 2.0 && numIterations <  MAX_ITERATIONS) {
            z = z.power(2).add(INITIAL_VALUE);
            numIterations++;
        }
        return numIterations;
    }

    
}
