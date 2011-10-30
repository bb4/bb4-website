/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common.search.examples;

import com.becker.common.geometry.Location;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;

/**
 * Create stub two player moves
 * Factory.
 *
 * @author Barry Becker
 */
public class MoveCreator {

    private static final int FAKE_BOARD_SIZE = 19;

    EvaluationPerspective evalPerspective;

    int moveCount = 0;

    public MoveCreator(EvaluationPerspective evalPersp) {
        evalPerspective = evalPersp;
    }


    public TwoPlayerMoveStub createMove(int value, boolean player1Move, TwoPlayerMoveStub parent) {

        return new TwoPlayerMoveStub(value, player1Move, createToLocation(), parent);
        //return new TwoPlayerMoveStub(getPerspectiveValue(value, player1Move), player1Move, parent);
    }

    private Location createToLocation() {
        moveCount++;
        return new Location(moveCount / FAKE_BOARD_SIZE, moveCount % FAKE_BOARD_SIZE);
    }

    /**
     * We can tell our depth in the tree, but looking at the number of ancestors we have.
     * @param parent
     * @return depth in game tree.
     */
    private int getDepth(TwoPlayerMoveStub parent) {
        int depth = 0;
        TwoPlayerMoveStub nextParent = parent;
        while (nextParent != null) {
            nextParent = nextParent.getParent();
            depth++;
        }
        return depth;
    }
}
