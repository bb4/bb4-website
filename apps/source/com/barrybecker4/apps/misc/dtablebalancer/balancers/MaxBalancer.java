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
public class MaxBalancer extends AbstractBalancer {

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
            newWidths[i] = Math.sqrt(meta.getMax());
            totalWidth += newWidths[i];

            meta = table.getRowMeta(i);
            newHeights[i] = Math.sqrt(meta.getMax());
            totalHeight += newHeights[i];
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
}
