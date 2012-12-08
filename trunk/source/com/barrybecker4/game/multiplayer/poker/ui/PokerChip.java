/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui;

import java.awt.*;

/**
 * @author Barry Becker
 *
 */
public enum PokerChip {

    FIVE_HUNDRED("500 Dollar Chip", 500, new Color(220, 0, 200), 1000, 500),
    TWENTY_FIVE("25 Dollar Chip", 25, new Color(0, 200, 10), 300, 200),
    TEN("Ten Dollar Chip", 10, new Color(0, 80, 255), 100, 70),
    FIVE("Five Dollar Chip", 5, new Color(200, 0, 0), 20, 15),
    ONE("One Dollar Chip", 1, new Color(255, 200, 0), 1, 1);

    private final String label_;

    // occurs one in this many hands
    private final int value_;

    private final Color color_;

    // only have chips of this type if highThresh is exceeeded.
    // lowThresh tells what amount to convert to these kinds of chips
    private final int highThresh_;
    private final int lowThresh_;


    PokerChip(String label, int value, Color color, int highThresh, int lowThresh) {
        label_ = label;
        value_ = value;
        color_ = color;
        highThresh_ = highThresh;
        lowThresh_ = lowThresh;
    }

    public String getLabel() {
        return label_;
    }

    int getValue() {
        return value_;
    }

    public Color getColor() {
        return color_;
    }


    /**
     * the integer array returned has an entry for each type of chip
     * @param amount of cash to convert to chips
     * @return array of numbers of chips of each type
     */
    public static int[] getChips(int amount) {
        int remainder = amount;
        int numberOfChipTypes = PokerChip.values().length;
        int[] numChips = new int[numberOfChipTypes];

        for (PokerChip chipType : PokerChip.values()) {
            assert (remainder >= 0) : "remainder "+remainder+" is negative";
            if (remainder >= chipType.highThresh_) {
                int amtToConvert = remainder - (remainder % chipType.lowThresh_);
                int nChips = amtToConvert / chipType.getValue();
                remainder -=  nChips * chipType.getValue();
                numChips[chipType.ordinal()] = nChips;
            }
        }
        assert (remainder == 0) : "remainder was "+remainder+", not 0 as expected";
        return numChips;
    }

}
