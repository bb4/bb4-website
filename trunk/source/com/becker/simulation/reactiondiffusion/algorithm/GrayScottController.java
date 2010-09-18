package com.becker.simulation.reactiondiffusion.algorithm;

import com.becker.common.concurrency.Parallelizer;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Makes the GrayScott algorithm run concurrently if setParallelized is set to true.
 * Primary purpose of this class is to handle breaking the algorithm up into concurrent worker threads.
 *
 * Here are some parallelism results using my Core2Duo 6400.
 * Without parallelism  8.62 fps
 * With parallelism (but not borders) 10.16 fps
 * With parallelism (and borders in sep thread) 10.36 fps
 * After more tuning 18 fps (num steps per frame = 10)
 *
 * Using offscreen rendering slowed things by about 10%
 *
 *                       pr/ns  pr/sync  npr/ns  npr/synch
 *                      ------- -------  -------  -------
 * parallel calc       | 23.8     21.1    20.9    20.5
 * n-par calc          | 19.0     17.1            17.0
 * n-par calc/offscreen|                  12.8    12.9
 * par calc/ofscreen   | 17.2     14.2    14.3    14.1
 *
 *    pr/ns : parallel rendering/ no synchronized
 *    npr/ns : no parallel renderin no synch.
 *   * Parallel rendering without synchranization is fast, but has bad renderin artifacts.
 *
 * @author Barry Becker
 */
public final class GrayScottController {

    /** default values for constants. */
    public static final double H0 = 0.01;
    
    /** Manages the worker threads. */
    private Parallelizer<Worker> parallelizer;

    private GrayScottModel model_;

    private GrayScottAlgorithm algorithm_;

    /** null if no new size has been requested. */
    private Dimension requestedNewSize;
       

    /**
     * Constructor
     * @param width width of computational space.
     * @param height height of computational space.
     */
    public GrayScottController(int width, int height) {
        model_ =  new GrayScottModel(width, height);
        algorithm_ = new GrayScottAlgorithm(model_);
        setParallelized(true);
    }

    public GrayScottModel getModel() {
        return model_;
    }

    /**
     * doesn't change the size immediately since running threads may
     * be using the current array. We wait until the current timeStep completes
     * before reinitializing with the new size.
     */
    public void setSize(int width, int height) {
        requestedNewSize = new Dimension(width, height);
    }


    public void reset() {
        algorithm_.setH(H0);
        model_.resetState();
    }

    public void setH(double h) {
        algorithm_.setH(h);
    }

    /** 
     * Set this to true if you want to run the version
     * that will partition the task of computing the next timeStop
     * into smaller pieces that can be run on different threads.
     * This should speed thinks up on a multi-core computer.
     */
    public void setParallelized(boolean parallelized) {
         parallelizer =
             parallelized ? new Parallelizer<Worker>() : new Parallelizer<Worker>(1);
    }
 
    public boolean isParallelized() {
       return (parallelizer.getNumThreads() > 1);
    }
    
    /**
     * Advance one time step increment.
     * u and v are calculated based on tmpU and tmpV, then the result is committed to tmpU and tmpV.
     *
     * @param dt time step in seconds.
     */
    public void timeStep(final double dt) {

        int numProcs = parallelizer.getNumThreads();
        List<Runnable> workers = new ArrayList<Runnable>(numProcs);
        int range = model_.getWidth() / numProcs;

        for (int i = 0; i < (numProcs - 1); i++) {
            int offset = i * range;
            workers.add(new Worker(1 + offset, offset + range, dt));
        }
        workers.add(new Worker(range * (numProcs - 1) + 1, model_.getWidth() - 2, dt));

        // also add the border calculations in a separate thread.
        Runnable edgeWorker = new Runnable() {
            public void run() {
                algorithm_.computeNewEdgeValues(dt);
            }
        };
        workers.add(edgeWorker);   
   
        // blocks until all Callables are done running.
        parallelizer.invokeAllRunnables(workers);
     
        model_.commitChanges();

        if (requestedNewSize != null) {
             model_.setSize(requestedNewSize);
             requestedNewSize = null;
             reset();
        }
    }
    
    /**
     * Runs one of the chunks.
     */
    private class Worker implements Runnable {
        private int minX_, maxX_;
        private double dt_;
        
        public Worker(int minX, int maxX, double dt) {
            minX_ = minX;
            maxX_ = maxX;
            dt_ = dt;
        }
        
        public void run() {
            algorithm_.computeNextTimeStep(minX_, maxX_, dt_);
        }
    }
}
