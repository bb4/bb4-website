package com.becker.game.twoplayer.go.test.whitebox;

import com.becker.game.twoplayer.go.*;
import com.becker.game.twoplayer.go.test.*;
import junit.framework.*;

import java.util.*;

/**
 * Verify that all the methods in GoBaord work as expected
 * @author Barry Becker
 */
public class TestGoBoard extends GoTestCase {

    // verify that the right stones are captured by a given move
    public void testFindCaptures1() {
        verifyCaptures("whitebox/findCaptures1", 5, 6, 6);
    }

    public void testFindCaptures2() {
        verifyCaptures("whitebox/findCaptures2", 6, 6, 9);
    }

    public void testFindCaptures3() {
        verifyCaptures("whitebox/findCaptures3", 5, 4, 7);
    }

    public void testFindCaptures4() {
        verifyCaptures("whitebox/findCaptures4", 4, 8, 16);
    }

    public void testFindCaptures5() {
        verifyCaptures("whitebox/findCaptures5", 10, 2, 11);
    }

    private void verifyCaptures(String file, int row, int col, int numCaptures) {

        restore(file);

        GoMove move = new GoMove(row, col, 0, new GoStone(true));

        GoBoard board = (GoBoard)controller_.getBoard();

        int numWhiteStonesBefore = board.getNumStones(false);

        controller_.makeMove(move);

        int numWhiteStonesAfter = board.getNumStones(false);

        int actualNumCaptures = move.getNumCaptures();

        Assert.assertTrue("move.captures=" + actualNumCaptures + " expected "+numCaptures,
                              actualNumCaptures == numCaptures);
        int diffWhite = numWhiteStonesBefore - numWhiteStonesAfter;
        Assert.assertTrue("diff in num white stones ("+ diffWhite + ") not = numcaptures ("+numCaptures+')', diffWhite == numCaptures);

        controller_.undoLastMove();
        // verify that all the captured stones get restored to the board
        numWhiteStonesAfter = board.getNumStones(false);
        Assert.assertTrue("numWhiteStonesBefore="+numWhiteStonesBefore +" not equal numWhiteStonesAfter="+numWhiteStonesAfter,
                          numWhiteStonesBefore == numWhiteStonesAfter );
    }




    // test group neighbor detection
    public void testGetGroupNbrs1() {
        verifyGroupNbrs("whitebox/groupNbr1", 3, 3, 1, 1);
    }

    public void testGetGroupNbrs2() {
        verifyGroupNbrs("whitebox/groupNbr2", 4, 4, 6, 8);
    }

    public void testGetGroupNbrs3() {
        verifyGroupNbrs("whitebox/groupNbr3", 5, 4, 1, 1);
    }

    public void testGetGroupNbrs4() {
        verifyGroupNbrs("whitebox/groupNbr4", 4, 4, 5, 5);
    }

    // note that only nobi and diagonal enemy nbrs are considered group neighbors
    // while all 20 possible group nbs are considered for friendly stones.
    public void testGetGroupNbrs5() {
        verifyGroupNbrs("whitebox/groupNbr5", 4, 4, 0, 1);
    }

    public void testGetGroupNbrs6() {
        verifyGroupNbrs("whitebox/groupNbr6", 4, 4, 0, 3);
    }

    public void testGetGroupNbrs7() {
        verifyGroupNbrs("whitebox/groupNbr7", 4, 4, 0, 4);
    }

     public void testGetGroupNbrs8() {
        verifyGroupNbrs("whitebox/groupNbr8", 4, 4, 4, 6);
    }

    public void testGetGroupNbrs9() {
        verifyGroupNbrs("whitebox/groupNbr9", 4, 4, 2, 6);
    }

    public void testGetGroupNbrs10() {
        verifyGroupNbrs("whitebox/groupNbr10", 4, 4, 0, 1);
    }

    public void testGetGroupNbrs11() {
        verifyGroupNbrs("whitebox/groupNbr11", 4, 4, 2, 3);
    }

    public void testGetGroupNbrs12() {
        verifyGroupNbrs("whitebox/groupNbr12", 4, 4, 5, 5);
    }

