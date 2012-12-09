// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Rank;

/**
 * A poker hand typically has 5 cards from a deck of normal playing cards.
 * @author Barry Becker
 */
public class PokerHandScorer {

    /**
     * Constructor
     */
    public PokerHandScorer() {}

    /**
     * Calculate a score for this poker hand so it can be compared with others.
     * Need to take into account the suit and rank when determining the score to break ties if 2 hands are the same
     * @return score for the hand
     */
    public float getScore(PokerHand hand) {

        PokerHandType type = determineType(hand);
        return type.odds() * 1000 + type.getTieBreakerScore(hand);
    }

    /**
     * @param hand the hand to determine the type of
     * @return the type of hand that we have. e.g. straight or flush
     */
    PokerHandType determineType(PokerHand hand) {

        // first sort the cards so its easier to tell what we have.
        hand.sort();

        // first check for a royal flush. If it exists return it, else check for straight flush, and so on.
        for (PokerHandType handType : PokerHandType.values()) {
            if (hasA(handType, hand)) {
                return handType;
            }
        }
        return PokerHandType.HIGH_CARD;
    }

    /**
     * @param handType type of poker hand to check for.
     * @return true if we have the specified handType.
     */
    boolean hasA(PokerHandType handType, PokerHand hand) {
        boolean hasStraight = hand.hasStraight();
        boolean hasFlush = hand.hasFlush();

        boolean hasPair = hand.hasNofaKind(2);

        switch (handType) {
            case FIVE_OF_A_KIND: return hand.hasNofaKind(5);
            case ROYAL_FLUSH: return (hasStraight && hasFlush && (hand.getLowestRank() == Rank.TEN));
            case STRAIGHT_FLUSH: return (hasStraight && hasFlush);
            case FOUR_OF_A_KIND: return hand.hasNofaKind(4);
            case FULL_HOUSE: return (hasPair && hand.hasNofaKind(3));
            case FLUSH: return hasFlush;
            case STRAIGHT: return hasStraight;
            case THREE_OF_A_KIND: return hand.hasNofaKind(3);
            case TWO_PAIR: return hasPair && hand.hasTwoPairs();
            case PAIR: return hasPair;
            case HIGH_CARD: return true;
        }
        return false;  // never reached
    }

}
