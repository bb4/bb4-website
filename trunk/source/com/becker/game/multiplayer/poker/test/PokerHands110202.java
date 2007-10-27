package com.becker.game.multiplayer.poker.test;

import java.io.*;
import java.util.*;

/**
 * programming challenge to test which poker hands are better
 * see  http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
 * for details
 *
 * Note: when I submitted this it did not compile because they require 1.2 or before, but I am using 1.5....
 *
 * When running, enter 2 poker hands to be compared. There are exactly 5 cards in a hand.
 * Each card has the form <rank><suit>, so for example enter
 *  QC 3H 4D 5H 6H 3S 4C JC 6C 7C
 * The first 5 cards are for the first hand, then last five for the second.
 *
 * author Barry Becker
 */
class PokerHands110202 {

    private static final int MAX_LG = 255;


    /**
     *  for reading from stdin for the programmnig contests
     *
     * expects input to be somehting like   2H 3H 4H 5H 6H 3C 4C 5C 6C 7C
     *
     */
    static String readLine(InputStream stream)  // utility function to read from stdin
    {
        byte lin[] = new byte [MAX_LG];
        int lg = 0, car = -1;

        try
        {
            while (lg < MAX_LG)
            {
                car = stream.read();
                if ((car < 0) || (car == '\n')) break;
                lin [lg++] += car;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        if ((car < 0) && (lg == 0)) {
            return null;  // eof
        }
        return (new String (lin, 0, lg));
    }


    private void evaluateLine(String line) {
         if (line == null || line.length() <2) {
            return;
        }

        List<Card> blackCards = new ArrayList<Card>(5);
            List<Card> whiteCards = new ArrayList<Card>(5);

        StringTokenizer tokenizer = new StringTokenizer(line, " ");

        // the first five entries for for black the second five are for white
        int ct = 0;
        while (tokenizer.hasMoreElements()) {
            String cardToken = (String) tokenizer.nextElement();

            if (ct < 5)  {
                blackCards.add(new Card(cardToken));
            } else if (ct < 10)  {
                whiteCards.add(new Card(cardToken));
            }
            ct++;
        }

        PokerHand blackHand = new PokerHand(blackCards);
        PokerHand whiteHand = new PokerHand(whiteCards);

        int blackWin = blackHand.compareTo(whiteHand);
        if (blackWin > 0) {
            System.out.println("Black wins.");
        } else if (blackWin < 0)  {
            System.out.println("White wins.");
        } else {
            System.out.println("Tie.");
        }
    }


    public void evaluate(InputStream stream) throws IOException {

        String line;

        while ((line = readLine(stream)) != null) {
            evaluateLine(line);
        }
    }


    /**
     *  entry point.
     * @param args
     */
    public static void main(String args[])  {

        PokerHands110202 app = new PokerHands110202();

        try {
            app.evaluate(System.in);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    // -----------------------------------------------------------------------------------------------------

    public enum Rank {

        DEUCE("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("J"),
        QUEEN("Q"),
        KING("K"),
        ACE("A");


        private final String symbol_;
        private static final Map<String,Rank> rankFromSymbol_ = new HashMap<String,Rank>();

        static {
            for (Rank r : values()) {
                rankFromSymbol_.put(r.getSymbol(), r);
            }
        }

        private Rank(String symbol) {
            symbol_ = symbol;

        }

        public String getSymbol() {
            return symbol_;
        }

        public static Rank getRankForSymbol(String symbol) {
            return rankFromSymbol_.get(symbol);
        }

    }



    // -----------------------------------------------------------------------------------------------------

    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    private static class Card {


        private final Rank rank;
        private final Suit suit;

        public Card(Rank rank, Suit suit) {
            this.rank = rank;
            this.suit = suit;
        }

        public Card(String cardToken) {
            int len = cardToken.length();
            assert (len < 3);

            this.rank = Rank.getRankForSymbol(cardToken.substring(0, len-1));
            char a_suit = cardToken.charAt(len-1);
            switch (a_suit) {
                case 'H' : this.suit = Suit.HEARTS; break;
                case 'D' : this.suit = Suit.DIAMONDS; break;
                case 'C' : this.suit = Suit.CLUBS; break;
                case 'S' : this.suit = Suit.SPADES; break;
                default: this.suit = null;  assert false;
            }
        }


        public Rank rank() { return rank; }

        public Suit suit() { return suit; }

        public String toString() { return rank + " of " + suit; }
    }



    // -------------------------------------------------------------------------------------------------

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


    public static class PokerHand implements Comparable {

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
            return new ArrayList<Card>(hand_);
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
            matchMap_ = computeMatchMap();
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
            // need to take into account the suit and rank when determining the score to break ties if 2 hands are the same
            return determineType().odds() * 1000 + this.determineType().getTieBreakerScore(this);
        }

        private void sort() {
            Comparator<Card> comparator = new CardComparator();
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
            }
            return false;   // never reached
        }

        /**
         * returns true if there are 5 cards are of the same suit
         */
        private boolean hasFlush() {

            int ct = 0;
            Suit suit = hand_.get(0).suit();
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
         * (note: there is not 2 of a kind if there is 4 of a kind)
         */
        private boolean hasNofaKind(int num) {

            Collection values = matchMap_.values();
            for (Object value : values) {
                if (value.equals(num))
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
                if (e.getValue().equals(num)) {
                    Rank r = (Rank)e.getKey();
                    if (highestRank == null || (r.ordinal() > highestRank.ordinal())) {
                        highestRank = r;
                    }
                }
            }

            return highestRank;
        }


        /**
         * returns true if there is exactly 2 pairs
         * (note: there are not 2 pairs if there is a full house)
         */
        private boolean hasTwoPairs() {

            Collection values = matchMap_.values();
            int numPairs = 0;
            for (Object value : values) {
                if (value.equals(2))
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
            Map<Rank,Integer> map = new HashMap<Rank,Integer>();

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
            // first do a coars comparison based on the type of the hand
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


        protected Map getMatchMap() {
            return matchMap_;
        }


        /**
         *
         * @param hand1
         * @param hand2
         * @return  return 1 if hand1 greater than hand2, 0 if equal, -1 if less.
         */
        public int compareHandsOfEqualType(PokerHand hand1, PokerHand hand2) {
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

        public String toString() { return "[" + hand_ + ']'; }

        /**
         * inner class used to define a sord order on cards in a poker hane.
         */
        private class CardComparator implements Comparator<Card> {

            public int compare(Card card1, Card card2) {



                if (card1.rank() == card2.rank())   {
                    return card1.suit().ordinal() - card2.suit().ordinal();
                }
                else {
                    return card1.rank().ordinal() - card2.rank().ordinal();
                }
            }
        }

    }

}
