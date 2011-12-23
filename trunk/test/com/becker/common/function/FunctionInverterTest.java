// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.common.function;

import com.becker.common.math.MathUtil;
import com.becker.common.math.Range;
import com.becker.common.math.function.ErrorFunction;
import com.becker.common.math.function.FunctionInverter;
import com.becker.common.math.function.InvertibleFunction;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author Barry Becker
 */
public class FunctionInverterTest extends TestCase {

    /** instance under test. */
    private FunctionInverter inverter;


    public void testInvertTrivialFunction() {
        
        double[] func = new double[] {0, 1.0};
        inverter = new FunctionInverter(func);
        double[] inverse = inverter.createInverseFunction(new Range(0, 1.0));

        System.out.println("inverse="+ Arrays.toString(inverse));
        assertFunctionsEqual(new double[] {0.0, 1.0}, inverse);
    }

    public void testInvertSimple3Function() {

        double[] func = new double[] {0, 0.1, 1.0};
        inverter = new FunctionInverter(func);
        double[] inverse = inverter.createInverseFunction(new Range(0, 1.0));
        //System.out.println("inverse="+ Arrays.toString(inverse));

        assertFunctionsEqual(new double[] {0.0, 0.72222, 1.0}, inverse);
    }

    public void testInvertSimple10Function() {

            double[] func = new double[] {0, 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.1, 0.2, 0.3, 1.0};
            inverter = new FunctionInverter(func);
            double[] inverse = inverter.createInverseFunction(new Range(0, 1.0));
            //System.out.println("inverse="+ Arrays.toString(inverse));

            assertFunctionsEqual(
               // new double[] {0.0, 0.7, 0.8, 0.9, 0.9142, 0.9285, 0.9428, 0.95714, 0.971428, 0.9857, 1.0},
                new double[]  {0.0, 0.7, 0.8, 0.9, 0.94, 0.95, 0.96, 0.97, 0.98, 0.99, 1.0} , // really want this
                inverse);
        }

    private void assertFunctionsEqual(double[] expFunc, double[] func) {
        assertEquals("Unequal length.", expFunc.length, func.length);
        boolean matched = true;
        for (int i = 0; i < expFunc.length; i++) {
            if (Math.abs(expFunc[i] - func[i]) > MathUtil.EPS_BIG)  {
                System.out.println("Mismatch at entry "+ i+". Expectd " +  expFunc[i] + " but got "+ func[i]);
                matched = false;
            }
        }
        assertTrue("Expected " + Arrays.toString(expFunc) +" but got " + Arrays.toString(func), matched);
    }

}