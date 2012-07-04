/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.optimizees;

import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Abstract base class for optimizer test problems.
 *
 * @author Barry Becker
 */
public abstract class OptimizeeTestProblem implements Optimizee {


    /**
     * @return  the exact solution for this problem.
     */
    public abstract ParameterArray getExactSolution();

    /**
     * @return  the exact solution for this problem.
     */
    public abstract ParameterArray getInitialGuess();

    /**
     *
     * @param sol solution
     * @return distance from the exact solution as the error.
     */
    public double getError(ParameterArray sol) {
        return 100.0 * sol.distance(getExactSolution()) / getFitnessRange();
    }

    public double getOptimalFitness() {
        return 0;
    }

    /**
     *
     * @return  approximate range of fitness values (usually 0 to this number).
     */
    public abstract double getFitnessRange();

}
