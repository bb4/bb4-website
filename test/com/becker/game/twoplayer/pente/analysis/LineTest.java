/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.board.GamePiece;
import com.becker.game.common.GameWeights;
import com.becker.game.twoplayer.pente.pattern.SimplePatterns;
import com.becker.game.twoplayer.pente.pattern.SimpleWeights;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Verify that we correctly evaluate lines on the board.
 *
 * @author Barry Becker
 */
public class LineTest extends TestCase  {

    private GameWeights weights = new SimpleWeights();


    public void testAppendEmpty() {

        Line line = new Line(new LineEvaluator(new SimplePatterns(), weights.getDefaultWeights()));
        BoardPosition pos = new BoardPosition(2, 2, null);
        line.append(pos);
        assertEquals("_", line.toString());
    }

    public void testAppendPlayer() {

        Line line = new Line(new LineEvaluator(new SimplePatterns(), weights.getDefaultWeights()));
        BoardPosition pos = new BoardPosition(2, 2, new GamePiece(true));
        line.append(pos);
        assertEquals("X", line.toString());

        pos = new BoardPosition(4, 3, new GamePiece(false));
        line.append(pos);
        assertEquals("XO", line.toString());
    }

    public void testComputeValueDifferenceXX_Integraction() {

        Line line = createLine("XX");
      
        int diff = line.computeValueDifference(0);
        assertEquals(9, diff);

        diff = line.computeValueDifference(1);
        assertEquals(9, diff);
    }

    public void testComputeValueDifferenceOO_Integraction() {

        Line line = createLine("OO");

        int diff = line.computeValueDifference(0);
        assertEquals(-9, diff);

        diff = line.computeValueDifference(1);
        assertEquals(-9, diff);
    }

    public void testComputeValueDifference_X_Integraction() {

        Line line = createLine("_X");
        assertEquals("_X", line.toString());

        int diff = line.computeValueDifference(1);
        assertEquals(1, diff);
    }

    public void testComputeValueDifferenceXX_TooShort() {

        Line line = createLineWithMock("XX");

        int diff = line.computeValueDifference(0);
        assertEquals(0, diff);

        diff = line.computeValueDifference(1);
        assertEquals(0, diff);
    }

    public void testComputeValueDifference_XXX() {

        Line line = createLineWithMock("_XXX");

        int diff = line.computeValueDifference(2);
        assertEquals(2, diff);

        diff = line.computeValueDifference(3);
        assertEquals(2, diff);
    }

    public void testComputeValueDifference_XOX() {

        Line line = createLineWithMock("_XOX");

        int diff = line.computeValueDifference(2);
        assertEquals(-2, diff);

        diff = line.computeValueDifference(3);
        assertEquals(2, diff);
    }


    /**
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private Line createLine(String linePattern) {
        return TstUtil.createLine(linePattern, createLineEvaluator());
    }

    /**
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private Line createLineWithMock(String linePattern) {
        return TstUtil.createLine(linePattern, new MockLineEvaluator(3));
    }


    private LineEvaluator createLineEvaluator() {
        return new LineEvaluator(new SimplePatterns(), weights.getDefaultWeights());
    }

    public static Test suite() {
        return new TestSuite(LineTest.class);
    }
}