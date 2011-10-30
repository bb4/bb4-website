/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common.search;

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
     * @return test file containing state of saved game to restore.
     */
    String getTestFile(String problemFileBase);

    /**
     * @return test file containing state of saved default game to restore.
     */
    String getDefaultTestFile();

    /**
     * @param progress how far into the game are we.
     * @param player1 true if player one has just palyed.
     * @return get the game file corresponding to the given amount of progress and the specified player.
     */
    String getTestFile(Progress progress, boolean  player1);
}