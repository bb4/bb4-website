package com.becker.puzzle.set;

import java.util.*;

/**
 * @author Barry Becker Date: Feb 4, 2006
 */
public class Card {

    public enum AttributeValue {
        FIRST, SECOND, THIRD
    }

    public enum Attribute {
        COLOR, SHAPE, NUMBER, TEXTURE
    }

    private final AttributeValue[] attributes_ = new AttributeValue[Attribute.values().length];

    private boolean isHighlighted_ = false;
    private boolean isSelected_ = false;

    public Card(AttributeValue color, AttributeValue shape, AttributeValue number, AttributeValue texture) {
        attributes_[Attribute.COLOR.ordinal()] = color;
        attributes_[Attribute.SHAPE.ordinal()] = shape;
        attributes_[Attribute.NUMBER.ordinal()] = number;
        attributes_[Attribute.TEXTURE.ordinal()] = texture;
    }


    public AttributeValue valueOfAttribute(Attribute a) {
         return attributes_[a.ordinal()];
    }
    public AttributeValue color() { return attributes_[Attribute.COLOR.ordinal()]; }
    public AttributeValue shape() { return attributes_[Attribute.SHAPE.ordinal()]; }
    public AttributeValue number() { return attributes_[Attribute.NUMBER.ordinal()]; }
    public AttributeValue texture() { return attributes_[Attribute.TEXTURE.ordinal()]; }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < attributes_.length; i++) {
            AttributeValue value = attributes_[i];
            buf.append(Attribute.values()[i] + ":" + value +"  ");
        }
        return buf.toString();
    }

    private static final List<Card> protoDeck = new ArrayList<Card>();


    public boolean isSelected() {
        return isSelected_;
    }

    public void setSelected(boolean selected) {
        isSelected_ = selected;
    }

    public boolean isHighlighted() {
        return isHighlighted_;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted_ = highlighted;
    }

    public void toggleSelect() {
        isSelected_ = !isSelected_;
    }

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

    /**
     * reutrn true if the set of cards passed in is a set
     */
    public static boolean isSet(List<Card> cards) {
        if (cards == null || cards.size() != 3) {
            return false;
        }
        // for each attribute, verify that the values are either all the same or all different.
        for (int i = 0; i < Attribute.values().length; i++) {

            Attribute attribute = Attribute.values()[i];
            AttributeValue val0 = cards.get(0).valueOfAttribute(attribute);
            AttributeValue val1 = cards.get(1).valueOfAttribute(attribute);
            AttributeValue val2 = cards.get(2).valueOfAttribute(attribute);
            boolean allSame = (val0 == val1) && (val1 == val2);
            boolean allDifferent = (val0 != val1) && (val1 != val2) && (val0 != val2);

            if (!(allSame || allDifferent)) {
               return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        List deck = newDeck();
        System.out.println("deck="+deck);
    }
}