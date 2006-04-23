package com.becker.puzzle.redpuzzle;

/**
 * Its that little thing on the edge of a piece that allows it to connect to another.
 * It can either be an inny or an outty like your belly button.
 * Immutable.
 *
 * @author Barry Becker
 */
public class Nub {

    /**
     * the complete set of nubs that you can have.
     */
    public static final Nub INNY_SPADE = new Nub(Suit.SPADE, false);
    public static final Nub OUTY_SPADE = new Nub(Suit.SPADE, true);

    public static final Nub INNY_CLUB = new Nub(Suit.CLUB, false);
    public static final Nub OUTY_CLUB = new Nub(Suit.CLUB, true);

    public static final Nub INNY_HEART = new Nub(Suit.HEART, false);
    public static final Nub OUTY_HEART = new Nub(Suit.HEART, true);

    public static final Nub INNY_DIAMOND = new Nub(Suit.DIAMOND, false);
    public static final Nub OUTY_DIAMOND = new Nub(Suit.DIAMOND, true);


    private Suit suit_;
    private boolean isOuty_;

    public Nub(Suit suit, boolean isOuty) {
        suit_ = suit;
        isOuty_ = isOuty;
    }

    /**
     * @return the char symbol used to represent this nub's suit.
     */
    public char getSuitSymbol() {
       return getSuit().getSymbol();
    }

    /**
     * @return the suit shape of the nub.
     */
    private Suit getSuit() {
        return suit_;
    }

    /**
     * @return true if an outward facing nub.
     */
    public boolean isOuty() {
        return isOuty_;
    }

    /**
     * @param nub other nub to try and fit with.
     * @return true if the nubs fit together.
     */
    public boolean fitsWith(Nub nub) {
        boolean suitMatch = this.getSuit() == nub.getSuit();
        boolean nubMatch =  this.isOuty() != nub.isOuty();
        return suitMatch && nubMatch;
    }

    /**
     * @return nice readable string representation of a nub
     */
    public String toString() {
       return "Nub: "+ (isOuty()?"outy":"inny") + ' ' +getSuit();
    }


    /**
     * Determines the shape of the nub.
     */
    private enum Suit {

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
}
