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
public class TestFalseEyeInformation extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.FalseEye;
    }

    public void testFalseKoEye1() {
        GoBoard b = initializeBoard("false_ko_eye1");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.KO);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.KO);
    }

    public void testFalseKoEye2() {
        GoBoard b = initializeBoard("false_ko_eye2");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.KO);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.KO);
    }

    public void testFalseBasicEyeTwoDeep() {
        GoBoard b = initializeBoard("false_basic_eye2");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.KO);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.KO);
    }

    public void testFalseBasicEyeThreeDeep() {
        GoBoard b = initializeBoard("false_basic_eye3");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
    }

    public void testFalseBasicEyeFourDeep() {
        GoBoard b = initializeBoard("false_basic_eye4");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
        checkEdgeWhiteEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
    }

    public void testFalseBasicEyeFiveDeep() {
        GoBoard b = initializeBoard("false_basic_eye5");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
    }

    public void testFalseBasicEyeSixDeep() {
        GoBoard b = initializeBoard("false_basic_eye6");

        checkCornerBlackEye(b, new FalseEyeInformation(), EyeStatus.NAKADE);
        checkEdgeWhiteEye(b, new FalseEyeInformation(), EyeStatus.NAKADE);
    }


    public static Test suite() {
        return new TestSuite(TestFalseEyeInformation.class);
    }
}