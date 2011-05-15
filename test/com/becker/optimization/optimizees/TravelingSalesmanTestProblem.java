package com.becker.optimization.optimizees;

import com.becker.common.util.FileUtil;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.IntegerParameter;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.PermutedParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;

/**
 * This is a simple search space to test the optimization package.
 *
 * @author Barry Becker
 */
public class TravelingSalesmanTestProblem extends OptimizeeTestProblem {

    private TravelingSalesmanVariation variation_ = TravelingSalesmanVariation.SIMPLE;


    /** constructor */
    public TravelingSalesmanTestProblem(TravelingSalesmanVariation variation) {
        variation_ = variation;
    }

    /**
     * we evaluate directly not by comparing with a different trial.
     */
    public boolean evaluateByComparison() {
        return false;
    }

    // not used
    public double compareFitness(ParameterArray a, ParameterArray b) {
        return 0.0;
    }

    /**
     * Use the cost matrix for the TSP variation to determine this.
     * @return fitness value
     */
    public double evaluateFitness(ParameterArray a) {
        return variation_.evaluateFitness(a);
    }

    @Override
    public ParameterArray getExactSolution() {
        return variation_.getExactSolution();
    }

    @Override
    public ParameterArray getInitialGuess() {
        return variation_.getInitialGuess();
    }

    @Override
    public double getFitnessRange() {
        return variation_.getFitnessRange();
    }

    /**
     * This finds the solution for the above optimization problem.
     */
    public static void main(String[] args) {
        TravelingSalesmanVariation v = TravelingSalesmanVariation.SIMPLE;
        OptimizeeTestProblem problem = new TravelingSalesmanTestProblem(v);
        Optimizer optimizer =
                new Optimizer(problem, FileUtil.PROJECT_HOME + "performance/test_optimizer/tsp_optimization.txt");

        ParameterArray initialGuess = problem.getInitialGuess();

        ParameterArray solution =
                optimizer.doOptimization(OptimizationStrategyType.SIMULATED_ANNEALING, initialGuess, v.getFitnessRange());

        System.out.println( "\n************************************************************************" );
        System.out.println( "The solution to the 7-11 Test Problem is :\n"+solution );
        System.out.println( "Which evaluates to: " + problem.evaluateFitness(solution));
        System.out.println( "We expected to get exactly 711000000:  p1-4 = {316, 125, 120, 150} " );
    }
}
