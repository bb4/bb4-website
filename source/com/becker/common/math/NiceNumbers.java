package com.becker.common.math;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculate nice round numbered cutpoints for a given range.
 * You can choose loose or tight labeling.
 * (derived from a Graphics Gems 1 article by Paul Heckbert)
 *
 * @author Barry Becker
 */
public final class NiceNumbers {

    /** We will never show a range less than this */
    private static final double MIN_RANGE = 1.0E-10;

    /** Used in calculating log base 10. */
    private static final double LOG10SCALE = 1.0 / Math.log(10.0);

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.##");


    /**
     * private constructor since all methods are static.
     */
    private NiceNumbers()
    {}

    /**
     * @param min lower end of tickmark range.
     * @param max upper end of tickmark range.
     * @param maxTicks  upper limit on number of cutponts to return.
     * @return an array of no more than maxTikcks nice cutpoints for the given interval.
     */
    public static double[] getCutPoints(double min, double max, int maxTicks) {
        return getCutPoints(min, max, maxTicks, false);
    }

    private static final double LABEL_PROXIMITY_THRESH = 0.2;

    /** labels for the found cutpoints. */
    public static String[] getCutPointLabels(double min, double max, int maxTicks, boolean useTightLabeling) {
        double [] cutPoints  = getCutPoints(min, max, maxTicks, useTightLabeling);
        DECIMAL_FORMAT.setMaximumFractionDigits(getNumberOfFractionDigits(min, max, maxTicks));

        int len = cutPoints.length;
        String[] labels = new String[len];
        for (int i=0; i<len; i++) {
            labels[i] = DECIMAL_FORMAT.format(cutPoints[i]);
        }
        return labels;
    }

    /**
     * Finds loos or tight labeling for a range of data (depending on the value of useTightLabeling)
     * See Graphics Gems Vol ! p61.
     * @param minimum lower end of tickmark range.
     * @param maximum upper end of tickmark range.
     * @param maxTicks  upper limit on number of cutponts to return.
     * @param useTightLabeling if false then loose labeling is used.
     */
    public static double[] getCutPoints(double minimum, double maximum, int maxTicks, boolean useTightLabeling) {

        checkArgs(minimum, maximum);
        if (MIN_RANGE >= maximum - minimum) {
            // avoid having only 1 label
            maximum = minimum + MIN_RANGE;
        }

        List<Double> positions = new ArrayList<Double>(10);

        if (MIN_RANGE > Math.abs(maximum - minimum)) {
            positions.add(minimum);
        } else {
            double range = roundNumber(maximum - minimum, false);
            double d = roundNumber(range / (maxTicks - 1), true);
            double min = Math.floor(minimum / d) * d;
            double max = Math.ceil(maximum / d) * d;

            if (useTightLabeling) {
                positions.add(checkSmallNumber(minimum));
                // this logic is to prevent the min or max label from overwriting one of the nice cut-points.
                double initialInc = d;
                double pct = (min + d - minimum) / d;
                if (LABEL_PROXIMITY_THRESH > pct) {
                    initialInc = 2 * d;
                }
                double finalInc = 0.5 * d;
                pct = (maximum - (max - d)) / d;
                if (LABEL_PROXIMITY_THRESH > pct) {
                    finalInc = 1.5 * d;
                }
                double stop = max - finalInc;
                for (double x = min + initialInc; x < stop; x += d) {
                    double val = checkSmallNumber(x);
                    positions.add(val);
                }
                positions.add(checkSmallNumber(maximum));
            } else {
                double stop = max + 0.5 * d;
                for (double x = min; x < stop; x += d) {
                    positions.add(checkSmallNumber(x));
                }
            }
        }

        double[] result = new double[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            result[i] = positions.get(i);
        }

        return result;
    }


    /**
     *
     * @param min value.
     * @param max value.
     * @param maxTicks no more than this many cutpoints.
     * @return Recommended number of fractional digits to display the cutpoints eg. 0, 1, 2, etc.
     */
    public static int getNumberOfFractionDigits(double min, double max, int maxTicks) {
        checkArgs(min, max);
        double max1 = max;
        if (Math.abs(max - min) <= MIN_RANGE) {
            max1 = min + MIN_RANGE;
        }

        double range = roundNumber(max1 - min, false);
        double d = roundNumber(range / (maxTicks - 1), true);
        return (int) Math.max(-Math.floor(log10(d)), 0);
    }

    /**
     * Find a "nice" number approximately equal to x.
     * Round the number if round = 1, take ceiling if round = 0.
     * corresponds to nicenum in graphics gems (page 659).
     * @param x
     * @param round
     * @return
     */
    private static double roundNumber(double x, boolean round) {
        double exp = Math.floor(log10(x));
        // f will be between 1 and 10.
        double f = x / exp10(exp);
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
        return nf * exp10(exp);
    }

    /**
     * Verify tha tthe min and max are valid.
     * @param min  range value.
     * @param max  range value.
     */
    private static void checkArgs(double min, double max) {
        if (Double.isNaN(min) || Double.isInfinite(min)) {
            throw new IllegalArgumentException("min is not a number");
        }
        if (Double.isNaN(max) || Double.isInfinite(max)) {
            throw new IllegalArgumentException("max is not a number");
        }
    }

    private static double checkSmallNumber(double value) {
        if (MIN_RANGE > Math.abs(value)) {
            return 0;
        }

        return value;
    }


    private static double log10(double val) {
        return Math.log(val) * LOG10SCALE;
    }

    private static double exp10(double val) {
        return Math.exp(val / LOG10SCALE);
    }
}