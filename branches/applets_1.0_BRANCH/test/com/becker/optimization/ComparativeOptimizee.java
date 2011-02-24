package com.becker.optimization;

import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Concrete adapter class for a comparison base Optimizee
 *
 * @author Barry Becker Date: Aug 20, 2006
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
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
