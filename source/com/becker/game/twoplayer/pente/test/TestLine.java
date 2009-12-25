package com.becker.game.twoplayer.pente.test;

import com.becker.game.common.BoardPosition;
import com.becker.game.common.GamePiece;
import com.becker.game.common.GameWeights;
import com.becker.game.twoplayer.pente.Line;
import com.becker.game.twoplayer.pente.PenteWeights;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class TestLine extends TestCase  {

    Line line;
    GameWeights weights;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        weights = new StubWeights();
    }

    public void testAppendEmpty() {

        line = new Line(new StubPatterns(), weights.getDefaultWeights());
        BoardPosition pos = new BoardPosition(2, 2, null);
        line.append(pos);
        assertEquals("_", line.toString());
    }

    public void testAppendPlayer() {

        line = new Line(new StubPatterns(), weights.getDefaultWeights());
        BoardPosition pos = new BoardPosition(2, 2, new GamePiece(true));
        line.append(pos);
        assertEquals("X", line.toString());

        pos = new BoardPosition(4, 3, new GamePiece(false));
        line.append(pos);
        assertEquals("XO", line.toString());
    }

    public void testEvalLineSimplePlayer1() {

        double worth;

        line = createLine("_X");
        worth = line.evalLine(true, 1, 0, 1);
        assertEquals(4.0, worth);

        line = createLine("_X");
        worth = line.evalLine(true, 0, 0, 1);
        assertEquals(4.0, worth);

        line = createLine("X_");
        worth = line.evalLine(false, 0, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("X_");
        worth = line.evalLine(true, 0, 0, 1);
        assertEquals(4.0, worth);

        line = createLine("XX");
        worth = line.evalLine(true, 0, 0, 1);
        assertEquals(20.0, worth);

        line = createLine("XX");
        worth = line.evalLine(true, 1, 0, 1);
        assertEquals(20.0, worth);
    }

    public void testEvaLineSimplePlayer2() {
        double worth;

        line = createLine("_O");
        worth = line.evalLine(false, 1, 0, 1);
        assertEquals(-4.0, worth);

        line = createLine("_O");
        worth = line.evalLine(false, 0, 0, 1);
        assertEquals(-4.0, worth);

        line = createLine("O_");
        worth = line.evalLine(true, 0, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("O_");
        worth = line.evalLine(false, 0, 0, 1);
        assertEquals(-4.0, worth);

        line = createLine("OO");
        worth = line.evalLine(false, 0, 0, 1);
        assertEquals(-20.0, worth);

        line = createLine("OO");
        worth = line.evalLine(false, 1, 0, 1);
        assertEquals(-20.0, worth);
    }


    public void testEvalLineLongerThanPattern() {

        line = createLine("X_XX");
        int worth = line.evalLine(true, 2, 2, 3);
        assertEquals(20, worth);

        worth = line.evalLine(true, 3, 2, 3);
        assertEquals(20, worth);

        worth = line.evalLine(true, 1, 1, 2);
        assertEquals(4, worth);

        worth = line.evalLine(true, 1, 0, 3);
        assertEquals(0, worth);

        worth = line.evalLine(true, 2, 0, 3);
        assertEquals(0, worth);

        // _XX is not a recognizable StubPattern
        worth = line.evalLine(true, 3, 0, 3);
        assertEquals(0, worth);
    }


    public void testEvalMixedLine() {
        int worth;

        line = createLine("XOX");
        worth = line.evalLine(true, 2, 0, 2);
        assertEquals(0, worth);

        line = createLine("XOX");
        worth = line.evalLine(true, 1, 0, 2);
        assertEquals(0, worth);

        line = createLine("X_OX");
        worth = line.evalLine(true, 2, 0, 3);
        // X_ gets 4.
        assertEquals(4, worth);

        line = createLine("X_OX");
        worth = line.evalLine(false, 2, 0, 3);
        assertEquals(-4, worth);

        line = createLine("X_O_X");
        worth = line.evalLine(true, 2, 0, 4);
        // X_ and _X get 4.
        assertEquals(8, worth);
    }


    public void testComputeValueDifference1() {

        line = createLine("XX");

        int diff = line.computeValueDifference(0);
        assertEquals(16, diff);

        diff = line.computeValueDifference(1);
        assertEquals(16, diff);
    }

    public void testComputeValueDifference2() {

        line = createLine("X_X");
        assertEquals("X_X", line.toString());

        int diff = line.computeValueDifference(0);
        assertEquals(0, diff);

        diff = line.computeValueDifference(1);
        assertEquals(0, diff);

        diff = line.computeValueDifference(2);
        assertEquals(0, diff);
    }

    public void testComputeValueDifference3() {

        line = createLine("XXX");

        int diff = line.computeValueDifference(0);
        assertEquals(0, diff);

        diff = line.computeValueDifference(1);
        assertEquals(0, diff);

        diff = line.computeValueDifference(2);
        assertEquals(0, diff);
    }

    /**
     *
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private Line createLine(String linePattern) {
        line = new Line(new StubPatterns(), weights.getDefaultWeights());
        for (int i=0; i<linePattern.length(); i++) {
             GamePiece piece = null;
             char c = linePattern.charAt(i);
              if (c == 'X') {
                  piece = new GamePiece(true);
              }
              if (c == 'O') {
                  piece = new GamePiece(false);
              }
              BoardPosition pos = new BoardPosition(0, 0, piece);
              line.append(pos);
         }
        return line;
    }

    public static Test suite() {
        return new TestSuite(TestLine.class);
    }
}