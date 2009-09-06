package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.search.tree.PruneType;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.tree.GameTreeViewable;
import com.becker.optimization.parameter.ParameterArray;

import java.util.List;

/**
 *  This is an abstract base class for a search strategy.
 *  It's sublcasses define the key search algorithms for 2 player zero sum games with
 *  perfect information.
 *  Create one of these right before you do a search.
 *
 *  @author Barry Becker
 */
public abstract class AbstractSearchStrategy implements SearchStrategy
{
 
    /** if true, then use alpha-beta pruning. */
    final boolean alphaBeta_;

    /** If true, then use additional qeiscent search to extent the search tree for urgent moves. */
    final boolean quiescence_;

    /** Don't go deeper than this when trying to find quiescence. */
    protected static final int MAX_QUIESCENT_DEPTH = 12;

    /** the interface implemented by the generic game controller that provides standard methods. */
    Searchable searchable_ = null;

    /** keep track of the number of moves searched so far. */
    int movesConsidered_ = 0;

    /** approximate percent of search that is complete at given moment. */
    int percentDone_ = 0;

    /** True when search is paused. */
    private boolean paused_ = false;

    /** The user can sometimes interrupt the search. */
    protected boolean interrupted_ = false;

    /** The optional ui component that will be updated to reflect the current search tree.  */
    protected GameTreeViewable gameTreeListener_;

    /**
     * Number of moves to consider at the top ply.
     * we use this number to determine how far into the search that we are.
     */
    protected int numTopLevelMoves_;


    /**
     * Construct the strategy.
     * do not call directly. Use createSearchStrategy factory method instead.
     * @param controller the game controller that has options and can make/undo moves.
     */
    protected AbstractSearchStrategy( Searchable controller )
    {
        searchable_ = controller;
        alphaBeta_ = searchable_.getAlphaBeta();
        quiescence_ = searchable_.getQuiescence();
        GameContext.log( 2, "alpha beta=" + alphaBeta_ + " quiescence=" + quiescence_ );
    }

    /**
     * @inheritDoc
     */
    public abstract TwoPlayerMove search( TwoPlayerMove lastMove, ParameterArray weights,
                                          int depth, int quiescentDepth,
                                          int alpha, int beta, SearchTreeNode parent );


    /**
     * add a move to the visual game tree (if parent not null).
     */
    protected SearchTreeNode addNodeToTree( SearchTreeNode parent, TwoPlayerMove theMove,
                                         int alpha, int beta, int i )
    {
        SearchTreeNode child = null;
        if (gameTreeListener_ != null) {
            child = new SearchTreeNode( theMove );
            child.setAlpha(alpha);
            child.setBeta(beta);
            gameTreeListener_.addNode(parent, child, i);
        }
        return child;
    }

    /**
     * Show the node in the game tree (if one is used. It is used if parent not null).
     *
     * @param list of pruned nodes
     * @param parent the tree node entry above the current position.
     * @param i th child.
     * @param val the worth of the node/move
     * @param thresh the alpha or beta threshold compared to.
     * @param type either PRUNE_ALPHA or PRUNE_BETA - pruned by comparison with Alpha or Beta.
     */
    protected void showPrunedNodesInTree( List list, SearchTreeNode parent,
                                                                        int i, int val, int thresh, PruneType type)
    {
        if (gameTreeListener_ != null) {
            gameTreeListener_.addPrunedNodes(list, parent, i, val, thresh, type);
        }
    }

    /**
     * @return true if the move list is empty.
     */
    static boolean emptyMoveList( List list, TwoPlayerMove lastMove )
    {
        if ( !list.isEmpty() ) return false;

        //If there are no next moves, the game is over and the last player to move won
        if ( lastMove.isPlayer1() )
            lastMove.setInheritedValue(WINNING_VALUE);
        else
            lastMove.setInheritedValue(-WINNING_VALUE);

        return true;
    }

    public final int getNumMovesConsidered()
    {
        return movesConsidered_;
    }

    public final int getPercentDone()
    {
        return percentDone_;
    }

    /**
     * Set an optional ui component that will update when the search tree is modified.
     * @param listener
     */
    public void setGameTreeEventListener(GameTreeViewable listener) {
        gameTreeListener_ = listener;
    }

    /**
     * Update the percentage done serching variable for the progress bar
     * if we are at the top level (otherwise this is a no-op).
     */
    protected void updatePercentDone(int depth,  List remainingNextMoves) {
        if (depth == searchable_.getLookAhead())   {
            percentDone_ = 100 * (numTopLevelMoves_-remainingNextMoves.size()) / numTopLevelMoves_;
        }
    }

    // these methods give an external thread debugging controls over the search


    public void pause()
    {
        paused_ = true;
    }


    public final boolean isPaused()
    {
        return paused_;
    }


    public void continueProcessing()
    {
        paused_ = false;
    }

    /**
     * pause if we are paused. Continue when not paused anymore.
     * The pause value is changed by the TwoPlayerBoardViewer
     */
    void checkPause() {
        try {
            while (paused_) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            //e.printStackTrace();
            GameContext.log(2, "interrupted" );
            interrupted_ = true;
            e.printStackTrace();
        }
    }

}