// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer.balancers;

import com.barrybecker4.apps.misc.dtablebalancer.DimensionMeta;
import com.barrybecker4.apps.misc.dtablebalancer.Table;

/**
 * @author Barry Becker
 */
public abstract class AbstractBalancer implements Balancer {

    /**
     * If the new dimensions are just off by a pixel or two because of round-off,
     * then adjust the last row/column
     * @param table the table to adjust
     */
    protected void finalAdjust(Table table) {
        double totalWidth = 0;
        double totalHeight = 0;
        for (int i=0; i<table.getSize(); i++) {
            totalWidth += table.getColMeta(i).getLength();
            totalHeight += table.getRowMeta(i).getLength();
        }
        double widthDiff = table.getWidth() - totalWidth;
        double heightDiff = table.getHeight() -totalHeight;
        assert widthDiff <= 2;
        assert heightDiff <= 2;
        if (widthDiff > 0) {
            DimensionMeta lastCol = table.getColMeta(table.getSize() - 1);
            lastCol.setLength(lastCol.getLength() + widthDiff);
        }
        if (heightDiff > 0) {
            DimensionMeta lastRow = table.getRowMeta(table.getSize() - 1);
            lastRow.setLength(lastRow.getLength() + heightDiff);
        }
    }
}
