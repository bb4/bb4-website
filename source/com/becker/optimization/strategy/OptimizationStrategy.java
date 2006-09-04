package com.becker.optimization.strategy;

import com.becker.common.Util;
import com.becker.optimization.*;

import java.io.*;

/**
 * Abstract base class for Optimization strategy.
 *
 * This and derived classes uses the strategy design pattern.
 * @see com.becker.optimization.Optimizer
 *
 * @author Barry Becker
 */
public abstract class OptimizationStrategy
{
    Optimizee optimizee_;

    // debug level of 0 means no debug info, 3 is all debug info.
    protected static final int DEBUG_LEVEL = 0;
    protected static final int DEFAULT_SAMPLING_RATE = 3;

    protected String sLogFile_ = null;

    // listen for optimization changed events. useful for debugging.
    protected OptimizationListener listener_;



    /**
     * Constructor
     * No log file specified in this constructor. (use this version if running in unsigned applet).
     * @param optimizee the thing to be optimized.
     */
    public OptimizationStrategy( Optimizee optimizee )
    {
        optimizee_ = optimizee;
    }

    /**
     * Constructor
     * @param optimizee the thing to be optimized.
     * @param optimizationLogFile the file that will record the results
     */
    public OptimizationStrategy( Optimizee optimizee, String optimizationLogFile )
    {
        optimizee_ = optimizee;
        sLogFile_ = optimizationLogFile;
    }


    /**
     *
     * @param initialParams the initial guess at the solution.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     */
    public abstract ParameterArray doOptimization(ParameterArray initialParams, double fitnessRange);


    public void setListener(OptimizationListener l) {
        listener_ = l;
    }

    public void removeListener() {
        listener_ = null;
    }

    /**
     * @param currentBest
     * @return true if the optimal fitness has been reached.
     */
    protected boolean isOptimalFitnessReached(ParameterArray currentBest) {
        boolean optimalFitnessReached = false;
        if (optimizee_.getOptimalFitness() > 0 && !optimizee_.evaluateByComparison()) {
             optimalFitnessReached = currentBest.getFitness() >= optimizee_.getOptimalFitness();
        }
        return optimalFitnessReached;
    }

    /**
     * Write a row to the file and close it again.
     * That way if we terminate, we still have something in the file.
     * @param iteration the current iteration.
     * @param fitness the current fitness level. Or increase if fitness if in comparison mode.
     * @param jumpSize the distance we moved in parameter space since the last iteration.
     * @param params the params to write.
     */
    protected final void writeToLog(int iteration, double fitness, double jumpSize, double distance,
                                  ParameterArray params, String comment)
    {
        String sep = Optimizer.SEPARATOR;
        String rowText = iteration + sep
                       + Util.formatNumber(fitness) + sep
                       + Util.formatNumber(jumpSize) + sep
                       + Util.formatNumber(distance) + sep
                       + params.toCSVString() + sep
                       + comment;

        if (sLogFile_ == null) {
            System.out.println( "<no logfile>: "+rowText );
            return;
        }
        try {
            // append to existing log file.
            FileWriter logFile = new FileWriter( sLogFile_, true );
            logFile.write( rowText+'\n' );
            logFile.flush();
            logFile.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
