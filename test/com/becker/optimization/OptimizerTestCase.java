package com.becker.optimization;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Barry Becker Date: Jun 29, 2006
 */
public abstract class OptimizerTestCase extends TestCase {


    public void testGlobalSampling() {

        doTest(OptimizationStrategyType.GLOBAL_SAMPLING);
    }

    public void testHillClimbing() {

        doTest(OptimizationStrategyType.HILL_CLIMBING);
    }

    public void testGlobalHillClimbing() {

        doTest(OptimizationStrategyType.GLOBAL_HILL_CLIMBING);
    }

    public void testSimulatedAnnealing() {

        doTest(OptimizationStrategyType.SIMULATED_ANNEALING);
    }

    public void testGeneticSearch() {

        doTest(OptimizationStrategyType.GENETIC_SEARCH);
    }

    public void testConcurrentGeneticSearch() {

        doTest(OptimizationStrategyType.CONCURRENT_GENETIC_SEARCH);
    }


    /**
     * run test for given optimization type
     * @param optType the optimization type to use.
     */
    protected abstract void doTest(OptimizationStrategyType optType);

    /**
     * Give an error if not withing errorThresh of the exact solution.
     */
    protected static void verifyTest(OptimizationStrategyType optType, OptimizeeTestProblem problem, ParameterArray initialGuess,
                                   Optimizer optimizer, double fitnessRange, double errorThresh, String title) {

        ParameterArray solution = optimizer.doOptimization(optType, initialGuess, fitnessRange);

        double error = problem.getError(solution);
        Assert.assertTrue(title +"\nAllowable error exceeded using "+ optType
                          + ". \nError = "+error + "\n The Test Solution was "+ solution
                          +"\n but we expected to get something very close to the exact solution:\n "
                          + problem.getExactSolution(),
                          error < errorThresh);

        System.out.println( "\n************************************************************************" );
        System.out.println( "The solution to the Problem using "+optType+" is :\n"+solution );
        System.out.println( "Which evaluates to: "+ optimizer.getOptimizee().evaluateFitness(solution));
    }

}
