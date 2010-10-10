package com.becker.game.twoplayer.common.search.examples;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;

import java.util.Arrays;

/**
 * An abstract game tree for testing search strategies.
 *
 * @author Barry Becker
 */
public class AbstractGameTreeExample {

    /** the root of the game tree. */
    protected TwoPlayerMoveStub initialMove;

    public AbstractGameTreeExample() {
    }

    protected MoveList createList(TwoPlayerMoveStub... moves) {
        MoveList moveList = new MoveList();
        //moveList.addAll(Arrays.asList(moves));
        for (TwoPlayerMoveStub m : moves) {
            moveList.add(m);
        }
        return moveList;

    }

    public TwoPlayerMove getInitialMove() {
        return initialMove;
    }

    /**
     * Print the tree in depth first search for debugging purposes
     */
    public void print() {
        initialMove.print();
    }
}
