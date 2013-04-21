package com.barrybecker4.apps.misc.restaurant;

/**
 * The producer-consumer approach to thread cooperation
 * From Bruce Eckel's Thinking in Java book.
 */
class Order {

    static int id;

    public Order(int orderId) {
       id = orderId;
    }

    public String toString() {
        return "Order " + id;
    }
}