// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.waxomatic;

import com.barrybecker4.common.concurrency.ThreadUtil;

class WaxOn extends WaxTask {

    WaxOn(Car c) { super(c); }

    public void run() {
        try {
            while(!Thread.interrupted()) {
                System.out.println("Wax On! ");
                ThreadUtil.sleep(200);
                car.waxed();
                car.waitForBuffing();
            }
        } catch(InterruptedException e) {
            System.out.println("Exiting via interrupt");
        }
        System.out.println("Ending Wax On task");
    }
}