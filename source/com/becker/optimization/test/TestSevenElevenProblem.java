package com.becker.optimization.test;

import com.becker.optimization.*;
import com.becker.common.*;
import junit.framework.*;

/**
 * @author Barry Becker Date: Jun 28, 2006
 */
public class TestSevenElevenProblem extends OptimizerTestCase {

    // the tolerances for each for the search strategies.
    private static final double[] ERROR_TOLERANCE_PERCENT = {0.005, 0.005, 0.005, 0.005, 0.005, 0.005, 0.005};

    protected void doTest(OptimizationType optType) {

       OptimizeeTestProblem problem = new SevenElevenTestProblem();
       Optimizer optimizer =
               new Optimizer(problem, Util.PROJECT_DIR + "performance/test_optimizer/sevenEleven_optimization.txt");

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
