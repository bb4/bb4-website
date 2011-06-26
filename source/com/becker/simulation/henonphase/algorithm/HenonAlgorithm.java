package com.becker.simulation.henonphase.algorithm;

import com.becker.common.concurrency.Parallelizer;
import com.becker.simulation.common.Profiler;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation common to all Henon Phase algorithms.
 * Uses concurrency when parallelized is set.
 * This will give good speedup on multi-core machines.
 *
 * @author Barry Becker
 */
public class HenonAlgorithm {

    public static final int DEFAULT_MAX_ITERATIONS = 5000;
    public static final int DEFAULT_FRAME_ITERATIONS = 50;
    public static final double DEFAULT_PHASE_ANGLE = 4.995; // Math.PI * Math.random();
    public static final int DEFAULT_NUM_TRAVELERS = 1000;

    private HenonModel model;

    /** Manages the worker threads. */
    private Parallelizer<Worker> parallelizer_;

    private int maxIterations_ = DEFAULT_MAX_ITERATIONS;
    private int iterations_ = 0;
    private int numStepsPerFrame = DEFAULT_FRAME_ITERATIONS;

    private boolean restartRequested = false;

    private static final int DEFAULT_SIZE = 300;
    private double angle = DEFAULT_PHASE_ANGLE;
    private boolean useUniformSeeds = true;
    private int numTravelors = DEFAULT_NUM_TRAVELERS;
    private boolean finished = false;



    public HenonAlgorithm() {

        model = new HenonModel(DEFAULT_SIZE, DEFAULT_SIZE, angle, useUniformSeeds, numTravelors);
        setParallelized(true);
    }

    public void setSize(int width, int height)  {

        if (width != model.width || height != model.height)   {
            requestRestart(width, height);
        }
    }

    public void setPhaseAngle(double newAngle) {
        if (newAngle != angle)   {
            angle = newAngle;
            requestRestart(model.width, model.height);
        }
    }

    public boolean getUseUniformSeeds() {
        return useUniformSeeds;
    }

    public void toggleUseUniformSeeds() {

        useUniformSeeds = !useUniformSeeds;
        requestRestart(model.width, model.height);
    }

    public void setNumTravelors(int newNumTravelors) {
        if (newNumTravelors != numTravelors)  {
            numTravelors= newNumTravelors;
            requestRestart(model.width, model.height);
        }
    }

    public void setMaxIterations(int value) {
        if (value != maxIterations_)  {
            maxIterations_ = value;
            requestRestart(model.width, model.height);
        }
    }



    public void setStepsPerFrame(int numSteps) {
        if (numSteps != numStepsPerFrame)
        {
            numStepsPerFrame = numSteps;
            requestRestart(model.width, model.height);
        }
    }


    private void requestRestart(int width, int height) {
        model = new HenonModel(width, height, angle, useUniformSeeds, numTravelors);
        restartRequested = true;
    }

    public boolean isParallelized() {
        return (parallelizer_.getNumThreads() > 1);
    }


    public void setParallelized(boolean parallelized) {
        if (parallelizer_ == null || parallelized != isParallelized()) {

            parallelizer_ =
                 parallelized ? new Parallelizer<Worker>() : new Parallelizer<Worker>(1);
        }
    }

    public BufferedImage getImage() {
        return model.getImage();
    }

    /**
     * @param timeStep number of rows to compute on this timestep.
     * @return true when done computing whole model.
     */
    public boolean timeStep(double timeStep) {

        if (restartRequested) {
            restartRequested = false;
            finished = false;
            iterations_ = 0;
            model.reset();
            Profiler.getInstance().startCalculationTime();
        }
        if (iterations_ > maxIterations_) {
            showProfileInfo();
            return true;  // we are done.
        }

        int numProcs = parallelizer_.getNumThreads();
        List<Runnable> workers = new ArrayList<Runnable>(numProcs);

        model.increment(numStepsPerFrame);
        iterations_ += numStepsPerFrame;


        // blocks until all Callables are done running.
        // parallelizer_.invokeAllRunnables(workers);

        return false;
    }


    private void showProfileInfo() {
        if (!finished) {
            finished = true;
            Profiler prof = Profiler.getInstance();
            prof.stopCalculationTime();
            prof.print();
            prof.resetAll();
        }
    }

    /**
     * Runs one of the chunks.
     */
    private class Worker implements Runnable {

        public Worker(int fromRow, int toRow) {
        }

        public void run() {
            computeChunk();
        }

        /**
         * Do a chunk of work (i.e. compute the specified rows)
         */
        private void computeChunk() {
        }
    }
}
