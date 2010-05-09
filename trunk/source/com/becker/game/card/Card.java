package com.becker.game.card;

import java.util.*;

/**
 * Represents a standard playing card.
 */
public class Card {

    private final Rank rank_;
    private final Suit suit_;

    /** A prototype deck that gets statically initialized. */
    private static final List<Card> protoDeck = new ArrayList<Card>();

    static {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                protoDeck.add(new Card(rank, suit));
            }
        }
    }

    /**
     * Constructor.
     * @param rank 2 - Ace
     * @param suit - space, diamond, clubs, hearts
     */
    public Card(Rank rank, Suit suit) {
        this.rank_ = rank;
        this.suit_ = suit;
    }

    public Card(String cardToken) {
        int len = cardToken.length();
        assert (len < 3);

        this.rank_ = Rank.getRankForSymbol(cardToken.substring(0, len-1));
        this.suit_ = Suit.getSuitForSymbol(cardToken.substring(1));
    }


    public Rank rank() { return rank_; }

    public Suit suit() { return suit_; }

    @Override
    public String toString() { return rank_ + " of " + suit_; }


    /**
     * @return Return a copy of prototype deck
     */
    public static List<Card> newDeck() {
        List<Card> deck = new ArrayList<Card>(protoDeck); 
        Collections.shuffle(deck);
        return deck;
    }

    public static void main(String[] args) {
        newDeck();
    }
}