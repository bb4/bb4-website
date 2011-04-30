package com.becker.puzzle.common;

import java.util.concurrent.CountDownLatch;


/**
 * ValueLatch
 * <p/>
 * Result-bearing latch used by ConcurrentPuzzleSolver
 * @ThreadSafe
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ValueLatch <T> {
    private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return (done.getCount() == 0);
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.await();
        synchronized (done) {
            return value;
        }
    }
}
