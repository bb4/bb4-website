package com.becker.optimization;

import com.becker.common.util.FileUtil;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker Date: Jun 28, 2006
 */
public class TestSevenElevenProblem extends OptimizerTestCase {

    // the tolerances for each for the search strategies.
    private static final double[] ERROR_TOLERANCE_PERCENT = {0.005, 0.005, 0.005, 0.005, 0.005, 0.005, 0.005};

    protected void doTest(OptimizationStrategyType optType) {

       OptimizeeTestProblem problem = new SevenElevenTestProblem();
       Optimizer optimizer =
               new Optimizer(problem, FileUtil.PROJECT_HOME + "performance/test_optimizer/sevenEleven_optimization.txt");

       ParameterArray initialGuess = problem.getInitialGuess();

       verifyTest(optType, problem, initialGuess, optimizer, problem.getFitnessRange(),
                  ERROR_TOLERANCE_PERCENT[optType.ordinal()], "");
   }


    /**
     * @return all the junit test caes to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestSevenElevenProblem.class);
    }


}
