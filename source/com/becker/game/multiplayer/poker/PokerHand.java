package com.becker.game.multiplayer.poker;

import com.becker.game.card.Card;
import com.becker.game.card.Rank;

import java.util.*;

/**
 * User: Barry Becker
 * Date: Feb 26, 2005
 */
public class PokerHand {

    private List<Card> hand_;
    private Map matchMap_;
    private boolean faceUp_;

    /**
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

    public void addCard(Card card) {
        hand_.add(card);
        // always keep the hand sorted
        update();
    }

    public List<Card> getCards() {
        // return a copy so the client cannot change our state out from under us.
        ArrayList<Card> cards = new ArrayList<Card>(hand_);
        return cards;
    }

    /**
     * @param card  the card you wish to discard from your hand.
     */
    public void removeCard(Card card) {
        hand_.remove(card);
        update();
    }

    private void update() {
        assert (!hand_.isEmpty()): "You can't have an empty poker hand!";
        sort();
        matchMap_  = getMatchMap();
    }

    /**
     * whether or not the cards are showing to the rest of the players
     * @param faceUp
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
        // need to take into account the suit and rank when determining the score to break ties if 2 handa are the same
        float score = determineType().odds();
        return score;
    }

    private void sort() {
        Comparator<Card> comparator = new CardComparator();
        Collections.sort(hand_, comparator);
    }


    public PokerHandEnum determineType() {

        // first sort the cards so its easier to tell wha twe have.
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
        if (hand_.get(0).rank() == Rank.ACE) {
            if (hand_.get(1).rank() == Rank.DEUCE) {
                rank = hand_.get(1).rank();
                run = 2;
            }
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
     * (note: the is not 2 of a kind if there is 4 of a kind)
     */
    private boolean hasNofaKind(int num) {

        Collection values = matchMap_.values();
        for (Object value : values) {
            if (((Integer)value).intValue() == num)
                return true;
        }
        return false;
    }

    /**
     * returns true if there is exactly 2 pairs
     * (note: there are not 2 pairs if there is a full house)
     */
    private boolean hasTwoPairs() {

        Collection values = matchMap_.values();
        int numPairs = 0;
        for (Object value : values) {
            if (((Integer)value).intValue() == 2)
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
    private Map getMatchMap() {
        Map map = new HashMap();

        for (Card c : hand_) {
            Integer num = (Integer)map.get(c.rank());
            if (num != null)  {
               map.put(c.rank(), num.intValue()+1);
            }
            else
               map.put(c.rank(), 1);
        }
        return map;
    }

    public String toString() { return "[" + hand_ + "]"; }

    /**
     * inner class used to define a sord order on cards in a poker hane.
     */
    private class CardComparator implements Comparator {

        public int compare(Object c1, Object c2) {

            Card card1 = (Card)c1;
            Card card2 = (Card)c2;

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

        ArrayList deck = Card.newDeck();
        System.out.println("deck="+deck+ "\n\n");


        // test out a bunch of hands
        for (int i=5; i<10; i++) {
            PokerHand hand = new PokerHand(deck, i);
             System.out.println("Poker Hand "+hand+" \n has a  "+hand.determineType() +"!      score="+hand.getScore());
        }

    }

}
