package com.becker.game.twoplayer.go.test;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.twoplayer.go.*;
import junit.framework.*;

import java.io.*;
import java.util.*;

/**
 * @author Barry Becker
 */
public class GoTestCase extends TestCase {


    // moved all test cases here so they are not included in the jar and do not need to be searched
    private static final String EXTERNAL_TEST_CASE_DIR =
            GameContext.getHomeDir() +"/test/go/cases/";

    //private static final String TEST_CASE_DIR =
    //        GameContext.getHomeDir() +"/source/"  +
    //        GameContext.GAME_ROOT  + "twoplayer/go/test/cases/";

    private static final String SGF_EXTENSION = ".sgf";

    protected GoController controller_;

    /**
     * common initialization for all go test cases.
     */
    protected void setUp() throws Exception {
        super.setUp();
        // this will load the resources for the specified game.
        //GameContext.verifyGameResources("go", "com.becker.game.twoplayer.go.ui.GoPanel");
        GameContext.setDebugMode(0);

        controller_ = new GoController(13, 13, 0);

        //controller_.allPlayersComputer();
        TwoPlayerOptions options = controller_.getTwoPlayerOptions();
        options.setAlphaBeta(true);
        options.setLookAhead(4);
        options.setPercentageBestMoves(40);
        //opttions.setQuiescence(true); // take stoo long if on
        options.setSearchStrategyMethod(SearchStrategy.MINIMAX);

    }

    protected void restore(String problemFile) {
        controller_.restoreFromFile(EXTERNAL_TEST_CASE_DIR + problemFile + SGF_EXTENSION);
    }


    /**
     * @param pattern
     * @return all the files matching the supplied pattern in the specified directory
     */
    protected static String[] getFilesMatching(String directory, String pattern) {

        File dir =  new File(EXTERNAL_TEST_CASE_DIR + directory);
        assert (dir.isDirectory());

        //System.out.println("pattern = "+pattern+ "dir="+dir.getAbsolutePath());
        FilenameFilter filter = new MyFileFilter(pattern);
        String[] list = dir.list(filter);

        return list;
    }

    protected GoMove getNextMove(String problemFile, boolean blackPlays) {


        System.out.println("finding next move for "+problemFile+" ...");
        long time = System.currentTimeMillis();
        restore(problemFile);
        controller_.requestComputerMove( blackPlays, true );

        GoMove m = (GoMove) controller_.getBoard().getLastMove();

        long elapsedTime = (System.currentTimeMillis() - time) / 1000;
        System.out.println("got " + m + " in " + elapsedTime + " seconds.");
        return m;
    }

    protected static void checkExpected(GoMove m, int row, int col) {

        Assert.assertTrue("Was expecting "+ row +", "+ col +", but instead got "+m,
                          m.getToRow() == row && m.getToCol() == col);
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
     * @param black
     * @return the biggest black group if black is true else biggest white group.
     */
    protected  GoGroup getBiggestGroup(boolean black) {

        Set groups = ((GoBoard) controller_.getBoard()).getGroups();
        GoGroup biggestGroup = null;

        for (Object g : groups) {
            GoGroup group = (GoGroup)g;
            if (((GoBoardPosition)group.getStones().iterator().next()).getPiece().isOwnedByPlayer1() == black) {
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


    private static class MyFileFilter implements FilenameFilter {

        private String pattern_;

        private MyFileFilter(String pattern) {
            pattern_ = pattern;
        }

        public boolean accept(File dir, String name) {
            return (name.contains(pattern_));
        }
    }

}