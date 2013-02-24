// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.restaurant;


/**
 * The kitchen contains the order that the chef produces and the waiter consumes.
 * Synchronize on this.
 */
public class FoodSupply {

    private final int initialFoodAmount;
    private int foodAmount;

    FoodSupply(int supply) {
        initialFoodAmount = supply;
        foodAmount = initialFoodAmount;
    }

    boolean hasFood() {
        return foodAmount > 0;
    }

    void takeSomeFood() {
        foodAmount--;
    }

    int getRemainingAmount() {
        return initialFoodAmount - foodAmount;
    }
}
