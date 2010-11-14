package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

import java.util.List;

/**
 *  Implementation of Upper Confidedence Tree (UCT) search strategy.
 *  This method uses a monte carlo (stochastic) method and is fundamentally different than minimax and its derivatives.
 *  It's sublcasses define the key search algorithms for 2 player zero sum games with perfect information.
 *
 *  @author Barry Becker
 */
public class UctStrategy extends AbstractSearchStrategy {

    /**
     * Construct the strategy.
     * do not call directly. Use createSearchStrategy factory method instead.
     * @param searchable the game controller that has options and can make/undo moves.
     * @param weights coefficients for the evaluation polynomial that indirectly determines the best move.
     */
    UctStrategy( Searchable searchable, ParameterArray weights ) {
        super(searchable, weights);
    }

    @Override
    public SearchOptions getOptions() {
        return searchable_.getSearchOptions();
    }

    /**
     * {@inheritDoc}
     */
    public TwoPlayerMove search(TwoPlayerMove lastMove, SearchTreeNode parent) {

        return searchInternal( lastMove, parent );
    }

     /**
     * {@inheritDoc}
     */
    TwoPlayerMove searchInternal(TwoPlayerMove lastMove, SearchTreeNode parent) {

        boolean done = searchable_.done( lastMove, false);

        // TODO
        return lastMove;
    }


    /**
     * Update the percentage done serching variable for the progress bar
     * if we are at the top level (otherwise this is a no-op).
     */
    @Override
    protected void updatePercentDone(int depth, List remainingNextMoves) {
        percentDone_ = 100 * (numTopLevelMoves_ - remainingNextMoves.size()) / numTopLevelMoves_;
    }


    /**
     * For minimax this is always true, but it depends on the player for the nega type searches.
     * @return true if we should evaluate the board from the point of view of player one.
     */
    @Override
    protected boolean fromPlayer1sPerspective(TwoPlayerMove lastMove) {
        return true;
    }


}