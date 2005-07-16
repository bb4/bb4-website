package com.becker.game.card;

/**
 * User: Barry Becker
 * Date: Mar 5, 2005
 * Time: 8:36:01 AM
 */
public enum Rank {

    DEUCE("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A");


    private final String symbol_;

    private Rank(String symbol) {
        symbol_ = symbol;
    }

    public String getSymbol() {
        return symbol_;
    }

}
