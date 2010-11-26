package com.becker.game.twoplayer.common.search.strategy;

import com.becker.common.util.Util;
import com.becker.game.common.GameContext;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.SearchWindow;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.tree.NodeAttributes;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

import java.util.List;

/**
 *  This is an abstract base class for all minimax based brute force search strategy.
 *  It's sublcasses define the key search algorithms for 2 player zero sum games with perfect information.
 *
 *  @author Barry Becker
 */
public abstract class AbstractBruteSearchStrategy extends AbstractSearchStrategy
{
    /** if true, then use alpha-beta pruning. */
    final boolean alphaBeta_;

    /** If true, then use additional qeiscent search to extent the search tree for urgent moves. */
    final boolean quiescence_;

    /** the number of plys to look ahead when searching. */
    final int lookAhead_;

    /** don't search more levels ahead than this during quiescent search. */
    private int maxQuiescentDepth_ = 0;

    /**
     * Number of moves to consider at the top ply.
     * we use this number to determine how far into the search that we are.
     */
    int numTopLevelMoves_;


    /**
     * Construct the strategy.
     * do not call directly. Use createSearchStrategy factory method instead.
     * @param searchable the game controller that has options and can make/undo moves.
     * @param weights coefficients for the evaluation polynomial that indirectly determines the best move.
     */
    AbstractBruteSearchStrategy( Searchable searchable, ParameterArray weights ) {
        super(searchable, weights);
        SearchOptions opts = getOptions();
        BruteSearchOptions bruteOpts = opts.getBruteSearchOptions();
        alphaBeta_ = bruteOpts.getAlphaBeta();
        quiescence_ = bruteOpts.getQuiescence();
        lookAhead_ = bruteOpts.getLookAhead();
        maxQuiescentDepth_ = bruteOpts.getMaxQuiescentDepth();
        GameContext.log( 2, "alpha beta=" + alphaBeta_ + " quiescence=" + quiescence_ + " lookAhead = " + lookAhead_);
    }

    @Override
    public SearchOptions getOptions() {
        return searchable_.getSearchOptions();
    }

    /**
     * {@inheritDoc}
     */
    public TwoPlayerMove search(TwoPlayerMove lastMove, SearchTreeNode parent) {

        SearchWindow window = getOptions().getBruteSearchOptions().getInitialSearchWindow();
        return searchInternal( lastMove, lookAhead_, window,  parent );
    }

    /**
     * {@inheritDoc}
     */
    TwoPlayerMove searchInternal(TwoPlayerMove lastMove,
                                int depth, SearchWindow window, SearchTreeNode parent) {

        boolean done = searchable_.done( lastMove, false);
        if ( depth <= 0 || done ) {
            if (doQuiescentSearch(depth, done, lastMove)) {
                return quiescentSearch(lastMove, depth, window, parent);
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
     * Get the next move and increment the number of moves considered.
     * @return next move in sorted generated next move list.
     */
    protected TwoPlayerMove getNextMove(MoveList list) {
        movesConsidered_++;
        return (TwoPlayerMove)list.remove(0);
    }

    /**
     * Search more if quiescense is on, depth is negative, but not yet at -maxQiuiescentDepth
     * and the last moved played created an urgent situation.
     * @return true of we should continue searching a bit to find a stable/quiescnet move.
     */
    protected boolean doQuiescentSearch(int depth, boolean done, TwoPlayerMove lastMove) {
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
    TwoPlayerMove quiescentSearch(TwoPlayerMove lastMove,
                                            int depth, SearchWindow window, SearchTreeNode parent) {

        MoveList urgentMoves = searchable_.generateUrgentMoves(lastMove, weights_, true);
        if (emptyMoveList(urgentMoves, lastMove)) return null;

        return findBestMove(lastMove, depth, urgentMoves, window, parent);
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
     * Show the node in the game tree (if one is used. It is used if parent not null).
     *
     * @param list of pruned nodes
     * @param parent the tree node entry above the current position.
     * @param i th child.
     */
    protected void showPrunedNodesInTree( MoveList list, SearchTreeNode parent,
                                          int i, int selectedValue, SearchWindow window) {
        if (hasGameTree()) {
            NodeAttributes attributes = new NodeAttributes();
            attributes.put("value", Util.formatNumber(selectedValue) );
            attributes.put("window", window.toString());
            attributes.pruned = true;
            attributes.put("pruned", "(value outside window)");
            super.addPrunedNodesInTree(list, parent, i, attributes);
        }
    }


    /**
     * add a move to the visual game tree (if parent not null).
     * @return the node added to the tree.
     */
    protected SearchTreeNode addNodeToTree( SearchTreeNode parent, TwoPlayerMove theMove,
                                         SearchWindow window )
    {
        NodeAttributes attributes = null;
        if (hasGameTree()) {
            attributes = new NodeAttributes();
            attributes.put("value", Util.formatNumber(theMove.getValue()) );
            attributes.put("inhVal", Util.formatNumber(theMove.getInheritedValue()) );
            attributes.put("window", window.toString());
        }
        return addNodeToTree(parent, theMove, attributes);
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

    protected String getIndent(int depth) {
        String indent = "";
        int numTabs = lookAhead_ - depth;
        for (int i=0; i < numTabs; i++) {
           indent += "   ";
        }
        return indent;
    }
}