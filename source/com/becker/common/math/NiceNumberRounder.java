// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.common.math;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Find a "nice" number approximately equal to x.
 * (from a Graphics Gems 1 article by Paul Heckbert)
 *
 * @author Barry Becker
 */
public final class NiceNumberRounder {

    /**
     * private constructor since all methods are static.
     */
    private NiceNumberRounder() {}

    /**
     * Find a "nice" number approximately equal to x.
     * Round the number down if round = 1, take ceiling if round is false.
     * Corresponds to "nicenum" in graphics gems (page 659).
     * @param x value to round
     * @param round if true, then round the number. If false, ceil it.
     * @return nice rounded number. Something like 1, 2 5   x10^j
     */
    public static double round(double x, boolean round) {

        int exp = (int) Math.floor(MathUtil.log10(x));
        // f will be between 1 and 10.
        double f = x / MathUtil.exp10(exp);
        double nf;

        if (round) {
            if (f < 1.5) {
                nf = 1.0;
            } else if (f < 3.0) {
                nf = 2.0;
            } else if (f < 7.0) {
                nf = 5.0;
            } else {
                nf = 10.0;
            }
        } else {
            if (f < 1.0) {
                nf = 1.0;
            } else if (f <= 2.0) {
                nf = 2.0;
            } else if (f < 5.0) {
                nf = 5.0;
            } else {
                nf = 10.0;
            }
        }
        return nf * MathUtil.exp10(exp);
    }

}