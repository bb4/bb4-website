/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.optimizees;

import com.becker.common.util.FileUtil;
import com.becker.optimization.Optimizer;
import com.becker.optimization.ui.OptimizerEvalFrame;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;

import java.awt.geom.Point2D;

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


    private static void doTest(OptimizationStrategyType optType, ParameterArray initialGuess, Optimizer optimizer,
                               AnalyticVariation v, double fitnessRange) {

        ParameterArray solution = optimizer.doOptimization(optType, initialGuess, fitnessRange);

        System.out.println( "\n************************************************************************" );
        System.out.println( "The solution to the (" + v + ") Polynomial Test Problem using "
                            + optType + " is :\n" + solution);
        System.out.println( "Which evaluates to: "+ optimizer.getOptimizee().evaluateFitness(solution));
        System.out.println( "We expected to get exactly p1 = "+ AnalyticFunctionConsts.P1 + " and p2 = " + AnalyticFunctionConsts.P2 );
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

        OptimizerEvalFrame oef = new OptimizerEvalFrame(optimizer, new Point2D.Double(1.0, 2.0));
        oef.setVisible(true);

        ParameterArray initialGuess = testProblem.getInitialGuess();

        //doTest(OptimizationStrategyType.GENETIC_SEARCH, initialGuess, optimizer, v, testProblem.getFitnessRange());
        for (OptimizationStrategyType type : OptimizationStrategyType.values()) {
             doTest(type, initialGuess, optimizer, v, testProblem.getFitnessRange());
             oef.repaint();
        }

    }

}
