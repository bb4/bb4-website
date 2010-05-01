package com.becker.common.test.interpolation;

import com.becker.common.math.interplolation.Interpolator;
import com.becker.common.math.interplolation.LinearInterpolator;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class LinearInterpolatorTest extends InterpolatorTstBase {


    @Override
    protected Interpolator createInterpolator(double[] func) {
          return new LinearInterpolator(func);
    }

    @Override
    protected double getExpectedSimpleInterpolation0_1() {
        return 0.2;
    }

    @Override
    protected  double getExpectedSimpleInterpolation0_9() {
        return 1.8;
    }


    @Override
    protected double getExpectedTypicalInterpolation0_1() {
        return 1.3; 
    }

    @Override
    protected double getExpectedTypicalInterpolation0_4() {
        return 2.8;
    }

    @Override
    protected double getExpectedTypicalInterpolation0_5() {
        return 3.6;
    }

    @Override
    protected double getExpectedTypicalInterpolation0_9() {
        return 3.7;
    }


    @Override
    protected double getExpectedOnePointInterpolation() {
        return 1.0;
    }


    @Override
    protected double getExpectedInterpolation2Points0_1() {
        return 0.1;
    }
}