package com.becker.game.multiplayer.poker.ui;

import java.awt.*;

/**
 * @author Barry Becker
 *
 */
public enum PokerChip {

    FIVE_HUNDRED("500 Dollar Chip", 500, new Color(200, 0, 180), 1000, 500),
    TWENTY_FIVE("25 Dollar Chip", 25, Color.GREEN, 300, 200),
    TEN("Ten Dollar Chip", 10, Color.BLUE, 100, 70),
    FIVE("Five Dollar Chip", 5, Color.RED, 20, 15),
    ONE("One Dollar Chip", 1, Color.WHITE, 1, 1);

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

    public int getValue() {
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
