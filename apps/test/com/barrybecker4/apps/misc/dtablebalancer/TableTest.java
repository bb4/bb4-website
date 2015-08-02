// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class TableTest {

    private static final double TOL = 0.000001;

    @Test
    public void testUniform3x3TableWithIdealArea() {
        Table table = new Table(TableExamples.UNIFORM_3x3, 300, 300);
        assertEquals("Unexpected",
                "1.00\t1.00\t1.00\t\n" +
                "1.00\t1.00\t1.00\t\n" +
                "1.00\t1.00\t1.00\t\n" +
                "Overall coverage: 1.00", table.toString());
    }

    @Test
    public void testUniform3x3Table() {
        Table table = new Table(TableExamples.UNIFORM_3x3, 100, 100);
        assertEquals("Unexpected",
                "1.00\t1.00\t1.00\t\n" +
                "1.00\t1.00\t1.00\t\n" +
                "1.00\t1.00\t1.00\t\n" +
                "Overall coverage: 1.0", table.toString());
    }

}
