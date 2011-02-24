package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoEye;
import com.becker.game.twoplayer.go.board.elements.GoGroup;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeType;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Set;

/**
 * Test that we can get the correct type and status for all the different eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestBigEyeAnalyzer extends GoTestCase {

    private static final String PATH_PREFIX = "board/analysis/eye/";


    protected String getPathPrefix() {
        return PATH_PREFIX;
    }

    public void testE111223() {

        restore(PATH_PREFIX + "BigEye_E111223");

        GoBoard board = (GoBoard)controller_.getBoard();
        checkEyeType(board, EyeType.E6.getInformation("E111223"), true, true, true);
    }

    public void testE111223b() {

        restore(PATH_PREFIX + "BigEye_E111223b");

        GoBoard board = (GoBoard)controller_.getBoard();
        checkEyeType(board, EyeType.E6.getInformation("E111223"), true, true, true);
    }

    /**
     * Check information for specified eye.
     */
    protected void checkEyeType(GoBoard board,
                              EyeInformation expectedInfo, boolean isBlack,
                              boolean isInCorner, boolean isOnEdge) {

        GoGroup group = getBiggestGroup(isBlack);

        Set<GoEye> eyes = group.getEyes(board);

        assertEquals("The group\n" + group + "\n did not have one eye",
                1, eyes.size());
        GoEye firstEye = group.getEyes(board).iterator().next();

        BigEyeAnalyzer eyeAnalyzer = new BigEyeAnalyzer(firstEye);


        EyeInformation information = eyeAnalyzer.determineEyeInformation();

        String eyeColor = isBlack? "black" : "white";
        assertEquals("Unexpected information found for " + eyeColor + " eye.",
                expectedInfo, information);

        assertEquals("Corner status unexpected", isInCorner, information.isInCorner(firstEye));
        assertEquals("Edge status unexpected", isOnEdge, information.isOnEdge(firstEye));
    }


    public static Test suite() {
        return new TestSuite(TestBigEyeAnalyzer.class);
    }


}