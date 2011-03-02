package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;

import static com.becker.game.twoplayer.go.board.analysis.eye.information.E7Information.Eye7Type.E1122222;

/**
 * Test that we can get the correct type and status for all the different 7 space eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE7Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E7;
    }

    public void testSevenStraightSpaceEye() {
        GoBoard b = initializeBoard("seven_space_straight_eye", 2);

        checkBlackEye(b, new E7Information(E1122222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E7Information(E1122222.toString()), EyeStatus.ALIVE);
    }

    
    // TODO


    public static Test suite() {
        return new TestSuite(TestE5Information.class);
    }
}