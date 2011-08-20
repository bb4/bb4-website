package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test that we can get the correct type and status for single point eyes.
 *
 * @author Barry Becker
 */
public class TestE1Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E1;
    }

    public void testSingleSpaceEye1() {
        GoBoard b = initializeBoard("single_space_eye1");

        checkBlackEye(b, new E1Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E1Information(), EyeStatus.NAKADE);
    }

    /** test edge and corner */
    public void testSingleSpaceEyeOnEdge1() {
        GoBoard b = initializeBoard("single_space_on_edge_eye1");

        checkBlackEye(b, new E1Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E1Information(), EyeStatus.NAKADE);
    }


    public static Test suite() {
        return new TestSuite(TestE1Information.class);
    }
}