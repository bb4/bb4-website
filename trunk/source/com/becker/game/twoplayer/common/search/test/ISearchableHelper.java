package com.becker.game.twoplayer.common.search.test;

import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;

/**
 * Created by IntelliJ IDEA. User: becker Date: Dec 31, 2009 Time: 7:32:13 AM To change this template use File |
 * Settings | File Templates.
 */
public interface ISearchableHelper {

    /**
     * Create the game options
     * @return 2 player options for use when testing..
     */
    TwoPlayerOptions createTwoPlayerGameOptions();

    /**
     * @return the controller containing the searchable to test.
     */
    TwoPlayerController createController();

    /**
     * @return test file containing state of saved default game to restore.
     */
    String getDefaultTestFile();

    /**
     * @return test file containing state of saved game to restore.
     */
    String getTestFile(String problemFileBase);
}