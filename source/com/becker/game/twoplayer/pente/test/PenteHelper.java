package com.becker.game.twoplayer.pente.test;

import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.pente.PenteController;
import com.becker.game.twoplayer.pente.PenteOptions;
import com.becker.game.twoplayer.tictactoe.TicTacToeController;
import com.becker.game.twoplayer.tictactoe.TicTacToeOptions;

/**
 * @author Barry Becker
 */
public class PenteHelper extends SearchableHelper {

    public TwoPlayerOptions createTwoPlayerGameOptions() {
        return new PenteOptions();
    }

    public TwoPlayerController createController() {
        return new PenteController(10, 10);
    }

    @Override
    public String getTestCaseDir() {
        return EXTERNAL_TEST_CASE_DIR + "pente/cases/searchable/";
    }

    @Override
    protected String getDefaultFileName() {
        return "XXXX";
    }

    @Override
    protected String getStartGameMoveFileName(boolean player1) {
        return player1 ? "x" : "y";
    }

    @Override
    protected String getMiddleGameMoveFileName(boolean player1) {
        return player1 ? "x" : "y";
    }

    @Override
    protected String getEndGameMoveFileName(boolean player1) {
        return player1 ? "x" : "y";
    }
}