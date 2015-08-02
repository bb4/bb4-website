// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer.balancers;

import com.barrybecker4.apps.misc.dtablebalancer.DimensionMeta;
import com.barrybecker4.apps.misc.dtablebalancer.Table;
import com.barrybecker4.apps.misc.dtablebalancer.TableValidator;

/**
 * The approach used here is to set the row or column height/width to the average of the mean and max values normalized
 * by the overall width/height.
 *
 * @author Barry Becker
 */
public class SimpleBalancer implements Balancer {

    /** never less than this many pixels */
    private static final int MIN_DIM = 1;

    @Override
    public void doBalancing(Table table) {
        double normScale = table.getNormalizationScale();
        double[] newWidths = new double[table.getSize()];
        double[] newHeights = new double[table.getSize()];
        double totalWidth = 0;
        double totalHeight = 0;

        for (int i = 0; i<table.getSize(); i++) {
            DimensionMeta meta = table.getColMeta(i);
            newWidths[i] = Math.sqrt((meta.getMax() + meta.getMean()) / 2.0);
            totalWidth += newWidths[i];
            //meta.setLength(newLen);

            meta = table.getRowMeta(i);
            newHeights[i] = Math.sqrt((meta.getMax() + meta.getMean()) / 2.0);
            totalHeight += newHeights[i];
            //meta.setLength(newLen);
        }

        for (int i = 0; i<table.getSize(); i++) {
            double newWidth = Math.max(MIN_DIM, table.getWidth() * newWidths[i] / totalWidth);
            table.getColMeta(i).setLength(newWidth);

            double newHeight = Math.max(MIN_DIM, table.getHeight() * newHeights[i] / totalHeight);
            table.getRowMeta(i).setLength(newHeight);
        }

        finalAdjust(table);
        table.updateMetaData();
    }

    /** if the new dimensions are just off by a pixel or two because of round-off, then adjust the last row/column */
    private void finalAdjust(Table table) {
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
