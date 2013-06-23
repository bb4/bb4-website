// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Deck;
import junit.framework.TestCase;

import static com.barrybecker4.game.multiplayer.poker.hand.PokerHandTstUtil.createHand;


/**
 * Verify that the distribution of the poker hand types roughly matches reality.
 * See http://www.dagnammit.com/poker/breakdown.html
 *
 * author Barry Becker
 */
public class PokerHandDistributionTest extends TestCase {

    private static final int NUM_HANDS = 7000;

    /** instance under test */
    PokerHand hand;

    public void testHandDistributions() {


        for (int i=0; i<NUM_HANDS; i++) {

        }
        assertFalse("Unexpectedly had a straight", createHand("2H 4S 4C 2D 4H").hasStraight());
    }



}
