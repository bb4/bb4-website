/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common;

import com.becker.game.common.AbstractGameProfiler;
import com.becker.game.common.GameProfiler;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;


/**
 * For searching two player games
 *
 * @author Barry Becker
 */
public abstract class AbstractSearchable implements Searchable {

    protected MoveList moveList_;
    protected SearchStrategy strategy_;

    /**
     * Constructor.
     */
    public AbstractSearchable(MoveList moveList) {

        moveList_ = moveList;
    }

    public TwoPlayerMove searchForNextMove(ParameterArray weights, TwoPlayerMove lastMove,
                                           IGameTreeViewable treeViewer) {

        getProfiler().startProfiling();

        strategy_ = getSearchOptions().getSearchStrategy(this, weights);

        SearchTreeNode root = null;
        if (treeViewer != null) {
            strategy_.setGameTreeEventListener(treeViewer);
            root = treeViewer.getRootNode();
        }

        TwoPlayerMove nextMove = strategy_.search( lastMove, root );
        getProfiler().stopProfiling(strategy_.getNumMovesConsidered());
        return nextMove;
    }

    public SearchStrategy getSearchStrategy() {
        return strategy_;
    }

    public int getNumMoves() {
        return moveList_.getNumMoves();
    }

    public MoveList getMoveList() {
        return moveList_;
    }

    /** @return the search options to use */
    public abstract SearchOptions getSearchOptions();

    protected AbstractGameProfiler getProfiler() {
        return GameProfiler.getInstance();
    }
}