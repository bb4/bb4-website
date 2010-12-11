package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoGroup;
import junit.framework.Assert;

/**
 * Verify that we come up with reasonable eye potential values (likelihood of making eyes in the group).
 *
 * @author Barry Becker
 */
public class TestEyePotentialAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/group/potential/";

    private static final double TOLERANCE = 0.001;


    public void testEyePotential_SingleIsolatedStone() {

        restoreGame("single_isolated_stone");
        verifyBlackEyePotential(1, 0.344f);
        verifyWhiteEyePotential(1, 0.344f);
    }

    public void testEyePotential_SingleStoneNearEdge() {

        restoreGame("single_stone_near_edge");
        verifyBlackEyePotential(1, 0.544f);
        verifyWhiteEyePotential(1, 0.596f);
    }

    public void testEyePotential_SingleStoneNearEnemy() {

        restoreGame("single_stone_near_enemy");
        verifyBlackEyePotential(1, 0.4865f);    // why bigger?
        verifyWhiteEyePotential(1, 0.344f);
    }

    public void testEyePotential_SingleStoneAdjacentToEnemyBlackNearEdge() {

        restoreGame("single_stone_adjacent_to_enemy_black_near_edge");
        verifyBlackEyePotential(1, 0.544f);
        verifyWhiteEyePotential(1, 0.344f);
    }

    public void testEyePotential_SingleStoneAdjacentToEnemyWhiteNearEdge() {

        restoreGame("single_stone_adjacent_to_enemy_white_near_edge");
        verifyBlackEyePotential(1, 0.344f);
        verifyWhiteEyePotential(1, 0.544f);
    }

    public void testEyePotential_IsolatedClumpy() {

        restoreGame("isolated_clumpy");
        verifyBlackEyePotential(7, 1.31f);
        verifyWhiteEyePotential(6, 0.544f);
    }

    public void testEyePotential_IsolatedClump() {

        restoreGame("isolated_clump");
        verifyBlackEyePotential(4, 0.4865f);
        verifyWhiteEyePotential(4, 0.4865f);
    }

    public void testEyePotential_ClumpNearEdge() {

        restoreGame("clump_near_edge");
        verifyBlackEyePotential(4, 0.688f);
        verifyWhiteEyePotential(4, 0.91f);
    }

    public void testEyePotential_ClumpNearEnemy() {

        restoreGame("clump_near_enemy");
        verifyBlackEyePotential(4, 0.644f);
        verifyWhiteEyePotential(4, 0.421f);
    }

    public void testEyePotential_SingleSpace() {

        restoreGame("single_space_eye");
        verifyBlackEyePotential(7, 1.24f);
        verifyWhiteEyePotential(6, 0.9421f);
    }

    public void testEyePotential_ThreeSpaces() {
        restoreGame("three_space_eye");
        verifyBlackEyePotential(9, 1.354f);
        verifyWhiteEyePotential(9, 1.276f);
    }

    public void testEyePotential_FourSpacesVertical() {
        restoreGame("four_space_vertical_eye");
        verifyBlackEyePotential(12, 1.252f);
        verifyWhiteEyePotential(12, 1.228f);
    }

    public void testEyePotential_FourSpacesHorizontal() {
        restoreGame("four_space_horizontal_eye");
        verifyBlackEyePotential(12, 1.1665963f);
        verifyWhiteEyePotential(11, 1.287f);
    }

    public void testEyePotential_BentFourSpaces() {
        restoreGame("bent_four_space_eye");
        verifyBlackEyePotential(14, 1.64f);
        verifyWhiteEyePotential(13, 1.31f);
    }

    public void testEyePotential_FourSpacesOneInside() {
        restoreGame("four_space_eye_one_inside");
        verifyBlackEyePotential(14, 1.5f);
        verifyWhiteEyePotential(13, 1.31f);
    }

    public void testEyePotential_FourSpacesTwoInside() {
        restoreGame("four_space_eye_two_inside");
        verifyBlackEyePotential(14, 1.64f);
        verifyWhiteEyePotential(13, 1.31f);
    }

    public void testEyePotential_FourSpacesThreeInside() {
        restoreGame("four_space_eye_three_inside");
        verifyBlackEyePotential(14, 1.64f);
        verifyWhiteEyePotential(13, 1.31f);
    }


    public void testEyePotential_ThreeOneSpaceJumps() {
        restoreGame("three_one_space_jumps");
        verifyBlackEyePotential(3, 1.115f);
        verifyWhiteEyePotential(3, 1.115f);
    }

    public void testEyePotential_CShape() {
        restoreGame("isolated_C_space");
        verifyBlackEyePotential(7, 0.842f);
        verifyWhiteEyePotential(7, 0.843f);
    }

    public void testEyePotential_SymmetricSmallC() {
        restoreGame("symmetric_small_c");
        verifyBlackEyePotential(5, 0.75f);
        verifyWhiteEyePotential(5, 0.666f);
    }
    
    public void testEyePotential_SymmetricLargeC() {
        restoreGame("symmetric_large_c");
        verifyBlackEyePotential(9, 1.074f);
        verifyWhiteEyePotential(9, 1.074f);
    }

    private void restoreGame(String file) {
        restore(PREFIX + file);
    }

    private void verifyBlackEyePotential(int expectedSizeOfGroup, float expectedPotential) {
        verifyEyePotential(true, expectedSizeOfGroup, expectedPotential);
    }

    private void verifyWhiteEyePotential(int expectedSizeOfGroup, float expectedPotential) {
        verifyEyePotential(false, expectedSizeOfGroup, expectedPotential);
    }



    /**
     * Use EyePotentialAnalyzer to find potiential of making eyes.
     */
    private void verifyEyePotential(boolean forBlackGroup, int expectedSizeOfGroup, float expectedPotential) {

        GoGroup group = getBiggestGroup(forBlackGroup);

        int size = group.getNumStones();
        Assert.assertEquals("Unexpected size of test group.", expectedSizeOfGroup, size);

        EyePotentialAnalyzer analyzer = new EyePotentialAnalyzer(group);
        analyzer.setBoard((GoBoard) controller_.getBoard());
        float eyePotential = analyzer.calculateEyePotential();

        Assert.assertEquals("Unexpected group eye potential", expectedPotential, eyePotential, TOLERANCE);
    }

}
