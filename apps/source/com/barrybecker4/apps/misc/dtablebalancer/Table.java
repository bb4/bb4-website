// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

import com.barrybecker4.common.format.FormatUtil;


/**
 * Represents a decision table graph.
 * We want to optimize the area of each cell that is filled with a bar chart coloring
 * by adjusting the width and height of cells. Each row and column have a constant height or width,
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

    /**
     * The amount of area covered by each unit of grid value.
     * It is the inverse of the largest grid value to cell area ratio.
     */
    private double normalizationScale;

    /**
     * Ratio of colored area in the table. A value between 0 and 1.
     * The thing to optimize - ratio of painted are to total area.
     * Coverage is optimal when this is 1.
     */
    private double overallCoverage;

    /**
     * Constructor.
     * Initially all the row heights and column widths are equal sized.
     * @param data initial weights in each cell
     */
    public Table(int[][] data, int width, int height) {
        grid = makeCopy(data);
        this.size = grid.length;
        this.width = width;
        this.height = height;
        initializeMeta();
        updateMetaData();
    }

    /** Copy constructor */
    public Table(Table table) {
        this(table.grid, table.width, table.height);
    }

    private static int[][] makeCopy(int[][] g) {
        int len = g.length;
        int[][] gridCopy = new int[len][len];
        for (int i=0; i<len; i++) {
            System.arraycopy(g[i], 0, gridCopy[i], 0, len);
        }
        return gridCopy;
    }

    public DimensionMeta getRowMeta(int i) {
        return rowMeta[i];
    }

    public DimensionMeta getColMeta(int i) {
        return colMeta[i];
    }

    public double getNormalizationScale() {
        return normalizationScale;
    }

    public int getSize() {
        return size;
    }

    public double getOverallCoverage() {
        return overallCoverage;
    }

    private void initializeMeta() {
        rowMeta = new DimensionMeta[size];
        colMeta = new DimensionMeta[size];
        double w = (double) this.width / size;
        double h = (double) this.height / size;

        for (int i = 0; i < grid.length; i++) {
            rowMeta[i] = new DimensionMeta(h);
            colMeta[i] = new DimensionMeta(w);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** this needs to be called any time the meta data is modified */
    public void updateMetaData() {
        for (int i=0; i<size; i++) {
            updateRowMeta(i);
            updateColMeta(i);
        }
        int grandTotal = 0;
        int max = 0;
        double largestValueToGridAreaRatio = 0;
        for (int i=0; i<size; i++) {
            grandTotal += rowMeta[i].getTotal();
            if (rowMeta[i].getMax() > max) {
                max = rowMeta[i].getMax();
            }
            for (int j=0; j<size; j++) {
                double cellArea = rowMeta[i].getLength() * colMeta[j].getLength();
                double valueToGridAreaRatio = (double) grid[i][j] / cellArea;
                //System.out.println("valueToGridRat=" + valueToGridAreaRatio);
                if (valueToGridAreaRatio > largestValueToGridAreaRatio) {
                    largestValueToGridAreaRatio = valueToGridAreaRatio;
                }
            }
        }

        normalizationScale = 1.0 / largestValueToGridAreaRatio;
        System.out.println("mormScale=" + normalizationScale);
        overallCoverage = (double) grandTotal * normalizationScale / (width * height);

        TableValidator.verifyDimensions(this);
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
        int total = 0;
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
        meta.update(max, total);
    }

    /**
     * @return proportion of cell coverage
     */
    private double getCellCoverage(int i, int j) {
        double area = rowMeta[i].getLength() * colMeta[j].getLength();
        return grid[i][j] * normalizationScale / area;
    }

    /**
     * @return string form -
     *  the proportion that each table cell is filled and an overall fill proportion.
     */
    public String toString() {
        String s = "";
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                s += FormatUtil.formatNumber(getCellCoverage(i, j)) + "\t";
            }
            s += "\n";
        }
        s += "Overall coverage: " + FormatUtil.formatNumber(overallCoverage);
        return s;
    }
}
