/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.optimizees;

import com.becker.common.util.FileUtil;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.viewer.OptimizerEvalFrame;
import com.becker.optimization.strategy.OptimizationStrategyType;

import javax.vecmath.Point2d;

/**
 * This is a simple search space to test the optimization package.
 * The function we will try to maximize is one of the AnalyticFunction variants.
 * Shows the solution visually
 *
 * @author Barry Becker
 */
public class GraphAnalyticFunctionSolution extends AnalyticFunctionTestProblem {

    /** Constructor */
    public GraphAnalyticFunctionSolution(AnalyticVariation v) {
        super(v);
    }

    /**
     * This finds the solution for the above optimization problem.
     * Shows the path to the solution graphically.
     */
    public static void main(String[] args) {
        AnalyticVariation v = AnalyticVariation.PARABOLA;
        OptimizeeTestProblem testProblem = new GraphAnalyticFunctionSolution(v);

        Optimizer optimizer =
                new Optimizer(testProblem, FileUtil.PROJECT_HOME + "performance/test_optimizer/poly_optimization.txt");

        Point2d solutionPosition = new Point2d(AnalyticFunctionConsts.P1, AnalyticFunctionConsts.P2);
        OptimizationStrategyType strategy = OptimizationStrategyType.GLOBAL_SAMPLING;

        new OptimizerEvalFrame(optimizer, solutionPosition, strategy, testProblem);
    }

}
