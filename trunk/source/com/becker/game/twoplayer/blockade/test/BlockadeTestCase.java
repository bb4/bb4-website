package com.becker.game.twoplayer.blockade.test;

import com.becker.common.util.FileUtil;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.common.*;
import com.becker.game.twoplayer.blockade.BlockadeController;
import com.becker.game.twoplayer.blockade.BlockadeMove;
import com.becker.game.twoplayer.common.*;
import com.becker.ui.filefilter.GenericFileFilter;
import junit.framework.*;

/**
 *Base class for all Blockade test cases.
 * @author Barry Becker
 */
public class BlockadeTestCase extends TestCase {


    /** moved all test cases here so they are not included in the jar and do not need to be searched */
    private static final String EXTERNAL_TEST_CASE_DIR =
            FileUtil.getHomeDir() +"/test/blockade/cases/";

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

        TwoPlayerOptions options = controller_.getTwoPlayerOptions();
        options.setAlphaBeta(true);
        options.setLookAhead(3);
        options.setPercentageBestMoves(100);
        //options.setQuiescence(true); // take stoo long if on
        options.setSearchStrategyMethod(SearchStrategyType.MINIMAX);

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

        Move m = controller_.getBoard().getLastMove();

        long elapsedTime = (System.currentTimeMillis() - time) / 1000;
        GameContext.log(1, "got " + m + " in " + elapsedTime + " seconds.");
        return m;
    }

    protected static void checkExpected(BlockadeMove m, int row, int col) {

        Assert.assertTrue("Was expecting "+ row +", "+ col +", but instead got "+m,
                          m.getToRow() == row && m.getToCol() == col);
    }



    protected static boolean approximatelyEqual(double value, double expectedValue, double thresh) {
        return (Math.abs(value - expectedValue) < thresh);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}