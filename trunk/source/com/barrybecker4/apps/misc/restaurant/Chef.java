package com.barrybecker4.apps.misc.restaurant;

class Chef extends Thread {

    private final Kitchen kitchen;

    Chef(Kitchen k) {
        kitchen = k;
    }

    @Override
    public void run() {

        while (true) {
            if(kitchen.getOrder() == null) {
                kitchen.createOrder();
                System.out.print("Order up! ");
                synchronized(kitchen) {
                    kitchen.notify();
                }
            }
            try {
                sleep(10);
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}