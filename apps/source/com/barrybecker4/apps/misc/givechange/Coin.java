/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.givechange;

/**
 * An enum representing the different coins.
 *
 * @author Barry Becker
 */
public enum Coin {

    PENNY("penny", "pennies", 1),
    NICKEL("nickel", "nickels", 5),
    DIME("dime", "dimes", 10),
    QUARTER("quarter", "quarters", 25),
    HALF_DOLLAR("half dollar", "half dollars", 50);

    private String name;
    private String pluralName;
    private int worthInPennies;

    /**
     * private constructor for class with all static methods.
     */
    Coin(String name, String pluralName, int worthInPennies) {
        this.name = name;
        this.pluralName = pluralName;
        this.worthInPennies = worthInPennies;
    }

    public String getName() {
        return name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public int getWorthInPennies() {
        return worthInPennies;
    }

}
