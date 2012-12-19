package com.barrybecker4.apps.misc.restaurant;

/**
 * The producer-consumer approach to thread cooperation
 * From Bruce Eckel's Thinking in Java book.
 */
public class Restaurant {

    Order order;

    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant();
        Waiter waitPerson = new Waiter(restaurant);
        new Chef(restaurant, waitPerson);
    }
}
