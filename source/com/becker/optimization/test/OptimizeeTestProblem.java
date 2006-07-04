package com.becker.optimization.test;

import com.becker.optimization.*;

/**
 * Abstract base class for optimizer test problems.
 *
 * @author Barry Becker Date: Jun 29, 2006
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

    public int getNumParameters() {
        return getInitialGuess().size();
    }

    /**
     *
     * @param sol solution
     * @return distance from the exact solution as the error.
     */
    public double getError(ParameterArray sol) {
        return 100.0 * sol.distance(getExactSolution()) / getFitnessRange();
    }

    /**
     *
     * @return  approximate range of fitness values (usually 0 to this number).
     */
    public abstract double getFitnessRange();
}
