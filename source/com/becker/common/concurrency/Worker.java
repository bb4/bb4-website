package com.becker.common.concurrency;


/**
 * Worker is an abstract class that you subclass to
 * perform (usually gui related) work in a dedicated thread.  For
 * instructions on and examples of using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * You must invoke start() on the Worker after
 * creating it.
 * ----
 *   I have modified the original Worker class so that it no longer
 * depends on Swing (hence the new name). I sometimes want to use this
 * class in a server process. So if you are using it on the gui make sure
 * that the body of the finished method is called from SwingUtilities.invokeLater().
 *   -Barry
 */
public abstract class Worker {

    /** value to return. Ssee getValue(), setValue()   */
    private Object returnValue_ = null;

    private final ThreadVar threadVar_;

    /**
     * Class to maintain reference to current worker thread
     * under separate synchronization control.
     */
    private static class ThreadVar {
        private Thread thread_;
        ThreadVar(Thread t) { 
            thread_ = t; 
        }
        private synchronized Thread get() { 
            return thread_; 
        }
        private synchronized void clear() {
            thread_ = null; 
        }
    }

    /**
     * @return the value produced by the worker thread, or null if it hasn't been constructed yet.
     */
    protected synchronized Object getValue() {
        return returnValue_;
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     * @return the result.
     */
    public abstract Object construct();

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished() {
        // intentionally empty
    }

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt() {
        Thread t = threadVar_.get();
        if (t != null) {
            t.interrupt();
        }
        threadVar_.clear();
    }

    /**
     * Return the value created by the <code>construct</code> method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     *
     * @return the value created by the <code>construct</code> method
     */
    public Object get() {
        while (true) {
            Thread t = threadVar_.get();
            if (t == null) {
                return getValue();
            }
            try {
                t.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // propagate
                return null;
            }
        }
    }


    /**
     * Start a thread that will call the <code>construct</code> method
     * and then exit.
     */
    public Worker() {

        Runnable doConstruct = new Runnable() {
            public void run() {
                try {
                    returnValue_ = construct();
                }
                finally {
                    threadVar_.clear();
                }

                // old: SwingUtilities.invokeLater(doFinished);
                // Now call directly, but if the body of finished is in the ui,
                // it should call SwingUtilities.invokeLater()
                finished();
            }
        };

        Thread t = new Thread(doConstruct);
        threadVar_ = new ThreadVar(t);
    }

    /**
     * Start the worker thread.
     */
    public void start() {
        Thread t = threadVar_.get();
        if (t != null) {
            t.start();
        }
    }
}
