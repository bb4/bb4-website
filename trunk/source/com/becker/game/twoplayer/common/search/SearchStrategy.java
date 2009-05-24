package com.becker.game.twoplayer.common.search;

import com.becker.common.util.Util;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.TwoPlayerMove;
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
public abstract class SearchStrategy
{

    // anything greater than this is considered a won game
    public static final int WINNING_VALUE = 1000;

    // applies only if computer vs computer game
    // @@ Should have a more abstract SearchOptions class that we get from the controller.
    final boolean alphaBeta_;
    final boolean quiescence_;

    // prune types
    protected static final int PRUNE_ALPHA = 1;
    protected static final int PRUNE_BETA = 2;

    protected static final int MAX_QUIESCENT_DEPTH = 12;

    // the interface implemented by the generic game controller that provides standard methods.
    Searchable searchable_ = null;

    // keep track of the number of moves searched
    int movesConsidered_ = 0;
    int percentDone_ = 0;

    private boolean paused_ = false;
    protected boolean interrupted_ = false;

    /**
     * Factory method for creating the search strategy to use.
     * Do not call the constructor directly.
     * @return the search method to use
     */
    public static SearchStrategy createSearchStrategy(SearchStrategyType method, Searchable s)
    {
        switch (method) {
            case MINIMAX:
                return new MiniMaxStrategy(s);
            case NEGAMAX:
                return new NegaMaxStrategy(s);
            case NEGASCOUT:
                return new NegaScoutStrategy(s);
            case NEGASCOUT_W_MEMORY:
                return new NegaScoutMemoryStrategy(s);
            case MTD:
                return new MtdStrategy(s);
        }
        return null;
    }

    /**
     * Construct the strategy.
     * do not call directly. Use createSearchStrategy factory method instead.
     */
    protected SearchStrategy( Searchable controller )
    {
        searchable_ = controller;
        alphaBeta_ = searchable_.getAlphaBeta();
        quiescence_ = searchable_.getQuiescence();
        GameContext.log( 2, "alpha beta=" + alphaBeta_ + " quiescence=" + quiescence_ );
    }

    /**
     * Show the node in the game tree (if one is used. It is used if parent not null).
     * @@ avoid reference to ui in server side code.
     * Probably ok since its unused if not showing the game tree.
     *
     * @param list
     * @param parent the tree node entry above the current position.
     * @param val the worth of the node/move
     * @param thresh the alpha or beta threshold compared to.
     * @param type either PRUNE_ALPHA or PRUNE_BETA - pruned by comparison with Alpha or Beta.
     * @param i th child.
     */
    static void showPrunedNodesInTree( List list, SearchTreeNode parent, int i, int val, int thresh, int type)
    {
        int index = i;
        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            SearchTreeNode child = new SearchTreeNode( theMove );
            child.setPruned(true);
            String sComp = (type == PRUNE_ALPHA) ? " < " : " > ";
            child.setComment("Children pruned because " +
                            Util.formatNumber(val) + sComp + Util.formatNumber(thresh) + '.');
            parent.insert( child, index );
            index++;
        }
    }

    /**
     * The search algorithm.
     * This method is the crux of all 2 player zero sum games with perfect information.
     *
     * @param lastMove the most recent move made by one of the players.
     * @param weights coefficient for the evaluation polunomial that indirectly determines the best move.
     * @param depth how deep in this local game tree that we are to search.
     * @param alpha same as p2best but for the other player. (alpha)
     * @param beta the maximum of the value that it inherits from above and the best move found at this level (beta).
     * @param parent for constructing a ui tree. If null no game tree is constructed.
     * @return the chosen move (ie the best move) (may be null if no next move).
     */
    public abstract TwoPlayerMove search( TwoPlayerMove lastMove, ParameterArray weights,
                                          int depth, int quiescentDepth,
                                          int alpha, int beta, SearchTreeNode parent );

    /**
     * return true if the move list is empty.
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

    /**
     * add a move to the visual game tree (if parent not null).
     */
    static SearchTreeNode addNodeToTree( SearchTreeNode parent, TwoPlayerMove theMove,
                                         int alpha, int beta, int i )
    {
        SearchTreeNode child = null;
        if ( parent != null ) {
            child = new SearchTreeNode( theMove );
            child.setAlpha(alpha);
            child.setBeta(beta);
            parent.insert( child, i );
        }
        return child;
    }

    /**
     * @return the number of moves considered in the search so far
     */
    public final int getNumMovesConsidered()
    {
        return movesConsidered_;
    }

    /**
     * Approximate percent completed for the search.
     * Approximate because pruning can cause the search to speed up considerably toward the end.
     *
     * @return the approximate percentage of total search time that has been completed.
     */
    public final int getPercentDone()
    {
        return percentDone_;
    }

    // these methods give an external thread debugging controls over the search

    public void pause()
    {
        paused_ = true;
        //System.out.println( "search strategy pause="+true );
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