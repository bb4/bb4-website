/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.common.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Using this class you should be able to easily parallelize a loop of long running tasks.
 * Immutable
 * 
 * @author Barry Becker
 */
public class Parallelizer <T> {

  
    /** The number of processors available on this computer */
    public static final int NUM_PROCESSORS = Runtime.getRuntime().availableProcessors();
    
    /** Recycle threads so we do not create thousands and eventually run out of memory. */
    private ExecutorService exec;   
    
    private static final int DEFAULT_NUM_THREADS = NUM_PROCESSORS + 1;
    
    private int numThreads;
   
    public Parallelizer()
    {
        this(DEFAULT_NUM_THREADS);
    }

    /**
     * @param numThreads number of thread. Must be 1 or greater. One means not parallelism.
     */
    public Parallelizer(int numThreads)
    {
        assert numThreads > 0;
        this.numThreads = numThreads;
        exec = Executors.newFixedThreadPool(numThreads);   
    }
    
    public int getNumThreads() {
        return numThreads;
    }
    
     /**
      * Invoke all the workers at once and block until they are all done
      * Once all the separate threads have completed there assigned work, you may want to commit the results.
      */
    public void invokeAllRunnables(List<Runnable> workers)  {
            
        // convert the runnables to callables so the invokeAll api works
        List<Callable<T>> callables = new ArrayList<Callable<T>>(workers.size());
        for (Runnable r : workers) {
            callables.add(new Worker(r));
        }
        
        List<Future<T>> futures =  invokeAll(callables);   
        
        for (Future<T> f :  futures) {
            try {
                f.get();
            } catch (InterruptedException ex) {
                Logger.getLogger(Parallelizer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Parallelizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Invoke all the workers at once and block until they are all done
     * Once all the separate threads have completed there assigned work, you may want to commit the results.
     * @return list of Future tasks.
     */
    public List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables)  {
            
        List<Future<T>> f = null;
        try {
           f =  exec.invokeAll(callables);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } 
        return f;
    }
    
    /**
     * Simple decorator class to convert a Runnable to a Callable.
     */
    private class Worker implements Callable<T> {
      
        private Runnable runnable;
        
        public Worker(Runnable r) {
            runnable = r;
        }
        
        public T call() {
            runnable.run(); 
            return null;
        }
    }

}