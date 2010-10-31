package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.GameContext;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.tree.GameTreeViewable;
import com.becker.game.twoplayer.common.search.tree.PruneType;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

import java.util.List;

/**
 *  This is an abstract base class for a search strategy.
 *  It's sublcasses define the key search algorithms for 2 player zero sum games with perfect information.
 *
 *  @author Barry Becker
 */
public abstract class AbstractSearchStrategy implements SearchStrategy
{
    /** if true, then use alpha-beta pruning. */
    protected final boolean alphaBeta_;

    /** If true, then use additional qeiscent search to extent the search tree for urgent moves. */
    protected final boolean quiescence_;

    /** the number of plys to look ahead when searching. */
    protected final int lookAhead_;

    /** the interface implemented by the generic game controller that provides standard methods. */
    protected Searchable searchable_ = null;

    /** keep track of the number of moves searched so far. Long because there could be quite a few. */
    protected long movesConsidered_ = 0;

    /** approximate percent of search that is complete at given moment. */
    private int percentDone_ = 0;

    /** don't search more levels ahead than this during quiescent search. */
    protected int maxQuiescentDepth_ = 0;

    /** weights coefficients for the evaluation polynomial that indirectly determines the best move.   */
    protected ParameterArray weights_;

    /** True when search is paused. */
    private volatile boolean paused_ = false;

    /** The optional ui component that will be updated to reflect the current search tree.  */
    protected GameTreeViewable gameTree_;

    /**
     * Number of moves to consider at the top ply.
     * we use this number to determine how far into the search that we are.
     */
    protected int numTopLevelMoves_;


    /**
     * Construct the strategy.
     * do not call directly. Use createSearchStrategy factory method instead.
     * @param searchable the game controller that has options and can make/undo moves.
     * @param weights coefficients for the evaluation polunomial that indirectly determines the best move.
     */
    protected AbstractSearchStrategy( Searchable searchable, ParameterArray weights )
    {
        searchable_ = searchable;
        SearchOptions opts = getOptions();
        alphaBeta_ = opts.getAlphaBeta();
        quiescence_ = opts.getQuiescence();
        lookAhead_ = opts.getLookAhead();
        maxQuiescentDepth_ = opts.getMaxQuiescentDepth();
        weights_ = weights;
        GameContext.log( 2, "alpha beta=" + alphaBeta_ + " quiescence=" + quiescence_ + " lookAhead = " + lookAhead_);
    }

    public SearchOptions getOptions() {
        return searchable_.getSearchOptions();
    }

    /**
     * {@inheritDoc}
     */
    public TwoPlayerMove search( TwoPlayerMove lastMove, SearchTreeNode parent ) {

        return searchInternal( lastMove, lookAhead_, getOptions().getInitialSearchWindow(),  parent );
    }

    /**
     * {@inheritDoc}
     */
    protected TwoPlayerMove searchInternal( TwoPlayerMove lastMove,
                                            int depth, SearchWindow window, SearchTreeNode parent) {

        boolean done = searchable_.done( lastMove, false);
        if ( depth <= 0 || done ) {
            if (doQuiescentSearch(depth, done, lastMove)) {
                return quiescentSearch(lastMove, depth-1, window, parent);
            }
            else {
                int sign = fromPlayer1sPerspective(lastMove) ? 1 : -1;
                lastMove.setInheritedValue(sign * lastMove.getValue());
                return lastMove;
            }
        }

        // generate a list of all (or bestPercent) candidate next moves, and pick the best one
        MoveList list = searchable_.generateMoves(lastMove,  weights_, true);

        if (depth == lookAhead_)
            numTopLevelMoves_ = list.size();

        if (emptyMoveList(list, lastMove)) {
            // if there are no possible next moves, return null (we hit the end of the game).
            return null;
        }

        return findBestMove(lastMove, depth, list, window, parent);
    }

    /**
     * Search more if quiescense is on, depth is negative, but not yet at -maxQiuiescentDepth
     * and the last moved played created an urgent situation.
     * @return true of we should continue searching a bit to find a stable/quiescnet move.
     */
    private boolean doQuiescentSearch(int depth, boolean done, TwoPlayerMove lastMove) {
        boolean inJeopardy = searchable_.inJeopardy(lastMove, weights_, true);
         return quiescence_
                 && depth > -maxQuiescentDepth_
                 && !done
                 && inJeopardy;
    }


