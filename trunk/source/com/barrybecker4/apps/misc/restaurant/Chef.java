package com.barrybecker4.apps.misc.restaurant;

class Chef extends Thread {

    private Restaurant restaurant;
    private final Waiter waitPerson;

    public Chef(Restaurant r, Waiter w) {
        restaurant = r;
        waitPerson = w;
        start();
    }

    @Override
    public void run() {

        while (true) {
            if(restaurant.order == null) {
                restaurant.order = new Order();
                System.out.print("Order up! ");
                synchronized(waitPerson) {
                    waitPerson.notify();
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