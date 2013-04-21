package com.barrybecker4.apps.misc.restaurant;

class Waiter extends Thread {

    private final Kitchen kitchen;

    public Waiter(Kitchen k) {
        kitchen = k;
    }

    @Override
    public void run() {

        while (true) {
            while (!kitchen.hasOrder()) {
                synchronized (kitchen) {
                    try {
                        kitchen.wait(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            System.out.println("Waiter got " + kitchen.getOrder());
            kitchen.clearOrder();
        }
    }
}