package com.becker.optimization;

import com.becker.common.util.FileUtil;
import com.becker.optimization.parameter.DoubleParameter;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;

import java.awt.geom.Point2D;

/**
 * This is a simple search space to test the optimization package.
 * The function we will try to maximize is
 *   z = 1 - (1 - p1)^2 - (2 - y)^2
 * Normally we have no idea what the real function is that we are trying to optimize.
 * Nor is a real life function likely to be as well behaved as this one is.
 * This function is very smooth (actually infinitely differentiable) - which is a
 * feature that makes hill-climbing algorithms work very well on.
 * But for this simple case I intentionally use a simple polynomial function with only
 * 2 parameters so that I can solve it analytically and compare it to the optimization results.
 * For this function the global maximum is 1 and it occurs only when p1 = 1 and p2 = 2.
 * There are no other local maxima. The shape of the surface formed by this function
 * is an inverted parabola centered at p1 = 1 and p2 = 2.
 *
 * There are a few variations on the analytic function to choose from, but they all have the same solution.
 *
 * @see SevenElevenTestProblem for somewhat harder example.
 *
 * @author Barry Becker
 */
public class AnalyticFunctionTestProblem extends OptimizeeTestProblem
{

    private static final double  P1 = 1.0;
    private static final double  P2 = 2.0;
    private static final Parameter[] EXACT_SOLUTION_PARAMS =
            {new DoubleParameter(P1, 0.0, 3.0, "p1"),
             new DoubleParameter(P2, 0.0, 3.0, "p2")};

    private static final ParameterArray EXACT_SOLUTION = new ParameterArray(EXACT_SOLUTION_PARAMS);


    // define the initialGuess in some bounded region of the 2-dimensional search space.
    private static final double[] vals    = {  6.81,  7.93};   // initialGuess
    private static final double[] minVals = {-20.0, -20.0};
    private static final double[] maxVals = { 20.0,  20.0};
    private static final String[] names   = {"p1",   "p2"};
    private static final ParameterArray INITIAL_GUESS = new ParameterArray(vals, minVals, maxVals, names);

    private static final double FITNESS_RANGE = 1000.0;

    // different types of 3d planar functions that all have the same maximum.
    // @@ have arrays of expected errorr
    public static enum Variation { PARABOLA, SINUSOIDAL, ABS_SINUSOIDAL, STEPPED };

    private Variation variation_ = Variation.PARABOLA;  // default


    public AnalyticFunctionTestProblem(Variation v) {
        variation_ = v;
    }

    // we evaluate directly not by comparing with a different trial.
    public boolean evaluateByComparison() {
        return false;
    }

    // not used
    public double compareFitness(ParameterArray a, ParameterArray b) {
        return 0.0;
    }

    public double getOptimalFitness() {
        return 0;
    }

    /**
     * @param a the position on the parabolic surface given the specified values of p1 and p2
     * @return fitness value
     */
    public double evaluateFitness(ParameterArray a) {
        switch (variation_) {
            case PARABOLA : return fitness1(a);
            case SINUSOIDAL: return fitness2(a);
            case ABS_SINUSOIDAL: return fitness3(a);
            case STEPPED: return fitness4(a);
        }
        return fitness1(a);    // never called
     }

    /**
     * Smoot inverted parabola.
     * @param a the position on the parabolic surface given the specified values of p1 and p2
     * @return fitness value
     */
    public double fitness1(ParameterArray a) {
        return  1000 + ((1.0 - Math.pow(P1 - a.get(0).getValue(), 2)
                             - Math.pow(P2 - a.get(1).getValue(), 2)));

    }

    /**
     * This version introduces a bit of sinusoidal noise.
     * @param a the position on the parabolic surface given the specified values of p1 and p2
     * @return fitness value
     */
    public double fitness2(ParameterArray a) {
        return  fitness1(a) + Math.cos(a.get(0).getValue() * a.get(1).getValue() - 2.0);

    }

    /**
     * This version introduces a bit of absolute value sinusoidal noise.
     * This means it will not be second order differentiable, making this type of search harder.
     * @param a the position on the parabolic surface given the specified values of p1 and p2
     * @return fitness value
     */
    public double fitness3(ParameterArray a) {
        return  fitness1(a) + Math.abs(Math.cos(a.get(0).getValue() * a.get(1).getValue() - 2.0));

    }

    /**
     *  this version introduces a bit of step function noise.
     * @param a the position on the parabolic surface given the specified values of p1 and p2
     * @return fitness value
     */
    public double fitness4(ParameterArray a) {
        return fitness1(a) - 0.2 * Math.round( Math.abs((P1 - a.get(0).getValue())) * Math.abs((P2 - a.get(1).getValue())));
    }


    private static void doTest(OptimizationStrategyType optType, ParameterArray initialGuess, Optimizer optimizer, Variation v) {

        ParameterArray solution = optimizer.doOptimization(optType, initialGuess, FITNESS_RANGE);

        System.out.println( "\n************************************************************************" );
        System.out.println( "The solution to the (" + v + ") Polynomial Test Problem using "
                            + optType + " is :\n" + solution);
        System.out.println( "Which evaluates to: "+ optimizer.getOptimizee().evaluateFitness(solution));
        System.out.println( "We expected to get exactly p1 = "+ P1 + " and p2 = " + P2 );
    }

    public ParameterArray getInitialGuess() {
        return INITIAL_GUESS;
    }

    public ParameterArray getExactSolution() {
        return EXACT_SOLUTION;
    }

    public double getFitnessRange() {
        return FITNESS_RANGE;
    }

    /**
     * This finds the solution for the above optimization problem.
     */
    public static void main(String[] args)
    {
        Variation v = Variation.ABS_SINUSOIDAL;
        OptimizeeTestProblem testProblem = new AnalyticFunctionTestProblem(v);
        Optimizer optimizer =
                new Optimizer(testProblem, FileUtil.PROJECT_HOME + "performance/test_optimizer/poly_optimization.txt");


        OptimizerEvalFrame oef = new OptimizerEvalFrame(optimizer, new Point2D.Double(1.0, 2.0));
        oef.setVisible(true);

        ParameterArray initialGuess = testProblem.getInitialGuess();

        doTest(OptimizationStrategyType.HILL_CLIMBING, initialGuess, optimizer, v);
        //for (OptimizationStrategyType type : OptimizationStrategyType.values()) {
        //    doTest(type, initialGuess, optimizer);
        //}

    }


}
