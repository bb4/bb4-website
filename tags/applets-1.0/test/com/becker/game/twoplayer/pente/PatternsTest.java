/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class PatternsTest extends TestCase  {

    Patterns patterns;


    public void testGetWeightIndexForPattern() {

        patterns = new StubPatterns();

        StringBuilder pattern;
        int wtIndex;

        // not a recognizable pattern.
        pattern = new StringBuilder("_");
        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 0);
        assertEquals(-1, wtIndex);

        pattern = new StringBuilder("X");
        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 0);
        assertEquals(-1, wtIndex);

        pattern = new StringBuilder("XX");
        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 1);
        assertEquals(1, wtIndex);

        pattern = new StringBuilder("_X");
        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 1);
        assertEquals(0, wtIndex);

        pattern = new StringBuilder("X_");
        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 1);
        assertEquals(0, wtIndex);
    }


    public void testGetWeightIndexForPentePattern() {

        patterns = new PentePatterns();

        StringBuilder pattern = new StringBuilder("X_XX_XXX");
        int wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 3);

        assertEquals(3, wtIndex);

        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 4);
        assertEquals(5, wtIndex);

        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 5);
        assertEquals(5, wtIndex);

        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 6);
        assertEquals(7, wtIndex);

        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 7);
        assertEquals(7, wtIndex);


        pattern = new StringBuilder("XX_XX");
        // X by itself is not a recognizable pattern.
        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 1);
        assertEquals(-1, wtIndex);

        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 3);
        assertEquals(3, wtIndex);

        wtIndex = patterns.getWeightIndexForPattern(pattern, 0, 4);
        assertEquals(7, wtIndex);
    }


    public static Test suite() {
        return new TestSuite(PatternsTest.class);
    }
}