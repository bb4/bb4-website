package com.becker.game.twoplayer.tictactoe.test;

import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.tictactoe.TicTacToeController;
import com.becker.game.twoplayer.tictactoe.TicTacToeOptions;

/**
 * @author Barry Becker
 */
public class TicTacToeHelper extends SearchableHelper {

    /**
     * @return default search options for all games
     */
    public TwoPlayerOptions createTwoPlayerGameOptions() {
        TwoPlayerOptions opts =  new TicTacToeOptions();
        SearchOptions options = opts.getSearchOptions();
        options.setLookAhead(2);
        options.setAlphaBeta(true);
        options.setPercentageBestMoves(100);
        options.setQuiescence(false);
        return opts;
    }

    public TwoPlayerController createController() {
        return new TicTacToeController();
    }

    @Override
    public String getTestCaseDir() {
        return EXTERNAL_TEST_CASE_DIR + "tictactoe/cases/searchable/";
    }

    @Override
    protected String getDefaultFileName() {
        return "midGameCenterX";
    }
}
