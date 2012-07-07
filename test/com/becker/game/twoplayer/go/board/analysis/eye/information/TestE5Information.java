/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;

import static com.becker.game.twoplayer.go.board.analysis.eye.information.E5Information.Eye5Type.*;

/**
 * Test that we can get the correct type and status for all the different 5 space eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE5Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E5;
    }

    public void testFiveStraightSpaceEye() {
        GoBoard b = initializeBoard("five_straight_eye", 2);

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    public void testFiveBentSpaceEye() {
        GoBoard b = initializeBoard("five_bent_eye", 2);

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    public void testFiveBentOneMiddleFilled() {
        GoBoard b = initializeBoard("five_bent_one_middle_filled", 4);

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    public void testFiveBentTwoMiddleFilled() {
        GoBoard b = initializeBoard("five_bent_two_middle_filled", 4);

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    /**
     * At first I thought this should be NAKADE, but ALIVE is actually correct because
     * at worst the group should live in seki.
     */
    public void testFiveBentThreeMiddleFilled() {
        GoBoard b = initializeBoard("five_bent_three_middle_filled", 4);

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    public void testFiveBentThreeFilledOnEnd() {
        GoBoard b = initializeBoard("five_bent_three_filled_on_end", 4);

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    public void testFiveBentFourFilled() {
        GoBoard b = initializeBoard("five_bent_four_filled", 4);

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    public void testFiveBentNearEdge() {
        GoBoard b = initializeBoard("five_bent_near_edge", 2);

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    public void testFiveBentOnEdge() {
        GoBoard b = initializeBoard("five_bent_on_edge", 2);

        checkCornerBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkEdgeWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    public void testFiveBentOnEdgeMiddleThreeFilled() {
        GoBoard b = initializeBoard("five_bent_on_edge_middle_three_filled", 4);

        checkCornerBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
        checkEdgeWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE);
    }

    public void testFiveAliveInAtari() {
        GoBoard b = initializeBoard("five_alive_in_atari", 6);

        checkWhiteEyeSurrounded(b, new E5Information(E11222.toString()), EyeStatus.ALIVE_IN_ATARI);
    }

    //////   E11123

    public void testFiveE11123Empty() {
        GoBoard b = initializeBoard("five_E11123_empty", 2);

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.ALIVE);
    }

    public void testFiveE11123OneVitalFilled() {
        GoBoard b = initializeBoard("five_E11123_one_vital_filled", 4);

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.UNSETTLED);
    }

    public void testFiveE11123TwoVitalsFilled() {
        GoBoard b = initializeBoard("five_E11123_two_vitals_filled", 4);

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE);
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE);
    }

    public void testFiveE11123TwoVitalsOneOtherFilled() {
        GoBoard b = initializeBoard("five_E11123_two_vitals_one_other_filled", 4);

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE);
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE);
    }

    public void testFiveE11123TwoVitalsTwoOthersFilled() {
        GoBoard b = initializeBoard("five_E11123_two_vitals_two_others_filled", 4);

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE);
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE);
    }

    public void testFiveE11123TwoVitalsAndEndFilled() {
        GoBoard b = initializeBoard("five_E11123_two_vitals_and_end_filled", 4);

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE);
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE);
    }

    //////   E11114

    public void testFiveStarEmpty() {
        GoBoard b = initializeBoard("five_star_empty", 2);

        checkBlackEye(b, new E5Information(E11114.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E5Information(E11114.toString()), EyeStatus.UNSETTLED);
    }

    public void testFiveStarVitalFilled() {
        GoBoard b = initializeBoard("five_star_vital_filled", 4);

        checkBlackEye(b, new E5Information(E11114.toString()), EyeStatus.NAKADE);
        checkWhiteEye(b, new E5Information(E11114.toString()), EyeStatus.NAKADE);
    }

    public void testFiveStarThreeOthersFilled() {
        GoBoard b = initializeBoard("five_star_three_others_filled", 4);

        checkBlackEye(b, new E5Information(E11114.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E5Information(E11114.toString()), EyeStatus.UNSETTLED);
    }

    public void testFiveStarVitalAndThreeOthersFilled() {
        GoBoard b = initializeBoard("five_star_vital_and_three_others_filled", 4);

        checkBlackEye(b, new E5Information(E11114.toString()), EyeStatus.NAKADE);
        checkWhiteEye(b, new E5Information(E11114.toString()), EyeStatus.NAKADE);
    }

    //////   E122223

    public void testFiveE122223Empty() {
        GoBoard b = initializeBoard("five_E12223_empty", 2);

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
    }

    public void testFiveE122223EmptyClose() {
        GoBoard b = initializeBoard("five_E12223_empty_close", 2);

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
    }

    public void testFiveE122223VitalFilled() {
        GoBoard b = initializeBoard("five_E12223_vital_filled", 4);

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
    }

    public void testFiveE122223VitalAndTwoOthersFilled() {
        GoBoard b = initializeBoard("five_E12223_vital_and_two_others_filled", 4);

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
    }

    public void testFiveE122223VitalAndEndFilled() {
        GoBoard b = initializeBoard("five_E12223_vital_and_end_filled", 4);

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED);
    }
}