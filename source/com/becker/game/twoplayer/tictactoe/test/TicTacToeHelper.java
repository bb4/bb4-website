package com.becker.game.twoplayer.tictactoe.test;

import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.tictactoe.TicTacToeController;
import com.becker.game.twoplayer.tictactoe.TicTacToeOptions;

/**
 * @author Barry Becker
 */
public class TicTacToeHelper extends SearchableHelper {

    public TwoPlayerOptions createTwoPlayerGameOptions() {
        return new TicTacToeOptions();
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
