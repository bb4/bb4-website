package com.barrybecker4.apps.misc.restaurant;

import com.barrybecker4.common.concurrency.ThreadUtil;

class Chef extends Thread {

    private final Kitchen kitchen;

    Chef(Kitchen k) {
        kitchen = k;
    }

    @Override
    public void run() {

        while (true) {
            if (!kitchen.hasOrder()) {
                prepareOrder();
            }
        }
    }

    private void prepareOrder() {
        kitchen.createOrder();
        prepareFood();

        System.out.print("Order up! ");
        synchronized(kitchen) {
            kitchen.notify();
        }
    }

    private void prepareFood() {
        ThreadUtil.sleep((int)(1000 * Math.random()));
    }
}