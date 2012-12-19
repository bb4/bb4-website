package com.barrybecker4.apps.misc.restaurant;

/**
 * The producer-consumer approach to thread cooperation
 * From Bruce Eckel's Thinking in Java book.
 */
public class Restaurant {

    public static void main(String[] args) {

        Kitchen kitchen = new Kitchen(10);

        new Waiter(kitchen).start();
        new Chef(kitchen).start();
    }

    private Restaurant() {}
}
