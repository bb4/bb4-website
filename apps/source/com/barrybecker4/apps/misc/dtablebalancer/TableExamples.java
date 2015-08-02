// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

/**
 * @author Barry Becker
 */
public class TableExamples {

    public static final int[][] UNIFORM_2x2 = new int[][] {
        {2, 2},
        {2, 2}
    };

    public static final int[][] UNIFORM_3x3 = new int[][] {
        {100, 100, 100},
        {100, 100, 100},
        {100, 100, 100}
    };

    public static final int[][] NARROW_MIDDLE_COLUMN_3x3 = new int[][] {
        {100, 10, 100},
        {100, 40, 100},
        {100, 15, 100}
    };

    public static final int[][] NARROW_MIDDLE_ROW_3x3 = new int[][] {
        {100, 100, 100},
        {10,  40,  15},
        {100, 100, 100}
    };

    public static final int[][] RANDOM_3x3 = new int[][] {
        {11, 100,  60},
        {40,  55,  15},
        {200, 100, 178}
    };

    public static final int[][] RANDOM_4x4 = new int[][] {
        {11, 100,  60, 321},
        {40,  55,  15, 33},
        {200, 100, 178, 378},
        {120, 450, 278, 478}
    };

    /**
     * Simulate race by native country census data.
     * Rows are race (Black, Asian, Hispanic, White),
     * cols native country (Africa, China, Brazil, U.S)
     */
    public static final int[][] RACE_BY_NATIVE_4x4 = new int[][] {
        {411,    6,   9,   351},
        {  6, 1655,  11,   273},
        {  9,    5,  98,   268},
        { 17,   11,  14,  1078}
    };
}
