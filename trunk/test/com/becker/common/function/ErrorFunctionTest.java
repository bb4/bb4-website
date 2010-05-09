package com.becker.common.function;

import com.becker.common.math.function.ErrorFunction;
import com.becker.common.math.function.InvertibleFunction;

/**
 * @author Barry Becker
 */
public class ErrorFunctionTest extends FunctionTstBase {

    @Override
    protected InvertibleFunction createFunction() {
        return new ErrorFunction();
    }

    @Override
    protected double getExpectedValue0_1() {
        return 0.11246296000000001;
    }

    @Override
    protected double getExpectedValue0_9() {
        return 0.7969081;
    }


    @Override
    protected double getExpectedInverseValue0_1() {
        return 0.089;
    }
    
    @Override
    protected double getExpectedInverseValue0_9() {
        return 0.7969081;
    }

}