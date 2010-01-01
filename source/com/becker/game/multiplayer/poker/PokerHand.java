package com.becker.game.multiplayer.poker;

import com.becker.game.card.Card;
import com.becker.game.card.Rank;

import java.util.*;

/**
 * @author Barry Becker
 */
public class PokerHand implements Comparable {

    private List<Card> hand_;
    private Map matchMap_;
    private boolean faceUp_;

    /**
     * Constructor
     * @param hand  the initial poker hand  (not necessarily 5 cards)
     */
    public PokerHand(List<Card> hand) {
        hand_ = hand;
        update();
    }

    /**
     * @param deck to deal from
     * @param numCards number of cards to deal from the deck
     */
    public PokerHand(List<Card> deck, int numCards) {
        // deal numCards from the deck and make the poker hand from that
        hand_ = new ArrayList<Card>();
        faceUp_ = false;
        assert(numCards <= deck.size()) : "you can't deal more cards than you have in the deck";
        for (int i = 0; i < numCards; i++)  {
            hand_.add(deck.remove(0));
        }
        update();
    }

    public List<Card> getCards() {
        // return a copy so the client cannot change our state out from under us.
        return new ArrayList<Card>(hand_);
    }

    private void update() {
        assert (!hand_.isEmpty()): "You can't have an empty poker hand!";
        sort();
        matchMap_ = computeMatchMap();
    }

    /**
     * whether or not the cards are showing to the rest of the players
     * @param faceUp true if the card is face up.
     */
    public void setFaceUp(boolean faceUp) {
        faceUp_ = faceUp;
    }

    public boolean isFaceUp() {
        return faceUp_;
    }

    /**
     *  Calculate a score for this poker hand so it can be compared with others
     * @return
     */
    public float getScore() {
        // need to take into account the suit and rank when determining the score to break ties if 2 hands are the same
        return determineType().odds() * 1000 + this.determineType().getTieBreakerScore(this);
    }

    private void sort() {
        CardComparator comparator = new CardComparator();
        // sort the cards from low to high
        Collections.sort(hand_, comparator);
    }


    public PokerHandEnum determineType() {

        // first sort the cards so its easier to tell what we have.
        sort();

        // first check for a royal flush. If it exists return it, else check for straight flush, and so on.
        for (PokerHandEnum handType : PokerHandEnum.values()) {
            if (hasA(handType)) {
                return handType;
            }
        }
        return PokerHandEnum.HIGH_CARD;
    }

    public boolean hasA(PokerHandEnum handType) {
        boolean hasStraight = hasStraight();
        boolean hasFlush = hasFlush();

        boolean hasPair = hasNofaKind(2);

        switch (handType) {
            case FIVE_OF_A_KIND: return hasNofaKind(5);
            case ROYAL_FLUSH: return (hasStraight && hasFlush && (hand_.get(0).rank() == Rank.TEN));
            case STRAIGHT_FLUSH: return (hasStraight && hasFlush);
            case FOUR_OF_A_KIND: return hasNofaKind(4);
            case FULL_HOUSE: return (hasPair && hasNofaKind(3));
            case FLUSH: return hasFlush;
            case STRAIGHT: return hasStraight;
            case THREE_OF_A_KIND: return hasNofaKind(3);
            case TWO_PAIR: return hasPair && hasTwoPairs();
            case PAIR: return hasPair;
            case HIGH_CARD: return true;
            default: assert false;
        }
        return false;   // never reached
    }

    /**
     * returns true if there are 5 cards are of the same suit
     */
    private boolean hasFlush() {

        int ct = 0;
        Card.Suit suit = hand_.get(0).suit();
        for (Card c : hand_) {
            if (c.suit() == suit)
                ct++;
        }
        return (ct >=5);
    }

    /**
     * returns true if there is a sequence of 5 cards
     */
    private boolean hasStraight() {

        Rank rank = hand_.get(0).rank();
        int run = 1;
        int start = 1;
        // special case for when ace is the low card in a straight
        if ((hand_.get(0).rank() == Rank.ACE) &&  (hand_.get(1).rank() == Rank.DEUCE)) {
            rank = hand_.get(1).rank();
            run = 2;
        }
        for (Card c : hand_.subList(start, size())) {
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
     * returns true if there is exactly N of a certain rank in the hand
     * (note: there is not 2 of a kind if there is 4 of a kind)
     */
    private boolean hasNofaKind(int num) {

        Collection values = matchMap_.values();
        for (Object value : values) {
            if ((Integer) value == num)
                return true;
        }
        return false;
    }

    /**
     * returns the rank of the n of a kind specified, null if does not have n of a kind.
     * (note: the is not 2 of a kind if there is 4 of a kind)
     * (note: if there is more than 1 n of a kind the highest rank is returned)
     */
    protected Rank getRankOfNofaKind(int num) {

        Set entries = matchMap_.entrySet();
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
        return hand_.get(hand_.size()-1);
    }

    /**
     * @param c  card to evaluate
     * @return the value of the card in terms of poker
     * (note value is always less than 100 because the ace of hearts is 4*12 + 3 = 51
     *
     * -- dont use suit to break ties - its wrong for poker
    public static int getValue(Card c)  {
         return 4 * c.rank().ordinal() + c.suit().ordinal();
    }  */

    /**
     * (note: there are not 2 pairs if there is a full house)
     * @return true if there is exactly 2 pairs
     */
    private boolean hasTwoPairs() {

        Collection values = matchMap_.values();
        int numPairs = 0;
        for (Object value : values) {
            if ((Integer) value == 2)
                numPairs++;
        }
        return (numPairs == 2);
    }

    public int size() {
        return hand_.size();
    }


    /**
     * @return a map which has an entry for each card rank represented in the hand and its associated count.
     */
    private Map computeMatchMap() {
        Map<Rank, Integer> map = new HashMap<Rank, Integer>();

        for (Card c : hand_) {
            Integer num = map.get(c.rank());
            if (num != null)  {
               map.put(c.rank(), num+1);
            }
            else
               map.put(c.rank(), 1);
        }
        return map;
    }


    /**
     * compare this poker hand to another
     * @param otherHand
     * @return 1 if this hand is higher than the other hand, -1 if lower, else 0.
     */
    public int compareTo(Object otherHand) {
        PokerHand hand = (PokerHand) otherHand;
        // first do a coarse comparison based on the type of the hand
        // if a tie, then look more closely
        float difference = determineType().odds() - hand.determineType().odds();
        if (difference > 0) {
            return 1;
        } else if (difference < 0) {
            return -1;
        } else {
            return compareHandsOfEqualType(this, hand);
        }
    }

    /**
     *
     * @param hand1
     * @param hand2
     * @return  return 1 if hand1 greater than hand2, 0 if equal, -1 if less.
     */
    public static int compareHandsOfEqualType(PokerHand hand1, PokerHand hand2) {
        assert(hand1.determineType() == hand2.determineType());
        int diff = hand1.determineType().getTieBreakerScore(hand1) - hand2.determineType().getTieBreakerScore(hand2);
        if ( diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else{
            return 0;
        }
    }

    public String toString() { return "[" + hand_ + "]"; }

    /**
     * inner class used to define a sord order on cards in a poker hane.
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

        List<Card> deck = Card.newDeck();
        System.out.println("deck="+deck+ "\n\n");


        // test out a bunch of hands
        for (int i=5; i<10; i++) {
            PokerHand hand = new PokerHand(deck, i);
             System.out.println("Poker Hand "+hand+" \n has a  "+hand.determineType() +"!      score="+hand.getScore());
        }
    }
}
