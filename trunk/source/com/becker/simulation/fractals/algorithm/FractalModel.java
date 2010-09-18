package com.becker.simulation.fractals.algorithm;

import com.becker.common.math.ComplexNumber;

import java.awt.*;

/**
 * Nothing but a big matrix to hold the resuling values.
 *
 * @author Barry Becker
 */
public class FractalModel  {

    int[][] values;
    private static final int FIXED_SIZE = 200;
    private int currentRow;

    
    public FractalModel() {
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
        values = new int[width][height];
        currentRow = 0;
    }

    public void setFractalValue(int x, int y, int value) {
        if (x<getWidth() && y<getHeight())
            values[x][y] = value;
    }

    public int getFractalValue(int x, int y) {
        if (x >= getWidth() || y >= getHeight())  {
            return 0;
        }
        return values[x][y];
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

    public void setCurrentRow(int value) {
        currentRow = value;
    }

    public int getCurrentRow() {
        return currentRow;
    }
}
