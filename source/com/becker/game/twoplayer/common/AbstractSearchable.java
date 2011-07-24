package com.becker.game.twoplayer.common;

import com.becker.common.geometry.Location;
import com.becker.game.common.*;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.transposition.HashKey;
import com.becker.game.twoplayer.common.search.transposition.ZobristHash;
import com.becker.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * For searching two player games
 *
 * @author Barry Becker
 */
public abstract class AbstractSearchable implements Searchable {

    protected SearchOptions options_;
    protected MoveList moveList_;
    protected SearchStrategy strategy_;

    /**
     * Constructor.
     */
    public AbstractSearchable(MoveList moveList, SearchOptions options) {

        moveList_ = moveList;
        options_ = options;
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

    public SearchOptions getSearchOptions() {
        return options_;
    }

    protected AbstractGameProfiler getProfiler() {
        return GameProfiler.getInstance();
    }
}