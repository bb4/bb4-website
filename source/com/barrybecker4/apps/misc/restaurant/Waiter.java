package com.barrybecker4.apps.misc.restaurant;

class Waiter extends Thread {

    private Restaurant restaurant;

    public Waiter(Restaurant r) {
        restaurant = r;
        start();
    }

    @Override
    public void run() {
        while (true) {
            while (restaurant.order == null)
                synchronized(this) {
                try {
                     wait();
                } catch(InterruptedException e) {
                     throw new RuntimeException(e);
                }
            }
            System.out.println("Waitperson got " + restaurant.order);
            restaurant.order = null;
        }
    }
}