// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Deck;
import junit.framework.TestCase;

import static com.barrybecker4.game.multiplayer.poker.hand.PokerHandTstUtil.createHand;


/**
 * programming challenge to test which poker hands are better
 * see http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
 *
 * author Barry Becker
 */
public class PokerHandTest extends TestCase {

    /** instance under test */
    PokerHand hand;

    public void testHasStraightWhenNone() {
        assertFalse("Unexpectedly had a straight", createHand("2H 4S 4C 2D 4H").hasStraight());
    }

    public void testHasNormalStraight() {
        PokerHand hand = createHand("3H 4S 5C 6D 7H");
        assertTrue("Straight not found", hand.hasStraight());
    }

    public void testHasNormalStraightTwoLow() {
        PokerHand hand = createHand("2H 3S 4C 5D 6H");
        assertTrue("Straight not found", hand.hasStraight());
    }

    public void testHasAceHighStraight() {
        assertTrue(createHand("10H JS QC KD AH").hasStraight());
    }

    public void testHasAceLowStraight() {
        assertTrue(createHand("AH 3S 2C 5D 4H").hasStraight());
    }

    public void testHasAceLowNoStraight() {
        assertFalse(createHand("AH 3S 4C 5D 6H").hasStraight());
    }

    public void testNotFlush() {
        assertFalse(createHand("AH 3S 4C 5D 6H").hasFlush());
    }

    public void testHasFlush() {
        assertTrue(createHand("AS 3S 4S 9S 6S").hasFlush());
    }

    public void testStraightFlushHasFlush() {
        assertTrue(createHand("AS 3S 4S 5S 6S").hasFlush());
    }

    /**
     * est out a bunch of hands of different sizes
     */
    public void testHands() {

        Deck deck = new Deck();

        for (int i=5; i<9; i++) {
            PokerHand hand = new PokerHand(deck, i);
            assertEquals("Unexpected size of hand.", i, hand.size());
        }
    }

}
