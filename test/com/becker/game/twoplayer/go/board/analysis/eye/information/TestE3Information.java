package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test that we can get the correct type and status for all the different 3 eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE3Information extends TestEyeTypeAnalyzer {

    @Override
    protected String getPathPrefix() {
        return PATH_PREFIX + "information/E3/";
    }

    public void testThreeSpaceEye() {
        GoBoard b = initializeBoard("three_space_eye");

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }

    public void testThreeSpaceEyeBent() {
        GoBoard b = initializeBoard("three_space_eye_bent");

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }

    public void testThreeSpaceEyeOnEdge() {
        GoBoard b = initializeBoard("three_space_eye_on_edge");

        checkEdgeBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkEdgeWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }

    public void testThreeSpaceEyeInCorner() {
        GoBoard b = initializeBoard("three_space_eye_in_corner");

        checkCornerBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkCornerWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }

    public void testThreeSpaceEyeKilled() {
        GoBoard b = initializeBoard("three_space_eye_killed", 4);

        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeBentKilled() {
        GoBoard b = initializeBoard("three_space_eye_bent_killed", 4);

        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeOnEdgeKilled() {
        GoBoard b = initializeBoard("three_space_eye_on_edge_killed", 4);

        checkEdgeBlackEye(b, new E3Information(), EyeStatus.NAKADE);
        checkEdgeWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeInCornerKilled() {
        GoBoard b = initializeBoard("three_space_eye_in_corner_killed", 4);

        checkCornerBlackEye(b, new E3Information(), EyeStatus.NAKADE);
        checkCornerWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeWithTwoFilled() {
        GoBoard b = initializeBoard("three_space_eye_two_filled", 4);

        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeWithTwoEndsFilled() {
        GoBoard b = initializeBoard("three_space_eye_two_ends_filled", 6);

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }


    public static Test suite() {
        return new TestSuite(TestE3Information.class);
    }
}