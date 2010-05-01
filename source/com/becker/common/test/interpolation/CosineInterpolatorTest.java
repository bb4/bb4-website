package com.becker.common.test.interpolation;

import com.becker.common.math.interplolation.CosineInterpolator;
import com.becker.common.math.interplolation.Interpolator;
import com.becker.common.math.interplolation.LinearInterpolator;

/**
 * @author Barry Becker
 */
public class CosineInterpolatorTest extends InterpolatorTstBase {


    @Override
    protected Interpolator createInterpolator(double[] func) {
          return new CosineInterpolator(func);
    }

    @Override
    protected double getExpectedSimpleInterpolation0_1() {
        return 0.09549150281252627;
    }

    @Override
    protected  double getExpectedSimpleInterpolation0_9() {
        return 1.9045084971874737;
    }


    @Override
    protected double getExpectedTypicalInterpolation0_1() {
        return 1.2061073738537633;
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
        return 3.7938926261462367;
    }


    @Override
    protected double getExpectedOnePointInterpolation() {
        return 1.0;
    }


    @Override
    protected double getExpectedInterpolation2Points0_1() {
        return 0.024471741852423234;
    }
}