/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker;



/**
 * User: Barry Becker
 * Date: Feb 26, 2005
 * Time: 6:16:43 AM
 */
public enum PokerHandEnum {

    // note: five of a kind can only happen if using wild cards
    FIVE_OF_A_KIND("Five of a Kind", 749740),
    ROYAL_FLUSH("Royal Flush", 649740),
    STRAIGHT_FLUSH("Straight Flush", 72192),
    FOUR_OF_A_KIND("Four of a Kind", 4164),
    FULL_HOUSE("Full House", 693),
    FLUSH("Flush", 508),
    STRAIGHT("Straight", 254),
    THREE_OF_A_KIND("Three of a Kind", 40),
    TWO_PAIR("Two Pair", 20),
    PAIR("Pair", 1.37f),
    HIGH_CARD("High Card", 1);


    private final String label_;

    // occurs one in this many hands
    private final float odds_;


    PokerHandEnum(String label, float odds) {
        label_ = label;
        odds_ = odds;
    }

    public String label()   { return label_; }

    public float odds() { return odds_; }

    public String toString() {
        return label_;
    }


    public int getTieBreakerScore(PokerHand hand) {
        int numCards = hand.size();
        int score = 0;
        for (int i = numCards-1; i>=0; i--) {
            score = score * numCards + hand.getCards().get(i).rank().ordinal();
        }
        return score;
    }

}

