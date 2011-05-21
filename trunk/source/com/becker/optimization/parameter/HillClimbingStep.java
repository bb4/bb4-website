package com.becker.optimization.parameter;

import com.becker.optimization.Optimizee;

import java.util.Set;


/**
 * A step in the Hill climbing optimization strategy.
 * Hopefully heads in the right direction.
 *
 * @author Barry Becker
 */
public class HillClimbingStep  {

    private Optimizee optimizee_;
    private HillClimbIteration iter_;
    private double gradLength;
    private Set<ParameterArray> cache;
    private double jumpSize;
    private double improvement;
    private double oldFitness;
    private boolean improved;

    /** continue optimization iteration until the improvement in fitness is less than this.  */
    protected static final double JUMP_SIZE_EPS = 0.000000001;

    public static final double JUMP_SIZE_INC_FACTOR = 1.3;
    public static final double JUMP_SIZE_DEC_FACTOR = 0.7;


    /**
     * Constructor
     * use a hardcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     * @param optimizee the thing to be optimized.
     */
    public HillClimbingStep(Optimizee optimizee, HillClimbIteration iter, double gradLength, Set<ParameterArray> cache,
                            double jumpSize, double oldFitness) {
        optimizee_ = optimizee;
        iter_ = iter;
        this.gradLength = gradLength;
        this.cache = cache;
        this.jumpSize = jumpSize;
        this.oldFitness = oldFitness;
    }

    public double getJumpSize() {
        return jumpSize;
    }

    public double getImprovement() {
        return improvement;
    }

    /**
     *
     * @param params the initial value for the parameters to optimize.
     * @return the parameters to try next.
     */
    public NumericParameterArray findNextParams(NumericParameterArray params) {

        NumericParameterArray currentParams = params;

        do {
            currentParams = findNextCandidateParams(currentParams);

        } while (!improved && (jumpSize > JUMP_SIZE_EPS) );

        return currentParams;
    }

    private NumericParameterArray findNextCandidateParams(NumericParameterArray params) {
        improved = true;
        NumericParameterArray currentParams = params;
        NumericParameterArray oldParams = currentParams.copy();

        iter_.updateGradient(jumpSize, gradLength);
        //System.out.println("gradient = " + Arrays.toString(iter.gradient));
        currentParams = currentParams.copy();
        currentParams.add( iter_.gradient );
        double gaussRadius = 0.01;
        boolean sameParams = false;

        // for problems with discrete params, we want to avoid testing the same candidate over again. */
        while (cache.contains(currentParams)) {
            sameParams = true;
            currentParams = currentParams.getRandomNeighbor(gaussRadius);
            //System.out.println("Cache hit. Nbr=" + currentParams + " rad="
            // + gaussRadius + " jumpSize="+jumpSize + " numInCache="+ cache.size());
            gaussRadius *= 2;
        }
        cache.add(currentParams);

        if (optimizee_.evaluateByComparison()) {
            currentParams.setFitness(optimizee_.compareFitness(currentParams, oldParams));
            if (currentParams.getFitness() < 0)  {
                improved = false;
            }
            improvement = currentParams.getFitness();
        }
        else {
            currentParams.setFitness(optimizee_.evaluateFitness(currentParams));
            if (currentParams.getFitness() <= oldFitness) {
                improved = false;
            }
            improvement = currentParams.getFitness() - oldFitness;
        }

        if (!improved) {
            currentParams = oldParams;
            if (!sameParams) {
                // we have not improved, try again with a reduced jump size.
                //System.out.println( "Warning: the new params are worse so reduce the step size and try again");
                //log(numIterations, currentParams.getFitness(), jumpSize, Double.NaN, currentParams, "not improved");
                jumpSize *= JUMP_SIZE_DEC_FACTOR;
            }
        }
        return currentParams;
    }
}
