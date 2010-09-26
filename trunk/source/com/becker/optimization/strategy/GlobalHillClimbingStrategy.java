package com.becker.optimization.strategy;

import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.ParameterArray;

/**
 * This is a hybrid optimization strategy.
 * @see GlobalSampleStrategy
 * @see HillClimbingStrategy
 *
 * @author Barry Becker
 */
public class GlobalHillClimbingStrategy extends OptimizationStrategy
{

    /**
     * Constructor
     * use a harcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     * @param optimizee the thing to be optimized.
     */
    public GlobalHillClimbingStrategy( Optimizee optimizee )
    {
        super(optimizee);
    }

    /**
     *
     * @param params
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return optimized params
     */
    @Override
    public ParameterArray doOptimization( ParameterArray params, double fitnessRange )
    {
        GlobalSampleStrategy gsStrategy = new GlobalSampleStrategy(optimizee_);
         // 3 sample points along each dimension
         gsStrategy.setSamplingRate(12 / optimizee_.getNumParameters());
         // first find a good place to start
         // perhaps we should try several of the better results from global sampling.
         ParameterArray sampledParams = gsStrategy.doOptimization(params, fitnessRange);

        OptimizationStrategy strategy = new HillClimbingStrategy(optimizee_);
        return strategy.doOptimization(sampledParams, fitnessRange);
    }
}