/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.optimizees;

import com.becker.common.util.FileUtil;
import com.becker.optimization.Optimizer;
import com.becker.optimization.OptimizerEvalFrame;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;

import java.awt.geom.Point2D;

/**
 * This is a simple search space to test the optimization package.
 * The function we will try to maximize is
 *
 *   z = 1 - (1 - p1)^2 - (2 - p2)^2
 *
 * Normally we have no idea what the real function is that we are trying to optimize.
 * Nor is a real habitat function likely to be as well behaved as this one is.
 * This function is very smooth (actually infinitely differentiable) - which is a
 * feature that makes hill-climbing algorithms work very well on.
 * But for this simple case I intentionally use a simple polynomial function with only
 * 2 parameters so that I can solve it analytically and compare it to the optimization results.
 * For this function the global maximum is 1 and it occurs only when p1 = 1 and p2 = 2.
 * There are no other local maxima. The shape of the surface formed by this function
 * is an inverted parabola centered at p1 = 1 and p2 = 2.
 *
 * There are a few variations on the analytic function to choose from, but they all have the same solution.
 * @see SevenElevenTestProblem for somewhat harder example.
 *
 * @author Barry Becker
 */
public class AnalyticFunctionTestProblem extends OptimizeeTestProblem {

    private static final double FITNESS_RANGE = 1000.0;

    private AnalyticVariation variation_ = AnalyticVariation.PARABOLA;

    /** Constructor */
    public AnalyticFunctionTestProblem(AnalyticVariation v) {
        variation_ = v;
    }

    public String getName() {
        return variation_.name();
    }

    // we evaluate directly not by comparing with a different trial.
    public boolean evaluateByComparison() {
        return false;
    }

    // not used
    public double compareFitness(ParameterArray a, ParameterArray b) {
        return 0.0;
    }

    /**
     * @param a the position on the parabolic surface given the specified values of p1 and p2
     * @return fitness value
     */
    public double evaluateFitness(ParameterArray a) {
        return variation_.evaluateFitness(a);
    }

    @Override
    public ParameterArray getInitialGuess() {
        return AnalyticFunctionConsts.INITIAL_GUESS;
    }

    @Override
    public ParameterArray getExactSolution() {
        return AnalyticFunctionConsts.EXACT_SOLUTION;
    }

    @Override
    public double getFitnessRange() {
        return FITNESS_RANGE;
    }

}