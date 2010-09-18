package com.becker.simulation.fractals.algorithm;

import com.becker.common.concurrency.Parallelizer;
import com.becker.common.math.ComplexNumber;
import com.becker.common.profile.ProfilerEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation common to all fractal algorithms.
 * Uses concurrency when parallelized is set.
 * This will give good speedup on multi-core machines.
 *
 * For core 2 Duo:
 *  - not-parallel 32.4 seconds
 *  - parallel     19.5 seconds
 * @author Barry Becker
 */
public abstract class FractalAlgorithm {

    protected FractalModel model;

    /** lower left corner of bounding box in complex plane. */
    private ComplexNumber firstCorner;

    /** range of bounding box in complex plane. */
    private ComplexNumber range;


    /** Manages the worker threads. */
    private Parallelizer<Worker> parallelizer;

    private ProfilerEntry timer_;


    public FractalAlgorithm(FractalModel model, ComplexNumber firstCorner, ComplexNumber secondCorner) {
        this.model = model;
        setRange(firstCorner, secondCorner);
        setParallelized(true);
    }

    public void setRange(ComplexNumber firstCorner, ComplexNumber secondCorner)  {
        this.firstCorner = firstCorner;
        this.range = secondCorner.subtract(firstCorner);
        model.setCurrentRow(0);
    }

    public boolean isParallelized() {
        return (parallelizer.getNumThreads() > 1);
    }

    public void setParallelized(boolean parallelized) {
        parallelizer =
             parallelized ? new Parallelizer<Worker>() : new Parallelizer<Worker>(1);
    }

    /**
     * @param timeStep number of rows to comput on this timestep.
     * @return true when done computing whole model.
     */
    public boolean timeStep(double timeStep) {

        if (model.isDone()) {
            stopTiming();
            return true;  // we are done.
        }

        int numProcs = parallelizer.getNumThreads();
        List<Runnable> workers = new ArrayList<Runnable>(numProcs);
        
        // we calculate a little more each "timestep"
        int currentRow = model.getCurrentRow();
        if (currentRow == 0) {
            startTiming();
        }


        int computeToRow = Math.min(model.getHeight(), currentRow + (int)timeStep * numProcs);

        int diff = computeToRow - currentRow;
        if (diff == 0) return true;

        int chunk = Math.max(1, diff / numProcs);


        for (int i = 0; i < numProcs; i++) {
            workers.add(new Worker(currentRow, currentRow + chunk));
            //System.out.println("creating worker ("+i+") to compute " + chunk +" rows.");
            currentRow += chunk;
        }

        // blocks until all Callables are done running.
        parallelizer.invokeAllRunnables(workers);

        model.setCurrentRow(currentRow);
        return false;
    }


    /**
     * @return the number of times we had to iterate before the point escaped (or not)
     */
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


    private void startTiming() {
        timer_ = new ProfilerEntry("Fractal");
        timer_.start();
    }

    private void stopTiming() {
         if (timer_ != null) {
              timer_.stop();
              timer_.print();
              timer_ = null;
         }
    }

    /**
     * Runs one of the chunks.
     */
    private class Worker implements Runnable {

        private int fromRow_;
        private int toRow_;

        public Worker(int fromRow, int toRow) {

            fromRow_ = fromRow;
            toRow_ = toRow;
        }

        public void run() {
            computeChunk(fromRow_, toRow_);
        }


        /**
         * Do a chunk of work (i.e. compute the specified rows)
         */
        private void computeChunk(int fromRow, int toRow) {

            int width = model.getWidth();
            for (int y = fromRow; y < toRow; y++)   {
                for (int x = 0; x < width; x++)   {
                    ComplexNumber z = getComplexPosition(x, y);
                    model.setFractalValue(x, y, getFractalValue(z));
                }
            }
        }
    }
}
