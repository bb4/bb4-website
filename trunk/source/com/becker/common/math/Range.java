package com.becker.common.math;

/**
 * Manage a double precision range.
 * 
 * @author Barry Becker
 */
public class Range {

    private double min_;
    private double max_;

    public Range() {
        this(Double.MAX_VALUE, -Double.MAX_VALUE);
    }

    /**
     * init with min and max valeus of the range.
     * @param minimum
     * @param maximum
     */
    public Range(double minimum, double maximum) {
        min_ = minimum;
        max_ = maximum;
    }

    /**
     * @return Returns the Max.
     */
    public double getMax() {
        return max_;
    }

    /**
     * @return Returns the axisMin.
     */
    public double getMin() {
        return min_;
    }

    /**
     *Extend this range by the range argument.
     */
    public void add(Range range) {
        add(range.getMin());
        add(range.getMax());
    }

    /**
     * Extend this range by the value argument.
     */
    public void add(double value) {
        if (value < min_) {
            min_ = value;
        }
        if (value > max_) {
            max_ = value;
        }
    }

    public double getExtent() {
        if (min_ > max_) {
            return Double.NaN;
        }
        return (max_ - min_);
    }

    /*
     * @return true if the range is completely contained by us.
     */
    public boolean inRange(Range range) {
        return range.getMin() >= getMin() && range.getMax() <= getMax();
    }

    /**
     * @param value
     * @return  normalized value assumeing 0 for min. 1 for max.
     */
    public double mapToUnitScale(double value) {
        double range = getExtent();
        if (range == 0) {
            return 0;
        }
        return (value - getMin()) / getExtent();
    }

    public String toString() {
        return  this.getMin() + " to " + this.getMax();
    }
}