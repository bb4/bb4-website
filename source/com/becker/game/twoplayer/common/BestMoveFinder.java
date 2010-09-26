package com.becker.game.twoplayer.common;

import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;

import java.util.Collections;

/**
 * Find the best moves from a list of moves
 *
 *  @author Barry Becker
 */
public class BestMoveFinder {

    SearchOptions searchOptions_;

    /**
     * Constructor.
     */
    public BestMoveFinder(SearchOptions searchOptions) {
        searchOptions_ = searchOptions;
    }


    /**
     * Take the list of all possible next moves and return just the top bestPercentage of them 
     * (or 10 moves, whichever is greater).
     *
     * sort the list so the better moves appear first.
     * This is a terrific improvement when used in conjunction with alpha-beta pruning.
     *
     * @param player1 true if its player one's turn
     * @param moveList the list of all generated moves
     * @param player1sPerspective if true than bestMoves are from player1s perspective
     * @return the best moves in order of how good they are.
     */
    public MoveList getBestMoves(boolean player1, MoveList moveList, boolean player1sPerspective ) {

        Collections.sort( moveList );

        // reverse the order so the best move (using static board evaluation) is first
        SearchStrategyType searchType = searchOptions_.getSearchStrategyMethod();
        if ( searchType.sortAscending(player1, player1sPerspective)) {
           Collections.reverse( moveList );
        }

        // We could potentially eliminate the best move doing this.
        // A move which has a low score this time might actually lead to the best move later.
        int numMoves = moveList.size();

        MoveList bestMoveList = moveList;
        int best = (int) ((float) searchOptions_.getPercentageBestMoves() / 100.0 * numMoves) + 1;
        if ( best < numMoves && numMoves > searchOptions_.getMinBestMoves())  {
            bestMoveList = moveList.subList(0, best);
        }
        return bestMoveList;
    }

}