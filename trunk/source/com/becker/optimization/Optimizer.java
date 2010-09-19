package com.becker.optimization;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategy;
import com.becker.optimization.strategy.OptimizationStrategyType;

/**
 * This class (the optimizer) uses a specified optimization strategy to optimize something (the optimizee).
 * @see OptimizationStrategyType for a list of the possible algorithms.
 *
 *  This class uses the delegation design pattern rather than inheritance
 * so that it can be reused across many classes. For example, I could have
 * added an optimize method into the game/TwoPlayerController class, and all the subclasses
 * of TwoPlayerController would be able to use it. However, by having the optimization
 * classes in their own package, they can be used by a variety of projects to do
 * optimization. Also it abstracts the concept of optimization and as a result
 * makes it easy to work on independently. For example, I use this package to
 * optimize the motion of the snake in com.becker.snake and the trebuchet simulation.
 *
 * This class also acts as a facade to the optimization package. The use of this package
 * really does not need to direclty construct or use the different optimization strategy classes.
 *
 * Details of the optimization algorithms can be found in
 *  How To Solve It: Modern Heuristics  by Michaelwics and Fogel
 *
 * Optimization is nearly the same thing as search. In the redpuzzle package, I use optimization
 * to search for a solution using the genetic algorithm strategy.
 *
 * @author Barry Becker
 */
public class Optimizer
{
    Optimizee optimizee_;

    // debug level of 0 means no debug info, 3 is all debug info.
    protected static final int DEBUG_LEVEL = 0;

    protected Logger logger_;
    public static final String SEPARATOR = ",\t";

    protected OptimizationListener listener_;


    /**
     * Constructor
     * No log file specified in this constructor. (use this version if running in unsigned applet).
     * @param optimizee the thing to be optimized.
     */
    public Optimizer( Optimizee optimizee )
    {
        optimizee_ = optimizee;
    }

    /**
     * Constructor
     * @param optimizee the thing to be optimized.
     * @param optimizationLogFile the file that will record the results
     */
    public Optimizer( Optimizee optimizee, String optimizationLogFile )
    {
        optimizee_ = optimizee;
        logger_ = new Logger(optimizationLogFile);
    }

    public Optimizee getOptimizee() {
        return optimizee_;
    }
    /**
     * This method will construct an optimization strategy object of the specified type and run it.
     *
     * @param optimizationType the type of search to perform
     * @param params the initialGuess at the solution. Also defines the bounds of the search space.
     * @param fitnessRange the approximate range (max-min) of the fitness values
     * @return the soution to the optimization problem.
     */
    public ParameterArray doOptimization(OptimizationStrategyType optimizationType, ParameterArray params, double fitnessRange )
    {


        OptimizationStrategy optStrategy = optimizationType.getStrategy(optimizee_, fitnessRange);
        if (logger_ != null) {
            logger_.initialize( params );
            optStrategy.setLogger(logger_);
        }
  
        optStrategy.setListener(listener_);
        return optStrategy.doOptimization(params, fitnessRange);
    }

    public void setListener(OptimizationListener l) {
        listener_ = l;
    }
}
