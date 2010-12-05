package com.becker.game.twoplayer.common;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;

import java.util.Collections;

/**
 * Find the best moves from a list of reasonable next moves using configured search options
 *
 *  @author Barry Becker
 */
public class BestMoveFinder {

    private BestMovesSearchOptions searchOptions_;

    /**
     * Constructor.
     */
    public BestMoveFinder(BestMovesSearchOptions searchOptions) {
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
        if (player1 == player1sPerspective) {
           Collections.reverse( moveList );
        }

        return determineBestMoves(moveList);
    }

    /**
     * Select just the best moves after sorting the reasonable next moves.
     * We could potentially eliminate the best move doing this, but we need to trade that off against search time.
     * A move which has a low score this time might actually lead to the best move later.
     *
     * @param moveList reasonable next moves.
     * @return  set of best moves from the orignal list
     */
    private MoveList determineBestMoves(MoveList moveList) {

        int minBest = searchOptions_.getMinBestMoves();
        int percentLessThanBestThresh = searchOptions_.getPercentLessThanBestThresh();
        MoveList bestMoveList;

        if (percentLessThanBestThresh > 0)   {
            bestMoveList =
                    determineMovesExceedingValueThresh(moveList, minBest, percentLessThanBestThresh);
        }
        else {
            int topPercent = searchOptions_.getPercentageBestMoves();
            bestMoveList =
                    determineTopPercentMoves(moveList, minBest, topPercent);
        }
        return bestMoveList;
    }

    /**
     *
     * @return top moves
     */
    private MoveList determineMovesExceedingValueThresh(MoveList moveList, int minBest, int percentLessThanBestThresh) {

        int numMoves = moveList.size();
        MoveList bestMoveList = new MoveList();
        if (numMoves > 0) {
            Move currentMove = moveList.getFirstMove();
            double thresholdValue = currentMove.getValue() * (1.0  - (float)percentLessThanBestThresh/100.0);

            bestMoveList.add(currentMove);
            int ct = 1;

            while ((currentMove.getValue() > thresholdValue || ct < minBest) && ct < numMoves) {
                currentMove = moveList.get(ct++);
                bestMoveList.add(currentMove);
            }
        }
        return bestMoveList;
    }

    /**
     *
     * @return top moves
     */
    private MoveList determineTopPercentMoves(MoveList moveList, int minBest, int topPercent) {
        int numMoves = moveList.size();
        MoveList bestMoveList = moveList;
        int best = (int) ((float) topPercent / 100.0 * numMoves) + 1;
        if ( best < numMoves && numMoves > minBest)  {
            bestMoveList = moveList.subList(0, best);
        }
        return bestMoveList;
    }

}