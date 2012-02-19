/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.board.GamePiece;
import com.becker.game.common.GameWeights;
import com.becker.game.twoplayer.pente.StubPatterns;
import com.becker.game.twoplayer.pente.StubWeights;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class LineTest extends TestCase  {

    Line line;
    GameWeights weights;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        weights = new StubWeights();
    }

    public void testAppendEmpty() {

        Line line = new Line(new LineEvaluator(new StubPatterns(), weights.getDefaultWeights()));
        BoardPosition pos = new BoardPosition(2, 2, null);
        line.append(pos);
        assertEquals("_", line.toString());
    }

    public void testAppendPlayer() {

        Line line = new Line(new LineEvaluator(new StubPatterns(), weights.getDefaultWeights()));
        BoardPosition pos = new BoardPosition(2, 2, new GamePiece(true));
        line.append(pos);
        assertEquals("X", line.toString());

        pos = new BoardPosition(4, 3, new GamePiece(false));
        line.append(pos);
        assertEquals("XO", line.toString());
    }

    public void testComputeValueDifference1() {

        Line line = createLine("XX");
      
        int diff = line.computeValueDifference(0);
        assertEquals(16, diff);

        diff = line.computeValueDifference(1);
        assertEquals(16, diff);
    }

    public void testComputeValueDifferenceOO() {

        Line line = createLine("OO");

        int diff = line.computeValueDifference(0);
        assertEquals(-16, diff);

        diff = line.computeValueDifference(1);
        assertEquals(-16, diff);
    }

    public void testComputeValueDifference_X() {

        Line line = createLine("_X");
        assertEquals("_X", line.toString());

        int diff = line.computeValueDifference(1);
        assertEquals(4, diff);
    }


    /**
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private Line createLine(String linePattern) {
        return TstUtil.createLine(linePattern, createLineEvaluator());
    }

    private StubLineEvaluator createLineEvaluator() {
        return new StubLineEvaluator(new StubPatterns(), weights.getDefaultWeights());
    }

    public static Test suite() {
        return new TestSuite(LineTest.class);
    }
}