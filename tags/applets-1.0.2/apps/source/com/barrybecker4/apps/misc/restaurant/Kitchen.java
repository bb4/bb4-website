package com.barrybecker4.apps.misc.restaurant;


/**
 * The kitchen contains the order that the chef produces and the waiter consumes.
 * Synchronize on this.
 */
public class Kitchen {

    private FoodSupply foodSupply;

    /**
     * must be volatile or we can have deadlock.
     * or alternatively synchronize all the methods in this class.
     */
    private volatile Order order;

    Kitchen(int supply) {
        foodSupply = new FoodSupply(supply);
    }

    boolean hasOrder() {
        return order != null;
    }

    Order getOrder() {
        return order;
    }

    void clearOrder() {
        order = null;
    }

    void createOrder() {
        order = new Order(getNewOrderId());
    }

    private synchronized int getNewOrderId() {

        if (!foodSupply.hasFood()) {
            System.out.println("Out of food, closing");
            System.exit(0);
        }
        foodSupply.takeSomeFood();
        return foodSupply.getRemainingAmount();
    }

}
