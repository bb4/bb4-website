// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.waxomatic;

import com.barrybecker4.common.concurrency.ThreadUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Derived from http://pervasive2.morselli.unimo.it/~nicola/courses/IngegneriaDelSoftware/java/ThinkingInJava.pdf
 */
class WaxOMatic {

    void start(int timeToRun) {
        Car car = new Car();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new WaxOff(car));
        exec.execute(new WaxOn(car));

        ThreadUtil.sleep(timeToRun); // Run for a while...
        exec.shutdownNow(); // Interrupt all tasks
    }

    public static void main(String[] args) throws Exception {
        new WaxOMatic().start(2000);
    }

}
