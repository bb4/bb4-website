/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Card;
import com.barrybecker4.game.card.Rank;
import com.barrybecker4.game.card.Suit;
import com.barrybecker4.game.card.Deck;

import java.io.Serializable;
import java.util.*;

/**
 * A poker hand typically has 5 cards from a deck of normal playing cards.
 * @author Barry Becker
 */
public class PokerHand implements Serializable {

    private static final long serialVersionUID = 1;

    /** internal list of cards in the hand. Always sorted */
    private final List<Card> hand;

    private Map matchMap;
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
        update();
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

    /** @return the rank of the lowest ranked card. Assumed that the cards are sorted */
    Rank getLowestRank() {
        return hand.get(0).rank();
    }

    private void update() {
        assert (!hand.isEmpty()): "You can't have an empty poker hand!";
        sort();
        matchMap = computeMatchMap();
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
        CardComparator comparator = new CardComparator();
        Collections.sort(hand, comparator);
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
     * returns true if there is a sequence of 5 cards
     */
    boolean hasStraight() {

        Rank rank = hand.get(0).rank();
        int run = 1;
        int start = 1;
        // special case for when ace is the low card in a straight
        if ((hand.get(0).rank() == Rank.ACE) &&  (hand.get(1).rank() == Rank.DEUCE)) {
            rank = hand.get(1).rank();
            run = 2;
        }
        for (Card c : hand.subList(start, size())) {
            Rank[] ranks = Rank.values();
            int nextRank = rank.ordinal()+1;
            if (nextRank < ranks.length &&  c.rank() == ranks[nextRank]) {
                run++;
            }
            else {
                run = 1;  // start over
            }
            rank = c.rank();
         }
        return run >= 5;
    }

    /**
     * @return true if there is exactly N of a certain rank in the hand
     * (note: there is not 2 of a kind if there is 4 of a kind)
     */
    boolean hasNofaKind(int num) {

        Collection values = matchMap.values();
        for (Object value : values) {
            if ((Integer) value == num)
                return true;
        }
        return false;
    }

    /**
     * @return the rank of the n of a kind specified, null if does not have n of a kind.
     * (note: the is not 2 of a kind if there is 4 of a kind)
     * (note: if there is more than 1 n of a kind the highest rank is returned)
     */
    private Rank getRankOfNofaKind(int num) {

        Set entries = matchMap.entrySet();
        Rank highestRank = null;
        for (Object entry : entries) {
            Map.Entry e = (Map.Entry) entry;
            if (((Integer)e.getValue()) == num) {
                Rank r = (Rank)e.getKey();
                if (highestRank == null || (r.ordinal() > highestRank.ordinal())) {
                    highestRank = r;
                }
            }
        }

        return highestRank;
    }

    /**
     * @return  the highest valued card in this hand
     */
    public Card getHighCard() {
        return hand.get(hand.size()-1);
    }

    /**
     * (note: there are not 2 pairs if there is a full house)
     * @return true if there is exactly 2 pairs
     */
    boolean hasTwoPairs() {

        Collection values = matchMap.values();
        int numPairs = 0;
        for (Object value : values) {
            if ((Integer) value == 2)
                numPairs++;
        }
        return (numPairs == 2);
    }

    public int size() {
        return hand.size();
    }

    /**
     * @return a map which has an entry for each card rank represented in the hand and its associated count.
     */
    private Map computeMatchMap() {
        Map<Rank, Integer> map = new HashMap<Rank, Integer>();

        for (Card c : hand) {
            Integer num = map.get(c.rank());
            if (num != null)  {
               map.put(c.rank(), num+1);
            }
            else
               map.put(c.rank(), 1);
        }
        return map;
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
     * inner class used to define a sort order on cards in a poker hand.
     */
    private static class CardComparator implements Comparator<Card> {

        public int compare(Card card1, Card card2) {

            if (card1.rank() == card2.rank())   {
                return card1.suit().ordinal() - card2.suit().ordinal();
            }
            else {
                return card1.rank().ordinal() - card2.rank().ordinal();
            }
        }
    }

    /**
     * Test out the poker hand functionality
     * @param args
     */
    public static void main(String[] args) {

        Deck deck = new Deck();
        System.out.println("deck="+deck+ "\n\n");

        // test out a bunch of hands
        for (int i=5; i<10; i++) {
            PokerHand hand = new PokerHand(deck, i);
             System.out.println("Poker Hand " + hand + "  score="+hand.getScore());
        }
    }
}
