package com.becker.optimization;

import com.becker.common.Util;

/**
 * This is a simple search space to test the optimization package.
 * The problem we will try to so;ve is :
 *   p1 + p2 + p3 + p4  = 711
 *   p1 * p2 * p3 * p4  = 711000000
 * Which corresponds to the problem of someone going into a 7-11 and buying 4 things
 * whose sum and product equal $7.11.
 * This problem can be solved analytically by finding the prime factors of 711 and
 * eliminating combinations until you are left with:
 *   316, 125, 120, 150
 * as being the only solution.
 * Our choice of evaluation function to maximize is somewhat arbitrary.
 * I chose to use:
 *  -abs(p1+p2+p3+p4-711)^3  - abs(711000000-p1*p2*p3*p4)
 * When this function evaluates to 0, we have a solution.
 *
 * @see PolynomialTestProblem for an easier optimization example.
 *
 * @author Barry Becker
 */
public class SevenElevenTestProblem implements Optimizee
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

        double sum = a.get(0).value + a.get(1).value + a.get(2).value + a.get(3).value;
        double prod = a.get(0).value * a.get(1).value * a.get(2).value * a.get(3).value;

        return  -Math.abs(Math.pow(711-sum, 3)) - Math.abs(711000000-prod);
    }


    // define the initialGuess in some bounded region of the 2-dimensional search space.
    private static final double[] vals    = {100,  200, 200, 200};   // initialGuess
    private static final double[] minVals = {   1,   1,   1,   1};
    private static final double[] maxVals = { 708, 708, 708, 708};
    private static final String[] names   = {"p1", "p2", "p3", "p4"};

    /**
     * This finds the solution for the above optimization problem.
     */
    public static void main(String[] args)
    {
        Optimizee problem = new SevenElevenTestProblem();
        Optimizer optimizer =
                new Optimizer(problem, Util.PROJECT_DIR + "performance/test_optimizer/seven11_optimization.txt");

        ParameterArray initialGuess = new ParameterArray(vals, minVals, maxVals, names);

        ParameterArray solution = optimizer.doOptimization(OptimizationType.SIMULATED_ANNEALING, initialGuess, 500);

        System.out.println( "\n************************************************************************" );
        System.out.println( "The solution to the 7-11 Test Problem is :\n"+solution );
        System.out.println( "Which evaluates to: "+ problem.evaluateFitness(solution));
        System.out.println( "We expected to get exactly 711000000:  p1-4 = {316, 125, 120, 150} " );

    }
}
