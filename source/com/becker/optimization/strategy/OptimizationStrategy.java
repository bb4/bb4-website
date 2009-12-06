package com.becker.optimization.strategy;

import com.becker.common.util.Util;
import com.becker.optimization.Logger;
import com.becker.optimization.OptimizationListener;
import com.becker.optimization.Optimizee;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.ParameterArray;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Abstract base class for Optimization strategy.
 *
 * This and derived classes uses the strategy design pattern.
 * @see Optimizer
 *
 * @author Barry Becker
 */
public abstract class OptimizationStrategy
{
    Optimizee optimizee_;

    /** debug level of 0 means no debug info, 3 is all debug info.  */
    protected static final int DEBUG_LEVEL = 0;

    protected Logger logger_;

    /** listen for optimization changed events. useful for debugging.  */
    protected OptimizationListener listener_;


    /**
     * Constructor
     * @param optimizee the thing to be optimized.
     */
    public OptimizationStrategy( Optimizee optimizee )
    {
        optimizee_ = optimizee;
    }

    /**
     * @param logger the file that will record the results
     */
    public void setLogger(Logger logger) {
        logger_ = logger;
    }

    /**
     *
     * @param initialParams the initial guess at the solution.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return optimized parameters.
     */
    public abstract ParameterArray doOptimization(ParameterArray initialParams, double fitnessRange);


    public void setListener(OptimizationListener l) {
        listener_ = l;
    }


    /**
     * @param currentBest current best parameter set.
     * @return true if the optimal fitness has been reached.
     */
    protected boolean isOptimalFitnessReached(ParameterArray currentBest) {
        boolean optimalFitnessReached = false;
        if (optimizee_.getOptimalFitness() > 0 && !optimizee_.evaluateByComparison()) {
             optimalFitnessReached = currentBest.getFitness() >= optimizee_.getOptimalFitness();
        }
        return optimalFitnessReached;
    }

}
