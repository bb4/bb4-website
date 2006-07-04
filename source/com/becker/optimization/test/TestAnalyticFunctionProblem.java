package com.becker.optimization.test;

import com.becker.common.*;
import com.becker.optimization.*;
import junit.framework.*;

/**
 * @author Barry Becker Date: Jun 28, 2006
 */
public class TestAnalyticFunctionProblem extends OptimizerTestCase {


    private static final double BASE_TOLERANCE = 0.0001;
    private static final double RELAXED_TOL = 0.001;
    private static final double GLOB_SAMP_TOL = 0.03;

    /**
     * Error tolerance for each search strategy and variation of the problem.
     */
    private static final double[][] ERROR_TOLERANCE_PERCENT = {
        // GLOB_SAMP    G_HILL_CLIMB    HIL_CLIMB       SIM_ANN      TABU         GENETIC    STATE_S
        {GLOB_SAMP_TOL, BASE_TOLERANCE, BASE_TOLERANCE, 0.04,       RELAXED_TOL,  0.042,      BASE_TOLERANCE},  // PARABOLA,
        {GLOB_SAMP_TOL, BASE_TOLERANCE, BASE_TOLERANCE, 0.04,       RELAXED_TOL,  0.042,     BASE_TOLERANCE},   // SINUSOIDAL,
        {GLOB_SAMP_TOL, 0.0128,         BASE_TOLERANCE, 0.03,       RELAXED_TOL,  0.03,       BASE_TOLERANCE}, // ABS_SINUSOIDAL
        {GLOB_SAMP_TOL, BASE_TOLERANCE, BASE_TOLERANCE, 0.03,       RELAXED_TOL,  0.042,    BASE_TOLERANCE},  // STEPPED
    };


    protected void doTest(OptimizationType optType) {

        for (AnalyticFunctionTestProblem.Variation v : AnalyticFunctionTestProblem.Variation.values()) {

            OptimizeeTestProblem problem = new AnalyticFunctionTestProblem(v);
            String logFile = Util.PROJECT_DIR + "performance/test_optimizer/analytic_" + v + "_optimization.txt";

            Optimizer optimizer =  new Optimizer(problem, logFile);

            ParameterArray initialGuess = problem.getInitialGuess();
            verifyTest(optType, problem, initialGuess, optimizer, problem.getFitnessRange(),
                       ERROR_TOLERANCE_PERCENT[v.ordinal()][optType.ordinal()], v.toString());
        }
    }




    /**
     * @return all the junit test caes to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestAnalyticFunctionProblem.class);
    }


}
