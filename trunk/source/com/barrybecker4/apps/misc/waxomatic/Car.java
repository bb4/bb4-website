// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.waxomatic;

/**
 * derived from rom http://pervasive2.morselli.unimo.it/~nicola/courses/IngegneriaDelSoftware/java/ThinkingInJava.pdf
 */
class Car {

    private boolean waxOn = false;

    public synchronized void waxed() {
        waxOn = true; // Ready to buff
        notifyAll();
    }
    public synchronized void buffed() {
        waxOn = false; // Ready for another coat of wax
        notifyAll();
    }

    public synchronized void waitForWaxing() throws InterruptedException {
        while(!waxOn) wait();
    }

    public synchronized void waitForBuffing() throws InterruptedException {
        while (waxOn) wait();
    }
}
