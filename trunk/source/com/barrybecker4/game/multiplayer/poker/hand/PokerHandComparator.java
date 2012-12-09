// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import java.util.*;

/**
 * Compares poker hands to see which is better. User for sorting player hands to find the best one.
 * @author Barry Becker
 */
public class PokerHandComparator implements Comparator<PokerHand> {

    private final PokerHandScorer scorer;

    /**
     * Constructor
     */
    public PokerHandComparator() {
        scorer = new PokerHandScorer();
    }

    /**
     * compare this poker hand to another
     * @return 1 if this hand is higher than the other hand, -1 if lower, else 0.
     */
    public int compare(PokerHand hand1, PokerHand hand2) {

        // first do a coarse comparison based on the type of the hand
        // if a tie, then look more closely
        float difference = scorer.determineType(hand1).odds() - scorer.determineType(hand2).odds();
        if (difference > 0) {
            return 1;
        } else if (difference < 0) {
            return -1;
        } else {
            return compareHandsOfEqualType(hand1, hand2);
        }
    }

    /**
     * In case of tie breaker, apply this method.
     * @return return 1 if hand1 greater than hand2, 0 if equal, -1 if less.
     */
    int compareHandsOfEqualType(PokerHand hand1, PokerHand hand2) {

        assert(scorer.determineType(hand1) == scorer.determineType(hand2));
        int diff =
                scorer.determineType(hand1).getTieBreakerScore(hand1)
              - scorer.determineType(hand2).getTieBreakerScore(hand2);

        if ( diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }

}
