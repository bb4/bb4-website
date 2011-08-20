package com.becker.apps.misc.givechange;

import com.becker.common.format.FormatUtil;
import com.becker.common.util.Input;

import javax.xml.soap.Name;
import java.io.IOException;


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
