package com.becker.game.twoplayer.common.search.examples;

import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * A simple game tree for testing search strategies.
 * It looks something like this
 *                 ____   []  _____
 *                /                \
 *             [-8]               [-2]
 *
 * Move scores are evaluated from player one's perspective.
 * @author Barry Becker
 */
public class OneLevelGameTreeExample extends AbstractGameTreeExample  {


    public OneLevelGameTreeExample(boolean player1PlaysNext, EvaluationPerspective persp) {

        super(persp);

        initialMove = moveCreator.createMove(6, !player1PlaysNext, null);

        // first ply
        TwoPlayerMoveStub move0 = moveCreator.createMove(-8, player1PlaysNext, initialMove);
        TwoPlayerMoveStub move1 = moveCreator.createMove(-2, player1PlaysNext, initialMove);

        initialMove.setChildren(createList(move0, move1));
    }

    public int getMaxDepth() {
        return 1;
    }
}
