package com.becker.game.twoplayer.go.board.analysis.neighbor;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import junit.framework.Assert;

/**
 * Verify that all our neighbor analysis methods work.
 * @author Barry Becker
 */
public class TestStringNeighborAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/neighbor/";


    public void testUnoccupiedNobiNbrs() {
        verifyStringNbrs("nobiNbr_Unoccupied", 5, 5, 2);
    }

    public void testUnoccupiedNobiNbrsInCorner() {
        verifyStringNbrs("nobiNbr_UnoccupiedInCorner", 1, 1, 2);
    }

    public void testFriendNobiNbrsOneBlack() {
        verifyStringNbrs("nobiNbr_OneBlackFriend", 5, 5, 1);
    }

    public void testFriendNobiNbrsFourWhite() {
        verifyStringNbrs("nobiNbr_FourWhiteFriends", 5, 5, 4);
    }

    public void testEnemyNobiNbrsOneWhite() {
        verifyStringNbrs("nobiNbr_OneWhiteEnemy", 5, 5, 1);
    }

    public void testEnemyNobiNbrsFourBlack() {
        verifyStringNbrs("nobiNbr_FourBlackEnemies", 5, 5, 4);
    }

    public void testNotFriendNobiNbrsTwo() {
        verifyStringNbrs("nobiNbr_TwoNotFriends", 5, 5, 2);
    }

    public void testNotFriendNobiNbrsThree() {
        verifyStringNbrs("nobiNbr_ThreeNotFriends", 5, 5, 3);
    }


    private void verifyStringNbrs(String file, int row, int col,
                                int expectedNumNbrs) {
        restore(PREFIX +file);

        GoBoard board = (GoBoard)controller_.getBoard();
        StringNeighborAnalyzer stringAnalyzer_ = new StringNeighborAnalyzer(board);
        GoBoardPosition pos = (GoBoardPosition) board.getPosition(row, col);

        int numNbrs = stringAnalyzer_.findStringNeighbors(pos).size();

        Assert.assertEquals("Unexpected number of neigbors.",
                expectedNumNbrs, numNbrs);
    }
}