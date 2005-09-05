package com.becker.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculate nicely spaced number cutpoints for an axis or legend.
 * You have an option to choose loose or tight labeling.
 *
 * @author Barry Becker    (from Graphics Gems 1V)
 */
public final class NiceCutPoints {

    private static final double SMALL_VALUE_CUTOFF = 1.0E-10;

    private NiceCutPoints() {};

    /**
     * @param min  num ticks that will be created for less than this value.
     * @param max  num ticks thatwill be created for greater than this value.
     * @param maxTicks  no more cutpoints than this number.
     */
    public static double[] cutpoints(double min, double max, int maxTicks) {
        return cutpoints(min, max, maxTicks, false);
    }

    private static final double LABEL_PROXIMITY_THRESH = 0.2;

    /**
     * @param minimum num ticks that will be created for less than this value.
     * @param maximum num ticls that will be created for greater than this value.
     * @param maxTicks
     *            no more cutpoints than this number.
     * @param useTightLabeling if false then loose labeling is used.
     */
    public static double[] cutpoints(double minimum, double maximum, int maxTicks, boolean useTightLabeling) {


        if (Double.isNaN(minimum)) {
            throw new IllegalArgumentException("NaN min");
        }
        if (Double.isNaN(maximum)) {
            throw new IllegalArgumentException("NaN max");
        }

        if (maximum - minimum <= SMALL_VALUE_CUTOFF) {
            // avoid having only 1 label
            maximum = minimum + SMALL_VALUE_CUTOFF;
        }

        List positions = new ArrayList();
        //int numfracdigits = 0;

        if (Math.abs(maximum - minimum) < SMALL_VALUE_CUTOFF) {
            positions.add(minimum);
        } else {
            double range = niceNumber(maximum - minimum, false);
            double d = niceNumber(range / (maxTicks - 1), true);
            double min = Math.floor(minimum / d) * d;
            double max = Math.ceil(maximum / d) * d;
            //numfracdigits = (int) Math.max(-Math.floor(log10(d)), 0);

            if (useTightLabeling) {
                positions.add(checkSmallNumber(minimum));
                // this logic is to avoid the min or max label overwriting one of the nice cutpoints.
                double initialInc = d;
                double pct = (min + d - minimum) / d;
                if (pct < LABEL_PROXIMITY_THRESH) {
                    initialInc = 2 * d;
                }
                double finalInc = 0.5 * d;
                pct = (maximum - (max - d)) / d;
                if (pct < LABEL_PROXIMITY_THRESH) {
                    finalInc = 1.5 * d;
                }

                for (double x = min + initialInc; x < (max - finalInc); x += d) {
                    double val = checkSmallNumber(x);
                    positions.add(val);
                }
                positions.add(checkSmallNumber(maximum));
            } else {
                for (double x = min; x < (max + 0.5 * d); x += d) {
                    positions.add(checkSmallNumber(x));
                }
            }
        }

        double[] result = new double[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            result[i] = ((Double) positions.get(i));
        }

        return result;
    }




    /**
     *
     * @param min
     * @param max
     * @param maxTicks
     * @return  Recommended number of fraction digits to display the cutpoints eg. 0, 1, 2, etc.
     */
    public static int getNumberOfFractionDigits(double min, double max, int maxTicks) {
    	if (Double.isNaN(min)) {
            throw new IllegalArgumentException("NaN min");
        }
        if (Double.isNaN(max)) {
            throw new IllegalArgumentException("NaN max");
        }

        double max1 = max;
        if (Math.abs(max - min) <= SMALL_VALUE_CUTOFF) {
            max1 = min + SMALL_VALUE_CUTOFF;
        }

        double range = niceNumber(max1 - min, false);
        double d = niceNumber(range / (maxTicks - 1), true);
        return (int) Math.max(-Math.floor(log10(d)), 0);

    }

    private static double niceNumber(double x, boolean round) {
        double exp = Math.floor(log10(x));
        double f = x / exp10(exp);
        double nf;

        if (round) {
            if (f < 1.5) {
                nf = 1.0;
            } else if (f < 3.0) {
                nf = 2.0;
            } else if (f < 4.0) {
                nf = 3.0;
            } else if (f < 7.0) {
                nf = 5.0;
            } else if (f < 8.0) {
                nf = 6.0;
            } else {
                nf = 10.0;
            }
        } else {
            if (f < 1.0) {
                nf = 1.0;
            } else if (f < 2.0) {
                nf = 2.0;
            } else if (f < 4.0) {
                nf = 4.0;
            } else if (f < 5.0) {
                nf = 5.0;
            } else if (f < 6.0) {
                nf = 6.0;
            } else {
                nf = 10.0;
            }
        }
        return nf * exp10(exp);
    }

    private static double checkSmallNumber(double value) {
        if (Math.abs(value) < SMALL_VALUE_CUTOFF) {
            return 0;
        }

        return value;
    }


    // For use in calculating log base 10. A log times this is a log base 10.
    private static final double LOG10SCALE = 1.0 / Math.log(10);

    private static double log10(double val) {
        return Math.log(val) * LOG10SCALE;
    }

    private static double exp10(double val) {
        return Math.exp(val / LOG10SCALE);
    }
}