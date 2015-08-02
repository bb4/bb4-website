// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

/**
 * Represents a decision table graph.
 * We want to optimize the about of each cell that is filled with a bar chart coloring
 * by adjusting the width and height of cells. Each row and column have a constant hieght or width,
 * but they are can all independently adjustable.
 * @author Barry Becker
 */
public class Table {

    private int[][] grid;
    private int size;
    private int width;
    private int height;

    private DimensionMeta[] rowMeta;
    private DimensionMeta[] colMeta;

    /** the thing to optimize - ratio of painted are to total area.  */
    private double overallCoverage;

    /**
     * Constructor.
     * Initially all the row heights and column widths are equal sized.
     * @param data initial weights in each cell
     */
    public Table(int[][] data, int width, int height) {
        grid = data;
        this.size = grid.length;
        this.width = width;
        this.height = height;
        initializeMeta();
        updateMetaData();
    }

    public int getSize() {
        return size;
    }

    private void initializeMeta() {
        rowMeta = new DimensionMeta[size];
        colMeta = new DimensionMeta[size];
        int w = this.width / size;
        int h = this.height / size;

        for (int i = 0; i < grid.length; i++) {
            rowMeta[i] = new DimensionMeta(h);
            colMeta[i] = new DimensionMeta(w);
        }
    }

    private void updateMetaData() {
        for (int i=0; i<size; i++) {
            updateRowMeta(i);
            updateColMeta(i);
        }
    }

    private void updateRowMeta(int i) {
        int[] row = new int[size];
        System.arraycopy(grid[i], 0, row, 0, size);
        updateDimMeta(row, rowMeta[i]);
    }

    private void updateColMeta(int j) {
        int[] col = new int[size];
        for (int i=0; i<size; i++) {
            col[i] = grid[i][j];

        }
        updateDimMeta(col, colMeta[j]);
    }

    private void updateDimMeta(int[] dim, DimensionMeta meta) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        double mean;
        double total = 0;
        for (int j=0; j<size; j++) {
            int val = dim[j];
            total += val;
            if (val < min) {
                min = val;
            }
            if (val > max) {
                max = val;
            }
        }
        mean = total / meta.getLength();
        meta.update(min, max, mean);
    }

    /**
     * @return proportion of cell coverage
     */
    private double getCellCoverage(int i, int j) {
        double area = rowMeta[i].getLength() * colMeta[j].getLength();
        return (double) grid[i][j] / area;
    }

    /**
     * @return string form -
     *  the proportion that each table cell is filled and an overall fill proportion.
     */
    public String toString() {
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                System.out.println();
            }
        }
       return "";
    }
}
