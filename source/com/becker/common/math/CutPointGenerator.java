package com.becker.common.math;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculate nice round numbered intervals for a given number range.
 * You can choose loose or tight labeling for the intervals.
 * (derived from a Graphics Gems 1 article by Paul Heckbert)
 *
 * @author Barry Becker
 */
public class CutPointGenerator {

    /** Never allow a range less than this */
    private static final double MIN_RANGE = 1.0E-10;

    /**
     * Don't allow the label to get closer to each other than this.
     * Prevents the min or max label from overwriting one of the nice cut-points.
     */
    private static final double LABEL_PROXIMITY_THRESH = 0.2;

    /** Default way to show the numbers as labels */
    private DecimalFormat formatter;

    /**
     * If true, show the precise min/max values at the extreme cut points (tight),
     * otherwise rounded values are shown (loose).
     */
    private boolean useTightLabeling;


    public CutPointGenerator() {
       this(true, new DecimalFormat("###,###.##"));
    }

    public CutPointGenerator(boolean useTightLabeling) {
        this(useTightLabeling, new DecimalFormat("###,###.##"));
    }

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

        double [] cutPoints = getCutPoints(range, maxTicks);
        formatter.setMaximumFractionDigits(getNumberOfFractionDigits(range, maxTicks));

        int len = cutPoints.length;
        String[] labels = new String[len];
        for (int i=0; i<len; i++) {
            labels[i] = formatter.format(cutPoints[i]);
        }
        return labels;
    }

    /**
     * Get the cut point values.
     * @param range tickmark range.
     * @param maxTicks  upper limit on number of cut points to return.
     * @return the cut points
     */
    public double[] getCutPoints(Range range, int maxTicks) {

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
            determineCutPoints(maxTicks, finalRange, positions);
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

    /**
     * Find the number of fractional digits to show in the nice numbers.
     * @param range range to check.
     * @param maxTicks no more than this many cut points.
     * @return Recommended number of fractional digits to display.
     *     The cut points eg. 0, 1, 2, etc.
     */
    int getNumberOfFractionDigits(Range range, int maxTicks) {
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
    private void checkArgs(Range range) {
        if (Double.isNaN(range.getExtent())) {
            throw new IllegalArgumentException("Min cannot be greater than max for " + range);
        }
        if (Double.isNaN(range.getMin()) || Double.isInfinite(range.getMin())) {
            throw new IllegalArgumentException("min is not a number");
        }
        if (Double.isNaN(range.getMax()) || Double.isInfinite(range.getMax())) {
            throw new IllegalArgumentException("max is not a number");
        }

    }

    private double checkSmallNumber(double value) {
        if (MIN_RANGE > Math.abs(value)) {
            return 0;
        }

        return value;
    }
}