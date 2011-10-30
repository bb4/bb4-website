/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.optimizees;

import com.becker.optimization.parameter.DoubleParameter;
import com.becker.optimization.parameter.NumericParameterArray;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Constants related to the analytics functions
 *
 * @author Barry Becker
 */
public class AnalyticFunctionConsts {

    public static final double  P1 = 1.0;
    public static final double  P2 = 2.0;

    private static final Parameter[] EXACT_SOLUTION_PARAMS =
            {new DoubleParameter(P1, 0.0, 3.0, "p1"),
             new DoubleParameter(P2, 0.0, 3.0, "p2")};

    public static final ParameterArray EXACT_SOLUTION = new NumericParameterArray(EXACT_SOLUTION_PARAMS);
    static {
        EXACT_SOLUTION.setFitness(1001.0);
    }

    // define the initialGuess in some bounded region of the 2-dimensional search space.
    private static final double[] vals    = {  6.81,  7.93};   // initialGuess
    private static final double[] minVals = {-20.0, -20.0};
    private static final double[] maxVals = { 20.0,  20.0};
    private static final String[] names   = {"p1",   "p2"};
    public static final ParameterArray INITIAL_GUESS = new NumericParameterArray(vals, minVals, maxVals, names);


    public static final double BASE_TOLERANCE = 0.0002;
    public static final double RELAXED_TOL = 0.001;
    /** Really relax this one because we do not expect it to ever get that close */
    public static final double GLOB_SAMP_TOL = 0.04;

    private AnalyticFunctionConsts() {}
}
