/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Card;
import com.barrybecker4.game.card.CardComparator;
import com.barrybecker4.game.card.Deck;
import com.barrybecker4.game.card.Rank;
import com.barrybecker4.game.card.Suit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static com.barrybecker4.game.card.Rank.*;

/**
 * A poker hand typically has 5 cards from a deck of normal playing cards.
 * @author Barry Becker
 */
public class PokerHand implements Serializable, Comparable<PokerHand> {

    /** internal list of cards in the hand. Always sorted from high to low */
    private final List<Card> hand;

    private MatchMap matchMap;
    private boolean faceUp;

    /** scores the hand for comparison purposes with other hands */
    private PokerHandScorer scorer;

    /**
     * Constructor
     * @param hand  the initial poker hand (not necessarily 5 cards)
     */
    public PokerHand(final List<Card> hand) {
        this.hand = hand;
        faceUp = false;
        scorer = new PokerHandScorer();
        initialize();
    }

    /**
     * Deal numCards from the deck and make the poker hand from that.
     * @param deck to deal from
     * @param numCards number of cards to deal from the deck
     */
    public PokerHand(final Deck deck, int numCards) {
        this(dealCards(deck, numCards));
    }

    /** @return a copy so the client cannot change our state out from under us. */
    public List<Card> getCards() {
        return new ArrayList<Card>(hand);
    }

    /** @return the rank of the lowest ranked card. Assumed that the cards are sorted from high to low. */
    Rank getLowestRank() {
        return hand.get(hand.size()-1).rank();
    }

    private void initialize() {
        assert (!hand.isEmpty()): "You can't have an empty poker hand!";
        sort();
        matchMap = new MatchMap(hand);
    }

    /**
     * whether or not the cards are showing to the rest of the players
     * @param faceUp true if the card is face up.
     */
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    /**
     * Calculate a score for this poker hand so it can be compared with others
     * need to take into account the suit and rank when determining the score to break ties if 2 hands are the same
     * @return the score for the hand.
     */
    public float getScore() {
        return scorer.getScore(this);
    }

    /** sort the cards from low to high by rank. */
    void sort() {
        Collections.sort(hand, new CardComparator());
        Collections.reverse(hand);
    }

    /**
     * @return true if there are 5 cards are of the same suit
     */
    boolean hasFlush() {

        int ct = 0;
        Suit suit = hand.get(0).suit();
        for (Card c : hand) {
            if (c.suit() == suit)
                ct++;
        }
        return (ct >=5);
    }

    /**
     * @return true if there is a sequence of 5 cards
     */
    boolean hasStraight() {

        boolean acePresent = getHighCard().rank() == ACE;
        int run = 1;
        int start = acePresent ? 1 : 0;
        Rank[] ranks = Rank.values();
        Rank lastRank = hand.get(start++).rank();
        Rank highRank = lastRank;

        for (Card card : hand.subList(start, size())) {

            int nextRank = lastRank.ordinal() - 1;
            if (nextRank >= 0 && card.rank() == ranks[nextRank]) {
                run++;
            }
            else {
                run = 1;  // start over
            }
            lastRank = card.rank();
        }
        return run >= 5
                || (acePresent && run >= 4 && (highRank == FIVE || highRank == KING));
    }

    /**
     * @return true if there is exactly N of a certain rank in the hand
     * (note: there is not 2 of a kind if there is 4 of a kind)
     */
    boolean hasNofaKind(int num) {
        return matchMap.hasNofaKind(num);
    }

    /**
     * @return the rank of the n of a kind specified. Error thrown if it does not have n of a kind.
     * (note: the is not 2 of a kind if there is 4 of a kind)
     * (note: if there is more than 1 n of a kind the highest rank is returned)
     */
    List<Rank> getRankOfNofaKind(int num) {
        return matchMap.getRankOfNofaKind(num);
    }

    /**
     * @return the highest valued card in this hand
     */
    public Card getHighCard() {
        return hand.get(0);
    }

    public Card getSecondaryHighCard() {
        return matchMap.getSecondaryHighCard(hand);
    }

    /**
     * (note: there are not 2 pairs if there is a full house)
     * @return true if there is exactly 2 pairs
     */
    boolean hasTwoPairs() {
        return matchMap.hasTwoPairs();
    }

    public int size() {
        return hand.size();
    }

    private static List<Card> dealCards(final Deck deck, int numCards) {
        List<Card> hand = new ArrayList<Card>();
        assert(numCards <= deck.size()) : "you can't deal more cards than you have in the deck";
        for (int i = 0; i < numCards; i++)  {
            hand.add(deck.remove(0));
        }
        return hand;
    }

    public String toString() { return "[" + hand + "]"; }

    /**
     * compare this poker hand to another
     * @return 1 if this hand is higher than the other hand, -1 if lower, else 0.
     */
    @Override
    public int compareTo(PokerHand hand) {
        float diff = scorer.getScore(this) - scorer.getScore(hand);
        return diff > 0 ? 1 : (diff < 0 ? -1 :0);
    }
}
