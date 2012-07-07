/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.blockade;

import com.becker.common.util.FileUtil;
import com.becker.game.common.player.Player;
import com.becker.game.twoplayer.blockade.board.move.BlockadeMove;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;
import com.becker.ui.file.GenericFileFilter;
import junit.framework.*;

/**
 *Base class for all Blockade test cases.
 * @author Barry Becker
 */
public abstract class BlockadeTestCase extends TestCase {

    /** moved all test cases here so they are not included in the jar and do not need to be searched */
    private static final String EXTERNAL_TEST_CASE_DIR =
            FileUtil.getHomeDir() +"/test/com/becker/game/twoplayer/blockade/cases/";

    private static final String SGF_EXTENSION = ".sgf";

    protected BlockadeController controller_;

    /**
     * common initialization for all test cases.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // this will load the resources for the specified game.
        GameContext.loadGameResources("blockade");
        GameContext.setDebugMode(0);

        controller_ = new BlockadeController();

        for (Player player : controller_.getPlayers())  {
            SearchOptions sOptions = ((TwoPlayerPlayerOptions)player.getOptions()).getSearchOptions();
            sOptions.getBruteSearchOptions().setAlphaBeta(true);
            sOptions.getBruteSearchOptions().setLookAhead(3);
            sOptions.getBestMovesSearchOptions().setPercentageBestMoves(100);
            //sOptions.setQuiescence(true); // takes too long if on
            sOptions.setSearchStrategyMethod(SearchStrategyType.MINIMAX);
        }
    }

    protected void restore(String problemFile) {
        controller_.restoreFromFile(EXTERNAL_TEST_CASE_DIR + problemFile + SGF_EXTENSION);
    }

    /**
     * @param directory to search for the files in.
     * @param pattern desired.
     * @return all the files matching the supplied pattern in the specified directory
     */
    protected static String[] getFilesMatching(String directory, String pattern) {

        return GenericFileFilter.getFilesMatching(EXTERNAL_TEST_CASE_DIR + directory, pattern);
    }

    protected Move getNextMove(String problemFile, boolean firstPlayerPlays) {


        GameContext.log(1, "finding next move for "+problemFile+" ...");
        long time = System.currentTimeMillis();
        restore(problemFile);
        controller_.requestComputerMove( firstPlayerPlays, true );

        Move m = controller_.getLastMove();

        long elapsedTime = (System.currentTimeMillis() - time) / 1000;
        GameContext.log(1, "got " + m + " in " + elapsedTime + " seconds.");
        return m;
    }

    protected static void checkExpected(BlockadeMove m, int row, int col) {

        Assert.assertTrue("Was expecting "+ row +", "+ col +", but instead got "+m,
                          m.getToRow() == row && m.getToCol() == col);
    }

}