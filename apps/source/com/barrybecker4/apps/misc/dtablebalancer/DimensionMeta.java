// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

/**
 * @author Barry Becker
 */
public class DimensionMeta {
    private int max;
    private int length;
    private int total;

    /**
     * @param len either the width or height for the dimension
     */
    DimensionMeta(int len) {
        this.length = len;
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

    public void update(int max, int sum) {
        this.max = max;
        this.total = sum;
    }
}
