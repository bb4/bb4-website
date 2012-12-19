package com.barrybecker4.apps.misc.restaurant;

/**
 * The producer-consumer approach to thread cooperation
 * From Bruce Eckel's Thinking in Java book.
 */
class Order {

    private static final int INITIAL_FOOD_SUPPLY = 10;
    private static int currentFoodSupply = INITIAL_FOOD_SUPPLY;
    private int count = INITIAL_FOOD_SUPPLY - --currentFoodSupply;

    public Order() {
        if (currentFoodSupply == 0) {
            System.out.println("Out of food, closing");
            System.exit(0);
        }
    }

    public String toString() {
        return "Order " + count;
    }
}