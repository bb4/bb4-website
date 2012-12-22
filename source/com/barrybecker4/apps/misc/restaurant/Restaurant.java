package com.barrybecker4.apps.misc.restaurant;

/**
 * The producer-consumer approach to thread cooperation.
 * The restaurant uses multiple threads for chef and wator.
 * Its a good example of using wait and notify.
 * Derived from Bruce Eckel's Thinking in Java book.
 */
public class Restaurant {

    public static void main(String[] args) {

        Kitchen kitchen = new Kitchen(10);

        new Chef(kitchen).start();
        new Waiter(kitchen).start();
    }

    private Restaurant() {}
}
