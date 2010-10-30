package com.becker.game.twoplayer.common.search.examples;

import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;

import java.util.Arrays;

/**
 * An abstract game tree for testing search strategies.
 * Note that in all the sample trees. Only the leaf values really matter, the non-leaf values should be ignored.
 * Though tht enon-leaf values may be used for move ordering, they should not effect the final inherited values.
 *
 * @author Barry Becker
 */
public interface GameTreeExample {


    /**
     * @return  the root move in the game tree.
     */
    TwoPlayerMove getInitialMove();

    /**
     * @return  maximum depth of the example tree
     */
    int getMaxDepth();

    /**
     * Print the tree in depth first search for debugging purposes
     */
    void print();
}
