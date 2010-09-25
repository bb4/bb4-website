package com.becker.simulation.fractals.algorithm;

import com.becker.common.math.ComplexNumber;
import com.becker.common.math.ComplexNumberRange;

/**
 * Populates the FractalModel using the iterative Julia set algorithm.
 *
 * @author Barry Becker
 */
public class JuliaAlgorithm extends FractalAlgorithm  {

    private static final ComplexNumberRange INITIAL_RANGE =
            new ComplexNumberRange(new ComplexNumber(-1.8, -1.7), new ComplexNumber(1.8, 1.7));

    public JuliaAlgorithm(FractalModel model) {
        super(model, INITIAL_RANGE);
        model.setCurrentRow(0);
    }

    @Override
    public double getFractalValue(ComplexNumber initialValue) {

        ComplexNumber z = initialValue;
        int numIterations = 0;

        while (z.getMagnitude() < 2.0 && numIterations < getMaxIterations()) {
            z = z.power(2).add(initialValue);
            numIterations++;
        }
        return (double) numIterations / getMaxIterations();
    }
    
}
