// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.waxomatic;

import com.barrybecker4.common.concurrency.ThreadUtil;

/**
 * @author Barry Becker
 */
class WaxOff extends WaxTask {

    WaxOff(Car c) { super(c); }

    public void run() {
        try {
            while(!Thread.interrupted()) {
                car.waitForWaxing();
                System.out.println("Wax Off! ");
                ThreadUtil.sleep(200);
                car.buffed();
            }
        } catch(InterruptedException e) {
             System.out.println("Exiting via interrupt");
        }
        System.out.println("Ending Wax Off task");
    }
}
