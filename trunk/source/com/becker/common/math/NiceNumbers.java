package com.becker.common.math;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculate nice round numbered intervals for a range.
 * You can choose loose or tight labeling.
 * (derived from a Graphics Gems 1 article by Paul Heckbert)
 *
 * @author Barry Becker
 */
public final class NiceNumbers {

    /** Never show a range less than this */
    private static final double MIN_RANGE = 1.0E-10;

    /** Default way to show the numbers as labels */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.##");

    /**
     * Don't allow the label to get closer to each other than this.
     * This is to prevent the min or max label from overwriting one of the nice cut-points.
     */
    private static final double LABEL_PROXIMITY_THRESH = 0.2;

    /**
     * private constructor since all methods are static.
     */
    private NiceNumbers() {}

    /**
     * labels for the found cutpoints.
     * @param range tickmark range.
     * @return cut point labels
     */
    public static String[] getCutPointLabels(Range range, int maxTicks, boolean useTightLabeling) {

        double [] cutPoints = getCutPoints(range, maxTicks, useTightLabeling);
        DECIMAL_FORMAT.setMaximumFractionDigits(getNumberOfFractionDigits(range, maxTicks));

        int len = cutPoints.length;
        String[] labels = new String[len];
        for (int i=0; i<len; i++) {
            labels[i] = DECIMAL_FORMAT.format(cutPoints[i]);
        }
        return labels;
    }

    /**
     * Get the cut point values.
     * @param range tickmark range.
     * @param maxTicks  upper limit on number of cutponts to return.
     * @return an array of no more than maxTicks nice cutpoints for the given interval.
     */
    public static double[] getCutPoints(Range range, int maxTicks) {
        return getCutPoints(range, maxTicks, false);
    }

    /**
     * Finds loos or tight labeling for a range of data (depending on the value of useTightLabeling)
     * See Graphics Gems Vol ! p61.
     * @param range tickmark range.
     * @param maxTicks  upper limit on number of cutpoints to return.
     * @param useTightLabeling if false then loose labeling is used.
     * @return the cutpoints
     */
    public static double[] getCutPoints(Range range, int maxTicks, boolean useTightLabeling) {

        checkArgs(range);
        Range finalRange = new Range(range);
        if (MIN_RANGE >= range.getExtent()) {
            // avoid having only 1 label
            finalRange.add(range.getMin() + MIN_RANGE);
        }

        List<Double> positions = new ArrayList<Double>(10);

        if (MIN_RANGE > finalRange.getExtent()) {
            positions.add(finalRange.getMin());
        } else {
            double extent = NiceNumberRounder.round(finalRange.getExtent(), false);
            double d = NiceNumberRounder.round(extent / (maxTicks - 1), true);
            double min = Math.floor(finalRange.getMin() / d) * d;
            double max = Math.ceil(finalRange.getMax() / d) * d;

            if (useTightLabeling) {
                positions.add(checkSmallNumber(finalRange.getMin()));

                double initialInc = d;
                double pct = (min + d - finalRange.getMin()) / d;
                if (LABEL_PROXIMITY_THRESH > pct) {
                    initialInc = 2 * d;
                }
                double finalInc = 0.5 * d;
                pct = (finalRange.getMax() - (max - d)) / d;
                if (LABEL_PROXIMITY_THRESH > pct) {
                    finalInc = 1.5 * d;
                }
                double stop = max - finalInc;
                for (double x = min + initialInc; x < stop; x += d) {
                    double val = checkSmallNumber(x);
                    positions.add(val);
                }
                positions.add(checkSmallNumber(finalRange.getMax()));
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
     * Find the number of fractional digits to show in the nice numbers.
     * @param range range to check.
     * @param maxTicks no more than this many cutpoints.
     * @return Recommended number of fractional digits to display.
     *     The cutpoints eg. 0, 1, 2, etc.
     */
    public static int getNumberOfFractionDigits(Range range, int maxTicks) {
        checkArgs(range);
        double max1 = range.getMax();
        if (range.getExtent() <= MIN_RANGE) {
            max1 = range.getMin() + MIN_RANGE;
        }

        double extent = NiceNumberRounder.round(max1 - range.getMin(), false);
        double d = NiceNumberRounder.round(extent / (maxTicks - 1), true);

        return (int) Math.max( -Math.floor(MathUtil.log10(d)), 0);
    }

    /**
     * Verify that the min and max are valid.
     * @param range range to check for NaN values.
     */
    private static void checkArgs(Range range) {
        if (Double.isNaN(range.getMin()) || Double.isInfinite(range.getMin())) {
            throw new IllegalArgumentException("min is not a number");
        }
        if (Double.isNaN(range.getMax()) || Double.isInfinite(range.getMax())) {
            throw new IllegalArgumentException("max is not a number");
        }
    }

    private static double checkSmallNumber(double value) {
        if (MIN_RANGE > Math.abs(value)) {
            return 0;
        }

        return value;
    }
}