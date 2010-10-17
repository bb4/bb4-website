package com.becker.game.twoplayer.common.search.examples;

import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * A simple game tree where one of the moves should get alpha pruned when alpha beta pruning is on.
 * Player2 to move next.
 *
 * @author Barry Becker
 */
public class AlphaPrunePlayer2Example extends AbstractGameTreeExample  {


    public AlphaPrunePlayer2Example() {

        initialMove = new TwoPlayerMoveStub(7, true, null);

        // first ply
        TwoPlayerMoveStub move0 = new TwoPlayerMoveStub(2, false, initialMove);
        TwoPlayerMoveStub move1 = new TwoPlayerMoveStub(3, false, initialMove);

        // second ply
        TwoPlayerMoveStub move00 = new TwoPlayerMoveStub(5, true, move0);
        TwoPlayerMoveStub move01 = new TwoPlayerMoveStub(9, true, move0);

        TwoPlayerMoveStub move10 = new TwoPlayerMoveStub(10, true, move1);
        TwoPlayerMoveStub move11 = new TwoPlayerMoveStub(11, true, move1);  // this should get alpha pruned


        initialMove.setChildren(createList(move0, move1));

        move0.setChildren(createList(move00, move01));
        move1.setChildren(createList(move10, move11));
    }
}
