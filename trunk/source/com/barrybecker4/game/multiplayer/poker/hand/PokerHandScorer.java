// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Card;
import com.barrybecker4.game.card.Rank;

import java.util.List;

/**
 * A poker hand typically has 5 cards from a deck of normal playing cards.
 * @author Barry Becker
 */
public class PokerHandScorer {

    /** The weighting to give the odd (1/probability) of getting the hands basic type (like a straight) */
    private static final float ODDS_WEIGHT = 1000.0f;

    /**
     * The weighting to give the cards that make up the type.
     * For example if 3 kings then the we will add TYPE_RANK_WEIGHT * the rank of king
     * If full house the type rank will be the rank of the card which appears three times plus the
     * rank of the pair card.
     */
    private static final float TYPE_RANK_WEIGHT = 10.0f;

    /**
     * Only matters if the Primary score is a tie. The secondary cards are the cards not part of the
     * primary hand. For example, if you have  three of a kind (QD QH QC 4D 6C)
     * then the other two cards (4D 6c) are the secondary cards.  Only the highest ranked secondary
     * card is used because it is impossible to have two cards with exactly the same value when suit is
     * used to break ties (unless wild cards are used). I realize that I may be using a non-standard
     * suit ordering (H, D, C, S) but its my game and I will do what I want. This is the same ordering used
     * in 13 (Vietnamese card game).
     */
    private static final float SECONDARY_CARD_WEIGHT = 0.1f;

    /**
     * Constructor
     */
    public PokerHandScorer() {}

    /**
     * Calculate a score for this poker hand so it can be compared with others.
     * Takes into account the suit and rank when determining the score to break ties if 2 hands are the same.
     * Note that many systems do not differentiate based on suit. I do to prevent ties.
     * First do a coarse comparison based on the type of the hand. If a tie, then look more closely
     * @return score for the hand
     */
    public float getScore(PokerHand hand) {

        PokerHandType type = determineType(hand);

        float score = ODDS_WEIGHT * type.odds()
                + TYPE_RANK_WEIGHT * getTypeRank(type, hand);

        Card secondaryHighCard = hand.getSecondaryHighCard();
        if (!type.equals(PokerHandType.HIGH_CARD) && secondaryHighCard != null) {
            int value = secondaryHighCard.rank().ordinal() * 4 + secondaryHighCard.suit().ordinal();
            score += SECONDARY_CARD_WEIGHT * value;
        }
        return score;
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

    /**
     * Used to differentiate between hands of the same type - like two full house hands
     * or two hands that are both three of a kind.
     */
    private int getTypeRank(PokerHandType handType, PokerHand hand) {

        Card highCard = hand.getHighCard();
        int highCardRank = highCard.rank().ordinal();
        int highSuit = highCard.suit().ordinal();

        switch (handType) {
            case FIVE_OF_A_KIND: return highCardRank;
            case ROYAL_FLUSH: return 0;   // there is only one sort of royal flush
            case STRAIGHT_FLUSH: return highCardRank * 4 + highSuit;
            case FOUR_OF_A_KIND: return hand.getRankOfNofaKind(4).get(0).ordinal();
            case FULL_HOUSE: return 14 * hand.getRankOfNofaKind(3).get(0).ordinal()
                                      +  hand.getRankOfNofaKind(2).get(0).ordinal();
            case FLUSH:
            case STRAIGHT: return 4 * highCardRank + highSuit;
            case THREE_OF_A_KIND: return hand.getRankOfNofaKind(3).get(0).ordinal();
            case TWO_PAIR:
                List<Rank> pairRanks = hand.getRankOfNofaKind(2);
                return 14 * pairRanks.get(0).ordinal() + pairRanks.get(1).ordinal();
            case PAIR: return hand.getRankOfNofaKind(2).get(0).ordinal();
            case HIGH_CARD: return highCardRank * 4 + highSuit;
        }
        assert false;
        return 0;
    }
}
