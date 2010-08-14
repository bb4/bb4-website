package com.becker.simulation.reactiondiffusion;

import com.becker.common.concurrency.Parallelizer;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;


/**
 * This is the core of the Gray-Scott reaction diffusion simulation.
 * based on implementation by Joakim Linde and modified by Barry Becker.
 *
 * Here are some parallelism results using my Core2Duo 6400.
 * Without parallelism  8.62 fps
 * With parallelism (but not borders) 10.16 fps
 * With parallelism (and borders in sep thread) 10.36 fps
 * After more tuning 19fps (num steps per frame = 10)
 *TODO:
 * - restore last valid state when no activity or all black.
 */
final class GrayScott {

    /** default values for constants. */
    public static final double K0 = 0.079;
    public static final double F0 = 0.02;
    public static final double H0 = 0.01;

    private static final double DU = 2.0e-5;
    private static final double DV = 1.0e-5;
    
    /** Recycle threads so we do not create thousands and eventually run out of memory. */
    private Parallelizer<Worker> parallelizer;

    /** concentrations of the 2 chemicals. */
    private double[][] u_;
    private double[][] v_;
    private double[][] tmpU_;
    private double[][] tmpV_;

    private double k_ = K0;
    private double f_ = F0;
    private double h_ = H0;

    private double duDivh2_;
    private double dvDivh2_;
    private int width_, height_;

    /** null if no new size has been requestsed. */
    private Dimension requestedNewSize;
       

    /**
     * constructor
     * @param width width of computational space.
     * @param height height of computational space.
     */
    GrayScott(int width, int height) {
        this(width, height, F0, K0, H0);
    }

    /**
     * Constructor that allows you to specify starting constants.
     */
    GrayScott(int width, int height, double f, double k, double h) {
        this.width_ = width;
        this.height_ = height;
        setParallelized(true);
        initialState(f, k, h);        
    }

    /**
     * doesn't change the size immediately since running threads may
     * be using the current array. We wait until the current timeStep completes
     * before reinitializing with the new size.
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {

        requestedNewSize = new Dimension(width, height);
    }
    
    public double getU(int x, int y) {
        return u_[x % width_][y % height_];
    }
    
    public double getV(int x, int y) {
        return v_[x % width_][y % height_];
    }


    public void reset() {
        initialState(F0, K0, H0);
    }

    public void initialState(double f, double k, double h) {
  
        this.f_ = f;
        this.k_ = k;
        setH(h);
        u_ = new double[width_][height_];
        v_ = new double[width_][height_];
        tmpU_ = new double[width_][height_];
        tmpV_ = new double[width_][height_];

        for (int x = 0; x < width_; x++) {
            for (int y = 0; y < height_; y++) {
                tmpU_[x][y] = 1;
                tmpV_[x][y] = 0;
            }
        }
        int w3 = width_ / 3;
        int h3 = height_ / 3;
        for (int x = 0; x < w3; x++) {
            for (int y = 0; y < h3; y++) {
                tmpU_[w3 + x][h3 + y] = 0.5;
                tmpV_[w3 + x][h3 + y] = 0.25;
            }
        }
        double w7 = (double) width_ / 7;
        double h5 = (double) height_ / 5;
        for (int x = 0; x < w7; x++) {
            for (int y = 0; y < h5; y++) {
                tmpU_[(int) (5 * w7) + x][(int) (3 * h5) + y] = 0.5;
                tmpV_[(int) (5 * w7) + x][(int) (3 * h5) + y] = 0.25;
            }
        }
    }

    public int getWidth() {
        return width_;
    }

    public int getHeight() {
        return height_;
    }

    public void setF(double f) {
        f_ = f;
    }

    public void setK(double k) {
        k_ = k;
    }

    public void setH(double h) {
        h_ = h;
        double h2 = h_ * h_;
        duDivh2_ = DU / h2;
        dvDivh2_ = DV / h2;
    }

    /** 
     *Set this to true if you want to run the version
     *that will partition the task of computing the next timeStop
     *into smaller pieces that can be run on different threads.
     *This should speed thinks up on a multi-core computer.
     */
    public void setParallelized(boolean parallelized) {
        if (parallelized)  {
            parallelizer = new Parallelizer<Worker>();
        }
        else {
            parallelizer = new Parallelizer<Worker>(1);
        }
    }
 
    public boolean isParallelized() {
       return (parallelizer.getNumThreads() > 1);
    }
    
