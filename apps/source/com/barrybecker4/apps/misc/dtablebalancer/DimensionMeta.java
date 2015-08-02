// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

/**
 * @author Barry Becker
 */
public class DimensionMeta {
    private int max;
    private int min;
    private int length;
    private int total;

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
        return (double) total / (double) length;
    }

    public int getLength() {
        return length;
    }

    public int getTotal() {
        return total;
    }

    public void update(int min, int max, int sum) {
        this.min = min;
        this.max = max;
        this.total = sum;
    }
}
