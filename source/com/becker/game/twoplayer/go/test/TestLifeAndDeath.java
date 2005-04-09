package com.becker.game.twoplayer.go.test;

import junit.framework.TestCase;
import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.ui.GUIUtil;


public class TestLifeAndDeath extends GoTestCase {


    public void testProblem57() {
        GoMove m = getNextMove("problem_life57", true);
        Assert.assertTrue(m.getToRow() == 6 && m.getToCol() ==5);
        // Assert.assertTrue(m.getToRow() == 6 && m.getToCol() == 1);
    }


     public void testProblem58() {
        GoMove m = getNextMove("problem_life58", true);
        Assert.assertTrue(m.getToRow() == 2 && m.getToCol() == 11);
        // Assert.assertTrue(m.getToRow() == 1 && m.getToCol() == 12);
    }

     public void testProblem59() {
        GoMove m = getNextMove("problem_life59", true);
        Assert.assertTrue(m.getToRow() == 11 && m.getToCol() == 8);
        // Assert.assertTrue(m.getToRow() == 12 && m.getToCol() == 1);
    }



    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {
        return new TestSuite(TestLifeAndDeath.class);
    }
}