// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.GameWeights;
import com.becker.game.twoplayer.pente.pattern.SimplePatterns;
import com.becker.game.twoplayer.pente.pattern.SimpleWeights;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class LineEvaluatorTest extends TestCase  {

    private StringBuilder line;
    private LineEvaluator lineEvaluator;
    private double worth;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        GameWeights weights = new SimpleWeights();
        lineEvaluator =
            new LineEvaluator(new SimplePatterns(), weights.getDefaultWeights());
    }

    public void testEvalLinePlayer1_X() {

        line = createLine("_X");
        double worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        assertEquals(1.0, worth);

        line = createLine("_X");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(1.0, worth);
    }

    public void testEvalLinePlayer1_X_simp() {

        line = createLine("_X_");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 2);
        assertEquals(3.0, worth);
    }

    public void testEvalLinePlayer1_X_() {

        line = createLine("_X_");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 2);
        assertEquals(3.0, worth);

        line = createLine("_X_");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 2);
        assertEquals(3.0, worth);

        // should be same if in longer line
        line = createLine("OO_X__");
        worth = lineEvaluator.evaluate(line, true, 3, 2, 4);
        assertEquals(3.0, worth);

        line = createLine("O_X_O_O");
        worth = lineEvaluator.evaluate(line, true, 2, 1, 3);
        assertEquals(3.0, worth);
    }

    public void testEvalLinePlayer1X_() {

        line = createLine("X_");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("X_");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(1.0, worth);

        line = createLine("X_");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        assertEquals(1.0, worth);
    }

    public void testEvalLinePlayer1XX() {

        line = createLine("XX");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(10.0, worth);

        line = createLine("XX");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        assertEquals(10.0, worth);
    }

    public void testEvaLineSimplePlayer2() {

        line = createLine("_O");
        worth = lineEvaluator.evaluate(line, false, 1, 0, 1);
        assertEquals(-1.0, worth);

        line = createLine("_O");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(-1.0, worth);

        line = createLine("O_");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("O_");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("O_");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(-1.0, worth);

        line = createLine("OO");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(-10.0, worth);

        line = createLine("OO");
        worth = lineEvaluator.evaluate(line, false, 1, 0, 1);
        assertEquals(-10.0, worth);
    }

    public void testEvalLineLongerThanPattern() {

        line = createLine("X_XX");
        
        worth = lineEvaluator.evaluate(line, true, 2, 2, 3);
        assertEquals(10.0, worth);

        worth = lineEvaluator.evaluate(line, false, 2, 2, 3);
        assertEquals(0.0, worth);

        worth = lineEvaluator.evaluate(line, true, 3, 2, 3);
        assertEquals(10.0, worth);

        worth = lineEvaluator.evaluate(line, true, 1, 1, 2);
        assertEquals(1.0, worth);

        worth = lineEvaluator.evaluate(line, true, 1, 0, 3);
        assertEquals(0.0, worth);

        worth = lineEvaluator.evaluate(line, true, 2, 0, 3);
        assertEquals(0.0, worth);

        // _XX is not a recognizable StubPattern
        worth = lineEvaluator.evaluate(line, true, 3, 0, 3);
        assertEquals(0.0, worth);
    }

    public void testEvalMixedLineXOX() {

        line = createLine("XOX");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 2);
        //checkRecordedPatterns(new String[] {"X"});
        assertEquals(0.0, worth);

        line = createLine("XOX");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 2);
        //checkRecordedPatterns(new String[] {"X", "X"});
        assertEquals(0.0, worth);
    }

    public void testEvalMixedLineX_OX() {

        line = createLine("X_OX");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 3);
        // X_ gets 4.
        assertEquals(1.0, worth);

        line = createLine("X_OX");
        worth = lineEvaluator.evaluate(line, false, 2, 0, 3);
        assertEquals(-1.0, worth);
    }

    public void testEvalMixedLineX_O_X() {

        line = createLine("X_O_X");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 4);
        // X_ and _X get 4.
        assertEquals(2.0, worth);
    }

    public void testEvalMixedLineXXOXX() {

        line = createLine("XXOXX");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 4);
        assertEquals(20.0, worth);

        line = createLine("XXOXX");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 4);
        assertEquals(10.0, worth);

        line = createLine("XXOXX");
        worth = lineEvaluator.evaluate(line, false, 2, 0, 4);
        assertEquals(0.0, worth);
    }
    
    /**
     * @return the line
     */
    private StringBuilder createLine(String line) {
        return new StringBuilder(line);
    }

    public static Test suite() {
        return new TestSuite(LineEvaluatorTest.class);
    }
}