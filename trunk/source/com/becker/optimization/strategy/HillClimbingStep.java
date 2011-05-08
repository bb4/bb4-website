package com.becker.optimization.strategy;

import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Set;


/**
 * Hill climbing optimization strategy.
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
    public ParameterArray findNextParams(ParameterArray params) {

        ParameterArray currentParams = params;

        do {
            currentParams = findNextCandidateParams(currentParams);

        } while (!improved && (jumpSize > HillClimbingStrategy.JUMP_SIZE_EPS) );

        return currentParams;
    }

    private ParameterArray findNextCandidateParams(ParameterArray params) {
        improved = true;
        ParameterArray currentParams = params;
        ParameterArray oldParams = currentParams.copy();

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
            if (currentParams.getFitness() < 0)
                improved = false;
            else
                improvement = currentParams.getFitness();
        }
        else {
            currentParams.setFitness(optimizee_.evaluateFitness(currentParams));
            if (currentParams.getFitness() <= oldFitness)
                improved = false;
            else
                improvement = currentParams.getFitness() - oldFitness;
        }

        if (!improved) {
            currentParams = oldParams;
            if (!sameParams) {
                // we have not improved, try again with a reduced jump size.
                //System.out.println( "Warning: the new params are worse so reduce the step size and try again");
                //log(numIterations, currentParams.getFitness(), jumpSize, Double.NaN, currentParams, "not improved");
                jumpSize *= HillClimbingStrategy.JUMP_SIZE_DEC_FACTOR;
            }
        }
        return currentParams;
    }
}
