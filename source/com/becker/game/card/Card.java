package com.becker.game.card;

import java.util.*;

public class Card {
  
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    private final Rank rank;
    private final Suit suit;

    private Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }


    public Rank rank() { return rank; }

    public Suit suit() { return suit; }

    public String toString() { return rank + " of " + suit; }

    private static final List<Card> protoDeck = new ArrayList<Card>();

    // Initialize prototype deck
    static {
        for (Suit suit : Suit.values())
            for (Rank rank : Rank.values())
                protoDeck.add(new Card(rank, suit));
    }

    public static ArrayList<Card> newDeck() {
        ArrayList<Card> deck = new ArrayList<Card>(protoDeck); // Return copy of prototype deck
        Collections.shuffle(deck);
        return deck;
    }

    public static void main(String[] args) {

        ArrayList deck = Card.newDeck();
        System.out.println("deck="+deck);
    }
}