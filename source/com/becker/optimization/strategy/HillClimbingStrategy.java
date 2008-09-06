package com.becker.optimization.strategy;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.Parameter;
import com.becker.common.util.Util;
import com.becker.optimization.*;

/**
 *  Hill climbing optimization strategy.
 *
 * @author Barry Becker
 */
public class HillClimbingStrategy extends OptimizationStrategy
{

    private static final double INITIAL_JUMP_SIZE = 0.7;
    private static final double MIN_DOT_PRODUCT = 0.3;
    private static final double MAX_DOT_PRODUCT = 0.98;

    // continue optimization iteration until the improvement in fitness is less than this.
    private static final double FITNESS_EPS_PERCENT = 0.0000001;
    private static final double JUMP_SIZE_EPS = 0.000001;

    private static final double JUMP_SIZE_INC_FACTOR = 1.3;
    private static final double JUMP_SIZE_DEC_FACTOR = 0.6;

    // approximate number of steps to take when marching across one of the parmater dimensions.
    // used to caclulate the stepsize in a dimension direction.
    private static final int NUM_STEPS = 30;


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
     *@@ this method should be parallized.
     *
     * @param params the initial value for the parameters to optimize.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return the optimized params.
     */
    public ParameterArray doOptimization( ParameterArray params, double fitnessRange )
    {
        int len = params.size();
        Iteration iter = new Iteration(params);

        double jumpSize = INITIAL_JUMP_SIZE;

        boolean evalByComparison = optimizee_.evaluateByComparison();
        if (!evalByComparison) {
            // get the initial baseline fitness value.
            params.setFitness(optimizee_.evaluateFitness(params));
        }
        int numIterations = 0;
        writeToLog(0, params.getFitness(), 0.0, 0.0, params, "initial test");

        double improvement = 0;
        double fitnessEps = fitnessRange * FITNESS_EPS_PERCENT / 100.0;
        double oldFitness = params.getFitness();

        // iterate until there is no significant improvement between iterations,
        // of the jumpSize is too small (below some threshold).
        do {
            System.out.println( "iter=" + numIterations + "  FITNESS ==== " + params.getFitness() + "  -----------------" );
            //assert ( params.getFitness() >= 0.0): "*** Error the fitness is less than 0!!" ;
            double sumOfSqs = 0;

            for ( int i = 0; i < len; i++ ) {
                ParameterArray testParams = params.copy();
                sumOfSqs = iter.incSumOfSqs(i, sumOfSqs, optimizee_, params, testParams);
            }
            double gradLength = Math.sqrt(sumOfSqs);
            System.out.println("Grad len = "+ gradLength);

            // now compute the gradient vector that will allow us
            // to make a quantum leap in the direction of greatest improvement.
            boolean improved;

            do {
                improved = true;
                ParameterArray oldParams = params.copy();

                iter.updateGradient(jumpSize, gradLength);

                //System.out.println( "gradient to add = " + params.vecToString( iter.gradient ) );
                params.add( iter.gradient );
                //System.out.println( "the new params are = \n" + params );

                if (evalByComparison) {
                    params.setFitness(optimizee_.compareFitness(params, oldParams));
                    if (params.getFitness() < 0)
                        improved = false;
                    else
                        improvement = params.getFitness();
                }
                else {
                    params.setFitness(optimizee_.evaluateFitness(params));
                    if (params.getFitness() < oldFitness)
                        improved = false;
                    else
                        improvement = params.getFitness() - oldFitness;
                }
                if (!improved) {
                    // we have not improved, try again with a reduced jump size.
                    System.out.println( "Warning: the new params are worse so reduce the step size and try again");
                    writeToLog(numIterations, params.getFitness(), jumpSize, Double.NaN, params, "not improved");
                    params = oldParams;
                    jumpSize *= JUMP_SIZE_DEC_FACTOR;
                }
            } while (!improved && (jumpSize > JUMP_SIZE_EPS) );

            double dotProduct = ParameterArray.dot( iter.gradient, iter.oldGradient );
            double divisor = (ParameterArray.length( iter.gradient ) * ParameterArray.length( iter.oldGradient ));
            dotProduct = (divisor==0.0) ? 1.0 : dotProduct/divisor;
            numIterations++;
            writeToLog(numIterations, params.getFitness(), jumpSize, dotProduct, params, Util.formatNumber(improvement));

            if (listener_ != null) {
                listener_.optimizerChanged(params);
            }

            // if we are headed in pretty much the same direction as last time, then we increase the jumpSize.
            // if we are headed off in a completely new direction, reduce the jumpsize until we start to stabilize.
            if ( dotProduct > MAX_DOT_PRODUCT )
                jumpSize *= JUMP_SIZE_INC_FACTOR;
            else if ( dotProduct < MIN_DOT_PRODUCT )
                jumpSize *= JUMP_SIZE_DEC_FACTOR;
            System.out.println( "new jumpsize = " + jumpSize );

            System.arraycopy(iter.gradient, 0, iter.oldGradient, 0, len);

            if (!evalByComparison)
                oldFitness = params.getFitness();

        } while ( (improvement > fitnessEps)
                && (jumpSize > JUMP_SIZE_EPS)
                && !isOptimalFitnessReached(params));

        System.out.println( "The optimized parameters after " + numIterations + " iterations are " + params );
        return params;
    }


    /**
     * private utility class for maintining the data vecs for the interation.
     */
    private static class Iteration {

        double[] delta;
        double[] fitnessDelta;
        double[] gradient;
        double[] oldGradient;

        Iteration(ParameterArray params) {
            delta = params.createDoubleArray();
            fitnessDelta = params.createDoubleArray();
            gradient = params.createDoubleArray();
            oldGradient = params.createDoubleArray();

            // initiallize the old gradient to the unit vector (any random direction will do)
            for ( int i = 0; i < params.size(); i++)
                oldGradient[i] = 1.0;
            oldGradient = ParameterArray.normalize(oldGradient);
        }

        /**
         * Compute the square in one of the iteration directions and add it to the running sum.
         * @param i
         * @param sumOfSqs
         * @param optimizee
         * @param params
         * @param testParams
         * @return the sum of squares in one of the iteration directions.
         */
        double incSumOfSqs(int i, double sumOfSqs, Optimizee optimizee,
                           ParameterArray params, ParameterArray testParams) {

            double fwdFitness;
            double bwdFitness;

            Parameter p = testParams.get( i );
            // this does the increment and returns the amount incremented (forward).
            delta[i] = p.increment( NUM_STEPS, 1 );

            if (optimizee.evaluateByComparison())
                fwdFitness = optimizee.compareFitness( testParams, params );
            else
                fwdFitness = optimizee.evaluateFitness( testParams );

            // this checks the fitness on the other side (backwards).
            p.increment( NUM_STEPS, -1 );
            p.increment( NUM_STEPS, -1 );

            if (optimizee.evaluateByComparison())
                bwdFitness = optimizee.compareFitness( testParams, params );
            else
                bwdFitness = optimizee.evaluateFitness( testParams );

            fitnessDelta[i] = fwdFitness - bwdFitness;
            double newSumOfSqs = sumOfSqs + (fitnessDelta[i] * fitnessDelta[i]) / (delta[i] * delta[i]);

            return newSumOfSqs;
        }

        void updateGradient(double jumpSize, double gradLength) {
            for ( int i = 0; i < delta.length; i++ ) {
                gradient[i] = jumpSize * fitnessDelta[i] / (delta[i] * gradLength);
            }
        }

    }
}
