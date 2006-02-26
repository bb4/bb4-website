package com.becker.puzzle.set;

import java.util.*;

/**
 * @author Barry Becker Date: Feb 4, 2006
 */
public class Card {

    public enum AttributeValue {
        FIRST, SECOND, THIRD
    }

    private final AttributeValue color_;
    private final AttributeValue shape_;
    private final AttributeValue number_;
    private final AttributeValue texture_;

    public Card(AttributeValue color, AttributeValue shape, AttributeValue number, AttributeValue texture) {
        this.color_ = color;
        this.shape_ = shape;
        this.number_ = number;
        this.texture_ = texture;
    }


    public AttributeValue color() { return color_; }
    public AttributeValue shape() { return shape_; }
    public AttributeValue number() { return number_; }
    public AttributeValue texture() { return texture_; }


    public String toString() {
        return "COLOR: "+color_  +" NUMBER: "+ number_ +" SHAPE: "+ shape_+" TEXTURE: "+ texture_;
    }

    private static final List<Card> protoDeck = new ArrayList<Card>();

    // Initialize prototype deck
    static {
        for (AttributeValue color : AttributeValue.values())
            for (AttributeValue shape : AttributeValue.values())
                 for (AttributeValue number : AttributeValue.values())
                     for (AttributeValue texture : AttributeValue.values())
                         protoDeck.add(new Card(color, shape, number, texture));
    }

    public static List<Card> newDeck() {
        List<Card> deck = new ArrayList<Card>(protoDeck); // Return copy of prototype deck
        Collections.shuffle(deck);
        return deck;
    }


    public static void main(String[] args) {

        List deck = newDeck();
        System.out.println("deck="+deck);
    }
}