    private void verifyGroupNbrs(String file, int row, int col, int expectedNumSameNbrs, int expectedNumNbrs) {
        restore(file);

        GoBoard board = (GoBoard)controller_.getBoard();
        GoBoardPosition pos = (GoBoardPosition)board.getPosition(row, col);
        int numSameNbrs = board.getGroupNeighbors(pos, true).size();
        int numNbrs = board.getGroupNeighbors(pos, false).size();

        Assert.assertTrue("numSameNbrs="+numSameNbrs+" expected "+ expectedNumSameNbrs, numSameNbrs == expectedNumSameNbrs);
        Assert.assertTrue("numNbrs="+numNbrs+" expected "+ expectedNumNbrs, numNbrs == expectedNumNbrs);
    }



    public void testFindOccupiedNbrs() {
        restore("whitebox/occupiedNbrs1");
        GoBoard board = (GoBoard)controller_.getBoard();

        List empties = new ArrayList(5);
        empties.add(board.getPosition(3, 3));
        empties.add(board.getPosition(3, 4));
        empties.add(board.getPosition(4, 3));
        empties.add(board.getPosition(4, 4));
        int numNbrs = board.findOccupiedNeighbors(empties).size();
        Assert.assertTrue("numNbrs="+numNbrs+" expected "+ 9, numNbrs == 6);
        //verifyOccupiedNbrs("whitebox/occupiedNbrs1", empties, 9);
    }




    public void testCausedAtari1() {
        restore("whitebox/causedAtari1");
        GoBoard board = (GoBoard)controller_.getBoard();

        GoMove m = new GoMove(4, 4, 0, new GoStone(false));
        int numInAtari = m.causesAtari(board);
        Assert.assertTrue("numInAtri="+numInAtari+" expected="+4, numInAtari == 4);
    }


    private void verifyOccupiedNbrs(String file, List empties, int expectedNumNbrs) {
        restore(file);

        GoBoard board = (GoBoard)controller_.getBoard();
        int numNbrs = board.findOccupiedNeighbors(empties).size();

        Assert.assertTrue("numNbrs="+numNbrs+" expected "+ expectedNumNbrs, numNbrs == expectedNumNbrs);
    }

    public void testCausedAtari2() {
        restore("whitebox/causedAtari2");

        GoMove m = new GoMove(2, 12,  0, new GoStone(true));
        controller_.makeMove(m);
        GoBoard board = (GoBoard)controller_.getBoard();
        int numInAtari = m.causesAtari(board);
        Assert.assertTrue("numInAtri="+numInAtari+" expected="+12, numInAtari == 12);
    }




    public void testNumLiberties1() {
        verifyGroupLiberties("whitebox/causedAtari2", 2, 9, 14,  2, 10, 2);
    }

    public void testNumLiberties2() {
        verifyGroupLiberties("whitebox/numLiberties2", 1, 2, 17,   3, 6, 16);
    }


    private void verifyGroupLiberties(String file,
                                      int bRow, int bCol, int expectedBlackLiberties,
                                      int wRow, int wCol, int expectedWhiteLiberties)   {
        restore(file);

        GoBoard board = (GoBoard)controller_.getBoard();

        GoBoardPosition pos = (GoBoardPosition)board.getPosition(bRow, bCol);
        int numGroupLiberties = pos.getGroup().getLiberties(board).size();
        Assert.assertTrue("numGroupLiberties="+numGroupLiberties+" expected="+expectedBlackLiberties, numGroupLiberties == expectedBlackLiberties);

        pos = (GoBoardPosition)board.getPosition(wRow, wCol);
        numGroupLiberties = pos.getGroup().getLiberties(board).size();
        Assert.assertTrue("numGroupLiberties="+numGroupLiberties+" expected="+expectedWhiteLiberties, numGroupLiberties == expectedWhiteLiberties);
    }


    public void testBadShape1() {
        verifyBadShape("whitebox/badShape1", 4, 4, 3);
    }

    public void testBadShape2() {
        verifyBadShape("whitebox/badShape2", 4, 4, 1);
    }

    public void testBadShape3() {
        verifyBadShape("whitebox/badShape3", 4, 4, 8);
    }

    public void verifyBadShape(String file, int row, int col, int expected) {
        restore(file);

        GoBoard board = (GoBoard)controller_.getBoard();
        GoBoardPosition pos = (GoBoardPosition)board.getPosition(row, col);
        int badShapeScore = board.formsBadShape(pos);
        Assert.assertTrue("badShapeScore="+badShapeScore+" expected="+expected, badShapeScore == expected);
    }


}
