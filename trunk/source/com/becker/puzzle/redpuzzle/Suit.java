package com.becker.puzzle.redpuzzle;

/**
 * @author Barry Becker
 */
public enum Suit {

    SPADE('S'),
    CLUB('C'),
    HEART('H'),
    DIAMOND('D');

    private final char symbol_;

    private Suit(char symbol) {
        symbol_ = symbol;

    }

    /**
     * @return the character symbol associated with this Suit
     */
    public char getSymbol() {
        return symbol_;
    }

}
