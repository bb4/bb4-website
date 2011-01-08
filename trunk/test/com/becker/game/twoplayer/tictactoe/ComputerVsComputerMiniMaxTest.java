package com.becker.game.twoplayer.tictactoe;

import com.becker.common.Location;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.ISearchableHelper;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;

import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class ComputerVsComputerMiniMaxTest extends TestCase {

    protected TwoPlayerController controller;
    protected SearchOptions searchOptions;
    protected ISearchableHelper helper;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        helper = new TicTacToeHelper();
        controller = helper.createController();
        TwoPlayerOptions options = helper.createTwoPlayerGameOptions();
        searchOptions = options.getSearchOptions();
        searchOptions.setSearchStrategyMethod(SearchStrategyType.MINIMAX);
        searchOptions.getBruteSearchOptions().setLookAhead(4);
        searchOptions.getBruteSearchOptions().setAlphaBeta(false);

        controller.setOptions(options);
    }


    /**
     * In a computer vs computer game, the second move must be in the corner, not on the edge
     */
    public void testSecondMoveInCorner() {

        controller.computerMovesFirst();
        TwoPlayerMove move = (TwoPlayerMove)controller.getLastMove();
        assertEquals("Unexpected first move for computer.", new Location(2, 2), move.getToLocation());

        controller.requestComputerMove(false, true);
        move = (TwoPlayerMove)controller.getLastMove();
        assertEquals("Unexpected second move for computer.", new Location(1, 1), move.getToLocation());
    }
}