    /**
     * Advance one time step increment.
     *u_ and v_ are calculated based on tmpU and tmpV, then the result is committed to tmpU and tmpV.
     *
     * @param dt time step in seconds.
     */
    public void timeStep(final double dt) {
       
        // calc center concurrently with multiple threads 
        int numProcs = Parallelizer.NUM_PROCESSORS;
        List<Runnable> workers = new ArrayList<Runnable>(numProcs + 1);
        int range = width_ / numProcs;
        for (int i = 0; i < (numProcs - 1); i++) {
            int offset = i * range;
            workers.add(new Worker(1 + offset, offset + range, dt));
        }
        workers.add(new Worker(range * (numProcs - 1) + 1, width_ - 2, dt));

        // also add the border calculations in a separate thread.
        Runnable edgeWorker = new Runnable() {
            public void run() {
                computeNewEdgeValues(dt);           
            }
        };
        workers.add(edgeWorker);   
   
        // blocks until all Callables are done running.
        parallelizer.invokeAllRunnables(workers);
     
        commitChanges();

        if (requestedNewSize != null) {
             this.width_ = requestedNewSize.width;
             this.height_ = requestedNewSize.height;
             requestedNewSize = null;
             reset();
        }
    }
    
    private void commitChanges() {
         for (int x = 0; x < width_; x++) {
            for (int y = 0; y < height_; y++) {
                tmpU_[x][y] = u_[x][y];
                tmpV_[x][y] = v_[x][y];
            }
        }
    }


    private void computeNewEdgeValues(double dt) {
        /* top and bottom edges*/
        for (int x = 0; x < width_; x++) {
            calcEdge(x, 0, dt);
            calcEdge(x, height_ - 1, dt);
        }

         /* left and right edges*/
        for (int y = 0; y < height_; y++) {
            calcEdge(0, y, dt);
            calcEdge(width_ - 1, y, dt);
        }
    }

    /**
     * Calculate new values on an edge.
     */
    private void calcEdge(int x, int y, double dt) {

        double uv2 = tmpU_[x][y] * tmpV_[x][y] * tmpV_[x][y];
        u_[x][y] = calcNewEdge(tmpU_, x, y, width_, height_, duDivh2_, true, uv2, dt);
        v_[x][y] = calcNewEdge(tmpV_, x, y, width_, height_, dvDivh2_, false, uv2, dt);
    }


    private double calcNewEdge(double[][] tmp, int x, int y, int ww, int hh,
                               double dDivh2, boolean useF, double uv2, double dt) {

        double sum = tmp[pBC(x + 1, ww)][y] + tmp[pBC(x - 1, ww)][y] +
                tmp[x][pBC(y + 1, hh)] + tmp[x][pBC(y - 1, hh)] -
                4 * tmp[x][y];

        return calcNewAux(tmp, x, y, sum, dDivh2, useF, uv2, dt);
    }


    private double calcNewAux(double[][] tmp, int x, int y, double sum,
                              double dDivh2, boolean useF, double uv2, double dt) {
        double txy = tmp[x][y];
        double c = useF ? -uv2 + f_ * (1.0 - txy)
                        :  uv2 - k_ * txy;

        double newVal = txy + dt * (dDivh2 * sum  + c);
        if (newVal < 0) {
            return 0;
        } else if (newVal > 1.0 || Double.isInfinite(newVal)) {
            return 1.0;
        } else {
            return newVal;
        }
    }

    /**
     * Periodic boundary conditions.
     */
    private static int pBC(int x, int max) {
        int xp = x;
        while (xp < 0) {
            xp += max;
        }
        while (xp >= max) {
            xp -= max;
        }
        return xp;
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
            double uv2;
            for (int x = minX_; x <= maxX_; x++) {
                for (int y = 1; y < height_ - 1; y++) {
                    uv2 = tmpU_[x][y] * tmpV_[x][y] * tmpV_[x][y];
                    u_[x][y] = calcNewCenter(tmpU_, x, y, duDivh2_, true, uv2, dt_);
                    v_[x][y] = calcNewCenter(tmpV_, x, y, dvDivh2_, false, uv2, dt_);
                }
            }       
        }

        private double calcNewCenter(double[][] tmp, int x, int y,
                                     double dDivh2, boolean useF, double uv2, double dt) {

            double sum = tmp[x + 1][y] + tmp[x - 1][y] +
                    tmp[x][y + 1] + tmp[x][y - 1] -
                    4 * tmp[x][y];
            return calcNewAux(tmp, x, y, sum, dDivh2, useF, uv2, dt);
        }

    }

}
