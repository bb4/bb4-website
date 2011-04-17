package com.becker.simulation.fractalexplorer.algorithm;

import com.becker.common.ColorMap;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Nothing but a big matrix to hold the resulting values.
 *
 * @author Barry Becker
 */
public class FractalModel  {

    double[][] values;
    private static final int FIXED_SIZE = 200;
    private int currentRow;
    private ColorMap cmap;

    BufferedImage image;

    
    public FractalModel(ColorMap cmap) {
        this.cmap = cmap;
        initialize(FIXED_SIZE, FIXED_SIZE);
    }

    /**
     * We can change the size of the model, but doing so will clear all current results.
     * We only resize if the new dimensions are different than we had to prevent clearing results unnecessarily.
     */
    public void setSize(int width, int height) {
        if (width != getWidth() || height != getHeight()) {
            initialize(width, height);
        }
    }

    private void initialize(int width, int height) {
        values = new double[width][height];
        currentRow = 0;
        System.out.println("creating image with dims width="+ width +" ht=" + height);

        // BUG - not sure why I need to add 10 to the dimensions to avoid range error.
        image = new BufferedImage(width + 10, height + 10, BufferedImage.TYPE_INT_RGB);
    }

    public void setFractalValue(int x, int y, double value) {
        if (x<getWidth() && y<getHeight())
            values[x][y] = value;
    }

    public double getFractalValue(int x, int y) {
        if (x<0 || x >= getWidth() || y<0 || y >= getHeight())  {
            return 0;
        }
        return values[x][y];
    }


    public ColorMap getColorMap() {
        return cmap;
    }

    public int getWidth() {
        return values.length;
    }

    public int getHeight() {
        return values[0].length;
    }

    public double getAspectRatio() {
        return getWidth() / getHeight();
    }
    
    public boolean isDone() {
        return currentRow >= getHeight();
    }

    /**
     * Set the row that we have calculated up to.
     * @param row new row
     */
    public void setCurrentRow(int row) {
        int lastRow = currentRow;
        currentRow = row;
        updateImage(lastRow);
    }

    public int getCurrentRow() {
        return currentRow;
    }

    /**
     * Update the global images with a new strip of just computed pixels.
     */
    private void updateImage(int lastRow)  {
        int width = getWidth();
        int rectHeight = currentRow - lastRow;
        if (rectHeight <= 0) return;
        int[] pixels = new int[width * rectHeight];


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < rectHeight; y++) {

                Color c = cmap.getColorForValue(getFractalValue(x, y + lastRow));
                pixels[y * width + x] = c.getRGB();
            }
        }

        //System.out.println("updateImage width= "  +width+ " ht="+ rectHeight + " offset="+ offset + " pixelDim="+ pixels.length
        //        + " currentRow="+ currentRow +" lastRow = " + lastRow);
        image.setRGB(0, lastRow, width, rectHeight, pixels, 0, width);
    }

    /**
     * @return the accumulated image so far.
     */
    public Image getImage() {

        return image;
    }
}
