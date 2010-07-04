package com.becker.game.twoplayer.go.board.analysis.neighbor;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Verify that all our neighbor analysis methods work.
 * @author Barry Becker
 */
public class TestNobiNeighborAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/neighbor/";

    /** instance under test */
    private NobiNeighborAnalyzer nobiAnalyzer_;


    public void testNoOccupiedNobiNbrs() {
        verifyOccupiedNobiNbrs("nobiNbr_NoneOccupied", new Location[] {new Location(5, 5)}, 0);
    }

    public void testBlackOccupiedNobiNbrs() {
        verifyOccupiedNobiNbrs("nobiNbr_BlackOccupied", new Location[] {new Location(7, 7)}, 3);
    }

    public void testMixOccupiedNobiNbrs() {
        verifyOccupiedNobiNbrs("nobiNbr_MixOccupied", new Location[] {new Location(7, 7)}, 3);
    }

    public void testMixOccupiedNobiNbrsOnEdge() {
        verifyOccupiedNobiNbrs("nobiNbr_MixOccupiedOnEdge", new Location[] {new Location(5, 1)}, 2);
    }

    public void testNobiNbrsOnEdge() {
        verifyOccupiedNobiNbrs("nobiNbr_MixOccupiedOnEdge", new Location[] {new Location(5, 1)}, 2);
    }


    public void testUnoccupiedNobiNbrs() {
        verifyNobiNbrs("nobiNbr_Unoccupied", 5, 5, false, NeighborType.UNOCCUPIED, 2);
    }

    public void testUnoccupiedNobiNbrsInCorner() {
        verifyNobiNbrs("nobiNbr_UnoccupiedInCorner", 1, 1, false, NeighborType.UNOCCUPIED, 2);
    }

    public void testFriendNobiNbrsOneBlack() {
        verifyNobiNbrs("nobiNbr_OneBlackFriend", 5, 5, true, NeighborType.FRIEND, 1);
    }

    public void testFriendNobiNbrsFourWhite() {
        verifyNobiNbrs("nobiNbr_FourWhiteFriends", 5, 5, false, NeighborType.FRIEND, 4);
    }

    public void testEnemyNobiNbrsOneWhite() {
        verifyNobiNbrs("nobiNbr_OneWhiteEnemy", 5, 5, true, NeighborType.ENEMY, 1);
    }

    public void testEnemyNobiNbrsFourBlack() {
        verifyNobiNbrs("nobiNbr_FourBlackEnemies", 5, 5, false, NeighborType.ENEMY, 4);
    }

    public void testNotFriendNobiNbrsTwo() {
        verifyNobiNbrs("nobiNbr_TwoNotFriends", 5, 5, false, NeighborType.NOT_FRIEND, 2);
    }

    public void testNotFriendNobiNbrsThree() {
        verifyNobiNbrs("nobiNbr_ThreeNotFriends", 5, 5, true, NeighborType.NOT_FRIEND, 3);
    }

    private void verifyOccupiedNobiNbrs(String file, Location[] empties, int expectedNumOccupiedNbrs) {
        restore(PREFIX +file);

        GoBoard board = (GoBoard)controller_.getBoard();
        nobiAnalyzer_ = new NobiNeighborAnalyzer(board);
        List<GoBoardPosition> emptyList = createPositionList(empties);

        int numOccupiedNbrs = nobiAnalyzer_.findOccupiedNobiNeighbors(emptyList).size();

        Assert.assertEquals("Unexpected number of occupied neigbors.",
                expectedNumOccupiedNbrs, numOccupiedNbrs);
    }

    private void verifyNobiNbrs(String file, int row, int col,
                                boolean friendOwnedByP1, NeighborType type,
                                int expectedNumNbrs) {
        restore(PREFIX +file);

        GoBoard board = (GoBoard)controller_.getBoard();
        nobiAnalyzer_ = new NobiNeighborAnalyzer(board);
        GoBoardPosition pos = (GoBoardPosition) board.getPosition(row, col);

        int numNbrs = nobiAnalyzer_.getNobiNeighbors(pos, friendOwnedByP1, type).size();

        Assert.assertEquals("Unexpected number of neigbors.",
                expectedNumNbrs, numNbrs);
    }
}