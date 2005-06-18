package com.becker.game.twoplayer.go.test;

import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;
import com.becker.game.twoplayer.go.GoBoard;
import com.becker.game.twoplayer.go.GoBoardPosition;
import com.becker.game.twoplayer.go.GoGroup;
import com.becker.game.common.BoardPosition;

import java.util.Set;


/**
 * Use Benson's algoritm to test for unconditional life of a group
 * @author Barry Becker
 */
public class TestUnconditionalLife extends GoTestCase {


    public void testUnconditionalLife1() {
        verifyUnconditionalLife("problem_unconditional_life1", true);
    }

    public void testUnconditionalLife2() {
        verifyUnconditionalLife("problem_unconditional_life2", true);
    }

     public void testUnconditionalLife3() {
        verifyUnconditionalLife("problem_unconditional_life3", false);
    }


    /**
     *
     * @param problemFile
     * @param unconditionaLifeExpected if true then we expect the black group to be unconditionally alive
     */
    private void verifyUnconditionalLife(String problemFile,
                                         boolean unconditionaLifeExpected) {

        System.out.println("Checking unconditional life for "+problemFile+" ...");
        restore(problemFile);

        GoBoard board = (GoBoard) controller_.getBoard();
        Set groups = board.getGroups();

        // consider only the biggest black group.
        GoGroup biggestBlackGroup = getBiggestGroup(true);

        Assert.assertTrue( "The following group was expected to have unconditionaLife = "
                            +unconditionaLifeExpected+" \n"+ biggestBlackGroup,
                           unconditionaLifeExpected == biggestBlackGroup.isUnconditionallyAlive(board));
    }




    public static Test suite() {
        return new TestSuite(TestUnconditionalLife.class);
    }
}