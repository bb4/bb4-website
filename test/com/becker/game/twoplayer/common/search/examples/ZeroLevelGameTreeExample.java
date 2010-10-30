package com.becker.game.twoplayer.common.search.examples;

import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * A simple game tree for testing search strategies.
 * It looks something like this
 *                 ____   [6]  _____
 *
 * @author Barry Becker
 */
public class ZeroLevelGameTreeExample extends AbstractGameTreeExample  {


    public ZeroLevelGameTreeExample(boolean player1PlaysNext, EvaluationPerspective persp) {

        super(persp);

        initialMove = moveCreator.createMove(6, !player1PlaysNext, null);
    }

    public int getMaxDepth() {
        return 0;
    }
}
