package com.becker.common.test.interpolation;

import com.becker.common.math.interplolation.HermiteInterpolator;
import com.becker.common.math.interplolation.Interpolator;
import com.becker.common.math.interplolation.LinearInterpolator;

/**
 * @author Barry Becker
 */
public class HermiteInterpolatorTest extends InterpolatorTstBase {

    @Override
    protected Interpolator createInterpolator(double[] func) {
          return new HermiteInterpolator(func);
    }

    @Override
    protected double getExpectedSimpleInterpolation0_1() {
        return 0.136;
    }

    @Override
    protected  double getExpectedSimpleInterpolation0_9() {
        return 1.864;
    }


    @Override
    protected double getExpectedTypicalInterpolation0_1() {
        return 1.2265;
    }

    @Override
    protected double getExpectedTypicalInterpolation0_4() {
        return 2.296;
    }

    @Override
    protected double getExpectedTypicalInterpolation0_5() {
        return 2.5;
    }

    @Override
    protected double getExpectedTypicalInterpolation0_9() {
        return 3.7735;
    }


    @Override
    protected double getExpectedOnePointInterpolation() {
        return 1.0;
    }


    @Override
    protected double getExpectedInterpolation2Points0_1() {
        return 0.064;
    }
}