package com.becker.game.twoplayer.tictactoe;

import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.SearchableHelper;

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
        return getMiddleGameMoveFileName(true);
    }   

    @Override
    protected String getStartGameMoveFileName(boolean player1) {
        return player1 ? "midGameCenterX" : "midGameCenterO";
    }

    @Override
    protected String getMiddleGameMoveFileName(boolean player1) {
        return player1 ? "lateMidGameX" : "lateMidGameO";
    }

    @Override
    protected String getEndGameMoveFileName(boolean player1) {
        return player1 ? "endGameX" : "endGameO";
    }
}
