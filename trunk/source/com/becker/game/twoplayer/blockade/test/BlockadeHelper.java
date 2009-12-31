package com.becker.game.twoplayer.blockade.test;

import com.becker.game.twoplayer.blockade.BlockadeController;
import com.becker.game.twoplayer.blockade.BlockadeOptions;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.tictactoe.TicTacToeController;
import com.becker.game.twoplayer.tictactoe.TicTacToeOptions;

/**
 * @author Barry Becker
 */
public class BlockadeHelper extends SearchableHelper {

    public TwoPlayerOptions createTwoPlayerGameOptions() {
        return new BlockadeOptions();
    }

    public TwoPlayerController createController() {
        return new BlockadeController();
    }

    @Override
    public String getTestCaseDir() {
        return EXTERNAL_TEST_CASE_DIR + "blockade/cases/searchable/";
    }

    @Override
    protected String getDefaultFileName() {
        return "XXXX";
    }
}