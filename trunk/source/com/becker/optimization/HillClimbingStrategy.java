package com.becker.optimization;

import com.becker.common.Util;

/**
 *  Hill climbing optimization strategy.
 *
 * @author Barry Becker
 */
public class HillClimbingStrategy extends OptimizationStrategy
{

    private static final double INITIAL_JUMP_SIZE = .7;
    private static final double MIN_DOT_PRODUCT = .3;
    private static final double MAX_DOT_PRODUCT = .98;


    // continue optimization iteration until the improvement in fitness is less than this.
    private static final double FITNESS_EPS = .000001;
    private static final double JUMP_SIZE_EPS = .00001;

    private static final double JUMP_SIZE_INC_FACTOR = 1.4;
    private static final double JUMP_SIZE_DEC_FACTOR = .5;



    /**
     * Constructor
     * use a harcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     * No log file specified in this constructor. (use this version if running in unsigned applet).
     * @param optimizee the thing to be optimized.
     */
    public HillClimbingStrategy( Optimizee optimizee )
    {
        super(optimizee);
    }

    /**
     * Constructor
     * @param optimizee the thing to be optimized.
     * @param optimizationLogFile the file that will record the results
     */
    public HillClimbingStrategy( Optimizee optimizee, String optimizationLogFile )
    {
        super(optimizee, optimizationLogFile);
    }



    /**
     * finds a local maxima.
     * Its a bit like newton's method, but in n dimensions.
     * If we make a jump and find that we are worse off than before, we will backtrack and reduce the stepsize so
     * that we can be quaranteed to improve my some amount on every iteration until the incremental improvement
     * is less than the threshold fitness_eps.
     *
     * @param params the initial value for the parameters to optimize.
     * @return the optimized params.
     */
    public ParameterArray doOptimization( ParameterArray params )
    {
        int len = params.size();
        double[] delta = params.createDoubleArray();
        double[] fitnessDelta = params.createDoubleArray();
        double[] gradient = params.createDoubleArray();
        double[] oldGradient = params.createDoubleArray();
        // initiallize the old gradient to the unit vector (any random direction will do)
        for ( int i = 0; i < len; i++ )
            oldGradient[i] = 1.0;
        oldGradient = ParameterArray.normalize(oldGradient);

        double jumpSize = INITIAL_JUMP_SIZE;
        double oldFitness = 0.0;
        if (!optimizee_.evaluateByComparison())
            oldFitness = optimizee_.evaluateFitness(params);
        int numIterations = 0;
        writeToLog(0, oldFitness, 0.0, 0.0, params, "initial test");

        double improvement = 0;
        // set the number of steps to get from one end of the param range to the other.
        int numSteps = 30;

        do {
            System.out.println( "iter=" + numIterations + "  FITNESS ==== " + oldFitness + "  -----------------" );
            assert ( oldFitness >= 0.0): "*** Error the fitness is less than 0!!" ;
            double sumOfSqs = 0;
            for ( int i = 0; i < len; i++ ) {
                ParameterArray testParams = params.copy();
                Parameter p = testParams.get( i );
                // this does the increment and returns the amount incremented
                delta[i] = p.increment( numSteps, 1 );

                double fwdFitness = 0.0;
                double bwdFitness = 0.0;
                if (optimizee_.evaluateByComparison())
                    fwdFitness = optimizee_.compareFitness( testParams, params );
                else
                    fwdFitness = optimizee_.evaluateFitness( testParams );

                p.increment( numSteps, -1 );
                p.increment( numSteps, -1 );
                if (optimizee_.evaluateByComparison())
                    bwdFitness = optimizee_.compareFitness( testParams, params );
                else
                    bwdFitness = optimizee_.evaluateFitness( testParams );

                fitnessDelta[i] = fwdFitness - bwdFitness;
                sumOfSqs += fitnessDelta[i]*fitnessDelta[i] / (4*delta[i]*delta[i]);

            }
            double gradLength = Math.sqrt(sumOfSqs);
            // now compute the gradient vector that will allow us
            // to make a quantum leap in the direction of greatest improvement.

            boolean improved = true;
            double newFitness = 0.0;
            do {
                improved = true;
                ParameterArray oldParams = params.copy();

                for ( int i = 0; i < len; i++ ) {
                    gradient[i] = jumpSize * 0.5 *  (fitnessDelta[i] / delta[i])/gradLength;
                }

                System.out.println( "gradient to add = " + params.vecToString( gradient ) );
                params.add( gradient );
                System.out.println( "the new params are = \n" + params );

                if (optimizee_.evaluateByComparison()) {
                    newFitness = optimizee_.compareFitness(params, oldParams);
                    if (newFitness < 0)
                        improved = false;
                    else
                        improvement = newFitness;
                }
                else {
                    newFitness = optimizee_.evaluateFitness(params);
                    if (newFitness < oldFitness)
                        improved = false;
                    else
                        improvement = newFitness - oldFitness;
                }
                if (!improved) {
                    // we have not improved, try again with a reduced jump size.
                    System.out.println( "Warning: the new params are worse so reduce the step size and try again");
                    writeToLog(numIterations, newFitness, jumpSize, Double.NaN, params, "not improved");
                    params = oldParams;
                    jumpSize *= JUMP_SIZE_DEC_FACTOR;
                }
            } while (!improved && (jumpSize > JUMP_SIZE_EPS) );

            double dotProduct = params.dot( gradient, oldGradient );
            double divisor = (params.length( gradient ) * params.length( oldGradient ));
            dotProduct = (divisor==0.0) ? 1.0 : dotProduct/divisor;
            numIterations++;
            writeToLog(numIterations, newFitness, jumpSize, dotProduct, params, Util.formatNumber(improvement));

            // if we are headed in pretty much the same direction as last time, then we increase the sumpSize.
            // if we are headed off in a completely new direction, reduce the jumpsize until we start to stabilize.
            if ( dotProduct > MAX_DOT_PRODUCT )
                jumpSize *= JUMP_SIZE_INC_FACTOR;
            else if ( dotProduct < MIN_DOT_PRODUCT )
                jumpSize *= JUMP_SIZE_DEC_FACTOR;
            System.out.println( "new jumpsize = " + jumpSize );

            for ( int i = 0; i < len; i++ ) {
                oldGradient[i] = gradient[i];
            }

            if (!optimizee_.evaluateByComparison())
                oldFitness = newFitness;

        } while ( (improvement > FITNESS_EPS) && (jumpSize > JUMP_SIZE_EPS) );

        System.out.println( "The optimized parameters after " + numIterations + " iteractions are " + params );
        return params;
    }

}
