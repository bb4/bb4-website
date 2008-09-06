package com.becker.optimization;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.*;

import java.io.*;

/**
 * This class (the optimizer) uses a specified optimization strategy to optimize something (the optimizee).
 * @see OptimizationType for a list of the possible algorithms.
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

    protected String sLogFile_ = null;
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
        sLogFile_ = optimizationLogFile;
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
     * @return the soution to the optimization problem
     */
    public ParameterArray doOptimization(OptimizationType optimizationType, ParameterArray params, double fitnessRange )
    {
        if (sLogFile_!=null)
            initializeLogFile( params );

        OptimizationStrategy optStrategy = null;

        switch (optimizationType) {
            case GLOBAL_SAMPLING:
                optStrategy = new GlobalSampleStrategy(optimizee_, sLogFile_);
                // 10 sample points in each dim. 1000 evaluations if 3 dimensions.
                ((GlobalSampleStrategy)optStrategy).setSamplingRate(120 / optimizee_.getNumParameters());
                break;
            case GLOBAL_HILL_CLIMBING:
                optStrategy = new GlobalSampleStrategy(optimizee_, sLogFile_);
                // 3 sample points along each dimensiont
                ((GlobalSampleStrategy)optStrategy).setSamplingRate(12 / optimizee_.getNumParameters());
                // first find a good place to start
                // @@ perhaps we should try several of the better results from global sampling.
                params = optStrategy.doOptimization(params, fitnessRange);
                optStrategy = new HillClimbingStrategy(optimizee_, sLogFile_);
                break;
            case HILL_CLIMBING:
                optStrategy = new HillClimbingStrategy(optimizee_, sLogFile_);
                break;
            case SIMULATED_ANNEALING:
                optStrategy = new SimulatedAnnealingStrategy(optimizee_, sLogFile_);
                ((SimulatedAnnealingStrategy)optStrategy).setMaxTemperature(fitnessRange/20.0);
                break;
            case GENETIC_SEARCH:
                optStrategy = new GeneticSearchStrategy(optimizee_, sLogFile_);
                ((GeneticSearchStrategy)optStrategy).setImprovementEpsilon(fitnessRange/100000000.0);
                break;
            case TABU_SEARCH:
                //optStrategy = new TabuStrategy(optimizee_, sLogFile_);
                break;
            case STATE_SPACE:
                break;
        }

        if (optStrategy == null) {
            System.out.println("Optimization strategy not implemented yet: " + optimizationType);
            return params;
        } else {
            optStrategy.setListener(listener_);
            return optStrategy.doOptimization(params, fitnessRange);
        }
    }

    public void setListener(OptimizationListener l) {
        listener_ = l;
    }

    public void removeListener() {
        listener_ = null;
    }


    /**
     * create and init the log file.
     * @param params used to determine param names.
     */
    protected final void initializeLogFile(ParameterArray params)
    {
        try {
            // create the log file (destroying it if it already existed)
            FileWriter logFile = new FileWriter( sLogFile_, false );

            logFile.write( "iteration"+SEPARATOR );
            logFile.write( "fitness"+SEPARATOR );
            logFile.write( "jumpSize"+SEPARATOR );
            logFile.write( "dotprod"+SEPARATOR );
            for (int i=0; i<params.size(); i++) {
                logFile.write( params.get(i).getName() +SEPARATOR );
            }
            logFile.write( "comment " );
            logFile.write( '\n' );
            logFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
