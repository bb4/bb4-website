package com.becker.game.twoplayer.common.search.examples;

import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;

/**
 * Create stub two player moves
 * Factory.
 *
 * @author Barry Becker
 */
public class MoveCreator {

    EvaluationPerspective evalPerspective;

    public MoveCreator(EvaluationPerspective evalPersp) {
        evalPerspective = evalPersp;
    }


    public TwoPlayerMoveStub createMove(int value, boolean player1Move, TwoPlayerMoveStub parent) {

        return new TwoPlayerMoveStub(value, player1Move, parent);
        //return new TwoPlayerMoveStub(getPerspectiveValue(value, player1Move), player1Move, parent);
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
