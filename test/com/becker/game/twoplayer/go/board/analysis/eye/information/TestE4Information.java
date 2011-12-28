/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;

import static com.becker.game.twoplayer.go.board.analysis.eye.information.E4Information.Eye4Type.*;

/**
 * Test that we can get the correct type and status for all the different 4 space eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE4Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E4;
    }

    // All in a row E1122

    public void testFourBentSpaceEye() {
        GoBoard b = initializeBoard("four_space_bent_eye", 2);

        checkEdgeBlackEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE);
    }

    public void testFourBentOneVitalFilled() {
        GoBoard b = initializeBoard("four_space_bent_one_vital_filled", 4);

        checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
    }

    public void testFourBentTwoVitalsFilled() {
        GoBoard b = initializeBoard("four_space_bent_two_vitals_filled", 4);

        checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.NAKADE);
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.NAKADE);
    }

    public void testFourBentOneEndFilled() {
        GoBoard b = initializeBoard("four_space_bent_one_end_filled", 4);

        checkEdgeBlackEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE);
    }

    public void testFourBentTwoEndsFilled() {
        GoBoard b = initializeBoard("four_space_bent_two_ends_filled", 6);

        checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE);
    }


    public void testFourBentTwoEndsFilledCut() {
        GoBoard b = initializeBoard("four_space_bent_two_ends_filled_cut", 7);

        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE);
        //checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE);
    }

    public void testFourBentThreeFilled() {
        GoBoard b = initializeBoard("four_space_bent_three_filled", 4);

        checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.NAKADE);
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED); 
    }

    public void testFourAliveInAtariVitalsSplit() {
        GoBoard b = initializeBoard("four_space_bent_in_atari_vitals_split", 6);

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.NAKADE);  // ALIVE_IN_ATARI?
        checkWhiteEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.NAKADE);  // ALIVE_IN_ATARI?
    }

    public void testFourInAtariVitalsStandard() {
        GoBoard b = initializeBoard("four_space_bent_in_atari_vitals_standard", 7);

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.NAKADE);
        checkWhiteEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.NAKADE);
    }

    public void testFourAliveInAtariEndsStandard() {
        GoBoard b = initializeBoard("four_space_bent_in_atari_ends_standard", 6);

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
        checkWhiteEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
    }

    public void testFourAliveInAtariEndsSeparate() {
        GoBoard b = initializeBoard("four_space_bent_in_atari_ends_separate", 6);

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
        checkWhiteEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
    }

    public void testFourAliveInAtariEndsSplit() {
        GoBoard b = initializeBoard("four_space_bent_in_atari_ends_split", 6);

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
        checkWhiteEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
    }

    public void testFourBentEdgeKo() {
        GoBoard b = initializeBoard("four_space_bent_edge_ko", 5);

        checkEdgeBlackEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
        checkEdgeWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED);
    }

    public void testFourBentCornerKo() {
        GoBoard b = initializeBoard("four_space_bent_corner_ko", 6);

        checkCornerBlackEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED); //ALIVE_IN_ATARI or KO?);
        checkCornerWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED); // KO
    }


    // pyramid

    public void testFourPyramidSpaceEye() {
        GoBoard b = initializeBoard("four_space_pyramid_eye", 2);

        checkBlackEye(b, new E4Information(E1113.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E4Information(E1113.toString()), EyeStatus.UNSETTLED);
    }

    public void testFourPyramidSpaceVitalFilled() {
        GoBoard b = initializeBoard("four_space_pyramid_vital_filled", 4);

        checkBlackEye(b, new E4Information(E1113.toString()), EyeStatus.NAKADE);
        checkWhiteEye(b, new E4Information(E1113.toString()), EyeStatus.NAKADE);
    }


    // clump

    public void testFourClumpSpaceEye() {
        GoBoard b = initializeBoard("four_space_clump_eye", 2);

        checkBlackEye(b, new E4Information(E2222.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E4Information(E2222.toString()), EyeStatus.UNSETTLED);
    }
}