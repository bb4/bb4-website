/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis;

/**
 * Used to keep track of evaluating a measure of score passed only on values at positions.
 *
 * @author Barry Becker
 */
public final class GameStageBoostCalculator {

    private int boardSize;

    /**
     * Construct the Go game controller.
     */
    public GameStageBoostCalculator(int boardSize) {

        this.boardSize = boardSize;
    }


    /**
     * Opening = 4.5 - 1.0;   middle = 1.0 - 0.5;    end = 0.5
     * See TestGameStageBoostCalculator for more examples
     *
     * @return a weight for the positional score based on how far along into the game we are.
     */
    public double getGameStageBoost(int numMovesSoFar) {
        float size2 = 2.0f * boardSize;
        float b = Math.max(0, size2 - (float) numMovesSoFar)/size2;
        return 0.5 + 8.0 * b * b;
    }

}