package com.barrybecker4.apps.misc.restaurant;


/**
 * The kitchen contains the order that the chef produces and the waiter consumes.
 * Synchronize on this.
 */
public class Kitchen {

    private final int initialFoodSupply;
    private int foodSupply ;

    Order order;

    Kitchen(int supply) {
        initialFoodSupply = supply;
        foodSupply = initialFoodSupply;
    }

    Order getOrder() {
        return order;
    }

    void createOrder() {
        order = new Order(getNewOrderId());
    }

    private boolean hasFood() {
        return foodSupply > 0;
    }

    private void takeSomeFood() {
        foodSupply--;
    }

    private int getNewOrderId() {

        if (!hasFood()) {
            System.out.println("Out of food, closing");
            System.exit(0);
        }
        takeSomeFood();
        return initialFoodSupply - foodSupply;
    }
}