    /**
     * This continues the search in situations where the board position is not stable.
     * For example, perhaps we are in the middle of a piece exchange (chess), or a large group is in atari (go).
     * @return best quescent move
     */
    protected TwoPlayerMove quiescentSearch(TwoPlayerMove lastMove,
                                            int depth, SearchWindow window, SearchTreeNode parent) {

        MoveList list = searchable_.generateUrgentMoves(lastMove, weights_, true);
        if (emptyMoveList(list, lastMove)) return null;

        System.out.println("quiescent search depth=" + depth + " win="+ window);
        return findBestMove(lastMove, depth, list, window, parent);
    }

    /**
     * This is the part of the search algorithm that varies most among the search strategies.
     * That is why I break it out into a separete overridable method.
     *
     * @param lastMove the most recent move made by one of the players.
     * @param depth how deep in this local game tree that we are to search.
     *   When depth becomes 0 we are at a leaf and should terminate (unless its an urgent move and quiescence is on).
      *@param list generated list of next moves to search.
     * @param window search window - alpha nd abeta
     * @param parent for constructing a ui tree. If null no game tree is constructed.
     * @return the chosen move (ie the best move) (may be null if no next move).
     */
    protected abstract TwoPlayerMove findBestMove(TwoPlayerMove lastMove, int depth, MoveList list,
                                                  SearchWindow window, SearchTreeNode parent);



    /**
     * add a move to the visual game tree (if parent not null).
     * @return the node added to the tree.
     */
    protected SearchTreeNode addNodeToTree( SearchTreeNode parent, TwoPlayerMove theMove,
                                         SearchWindow window, int i )
    {
        SearchTreeNode child = null;
        if (gameTree_ != null) {
            child = new SearchTreeNode( theMove );
            child.setWindow(window);
            gameTree_.addNode(parent, child, i);
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
    protected void showPrunedNodesInTree( MoveList list, SearchTreeNode parent,
                                          int i, int val, int thresh, PruneType type)
    {
        if (gameTree_ != null) {
            gameTree_.addPrunedNodes(list, parent, i, val, thresh, type);
        }
    }

    /**
     * @return true if the move list is empty.
     */
    static boolean emptyMoveList( MoveList list, TwoPlayerMove lastMove )
    {
        if ( !list.isEmpty() ) return false;

        //If there are no next moves, the game is over and the last player to move won
        if ( lastMove.isPlayer1() )
            lastMove.setInheritedValue(WINNING_VALUE);
        else
            lastMove.setInheritedValue(-WINNING_VALUE);

        return true;
    }

    public final long getNumMovesConsidered()
    {
        return movesConsidered_;
    }

    public final int getPercentDone()
    {
        return percentDone_;
    }

    /**
     * Set an optional ui component that will update when the search tree is modified.
     * @param listener game tree listener
     */
    public void setGameTreeEventListener(GameTreeViewable listener) {
        gameTree_ = listener;
    }

    /**
     * Update the percentage done serching variable for the progress bar
     * if we are at the top level (otherwise this is a no-op).
     */
    protected void updatePercentDone(int depth, List remainingNextMoves) {
        if (depth == lookAhead_)   {
            percentDone_ = 100 * (numTopLevelMoves_ - remainingNextMoves.size()) / numTopLevelMoves_;
        }
    }

    /**
     * Get the next move and increment the number of moves considered.
     * @return next move in sorted generated next move list.
     */
    protected TwoPlayerMove getNextMove(MoveList list) {
        movesConsidered_ ++;
        return (TwoPlayerMove)list.remove(0);
    }

    /**
     * For minimax this is always true, but it depends on the player for the nega type searches.
     * @return true if we should evaluate the board from the point of view of player one.
     */
    protected abstract boolean fromPlayer1sPerspective(TwoPlayerMove lastMove);


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
     * @return false right away if not paused. Returns true only if
     *  a long pause has been interrupted.
     */
    boolean pauseInterrupted() {
        try {
            while (paused_) {
                Thread.sleep(100);
            }
            return false;
        } catch (InterruptedException e) {
            //e.printStackTrace();
            GameContext.log(2, "interrupted" );
            e.printStackTrace();
            return true;
        }
    }
}