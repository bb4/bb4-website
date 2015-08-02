// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

/**
 * @author Barry Becker
 */
public class TableValidator {

    private static final double TOL = 0.0000000001;

    /** verify that the sum of the meta widths and heights are still equal to the overall width and height */
    public static final void verifyDimensions(Table table) {
        double totalWidth = 0;
        double totalHeight = 0;
        for (int i=0; i<table.getSize(); i++) {
            totalWidth += table.getColMeta(i).getLength();
            totalHeight += table.getRowMeta(i).getLength();
        }

        assert totalWidth - table.getWidth() < TOL:
                "Width is off. Got: " + totalWidth + " expected: " + table.getWidth();
        assert totalHeight - table.getHeight() < TOL :
                "Height is off. Got: " + totalHeight + " expected: " + table.getHeight();
    }
}
