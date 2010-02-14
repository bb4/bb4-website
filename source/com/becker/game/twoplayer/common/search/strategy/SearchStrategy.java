package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.tree.GameTreeViewable;

/**
 * Interface for all SearchStrategies for 2 player games with perfect information.
 *
 * @author Barry Becker
 */
public interface SearchStrategy {

    /** anything greater than this is considered a won game. */
    int WINNING_VALUE = 4096;

    /** For our purposes, this is effectively infinity. */
    int INFINITY = 10000000;


    /**
     * The search algorithm.
     * This method is the crux of all 2 player zero sum games with perfect information.
     * Derived classes work by narrowing a bound on the value of the optimal move.
     *
     * @param lastMove the most recent move made by one of the players.
     * @param alpha same as p2best but for the other player. (alpha)
     * @param beta the maximum of the value that it inherits from above and the best move found at this level (beta).
     * @param parent for constructing a ui tree. If null no game tree is constructed.
     * @return the chosen move (ie the best move) (may be null if no next move).
     */
    TwoPlayerMove search( TwoPlayerMove lastMove, 
                          int alpha, int beta, SearchTreeNode parent );


    /**
     * @return the number of moves considered in the search so far.
     */
    int getNumMovesConsidered();

    /**
     * Approximate percent completed for the search.
     * Approximate because pruning can cause the search to speed up considerably toward the end.
     *
     * @return the approximate percentage of total search time that has been completed.
     */
    int getPercentDone();

    /**
     * An optional game tree event listener. There can be at most one.
     * @param listener event listener
     */
    void setGameTreeEventListener(GameTreeViewable listener);


    // these methods give an external thread debugging controls over the search.

    /**
     * Cause search to become paused.
     */
    void pause();

    /**
     * @return true if search is paused.
     */
    boolean isPaused();

    /**
     * Continue processing if the search was paused.
     */
    void continueProcessing();
}
