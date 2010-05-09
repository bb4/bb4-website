package com.becker.game.card;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Barry Becker
 */
public enum Suit {

    HEARTS("H"),
    DIAMONDS("D"),
    CLUBS("C"),
    SPADES("S");
 
    private final String symbol_;
    private static final Map<String, Suit> suitFromSymbol_ = new HashMap<String, Suit>();

    static {
        for (Suit r : values()) {
            suitFromSymbol_.put(r.getSymbol(), r);
        }
    }

    Suit(String symbol) {
        symbol_ = symbol;
    }

    public String getSymbol() {
        return symbol_;
    }

    public static Suit getSuitForSymbol(String symbol) {
        return suitFromSymbol_.get(symbol);
    }

}