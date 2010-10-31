package com.becker.game.twoplayer.common.search.examples;

import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * A simple game tree for testing search strategies.
 * It looks something like this
 *                 ____   []  _____
 *                /                \
 *            [-8]                  [-2]
 *         /      \               /      \
 *     [-1]       [7]           [8]       [2]
 *
 * Move scores are evaluated from player one's perspective.
 * @author Barry Becker
 */
public class TwoLevelGameTreeExample extends AbstractGameTreeExample  {


    public TwoLevelGameTreeExample(boolean player1PlaysNext, EvaluationPerspective persp) {

        super(persp);

        initialMove = moveCreator.createMove(6, !player1PlaysNext, null);

        // first ply
        TwoPlayerMoveStub move0 = moveCreator.createMove(-8, player1PlaysNext, initialMove);
        TwoPlayerMoveStub move1 = moveCreator.createMove(-2, player1PlaysNext, initialMove);

        // second ply
        TwoPlayerMoveStub move00 = moveCreator.createMove(-1, !player1PlaysNext, move0);
        TwoPlayerMoveStub move01 = moveCreator.createMove(7, !player1PlaysNext, move0);

        TwoPlayerMoveStub move10 = moveCreator.createMove(8, !player1PlaysNext, move1);
        TwoPlayerMoveStub move11 = moveCreator.createMove(2, !player1PlaysNext, move1);

        initialMove.setChildren(createList(move0, move1));

        move0.setChildren(createList(move00, move01));
        move1.setChildren(createList(move10, move11));

    }
}
