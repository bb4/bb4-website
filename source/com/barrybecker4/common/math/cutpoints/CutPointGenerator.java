package com.barrybecker4.common.math.cutpoints;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.common.math.Range;

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

    /** The range should not be less than this. */
    private static final double SMALLEST_ALLOWED_RANGE = 1.0E-10;

    /** Default way to show the numbers as labels */
    private DecimalFormat formatter;

    /** If true, show the precise min/max values at the extreme cut points (tight), else loose labels */
    private AbstractCutPointFinder cutPointFinder;


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
        setUseTightLabeling(useTightLabeling);
        this.formatter = formatter;
    }

    public void setUseTightLabeling(boolean useTight) {
        cutPointFinder = useTight ? new TightCutPointFinder() : new LooseCutPointFinder();
    }

    /**
     * Retrieve the cut point values.
     * If its a really small range include both min and max to avoid having just one label.
     * @param range range to be divided into intervals.
     * @param maxNumTicks upper limit on number of cut points to return.
     * @return the cut points
     */
    public double[] getCutPoints(Range range, int maxNumTicks) {

        return cutPointFinder.getCutPoints(range, maxNumTicks);
    }

    /**
     * Labels for the found cut points.
     * @param range tickmark range.
     * @return cut point labels
     */
    public String[] getCutPointLabels(Range range, int maxTicks) {

        double[] cutPoints = cutPointFinder.getCutPoints(range, maxTicks);
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
     * Determine the number of fractional digits to show in the nice numbered cut points.
     * @param range the range to check.
     * @param maxNumTicks no more than this many cut points.
     * @return Recommended number of fractional digits to display. The cut points: eg. 0, 1, 2, etc.
     */
    int getNumberOfFractionDigits(Range range, int maxNumTicks) {

        double max1 = range.getMax();
        if (range.getExtent() <= SMALLEST_ALLOWED_RANGE) {
            max1 = range.getMin() + SMALLEST_ALLOWED_RANGE;
        }

        double extent = NiceNumberRounder.round(max1 - range.getMin(), false);
        double d = NiceNumberRounder.round(extent / (maxNumTicks - 1), true);

        return (int) Math.max( -Math.floor(MathUtil.log10(d)), 0);
    }
}