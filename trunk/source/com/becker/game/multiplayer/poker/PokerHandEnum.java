package com.becker.game.multiplayer.poker;

import com.becker.game.card.Card;
import com.becker.game.card.Rank;

import java.util.Map;
import java.util.List;

/**
 * User: Barry Becker
 * Date: Feb 26, 2005
 * Time: 6:16:43 AM
 */
public enum PokerHandEnum {

    // note: five of a kind can only happen if using wild cards
    FIVE_OF_A_KIND("Five of a Kind", 749740) {
        public int getTieBreakerScore(PokerHand hand) {return fiveOfaKindTieBreaker(hand);}
    },
    ROYAL_FLUSH("Royal Flush", 649740) {
        public int getTieBreakerScore(PokerHand hand) {return highCardTieBreaker(hand);}
    },
    STRAIGHT_FLUSH("Straight Flush", 72192) {
        public int getTieBreakerScore(PokerHand hand) {return highCardTieBreaker(hand);}
    },
    FOUR_OF_A_KIND("Four of a Kind", 4164) {
        public int getTieBreakerScore(PokerHand hand) {return fourOfaKindTieBreaker(hand);}
    },
    FULL_HOUSE("Full House", 693) {
        public int getTieBreakerScore(PokerHand hand) {return fullHouseTieBreaker(hand);}
    },
    FLUSH("Flush", 508) {
        public int getTieBreakerScore(PokerHand hand) {return highCardTieBreaker(hand);}
    },
    STRAIGHT("Straight", 254) {
        public int getTieBreakerScore(PokerHand hand) {return highCardTieBreaker(hand);}
    },
    THREE_OF_A_KIND("Three of a Kind", 40) {
        public int getTieBreakerScore(PokerHand hand) {return threeOfaKindTieBreaker(hand);}
    },
    TWO_PAIR("Two Pair", 20) {
        public int getTieBreakerScore(PokerHand hand) {return twoPairTieBreaker(hand);}
    },
    PAIR("Pair", 1.37f) {
        public int getTieBreakerScore(PokerHand hand) {return pairTieBreaker(hand);}
    },
    HIGH_CARD("High Card", 1) {
        public int getTieBreakerScore(PokerHand hand) {return highCardTieBreaker(hand);}
    };


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


    /**
     * @param hand hte poker hand to evaluate
     * @return a score that allows you to differentiate among ties. higher score is better hand.
     */
    public abstract int getTieBreakerScore(PokerHand hand);



    private static int fiveOfaKindTieBreaker(PokerHand hand) {
        Rank r = hand.getRankOfNofaKind(5);
        return r.ordinal();
    }

    private static int highCardTieBreaker(PokerHand hand) {
        int score = PokerHand.getValue(hand.getHighCard());
        return score;
    }

    private static int fourOfaKindTieBreaker(PokerHand hand) {
        Rank r = hand.getRankOfNofaKind(4);
        return r.ordinal();
    }

    private static int fullHouseTieBreaker(PokerHand hand) {
        Rank r3 = hand.getRankOfNofaKind(3);
        Rank r2 = hand.getRankOfNofaKind(2);
        return 100*r3.ordinal() + r2.ordinal();
    }

    private static int threeOfaKindTieBreaker(PokerHand hand) {
        Rank r3 = hand.getRankOfNofaKind(3);
        Card c = getHighCardNotInNofaKind(3, hand);
        return 100*r3.ordinal() + PokerHand.getValue(c);
    }

    private static int twoPairTieBreaker(PokerHand hand) {
        Rank rp1 = hand.getRankOfNofaKind(2);
        Rank rp2 = hand.getRankOfNofaKind(2);
        // @@ its exceedingly unlikely, but possible that the 2 pairs could have the same rank in each hand
        // in this case the one card not in the pairs should be compared
        if (rp1.ordinal() >rp2.ordinal())
            return 100*rp1.ordinal() + rp2.ordinal();
        else {
            return 100*rp2.ordinal() + rp1.ordinal();
        }
    }

    private static int pairTieBreaker(PokerHand hand) {
        Rank r2 = hand.getRankOfNofaKind(2);
        Card c = getHighCardNotInNofaKind(2, hand);
        return 100*r2.ordinal() + PokerHand.getValue(c);
    }

    private static Card getHighCardNotInNofaKind(int n, PokerHand hand) {
        Rank r = hand.getRankOfNofaKind(n);
        // this is guaranteed to be an ordered list of cards
        List<Card> cards =  hand.getCards();
        int i=cards.size()-1;
        while (cards.get(i).rank() == r && i >= 0) {
            i--;
        }
        return cards.get(i);
    }

}

