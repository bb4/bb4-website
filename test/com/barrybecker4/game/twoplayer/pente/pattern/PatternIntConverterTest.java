// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class PatternIntConverterTest extends TestCase  {

    /** instance under test. */
    PatternToIntConverter converter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        converter = new PatternToIntConverter();
    }

    /** converts to 1 in binary because we always have a leading1 .*/
    public void testGetInForEmptyPattern() {
        assertEquals(1, converter.convertPatternToInt(""));
    }

    /** "_" converts to 10 in binary (i.e. 2 in decimal) */
    public void testGetInForUnoccupied() {

        verify(2, Character.toString(Patterns.UNOCCUPIED));
    }

    /** Converts to 11 in binary */
    public void testGetInForX() {
        verify(3, "X");
    }

    public void testGetInForO() {
        verify(3, "O");
    }

    /** The character in the patter does not matter. we just check that it is not the UNOCCUPIED char. */
    public void testGetInForW() {
        verify(3, "W");
    }

    /** Converts to 100 in binary */
    public void testGetInFor__() {
        verify(4, "__");
    }

    /** Converts to 101 in binary */
    public void testGetInFor_X() {
        verify(5, "_X");
    }

    /** Converts to 110 in binary */
    public void testGetInForX_() {
        verify(6, "X_");
    }

    /** Converts to 1000 in binary */
    public void testGetInFor___() {
        verify(8, "___");
    }

    /** Converts to 1100 in binary */
    public void testGetInForX__() {
        verify(12, "X__");
    }

    /** Converts to 1010 in binary */
    public void testGetInFor_X_() {
        verify(10, "_X_");
    }

    /** Converts to 1001 in binary */
    public void testGetInFor__X() {
        verify(9, "__X");
    }

    /** Converts to 1110 in binary */
    public void testGetInForXX_() {
        verify(14, "XX_");
    }

    /** Converts to 1011 in binary */
    public void testGetInFor_XX() {
        verify(11, "_XX");
    }

    /** Converts to 1111 in binary */
    public void testGetInForXXX() {
        verify(15, "XXX");
    }

    /** The character in the patter does not matter. we just check that it is not the UNOCCUPIED char. */
    public void testGetInForNonXCharsInPatter() {
        verify(15, "Y X");
    }

    /** Converts to 110101 in binary */
    public void testGetInForX_X_X() {
        verify(53, "X_X_X");
    }

    /** Converts to 110101 in binary */
    public void testGetInForO_O_O() {
        verify(53, "O_O_O");
    }

    /** Converts to 110101 in binary */
    public void testGetInForO_X_O() {
        verify(53, "O_X_O");
    }

    /** Converts to 110101 in binary given the range */
    public void testGetInForOOX_X_XO() {
        String pattern = "OOX_X_XO";
        assertEquals("Unexpected integer for pattern.",
                        53, converter.convertPatternToInt(pattern, 2, 6));
    }


    private void verify(int expectedInt, String pattern) {
        assertEquals("Unexpected integer for pattern.",
                expectedInt, converter.convertPatternToInt(pattern));
    }

    public static Test suite() {
        return new TestSuite(PatternIntConverterTest.class);
    }
}