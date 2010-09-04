package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;

import static com.becker.game.twoplayer.go.board.analysis.eye.information.E6Information.Eye6Type.*;

/**
 * Test that we can get the correct type and status for all the different 6 space eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE6Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E6;
    }

    public void testSixStraightSpaceEye() {
        GoBoard b = initializeBoard("six_space_straight_eye", 2);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }
    // TODO


    public static Test suite() {
        return new TestSuite(TestE5Information.class);
    }
}