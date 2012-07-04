// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.optimization.optimizees;

import com.becker.optimization.parameter.NumericParameterArrayTest;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import junit.framework.TestCase;

import static com.becker.optimization.optimizees.AnalyticFunctionConsts.*;

/**
 * Verify that the maximum value of each variation is the same (1001).
 *
 * @author Barry Becker
 */
public class AnalyticVariationTest extends TestCase {

    /** instance under test */
    private AnalyticVariation variation;

    public void testVariationMaximum() {
        for (AnalyticVariation variant : AnalyticVariation.values()) {
            ParameterArray param = NumericParameterArrayTest.createParamArray(P1, P2);
            assertEquals("Unexpected maximum value for " + variant.toString(),
                    AnalyticFunctionConsts.EXACT_SOLUTION.getFitness(), variant.evaluateFitness(param));
        }
    }

}