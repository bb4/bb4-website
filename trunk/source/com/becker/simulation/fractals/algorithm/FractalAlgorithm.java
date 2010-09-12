package com.becker.simulation.fractals.algorithm;

import com.becker.common.math.ComplexNumber;

/**
 * Abstract implementation common to all fractal algorithms.
 *
 * @author Barry Becker
 */
public abstract class FractalAlgorithm {

    protected FractalModel model;

    /** lower left corner of bounding box in complex plane. */
    private ComplexNumber firstCorner;

    /** range of bounding box in complex plane. */
    private ComplexNumber range;

    private boolean parallelized_;


    public FractalAlgorithm(FractalModel model, ComplexNumber firstCorner, ComplexNumber secondCorner) {
        this.model = model;
        setRange(firstCorner, secondCorner);
    }

    public void setRange(ComplexNumber firstCorner, ComplexNumber secondCorner)  {
        this.firstCorner = firstCorner;
        this.range = secondCorner.subtract(firstCorner);
        model.setCurrentRow(0);
    }

    public boolean isParallelized() {
        return parallelized_;
    }

    public void setParallelized(boolean value) {
        parallelized_ = value;

    }

    /**
     * @param timeStep number of rows to comput on this timestep.
     * @return true when done computing whole model.
     */
    public abstract boolean timeStep(double timeStep);

    public abstract int getFractalValue(ComplexNumber seed);

    /**
     * Converts from screen coordinates to data coordinates.
     * @param x
     * @param y
     * @return corresponding position in complex number plane represented by the model.
     */
    public ComplexNumber getComplexPosition(int x, int y) {
         return new ComplexNumber(firstCorner.getReal() + range.getReal() * x / model.getWidth(),
                                  firstCorner.getImaginary() + range.getImaginary() * y / model.getHeight());
    }
}
