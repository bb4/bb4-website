package com.becker.optimization;

import com.becker.optimization.parameter.ParameterArray;

/**
 * Concrete adapter class for optimizee that does not evaluate by comparison.
 *
 * @author Barry Becker Date: Aug 20, 2006
 */
public abstract class AbsoluteOptimizee implements Optimizee {

    public boolean evaluateByComparison() {
        return false;
    }

    // not used by AbsoluteOptimizees
    public double compareFitness(ParameterArray params1, ParameterArray params2) {
        return 0;
    }

    /**
     * Optional.
     * Override this only if you know that there is some optimal fitness that you need to reach.
     * @return  optimal fitness value. Terminate search when reached.
     */
    public double getOptimalFitness() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

}