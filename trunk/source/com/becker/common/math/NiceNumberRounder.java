// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.common.math;


/**
 * Rounds numbers in a "nice" way so that they are easy to read.
 * (from Graphics Gems 1 article by Paul Heckbert)
 *
 * @author Barry Becker
 */
final class NiceNumberRounder {

    /**
     * private constructor since all methods are static.
     */
    private NiceNumberRounder() {}

    /**
     * Find a "nice" number approximately equal to numberToRound.
     * Round the number down if round is true; round up if round is false.
     * Corresponds to "nicenum" in graphics gems (page 659).
     *
     * @param numberToRound the value to round
     * @param roundDown if true, then round the number down. If false, take the ceil of it.
     * @return nice rounded number. Something like 1, 2 5   x10^j
     */
    public static double round(double numberToRound, boolean roundDown) {

        int exp = (int) Math.floor(MathUtil.log10(numberToRound));

        // f will be between 1 and 10.
        double normalizedNumber = numberToRound / MathUtil.exp10(exp);
        double nf;

        if (roundDown) {
            nf = roundNumberDown(normalizedNumber);
        } else {
            nf = roundNumberUp(normalizedNumber);
        }
        return nf * MathUtil.exp10(exp);
    }

    private static double roundNumberUp(double f) {
        double nf;
        if (f < 1.0) {
            nf = 1.0;
        } else if (f <= 2.0) {
            nf = 2.0;
        } else if (f < 5.0) {
            nf = 5.0;
        } else {
            nf = 10.0;
        }
        return nf;
    }

    private static double roundNumberDown(double f) {
        double nf;
        if (f < 1.5) {
            nf = 1.0;
        } else if (f < 3.0) {
            nf = 2.0;
        } else if (f < 7.0) {
            nf = 5.0;
        } else {
            nf = 10.0;
        }
        return nf;
    }

}