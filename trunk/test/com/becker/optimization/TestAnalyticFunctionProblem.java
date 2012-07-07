/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization;

import com.becker.common.math.MathUtil;
import com.becker.optimization.optimizees.AnalyticFunctionTestProblem;
import com.becker.optimization.optimizees.AnalyticVariation;
import com.becker.optimization.optimizees.OptimizeeTestProblem;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAnalyticFunctionProblem extends OptimizerTestCase {



    @Override
    protected void doTest(OptimizationStrategyType optimizationType) {

        for (AnalyticVariation variation : AnalyticVariation.values()) {

            MathUtil.RANDOM.setSeed(0);
            OptimizeeTestProblem problem = new AnalyticFunctionTestProblem(variation);
            String logFile =  LOG_FILE_HOME + "analytic_" + variation + "_optimization.txt";

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
        return new TestSuite(TestAnalyticFunctionProblem.class);
    }
}
