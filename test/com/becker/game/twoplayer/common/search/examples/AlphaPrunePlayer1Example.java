package com.becker.game.twoplayer.common.search.examples;

import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * A simple game tree where one of the moves should get alpha pruned when alpha beta pruning is on.
 * Player1 to move next.
 *
 * @author Barry Becker
 */
public class AlphaPrunePlayer1Example extends AbstractGameTreeExample  {


    public AlphaPrunePlayer1Example() {

        initialMove = new TwoPlayerMoveStub(7, false, null);

        // first ply
        TwoPlayerMoveStub move0 = new TwoPlayerMoveStub(3, true, initialMove);
        TwoPlayerMoveStub move1 = new TwoPlayerMoveStub(2, true, initialMove);

        // second ply
        TwoPlayerMoveStub move00 = new TwoPlayerMoveStub(5, false, move0);
        TwoPlayerMoveStub move01 = new TwoPlayerMoveStub(9, false, move0);

        TwoPlayerMoveStub move10 = new TwoPlayerMoveStub(4, false, move1);
        TwoPlayerMoveStub move11 = new TwoPlayerMoveStub(3, false, move1);  // this should get alpha pruned


        initialMove.setChildren(createList(move0, move1));

        move0.setChildren(createList(move00, move01));
        move1.setChildren(createList(move10, move11));
    }
}
