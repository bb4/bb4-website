package com.becker.optimization;

import com.becker.common.Util;

import java.io.*;

/**
 * Abstract base class for Optimization strategies.
 *
 * This and derived classes uses the strategy design pattern.
 * @see Optimizer
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
     * @param initialParams the initial guess at the solution
     */
    public abstract ParameterArray doOptimization(ParameterArray initialParams);




    /**
     * Write a row to the file and close it again.
     * That way if we terminate, we still have something in the file.
     * @param iteration the current iteraction.
     * @param fitness the current fitness level. Or increase if fitness if in comparison mode.
     * @param jumpSize the distance we moved in parameter space since the last iteraction.
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
