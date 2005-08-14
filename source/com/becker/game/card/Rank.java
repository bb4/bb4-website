package com.becker.game.card;

import java.util.Map;
import java.util.HashMap;

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
    private static final Map rankFromSymbol_ = new HashMap();

    static {
        for (Rank r : values()) {
            rankFromSymbol_.put(r.getSymbol(), r);
        }
    }

    private Rank(String symbol) {
        symbol_ = symbol;

    }

    public String getSymbol() {
        return symbol_;
    }

    public static Rank getRankForSymbol(String symbol) {
        return (Rank) rankFromSymbol_.get(symbol);
    }

}
