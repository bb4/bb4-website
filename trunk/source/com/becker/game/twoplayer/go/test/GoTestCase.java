package com.becker.game.twoplayer.go.test;

import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoGroup;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.common.util.Util;
import com.becker.common.Location;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.twoplayer.go.*;
import com.becker.ui.filefilter.GenericFileFilter;
import junit.framework.*;

import java.io.*;
import java.util.*;

/**
 *Base class for all Go test cases.
 *@@ merge with BlockadeTestCase.
 *
 * @author Barry Becker
 */
public class GoTestCase extends TestCase {


    // moved all test cases here so they are not included in the jar and do not need to be searched
    private static final String EXTERNAL_TEST_CASE_DIR =
            GameContext.getHomeDir() + "/test/go/cases/";

    private static final String SGF_EXTENSION = ".sgf";

    /** usually 0, but 1 or 2 may be useful when debugging. */
    private static final int DEBUG_LEVEL = 0;

    protected GoController controller_;

    /**
     * common initialization for all go test cases.
     * Override setOptionOverides if you want different search parameters.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // this will load the resources for the specified game.
        GameContext.loadGameResources("go");
        GameContext.setDebugMode(DEBUG_LEVEL);

        controller_ = new GoController(getBoardSize(), getBoardSize(), 0);

        TwoPlayerOptions options = controller_.getTwoPlayerOptions();
        setOptionOverrides(options);
    }

    /**
     * Derived classes should override if they want different options.
     * @param options default options to override
     */
    protected void setOptionOverrides(TwoPlayerOptions options) {
        options.setAlphaBeta(true);
        options.setLookAhead(2);
        options.setPercentageBestMoves(40);
        //options.setQuiescence(true); // takes too long if on
        options.setSearchStrategyMethod(SearchStrategyType.MINIMAX);
    }

    /**
     * Override if your test suite requires a different size.
     * @return board size to use for tests.
     */
    protected int getBoardSize() {
        return 13;
    }

    protected void restore(String problemFile) {
        controller_.restoreFromFile(EXTERNAL_TEST_CASE_DIR + problemFile + SGF_EXTENSION);
    }


    /**
     * @param pattern
     * @return all the files matching the supplied pattern in the specified directory
     */
    protected static String[] getFilesMatching(String directory, String pattern) {

        return GenericFileFilter.getFilesMatching(EXTERNAL_TEST_CASE_DIR + directory, pattern);
    }

    protected GoMove getNextMove(String problemFile, boolean blackPlays) {
        System.out.println("finding next move for "+problemFile+" ...");
        long time = System.currentTimeMillis();
        restore(problemFile);
        //System.out.println("problem restored.");
        controller_.requestComputerMove( blackPlays, true );

        GoMove m = (GoMove) controller_.getBoard().getLastMove();

        double elapsedTime = (System.currentTimeMillis() - time) / 1000.0;
        System.out.println("got " + m + " in " + Util.formatNumber(elapsedTime) + " seconds.");
        return m;
    }

    protected static void verifyExpected(GoMove m, int row, int col) {

        Assert.assertTrue("Was expecting "+ row +", "+ col +", but instead got "+m,
                      isExpected(m, row, col));
    }



    protected static boolean isExpected(GoMove m, Location loc) {

        return isExpected(m, m.getToRow(), loc.getCol());
    }

    protected static boolean isExpected(GoMove m, int row, int col) {

        return m.getToRow() == row && m.getToCol() == col;
    }


    protected void updateLifeAndDeath(String problemFile) {
        GameContext.log(0, "finding score for "+problemFile+" ...");
        restore(problemFile);

        // must check the worth of the board once to update the scoreContributions fo empty spaces.
        //List moves =
        controller_.getMoveList();
        //double w = controller_.worth((GoMove)moves.get(moves.size()-3), controller_.getDefaultWeights(), true); // need?
        controller_.updateLifeAndDeath();   // this updates the groups and territory as well.
    }


    /**
     * @param isBlack true if black
     * @return the biggest black group if black is true else biggest white group.
     */
    protected GoGroup getBiggestGroup(boolean isBlack) {

        Set<GoGroup> groups = ((GoBoard) controller_.getBoard()).getGroups();
        GoGroup biggestGroup = null;

        for (GoGroup group : groups) {
            Set<GoBoardPosition> stones = group.getStones();
            if (stones.iterator().next().getPiece().isOwnedByPlayer1() == isBlack) {
                if (biggestGroup == null || group.getNumStones() > biggestGroup.getNumStones()) {
                    biggestGroup = group;
                }
            }
        }
        return biggestGroup;
    }


    protected static boolean approximatelyEqual(double value, double expectedValue, double thresh) {
        return (Math.abs(value - expectedValue) < thresh);
    }


    protected void tearDown() throws Exception {
        super.tearDown();
    }

}