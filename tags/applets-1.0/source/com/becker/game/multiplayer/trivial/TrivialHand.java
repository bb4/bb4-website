/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.trivial;


/**
 * User: Barry Becker
 * Date: Feb 26, 2005
 */
class TrivialHand implements Comparable {

    private int value;

    /**
     * The trivial hand contains a value between 0 and 1 million.
     */
    public TrivialHand() {
        value = (int) (100000.99999 *Math.random());
    }
   
    /**
     *  Calculate a score for this trivial hand so it can be compared with others
     * @return
     */
    int getValue() {
        // need to take into account the suit and rank when determining the score to break ties if 2 hands are the same
        return value;
    }

    /**
     * compare this trivial hand to another
     * @param otherHand
     * @return 1 if this hand is higher than the other hand, -1 if lower, else 0.
     */
    public int compareTo(Object otherHand) {
        TrivialHand hand = (TrivialHand) otherHand;
        // first do a coars comparison based on the type of the hand
        // if a tie, then look more closely
        
        int difference = this.getValue() - hand.getValue();
        if (difference > 0) {
            return 1;
        } else if (difference < 0) {
            return -1;
        } else {
            return 0;
        }
    }


    public String toString() { return "[" + getValue() + "]"; }

}
