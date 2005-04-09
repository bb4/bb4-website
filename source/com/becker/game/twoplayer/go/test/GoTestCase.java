package com.becker.game.twoplayer.go.test;

import junit.framework.TestCase;
import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.common.search.SearchStrategy;


public class GoTestCase extends TestCase {

    protected static final String TEST_CASE_DIR =
            GameContext.getHomeDir() +"/projects/java_projects/source/"  +
            GameContext.GAME_ROOT  + "twoplayer/go/test/";

    protected GoController controller_;

    /**
     * common initialization for all go test cases.
     */
    protected void setUp() {
        // this will load the resources for the specified game.
        GameContext.loadGameResources("go", "com.becker.game.twoplayer.go.ui.GoPanel");
        GameContext.setDebugMode(0);

        controller_ = new GoController(13, 13, 0);

        //controller_.allPlayersComputer();
        controller_.setAlphaBeta(true);
        controller_.setLookAhead(4);
        controller_.setPercentageBestMoves(30);
        //controller_.setQuiescence(true); // take stoo long if on
        controller_.setSearchStrategyMethod(SearchStrategy.MINIMAX);

    }

    protected GoMove getNextMove(String problemFile, boolean blackPlays) {
        System.out.println("testing "+problemFile+" ...");
        controller_.restoreFromFile(TEST_CASE_DIR + problemFile + ".sgf");
        controller_.requestComputerMove( true, blackPlays );
        GoMove m = (GoMove) controller_.getLastMove();
        System.out.println("got " + m);
        return m;
    }

    protected void tearDown() {

    }

}