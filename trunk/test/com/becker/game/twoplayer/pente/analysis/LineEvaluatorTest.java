// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.analysis;

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
public class LineEvaluatorTest extends TestCase  {

    StringBuilder line;
    StubLineEvaluator lineEvaluator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        GameWeights weights = new StubWeights();
        lineEvaluator =
                new StubLineEvaluator(new StubPatterns(), weights.getDefaultWeights());
    }


    public void testEvalLineSimplePlayer1() {

        double worth;

        line = createLine("_X");

        worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        checkRecordedPatterns(new String[] {"_X"});
        assertEquals(4.0, worth);

        line = createLine("_X");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(4.0, worth);

        line = createLine("X_");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("X_");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(4.0, worth);

        line = createLine("XX");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(20.0, worth);

        line = createLine("XX");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        assertEquals(20.0, worth);
    }

    public void testEvaLineSimplePlayer2() {
        double worth;

        line = createLine("_O");
        
        worth = lineEvaluator.evaluate(line, false, 1, 0, 1);
        assertEquals(-4.0, worth);

        line = createLine("_O");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(-4.0, worth);

        line = createLine("O_");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("O_");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(-4.0, worth);

        line = createLine("OO");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(-20.0, worth);

        line = createLine("OO");
        worth = lineEvaluator.evaluate(line, false, 1, 0, 1);
        assertEquals(-20.0, worth);
    }

    public void testEvalLineLongerThanPattern() {

        line = createLine("X_XX");
        
        int worth = lineEvaluator.evaluate(line, true, 2, 2, 3);
        checkRecordedPatterns(new String[] {"XX"});
        assertEquals(20, worth);

        worth = lineEvaluator.evaluate(line, true, 3, 2, 3);
        checkRecordedPatterns(new String[] {"XX"});
        assertEquals(20, worth);

        worth = lineEvaluator.evaluate(line, true, 1, 1, 2);
        checkRecordedPatterns(new String[] {"_X"});
        assertEquals(4, worth);

        worth = lineEvaluator.evaluate(line, true, 1, 0, 3);
        checkRecordedPatterns(new String[] {"X_XX"});
        assertEquals(0, worth);

        worth = lineEvaluator.evaluate(line, true, 2, 0, 3);
        checkRecordedPatterns(new String[] {"X_XX"});
        assertEquals(0, worth);

        // _XX is not a recognizable StubPattern
        worth = lineEvaluator.evaluate(line, true, 3, 0, 3);
        checkRecordedPatterns(new String[] {"X_XX"});
        assertEquals(0, worth);
    }

    public void testEvalMixedLine() {
        int worth;

        line = createLine("XOX");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 2);
        checkRecordedPatterns(new String[] {"X"});
        assertEquals(0, worth);

        line = createLine("XOX");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 2);
        checkRecordedPatterns(new String[] {"X", "X"});
        assertEquals(0, worth);

        line = createLine("X_OX");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 3);
        checkRecordedPatterns(new String[] {"X_", "X"});
        // X_ gets 4.
        assertEquals(4, worth);

        line = createLine("X_OX");
        worth = lineEvaluator.evaluate(line, false, 2, 0, 3);
        checkRecordedPatterns(new String[] {"_O"});
        assertEquals(-4, worth);

        line = createLine("X_O_X");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 4);
        checkRecordedPatterns(new String[] {"X_", "_X"});
        // X_ and _X get 4.
        assertEquals(8, worth);

        line = createLine("XXOXX");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 4);
        checkRecordedPatterns(new String[] {"XX", "XX"});
        // XX and XX get 20.
        assertEquals(40, worth);

        line = createLine("XXOXX");
        worth = lineEvaluator.evaluate(line, false, 2, 0, 4);
        checkRecordedPatterns(new String[] {"O"});
        // O gets 0
        assertEquals(0, worth);
    }


    private void checkRecordedPatterns(String[] expectedPatterns) {
        List<String> checkedPatterns = lineEvaluator.getPatternsChecked();
        int i=0;
        System.out.println("checkedPatterns = " + TstUtil.quoteStringList(checkedPatterns));
        assertEquals(expectedPatterns.length, checkedPatterns.size());
        for (String pat : checkedPatterns) {
            assertEquals(expectedPatterns[i++], pat);
        }
        checkedPatterns.clear();
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