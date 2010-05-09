package com.becker.optimization;

import com.becker.common.util.FileUtil;
import com.becker.optimization.parameter.DoubleParameter;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;

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
 * @see AnalyticFunctionTestProblem for an easier optimization example.
 *
 * @author Barry Becker
 */
public class SevenElevenTestProblem extends OptimizeeTestProblem
{
    // define the initialGuess in some bounded region of the 2-dimensional search space.
    private static final double[] vals    = {100,  200, 200, 200};   // initialGuess
    private static final double[] minVals = {   1,   1,   1,   1};
    private static final double[] maxVals = { 708, 708, 708, 708};
    private static final String[] names   = {"p1", "p2", "p3", "p4"};

    private static final double  P1 = 316.0;
    private static final double  P2 = 125.0;
    private static final double  P3 = 120.0;
    private static final double  P4 = 150.0;
    private static final Parameter[] EXACT_SOLUTION_PARAMS =
            {new DoubleParameter(P1, 0.0, 1000.0, "p1"),
             new DoubleParameter(P2, 0.0, 1000.0, "p2"),
             new DoubleParameter(P3, 0.0, 1000.0, "p3"),
             new DoubleParameter(P4, 0.0, 1000.0, "p4")};

    private static final ParameterArray INITIAL_GUESS = new ParameterArray(vals, minVals, maxVals, names);

    private static final ParameterArray EXACT_SOLUTION = new ParameterArray(EXACT_SOLUTION_PARAMS);

    // @@ exp errors.
    private static final double FITNESS_RANGE = 5000000.0;

    //public static enum Variation { STANDARD };
    //private Variation variation_ = Variation.STANDARD;


    public SevenElevenTestProblem() {
    }

    /**
     * we evaluate directly not by comparing with a different trial.
     */
    public boolean evaluateByComparison() {
        return false;
    }

    // not used
    public double compareFitness(ParameterArray a, ParameterArray b) {
        return 0.0;
    }

    /**
     * @param a the position in the search space given values of p1, p2, p4, p4.
     * @return fitness value
     */
    public double evaluateFitness(ParameterArray a) {

        double sum = a.get(0).getValue() + a.get(1).getValue() + a.get(2).getValue() + a.get(3).getValue();
        double prod = a.get(0).getValue() * a.get(1).getValue() * a.get(2).getValue() * a.get(3).getValue();

        return  -Math.abs(Math.pow(711 - sum, 3)) - Math.abs(711000000 - prod);
    }


    public ParameterArray getExactSolution() {
        return EXACT_SOLUTION;
    }

    public ParameterArray getInitialGuess() {
        return INITIAL_GUESS;
    }

    public double getFitnessRange() {
        return FITNESS_RANGE;
    }
    /**
     * This finds the solution for the above optimization problem.
     */
    public static void main(String[] args)
    {
        OptimizeeTestProblem problem = new SevenElevenTestProblem();
        Optimizer optimizer =
                new Optimizer(problem, FileUtil.PROJECT_DIR + "performance/test_optimizer/seven11_optimization.txt");

        ParameterArray initialGuess = problem.getInitialGuess();

        ParameterArray solution = optimizer.doOptimization(OptimizationStrategyType.GLOBAL_SAMPLING, initialGuess, FITNESS_RANGE);

        System.out.println( "\n************************************************************************" );
        System.out.println( "The solution to the 7-11 Test Problem is :\n"+solution );
        System.out.println( "Which evaluates to: "+ problem.evaluateFitness(solution));
        System.out.println( "We expected to get exactly 711000000:  p1-4 = {316, 125, 120, 150} " );

    }
}
