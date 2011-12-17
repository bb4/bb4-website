/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization;

import com.becker.optimization.optimizees.*;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestTravelingSalesmanProblem extends OptimizerTestCase {


    @Override
    protected void doTest(OptimizationStrategyType optimizationType) {

        for (TravelingSalesmanVariation variation : TravelingSalesmanVariation.values()) {

            OptimizeeTestProblem problem = new TravelingSalesmanTestProblem(variation);
            String logFile = LOG_FILE_HOME + "analytic_" + variation + "_optimization.txt";

            Optimizer optimizer = new Optimizer(problem, logFile);

            ParameterArray initialGuess = problem.getInitialGuess();
            verifyTest(optimizationType, problem, initialGuess, optimizer, problem.getFitnessRange(),
                    variation.getErrorTolerancePercent(optimizationType), variation.toString());
        }
    }


    /**
     * @return all the junit test cases to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestTravelingSalesmanProblem.class);
    }

}
