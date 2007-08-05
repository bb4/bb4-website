package com.becker.simulation.reactiondiffusion;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the core of the Gray-Scott reaction diffusion simulation.
 * based on implmentation by Joakim Linde and modified by Barry Becker.
 *
 *Here are some parallelism results using my Core2Duo 6400.
 * Without parallelism  8.62 fps
 * With parallelism (but not borders) 10.16 fps
 * With parallelism (and borders in sep thread) 10.36 fps
 */
final class GrayScott {

    /** default values for constants. */
    public static final double K0 = 0.079;
    public static final double F0 = 0.02;
    public static final double H0 = 0.01;

    private static final double DU = 2.0e-5;
    private static final double DV = 1.0e-5;
    
    /** 
     * set this to true if you want to run the version
     *that will particiton the task of computing the next timeStop 
     *into smaller pieces that can be run on different threads.
     *This should speed thinks up on a multi-core computer.
     */
    private static final boolean PARALLELIZED = true;
    
    /** the number of processors available on this computer */
    private static final int NUM_PROCESSORS = Runtime.getRuntime().availableProcessors();
    
    /** Recycle threads so we do not create thousands and eventually run out of memory. */
    private Executor exec = Executors.newFixedThreadPool(NUM_PROCESSORS + 1);
    
    /** Force concurrent threads to pause until they are all complete. */
    private CountDownLatch latch_;
   
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
    int width_, height_;
       

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

        initialState(f, k, h);
    }
    
    public double getU(int x, int y) {
        return u_[x][y];
    }
    
    public double getV(int x, int y) {
        return v_[x][y];
    }

    public void reset() {
        initialState(F0, K0, H0);
    }

    public void initialState(double f, double k, double h) {

        System.out.println("NUM_PROCESSORS="+NUM_PROCESSORS);
  
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
    public double getF() { return f_; }
    public double getK() { return k_; }
    public double getH() { return h_; }

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
     * Advance one time step increment.
     *u_ and v_ are calculated based on tmpU and tmpV, then the result is committed to tmpU and tmpV.
     *
     * @param dt time step in seconds.
     */
    public void timeStep(double dt) {

        if (PARALLELIZED) {
            concurrentTimeStep(dt);
        } else {                   
          
            /*center*/
            double uv2;
            for (int x = 1; x < width_ - 1; x++) {
                for (int y = 1; y < height_ - 1; y++) {
                    uv2 = tmpU_[x][y] * tmpV_[x][y] * tmpV_[x][y];
                    u_[x][y] = calcNewCenter(tmpU_, x, y, duDivh2_, true, uv2, dt);
                    v_[x][y] = calcNewCenter(tmpV_, x, y, dvDivh2_, false, uv2, dt);
                }
            }
            computeNewEdgeValues(dt);
            commitChanges();    
        }
    }
    
    /**
     * Break task into chunks and run on seperate tasks.
     * Once all the separate threads have completed there assigned work, commit the results
     * into tmpU and tmpV to prepare for the next iteration.
     * The CyclicBarrier prevents the commit from happening until all threads are done.
     */
    public void concurrentTimeStep(final double dt) {
       
        latch_ = new CountDownLatch(NUM_PROCESSORS + 1);
                 
        /* calc center concurrently with multiple threads */
        Runnable[] workers = new Runnable[NUM_PROCESSORS + 1];
        int range = width_ / NUM_PROCESSORS;
        for (int i = 0; i < (NUM_PROCESSORS - 1); i++) {
            int offset =  i*range;
            workers[i] = new Worker(1 + offset, offset + range, dt);      
        }
         workers[NUM_PROCESSORS-1] = new Worker(range * (NUM_PROCESSORS - 1) + 1, width_-2, dt);    
                  
         // also add the border calculations in a separate thread.
         workers[NUM_PROCESSORS] = new Runnable() {
             public void run() {
                 computeNewEdgeValues(dt);
                 latch_.countDown();   
             }
         };
         for (int i = 0; i <= NUM_PROCESSORS; i++) {
             exec.execute((workers[i]));          
         }
           
         // wait until that latch has counted down to 0.         
         try {
             latch_.await();
         } catch (InterruptedException e) {
             System.out.println("Interrupted "+ e.getCause());
         }
                  
         commitChanges();      
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
            latch_.countDown();   
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


    private final double calcNewCenter(double[][] tmp, int x, int y,
                                 double dDivh2, boolean useF, double uv2, double dt) {

        double sum = tmp[x + 1][y] + tmp[x - 1][y] +
                tmp[x][y + 1] + tmp[x][y - 1] -
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

}
