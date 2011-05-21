package com.becker.optimization.strategy;

import com.becker.common.util.Util;
import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.ParameterArray;

import java.util.*;

/**
 * Hill climbing optimization strategy.
 *
 * @author Barry Becker
 */
public class HillClimbingStrategy extends OptimizationStrategy {

    /** make steps of this size toward the local maxima, until we need something else. */
    private static final double INITIAL_JUMP_SIZE = 0.7;

    /** If the dot product of the new gradient with the old is less than this, then decrease the jump size. */
    private static final double MIN_DOT_PRODUCT = 0.3;
    /** If the dot product of the new gradient with the old is greater than this, then increase the jump size. */
    private static final double MAX_DOT_PRODUCT = 0.98;

    /** continue optimization iteration until the improvement in fitness is less than this.  */
    private static final double FITNESS_EPS_PERCENT = 0.0000001;
    protected static final double JUMP_SIZE_EPS = 0.000000001;

    private static final double JUMP_SIZE_INC_FACTOR = 1.3;
    protected static final double JUMP_SIZE_DEC_FACTOR = 0.7;


    /**
     * Constructor
     * use a hardcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     * @param optimizee the thing to be optimized.
     */
    public HillClimbingStrategy( Optimizee optimizee ) {
        super(optimizee);
    }

    /**
     * Finds a local maxima.
     * Its a bit like newton's method, but in n dimensions.
     * If we make a jump and find that we are worse off than before, we will backtrack and reduce the stepsize so
     * that we can be guaranteed to improve my some amount on every iteration until the incremental improvement
     * is less than the threshold fitness_eps.
     *
     * @param params the initial value for the parameters to optimize.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return the optimized params.
     */
    @Override
    public ParameterArray doOptimization( ParameterArray params, double fitnessRange ) {

        ParameterArray currentParams = params.copy();
        HillClimbIteration iter = new HillClimbIteration(currentParams);

        double jumpSize = INITIAL_JUMP_SIZE;

        if (!optimizee_.evaluateByComparison()) {
            // get the initial baseline fitness value.
            currentParams.setFitness(optimizee_.evaluateFitness(currentParams));
        }
        int numIterations = 0;
        log(0, currentParams.getFitness(), 0.0, 0.0, currentParams, "initial test");

        double improvement;
        double fitnessEps = fitnessRange * FITNESS_EPS_PERCENT / 100.0;
        double oldFitness = currentParams.getFitness();

        // Use cache to avoid repeats. This can be a real issue if  we have a discrete problem space.
        Set<ParameterArray> cache = new HashSet<ParameterArray>();
        cache.add(currentParams);

        // iterate until there is no significant improvement between iterations,
        // of the jumpSize is too small (below some threshold).
        do {
            System.out.println( "iter=" + numIterations + " FITNESS = " + currentParams.getFitness() + "  ---------------");
            double sumOfSqs = 0;

            for ( int i = 0; i < params.size(); i++ ) {
                ParameterArray testParams = currentParams.copy();
                sumOfSqs = iter.incSumOfSqs(i, sumOfSqs, optimizee_, currentParams, testParams);
            }
            double gradLength = Math.sqrt(sumOfSqs);

            HillClimbingStep step =
                    new HillClimbingStep(optimizee_, iter, gradLength, cache, jumpSize, oldFitness);
            currentParams = step.findNextParams(currentParams);
            jumpSize = step.getJumpSize();
            improvement = step.getImprovement();

            double dotProduct = iter.calcDotProduct();

            numIterations++;
            log(numIterations, currentParams.getFitness(), jumpSize, dotProduct, currentParams, Util.formatNumber(improvement));
            notifyOfChange(currentParams);

            // if we are headed in pretty much the same direction as last time, then we increase the jumpSize.
            // if we are headed off in a completely new direction, reduce the jumpSize until we start to stabilize.
            if ( dotProduct > MAX_DOT_PRODUCT )
                jumpSize *= JUMP_SIZE_INC_FACTOR;
            else if ( dotProduct < MIN_DOT_PRODUCT )
                jumpSize *= JUMP_SIZE_DEC_FACTOR;
            //System.out.println( "new jumpsize = " + jumpSize );

            iter.gradient.copyFrom(iter.oldGradient);

            if (!optimizee_.evaluateByComparison())
                oldFitness = currentParams.getFitness();

        } while ( (improvement > fitnessEps)
                && (jumpSize > JUMP_SIZE_EPS)
                && !isOptimalFitnessReached(currentParams));

        System.out.println("The optimized parameters after " + numIterations + " iterations are " + currentParams);
        System.out.println("Last improvement = " + improvement + " jumpSize=" + jumpSize);
        return currentParams;
    }

    private void notifyOfChange(ParameterArray params) {
        if (listener_ != null) {
            listener_.optimizerChanged(params);
        }
    }
}
