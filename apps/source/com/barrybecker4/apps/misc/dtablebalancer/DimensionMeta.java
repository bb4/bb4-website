// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

/**
 * @author Barry Becker
 */
public class DimensionMeta {
    private double mean;
    private int max;
    private int min;
    private int length;

    /**
     * @param len either the width or height for the dimension
     */
    DimensionMeta(int len) {
        this.length = len;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public double getMean() {
        return mean;
    }

    public int getLength() {
        return length;
    }

    public void update(int min, int max, double mean) {
        this.min = min;
        this.max = max;
        this.mean = mean;
    }


}
