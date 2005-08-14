package com.becker.game.card;

import java.util.*;

public class Card {
  
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

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

    private static final List<Card> protoDeck = new ArrayList<Card>();

    // Initialize prototype deck
    static {
        for (Suit suit : Suit.values())
            for (Rank rank : Rank.values())
                protoDeck.add(new Card(rank, suit));
    }

    public static List newDeck() {
        List deck = new ArrayList<Card>(protoDeck); // Return copy of prototype deck
        Collections.shuffle(deck);
        return deck;
    }


    public static void main(String[] args) {

        List deck = newDeck();
        System.out.println("deck="+deck);
    }
}