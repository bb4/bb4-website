package com.becker.optimization;

import com.becker.common.Util;

/**
 * This is a simple search space to test the optimization package.
 * The function we will try to maximize is
 *   z = 1 - (1 - p1)^2 + (2 - y)^2
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
 * @see SevenElevenTestProblem for somewhat harder example.
 *
 * @author Barry Becker
 */
public class PolynomialTestProblem implements Optimizee
{
    // we evaluate directly not by comparing with a different trial.
    public boolean evaluateByComparison() {
        return false;
    }

    // not used
    public double compareFitness(ParameterArray a, ParameterArray b) {
        return 0.0;
    }

    /**
     * @param a the position on the parabolic surface given the specified values of p1 and p2
     * @return
     */
    public double evaluateFitness(ParameterArray a) {
        return (1.0 - Math.pow(1.0-a.get(0).value, 2)
                    - Math.pow(2.0-a.get(1).value, 2)
                );
    }


    // define the initialGuess in some bounded region of the 2-dimensional search space.
    private static final double[] vals    = {11.81,   7.93};   // initialGuess
    private static final double[] minVals = {-30.0, -20.0};
    private static final double[] maxVals = {30.0,   20.0};
    private static final String[] names   = {"p1",   "p2"};

    /**
     * This finds the solution for the above optimization problem.
     */
    public static void main(String[] args)
    {
        Optimizee polynomialProblem = new PolynomialTestProblem();
        Optimizer optimizer =
                new Optimizer(polynomialProblem, Util.PROJECT_DIR + "performance/test_optimizer/poly_optimization.txt");

        ParameterArray initialGuess = new ParameterArray(vals, minVals, maxVals, names);

        ParameterArray solution = optimizer.doOptimization(OptimizationType.GENETIC_SEARCH, initialGuess, 200.0);

        System.out.println( "\n************************************************************************" );
        System.out.println( "The solution to the Polynomial Test Problem is :\n"+solution );
        System.out.println( "Which evaluates to: "+ polynomialProblem.evaluateFitness(solution));
        System.out.println( "We expected to get exactly p1 = 1.0 and p2 = 2.0. " );

    }
}
