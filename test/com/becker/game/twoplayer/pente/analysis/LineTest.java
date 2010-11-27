package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.board.GamePiece;
import com.becker.game.common.GameWeights;
import com.becker.game.twoplayer.pente.StubPatterns;
import com.becker.game.twoplayer.pente.StubWeights;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class LineTest extends TestCase  {

    LineRecorder line;
    GameWeights weights;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        weights = new StubWeights();
    }

    public void testAppendEmpty() {

        Line line = new Line(new StubPatterns(), weights.getDefaultWeights());
        BoardPosition pos = new BoardPosition(2, 2, null);
        line.append(pos);
        assertEquals("_", line.toString());
    }

    public void testAppendPlayer() {

        Line line = new Line(new StubPatterns(), weights.getDefaultWeights());
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
        checkRecordedPatterns(new String[] {"_X"});
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
        checkRecordedPatterns(new String[] {"XX"});
        assertEquals(20, worth);

        worth = line.evalLine(true, 3, 2, 3);
        checkRecordedPatterns(new String[] {"XX"});
        assertEquals(20, worth);

        worth = line.evalLine(true, 1, 1, 2);
        checkRecordedPatterns(new String[] {"_X"});
        assertEquals(4, worth);

        worth = line.evalLine(true, 1, 0, 3);
        checkRecordedPatterns(new String[] {"X_XX"});
        assertEquals(0, worth);

        worth = line.evalLine(true, 2, 0, 3);
        checkRecordedPatterns(new String[] {"X_XX"});
        assertEquals(0, worth);

        // _XX is not a recognizable StubPattern
        worth = line.evalLine(true, 3, 0, 3);
        checkRecordedPatterns(new String[] {"X_XX"});
        assertEquals(0, worth);
    }


    public void testEvalMixedLine() {
        int worth;

        line = createLine("XOX");
        worth = line.evalLine(true, 2, 0, 2);
        checkRecordedPatterns(new String[] {"X"});
        assertEquals(0, worth);

        line = createLine("XOX");
        worth = line.evalLine(true, 1, 0, 2);
        checkRecordedPatterns(new String[] {"X", "X"});
        assertEquals(0, worth);

        line = createLine("X_OX");
        worth = line.evalLine(true, 2, 0, 3);
        checkRecordedPatterns(new String[] {"X_", "X"});
        // X_ gets 4.
        assertEquals(4, worth);

        line = createLine("X_OX");
        worth = line.evalLine(false, 2, 0, 3);
        checkRecordedPatterns(new String[] {"_O"});
        assertEquals(-4, worth);

        line = createLine("X_O_X");
        worth = line.evalLine(true, 2, 0, 4);
        checkRecordedPatterns(new String[] {"X_", "_X"});
        // X_ and _X get 4.
        assertEquals(8, worth);

        line = createLine("XXOXX");
        worth = line.evalLine(true, 2, 0, 4);
        checkRecordedPatterns(new String[] {"XX", "XX"});
        // XX and XX get 20.
        assertEquals(40, worth);

        line = createLine("XXOXX");
        worth = line.evalLine(false, 2, 0, 4);
        checkRecordedPatterns(new String[] {"O"});
        // O gets 0
        assertEquals(0, worth);
    }

    public void testComputeValueDifference1() {

        line = createLine("XX");

        int diff = line.computeValueDifference(0);
        assertEquals(16, diff);

        diff = line.computeValueDifference(1);
        assertEquals(16, diff);
    }

    public void testComputeValueDifferenceXX() {

        line = createLine("XX");

        int diff = line.computeValueDifference(0);
        assertEquals(16, diff);

        diff = line.computeValueDifference(1);
        assertEquals(16, diff);
    }

    public void testComputeValueDifference_X() {

        line = createLine("_X");
        assertEquals("_X", line.toString());

        int diff = line.computeValueDifference(0);
        assertEquals(0, diff);

        diff = line.computeValueDifference(1);
        assertEquals(4, diff);
    }


    private void checkRecordedPatterns(String[] expectedPatterns) {
        List<String> checkedPatterns = line.getPatternsChecked();
        int i=0;
        //System.out.println("checkedPatterns = " + TstUtil.quoteStringList(checkedPatterns));
        assertEquals(expectedPatterns.length, checkedPatterns.size());
        for (String pat : checkedPatterns) {
            assertEquals(expectedPatterns[i++], pat);
        }
        checkedPatterns.clear();
    }

    /**
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private LineRecorder createLine(String linePattern) {
        return TstUtil.createLine(linePattern, new StubPatterns(), weights.getDefaultWeights());
    }




    public static Test suite() {
        return new TestSuite(LineTest.class);
    }
}