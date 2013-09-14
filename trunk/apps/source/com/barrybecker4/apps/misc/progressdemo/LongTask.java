/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.progressdemo;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.common.concurrency.Worker;

/**
 * Uses a Worker to perform a time-consuming, fake task.
 */
public class LongTask {
    private int lengthOfTask;
    private int current = 0;
    private String statMessage = "begun";

    /**
     * Compute magnitude of task...
     * In a real program, this would figure out
     * the number of bytes to read or whatever.
     * @param size
     */
    LongTask(Integer size) {
        lengthOfTask = size;
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    void go() {
        current = 0;
        final Worker worker = new Worker() {
            @Override
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    int getCurrent() {
        return current;
    }

    void stop() {
        current = lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out if the task has completed.
     */
    boolean done() {
        return current >= lengthOfTask;
    }

    String getMessage() {
        return statMessage;
    }

    /**
     * The actual long running task.  This runs in a Worker thread.
     */
    private class ActualTask {
        ActualTask () {
            // Fake a long task,
            // make a random amount of progress every second.
            while (current < lengthOfTask) {
                ThreadUtil.sleep(1000);
                // make some progress
                current += Math.random() * 100;
                if (current > lengthOfTask) {
                    current = lengthOfTask;
                }
                statMessage = "Completed " + current +
                              " out of " + lengthOfTask + ".";
            }
        }
    }
}
