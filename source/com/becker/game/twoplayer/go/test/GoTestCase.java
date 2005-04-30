package com.becker.game.twoplayer.go.test;

import junit.framework.TestCase;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.common.search.SearchStrategy;

import java.util.List;


public class GoTestCase extends TestCase {

    private static final String TEST_CASE_DIR =
            GameContext.getHomeDir() +"/source/"  +
            GameContext.GAME_ROOT  + "twoplayer/go/test/cases/";

    GoController controller_;

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
        controller_.setPercentageBestMoves(50);
        //controller_.setQuiescence(true); // take stoo long if on
        controller_.setSearchStrategyMethod(SearchStrategy.MINIMAX);

    }

    protected void restore(String problemFile) {
        controller_.restoreFromFile(TEST_CASE_DIR + problemFile + ".sgf");
    }

    GoMove getNextMove(String problemFile, boolean blackPlays) {

        System.out.println("finding next move for "+problemFile+" ...");
        restore(problemFile);
        controller_.requestComputerMove( true, blackPlays );

        GoMove m = (GoMove) controller_.getBoard().getLastMove();
        System.out.println("got " + m);
        return m;
    }


    void updateLifeAndDeath(String problemFile) {
        System.out.println("finding score for "+problemFile+" ...");
        restore(problemFile);

        // must check the worth of the board once to update the scoreContributions fo empty spaces.
        List moves = controller_.getMoveList();
        double w = controller_.worth((GoMove)moves.get(moves.size()-3), controller_.getDefaultWeights(), true);    
        controller_.updateLifeAndDeath();   // this updates the groups and territory as well.
    }


    protected void tearDown() {

    }

}