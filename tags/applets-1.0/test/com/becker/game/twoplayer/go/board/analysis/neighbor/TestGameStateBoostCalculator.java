/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.neighbor;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.GameStageBoostCalculator;

/**
 *Test that candidate moves can be generated appropriately.
 *
 * @author Barry Becker
 */
public class TestGameStateBoostCalculator extends GoTestCase {

    /** Create calculator for 13 by 13 board */
    private GameStageBoostCalculator calculator = new GameStageBoostCalculator(13);

    private static final double TOL = 0.01;


    public void testMove0Boost() {
        verifyBoost(0, 8.5);
    }

    public void testMove1Boost() {
        verifyBoost(1, 7.9);
    }

    public void testMove2Boost() {
        verifyBoost(2, 7.31);
    }

    public void testMove5Boost() {
        verifyBoost(5, 5.72);
    }

    public void testMove10Boost() {
        verifyBoost(10, 3.53);
    }

    public void testMove15Boost() {
        verifyBoost(15, 1.93);
    }

    public void testMove20Boost() {
        verifyBoost(20, 0.93);
    }

    public void testMove25Boost() {
        verifyBoost(25, 0.51);
    }

    public void testMove30Boost() {
        verifyBoost(30, 0.5);
    }

    public void testMove35Boost() {
        verifyBoost(35, 0.5);
    }

    public void testMove40Boost() {
        verifyBoost(40, 0.5);
    }


    private void verifyBoost(int numMoves, double expectedBoost) {
        assertEquals("Unexpected boost for "+numMoves+" into the game.",
                expectedBoost, calculator.getGameStageBoost(numMoves), TOL);
    }
}
