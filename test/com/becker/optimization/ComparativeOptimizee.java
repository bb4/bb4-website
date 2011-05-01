package com.becker.optimization;

import com.becker.optimization.parameter.ParameterArray;

/**
 * Concrete adapter class for a comparison base Optimizee
 *
 * @author Barry Becker
 */
public abstract class ComparativeOptimizee implements Optimizee {

    public boolean evaluateByComparison() {
        return true;
    }

    // not used for ComparativeOptimizee
    public double evaluateFitness(ParameterArray params) {
        return 0;
    }

    public double getOptimalFitness() {
        return 0;
    }

}
