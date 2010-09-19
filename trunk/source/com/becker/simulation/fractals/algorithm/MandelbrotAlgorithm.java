package com.becker.simulation.fractals.algorithm;

import com.becker.common.math.ComplexNumber;

/**
 * Populates the FractalModel using the iterative Mandelbrot algorithm..
 *
 * @author Barry Becker
 */
public class MandelbrotAlgorithm extends FractalAlgorithm  {


    public MandelbrotAlgorithm(FractalModel model) {
        super(model, new ComplexNumber(-2.1, -1.5), new ComplexNumber(1.1, 1.5));
        model.setCurrentRow(0);
    }

    @Override
    public double getFractalValue(ComplexNumber initialValue) {

        ComplexNumber z = initialValue;
        int numIterations = 0;

        while (z.getMagnitude() < 2.0 && numIterations <  getMaxIterations()) {
            z = z.power(2).add(initialValue);
            numIterations++;
        }

        return (double) numIterations / getMaxIterations();
    }
    
}
