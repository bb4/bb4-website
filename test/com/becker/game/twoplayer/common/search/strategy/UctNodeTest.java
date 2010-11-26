package com.becker.game.twoplayer.common.search.strategy;

import com.becker.common.Location;
import com.becker.common.util.Util;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.becker.game.twoplayer.pente.Patterns;
import com.becker.game.twoplayer.pente.PentePatterns;
import com.becker.game.twoplayer.pente.StubPatterns;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class UctNodeTest extends TestCase  {

    /** instance under test */
    UctNode  uctNode;

    /** a move to put in the node */
    private static final TwoPlayerMove MOVE = new TwoPlayerMoveStub(10, false, new Location(1,1), null);

    private static final double TOL = 0.0001;


    public void testConstructionOfNodeWithNoChildren() {

        uctNode = new UctNode(MOVE);

        assertEquals("Unexpected move", MOVE, uctNode.move);
        assertEquals("Unexpected attrs", "{visits=0, wins=0}", uctNode.getAttributes().toString());
        assertEquals("Unexpected bestNode", null, uctNode.bestNode);
        assertFalse("Unexpected children", uctNode.hasChildren());
        assertEquals("Unexpected numVisits", 0, uctNode.numVisits);
        assertEquals("Unexpected winRate", 0.0, uctNode.getWinRate()); // div by 0
        assertEquals("Unexpected uctValue",
                1730.9678, uctNode.calculateUctValue(1.0, 1), TOL);
    }

    public void testUpdateWin() {

        uctNode = new UctNode(MOVE);
        uctNode.numVisits = 1;

        uctNode.updateWin(true);
        assertEquals("Unexpected numWins", 0.0, uctNode.getWinRate());

        uctNode.updateWin(false);
        assertEquals("Unexpected numWins", 1.0, uctNode.getWinRate());
    }

    public void testSetBestNodeWithAllZeroWinrate() {

        uctNode = new UctNode(MOVE);

        MoveList moves = new MoveList();
        TwoPlayerMove firstMove = new TwoPlayerMoveStub(5, false, new Location(1,1), null);
        TwoPlayerMove goodMove = new TwoPlayerMoveStub(10, false, new Location(1,2), null);
        moves.add(firstMove);
        moves.add(goodMove);
        moves.add(new TwoPlayerMoveStub(9, false, new Location(1,3), null));

        uctNode.addChildren(moves);
        uctNode.setBestNode();

        // firstMove is selected over good move if all the winreates are the same.
        assertEquals("Unexpected bestMove", firstMove, uctNode.bestNode.move);
    }

    public void testSetBestNode() {

        uctNode = new UctNode(MOVE);

        MoveList moves = new MoveList();
        TwoPlayerMove firstMove = new TwoPlayerMoveStub(5, false, new Location(1,1), null);
        TwoPlayerMove goodMove = new TwoPlayerMoveStub(10, false, new Location(1,2), null);
        moves.add(firstMove);
        moves.add(goodMove);
        moves.add(new TwoPlayerMoveStub(9, false, new Location(1,3), null));

        uctNode.addChildren(moves);
        UctNode secondNode = uctNode.getChildren().get(1);
        secondNode.numVisits = 2;
        secondNode.updateWin(false);
        uctNode.setBestNode();

        // firstMove is selected over good move if all the winrates are the same.
        assertEquals("Unexpected bestMove", secondNode, uctNode.bestNode);      
    }

    /** It should be a large value in this case so it gets selected to be visited first. */
    public void testCalcUctValueWhenNoVisits() {
        uctNode = new UctNode(MOVE);
        uctNode.numVisits = 0;

        assertTrue("Unexpected uctValue",
                uctNode.calculateUctValue(1.0, 1) > 100);
    }

    public void testCalcUctValueWithOneVisitNoWinsOneParentVisit() {
        uctNode = new UctNode(MOVE);
        uctNode.numVisits = 1;

        assertEquals("Unexpected uctValue",
                0.0, uctNode.calculateUctValue(1.0, 1) );
    }

    public void testCalcUctValueWithOneVisitNoWinsTwoParentVisits() {
        uctNode = new UctNode(MOVE);
        uctNode.numVisits = 1;

        assertEquals("Unexpected uctValue",
                0.3723297411, uctNode.calculateUctValue(1.0, 2), TOL);
    }

    public void testCalcUctValueWithOneVisitNoWins32ParentVisits() {
        uctNode = new UctNode(MOVE);
        uctNode.numVisits = 1;

        assertEquals("Unexpected uctValue",
                0.83255461, uctNode.calculateUctValue(1.0, 32), TOL);
    }

    public void testCalcUctValueWith10Visits1Win32ParentVisits() {
        uctNode = new UctNode(MOVE);
        uctNode.numVisits = 10;
        uctNode.updateWin(false);

        assertEquals("Unexpected uctValue",
                0.12633, uctNode.calculateUctValue(0.1, 32), TOL);
        assertEquals("Unexpected uctValue",
                0.36328, uctNode.calculateUctValue(1.0, 32), TOL);
        assertEquals("Unexpected uctValue",
                2.73277, uctNode.calculateUctValue(10.0, 32), TOL);
    }

    /**
     * Used to produce data for visualizing the effect of parameters on the UCT value.
     */
    public void CalcUctTable() {

        //int parentVisits = 1024; // 2^25 = 33,554,432

        System.out.println("parentVisits, numVisits, winRate, eeRatio, uctValue");
        calcUctTable(2);
        calcUctTable(32);
        calcUctTable(1024);
        System.out.println("done");
    }

    private void calcUctTable(int parentVisits) {
        for (int numVisits=5; numVisits<=25; numVisits+=5) {
            uctNode = new UctNode(MOVE);
            uctNode.numVisits = numVisits;
            for (double winRate = 0; winRate <=1.0; winRate+=0.20) {

                // get the winrate to where it is supposed to be
                while (Math.round(uctNode.getWinRate()*100.0) < Math.round(100.0*winRate)) {
                    uctNode.updateWin(false);
                }
                for (double eeRatio = 0; eeRatio<=2.0; eeRatio += 0.5) {
                    double v = uctNode.calculateUctValue(eeRatio, parentVisits);
                    System.out.println("t"+parentVisits + "\tt" + numVisits + "\tt"+uctNode.getWinRate() +"\tt" + eeRatio + "\t"  + Util.formatNumber(v));
                }
            }
        }
    }


    public static Test suite() {
        return new TestSuite(UctNodeTest.class);
    }
}