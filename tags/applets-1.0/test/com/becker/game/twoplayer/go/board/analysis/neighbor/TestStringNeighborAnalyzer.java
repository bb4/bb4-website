/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.neighbor;

import com.becker.common.geometry.Box;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionList;
import junit.framework.Assert;

/**
 * Verify that all our neighbor analysis methods work.
 * @author Barry Becker
 */
public class TestStringNeighborAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/neighbor/";

    /** instance under test */
    StringNeighborAnalyzer stringAnalyzer_;
    GoBoard board_;

    public void testPushStringNbrsNone() {
        initializeAnalyzer("stringNbr_Unoccupied");
        verifyPushStringNbrs(5, 5, true, true, 0);
        verifyPushStringNbrs(5, 5, false, true, 0);
        verifyPushStringNbrs(5, 5, true, false, 0);
        verifyPushStringNbrs(5, 5, false, false, 0);
    }

    public void testPushStringNbrsOne() {
        initializeAnalyzer("stringNbr_oneStone");
        verifyPushStringNbrs(5, 5, true, true, 1);
        verifyPushStringNbrs(5, 8, false, true, 1);
        verifyPushStringNbrs(5, 5, true, false, 1);
        verifyPushStringNbrs(5, 8, false, false, 2);
    }

    public void testfindStringOne() {
        initializeAnalyzer("findString_oneStone");
        verifyFindFriendString(5, 5, true, 1);
        verifyFindFriendString(5, 8, false, 1);
    }

    public void testfindStringTwo() {
        initializeAnalyzer("findString_twoStones");
        verifyFindFriendString(5, 5, true, 2);
        verifyFindFriendString(5, 8, false, 2);
    }

    public void testfindStringFour() {
        initializeAnalyzer("findString_fourStones");
        verifyFindFriendString(5, 5, true, 4);
        verifyFindFriendString(5, 8, false, 4);
    }

    public void testfindStringClosedLoop() {
        initializeAnalyzer("findString_closedLoop");
        verifyFindFriendString(5, 5, true, 10);
        verifyFindFriendString(5, 8, false, 12);
    }

    public void testfindStringClump() {
        initializeAnalyzer("findString_clump");
        verifyFindFriendString(5, 5, true, 9);
        verifyFindFriendString(5, 8, false, 10);
    }

    public void testfindEyeStringThree() {
        initializeAnalyzer("findEyeString_three");
        verifyFindNonFriendString(5, 5, true, 3);
        verifyFindNonFriendString(5, 8, false, 3);
    }

    public void testfindEyeStringLarge() {
        initializeAnalyzer("findEyeString_large");
        verifyFindNonFriendString(5, 5, true, 11);
        verifyFindNonFriendString(5, 8, false, 9);
    }

    private void initializeAnalyzer(String file) {
        restore(PREFIX +file);
        board_ = (GoBoard)controller_.getBoard();
        stringAnalyzer_ = new StringNeighborAnalyzer(board_);
    }

    private void verifyPushStringNbrs(int row, int col, boolean friendP1, boolean samePlayerOnly,
                                      int expectedNumNbrs) {
        GoBoardPosition pos = (GoBoardPosition) board_.getPosition(row, col);

        GoBoardPositionList stack = new GoBoardPositionList();
        int numNbrs = stringAnalyzer_.pushStringNeighbors(pos, friendP1, stack, samePlayerOnly);

        Assert.assertEquals("Unexpected number of neigbors pushed on stack.",
                expectedNumNbrs, numNbrs);
    }

    private void verifyFindFriendString(int row, int col, boolean friendP1, int expectedNumNbrs) {
        // same result for both neighbor types
        verifyFindString(row, col, friendP1, NeighborType.FRIEND, expectedNumNbrs);
        verifyFindString(row, col, friendP1, NeighborType.OCCUPIED, expectedNumNbrs);
    }

    private void verifyFindNonFriendString(int row, int col, boolean friendP1, int expectedNumNbrs) {
        verifyFindString(row, col, friendP1, NeighborType.NOT_FRIEND, expectedNumNbrs);
    }

    private void verifyFindString(int row, int col, boolean friendP1, NeighborType type, int expectedNumNbrs) {

        GoBoardPosition pos = (GoBoardPosition) board_.getPosition(row, col);
        Box box = new Box(1, 1, board_.getNumRows(), board_.getNumCols());
        int numNbrs =
                stringAnalyzer_.findStringFromInitialPosition(pos, friendP1, true, type, box).size();

        Assert.assertEquals("Unexpected number of stones in string.",
                expectedNumNbrs, numNbrs);
    }
}