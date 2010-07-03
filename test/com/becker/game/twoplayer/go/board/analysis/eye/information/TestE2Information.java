package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test that we can get the correct type and status for all the different eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE2Information extends TestEyeTypeAnalyzer {

    public void testTwoSpaceEye1() {
        GoBoard b = initializeBoard("two_space_eye1");

        checkBlackEye(b, new E2Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E2Information(), EyeStatus.NAKADE);
    }

    public void testTwoSpaceEyeOnEdge1() {
        GoBoard b = initializeBoard("two_space_eye_on_edge1");

        checkBlackEye(b, new E2Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E2Information(), EyeStatus.NAKADE);
    }

    public static Test suite() {
        return new TestSuite(TestE2Information.class);
    }
}