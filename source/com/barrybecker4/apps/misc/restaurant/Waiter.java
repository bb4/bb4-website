package com.barrybecker4.apps.misc.restaurant;

class Waiter extends Thread {

    private final Kitchen kitchen;

    public Waiter(Kitchen k) {
        kitchen = k;
    }

    @Override
    public void run() {

        while (true) {
            while (kitchen.getOrder() == null) {
                synchronized (kitchen) {
                    try {
                        kitchen.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            System.out.println("Waitperson got " + kitchen.getOrder());
            kitchen.order = null;
        }
    }
}