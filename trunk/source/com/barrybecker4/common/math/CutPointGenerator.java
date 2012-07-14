package com.barrybecker4.common.math;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculates nicely rounded intervals for a specified range.
 * From an article by Paul Heckbert in Graphics Gems 1.
 *
 * @author Barry Becker
 */
public class CutPointGenerator {

    /** Prevent labels from getting closer to each other than this. */
    private static final double MIN_LABEL_SEPARATION = 0.2;

    /** Never allow a range to be less than this. */
    private static final double SMALLEST_ALLOWED_RANGE = 1.0E-10;

    /** Default way to show the numbers as labels */
    private DecimalFormat formatter;

    /** If true, show the precise min/max values at the extreme cut points (tight), else loose labels */
    private boolean useTightLabeling;


    /** Constructor */
    public CutPointGenerator() {
       this(true, new DecimalFormat("###,###.##"));
    }

    /**
     * Constructor
     * @param useTightLabeling whether or not to use tight labeling.
     * @param formatter method for formatting the label values.
     */
    public CutPointGenerator(boolean useTightLabeling, DecimalFormat formatter) {
        this.useTightLabeling = useTightLabeling;
        this.formatter = formatter;
    }

    public void setUseTightLabeling(boolean useTight) {
        this.useTightLabeling = useTight;
    }

    /**
     * Labels for the found cut points.
     * @param range tickmark range.
     * @return cut point labels
     */
    public String[] getCutPointLabels(Range range, int maxTicks) {

        double[] cutPoints = getCutPoints(range, maxTicks);
        formatter.setMaximumFractionDigits(
                getNumberOfFractionDigits(range, maxTicks));

        int length = cutPoints.length;
        String[] labels = new String[length];
        for (int i = 0; i < length; i++) {
            labels[i] = formatter.format(cutPoints[i]);
        }
        return labels;
    }

    /**
     * Retrieve the cut point values.
     * If its a really small range include both min and max to avoid having just one label.
     * @param range range to be divided into intervals.
     * @param maxNumTicks upper limit on number of cut points to return.
     * @return the cut points
     */
    public double[] getCutPoints(Range range, int maxNumTicks) {

        validateArguments(range);
        Range finalRange = new Range(range);
        if (range.getExtent() <= SMALLEST_ALLOWED_RANGE) {
            finalRange.add(range.getMin() + SMALLEST_ALLOWED_RANGE);
        }

        List<Double> positions = new ArrayList<Double>(10);

        if (finalRange.getExtent() < SMALLEST_ALLOWED_RANGE) {
            positions.add(finalRange.getMin());
        } else {
            determineCutPoints(maxNumTicks, finalRange, positions);
        }

        double[] result = new double[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            result[i] = positions.get(i);
        }

        return result;
    }

    private void determineCutPoints(int maxTicks, Range finalRange, List<Double> positions) {

        double extent = NiceNumberRounder.round(finalRange.getExtent(), false);
        double d = NiceNumberRounder.round(extent / (maxTicks - 1), true);
        Range roundedRange =
                new Range(Math.floor(finalRange.getMin() / d) * d, Math.ceil(finalRange.getMax() / d) * d);

        if (useTightLabeling) {
            positions.add(checkSmallNumber(finalRange.getMin()));

            double initialInc = d;
            double pct = (roundedRange.getMin() + d - finalRange.getMin()) / d;
            if (MIN_LABEL_SEPARATION > pct) {
                initialInc = 2 * d;
            }
            double finalInc = 0.5 * d;
            pct = (finalRange.getMax() - (roundedRange.getMax() - d)) / d;
            if (MIN_LABEL_SEPARATION > pct) {
                finalInc = 1.5 * d;
            }
            double stop = roundedRange.getMax() - finalInc;
            for (double x = roundedRange.getMin() + initialInc; x < stop; x += d) {
                double val = checkSmallNumber(x);
                positions.add(val);
            }
            positions.add(checkSmallNumber(finalRange.getMax()));
        } else {
            double stop = roundedRange.getMax() + 0.5 * d;
            for (double x = roundedRange.getMin(); x < stop; x += d) {
                positions.add(checkSmallNumber(x));
            }
        }
    }

    /**
     * Determine the number of fractional digits to show in the nice numbered cut points.
     * @param range the range to check.
     * @param maxNumTicks no more than this many cut points.
     * @return Recommended number of fractional digits to display. The cut points: eg. 0, 1, 2, etc.
     */
    int getNumberOfFractionDigits(Range range, int maxNumTicks) {
        validateArguments(range);
        double max1 = range.getMax();
        if (range.getExtent() <= SMALLEST_ALLOWED_RANGE) {
            max1 = range.getMin() + SMALLEST_ALLOWED_RANGE;
        }

        double extent = NiceNumberRounder.round(max1 - range.getMin(), false);
        double d = NiceNumberRounder.round(extent / (maxNumTicks - 1), true);

        return (int) Math.max( -Math.floor(MathUtil.log10(d)), 0);
    }

    /**
     * Verify that the min and max are valid.
     * @param range range to check for NaN values.
     */
    private void validateArguments(Range range) {
        if (Double.isNaN(range.getExtent())) {
            throw new IllegalArgumentException("Min cannot be greater than max for " + range);
        }
        if (Double.isNaN(range.getMin())
                || Double.isInfinite(range.getMin())
                || Double.isNaN(range.getMax())
                || Double.isInfinite(range.getMax())) {
            throw new IllegalArgumentException("Min or max of the range [" + range + "] is not a number.");
        }
    }

    /**
     * If real small just assume it is zero.
     * @param value the value to check.
     * @return zero if value is below the smallest allowed range, else return value.
     */
    private double checkSmallNumber(double value) {
        if (Math.abs(value) < SMALLEST_ALLOWED_RANGE) {
            return 0;
        }
        return value;
    }
}