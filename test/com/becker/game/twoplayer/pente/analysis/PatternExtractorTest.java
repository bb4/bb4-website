// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.analysis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class PatternExtractorTest extends TestCase  {

    /** instance under test. */
    private PatternExtractor extractor;
    CharSequence pattern;


    public void testEmpty() {

        extractor = new PatternExtractor("");
        try {
            CharSequence pattern = extractor.getPattern('O', 0, 0, 0);
            fail("did not expect to get here");
        }
        catch (StringIndexOutOfBoundsException e) {
            // success
        }
    }

    public void testGetPatternFromX() {

        extractor = new PatternExtractor("X");

        pattern = extractor.getPattern('O', 0, 0, 0);
        assertEquals("X", pattern);

        //pattern = extractor.getPattern('X', 0, 0, 0);
        //assertEquals("", pattern);
    }

    public void testGetPattherFromXX() {

        extractor = new PatternExtractor("XX");

        pattern = extractor.getPattern('O', 0, 0, 1);
        assertEquals("XX", pattern);

        pattern = extractor.getPattern('X', 0, 0, 1);
        assertEquals("", pattern);
    }

    public void testGetPattherFrom_XO() {

        extractor = new PatternExtractor("_XO");

        pattern = extractor.getPattern('O', 0, 0, 2);
        assertEquals("_X", pattern);

        pattern = extractor.getPattern('O', 1, 0, 2);
        assertEquals("_X", pattern);

        pattern = extractor.getPattern('O', 2, 0, 2);
        assertEquals("", pattern);

        pattern = extractor.getPattern('X', 0, 0, 2);
        assertEquals("_", pattern);

        pattern = extractor.getPattern('X', 1, 0, 2);
        assertEquals("", pattern);

        pattern = extractor.getPattern('X', 2, 0, 2);
        assertEquals("O", pattern);
    }


    public static Test suite() {
        return new TestSuite(PatternExtractorTest.class);
    }
}