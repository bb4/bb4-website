// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Barry Becker
 */
public class TestCases extends ArrayList<Object[]> {

    TestCases() {
        this.add(new Object[] {new Table(TableExamples.UNIFORM_2x2, 200, 200), 1.0});
        this.add(new Object[] {new Table(TableExamples.UNIFORM_2x2, 100, 100), 1.0});
        this.add(new Object[] {new Table(TableExamples.UNIFORM_3x3, 300, 300), 1.0});
        this.add(new Object[] {new Table(TableExamples.NARROW_MIDDLE_COLUMN_3x3, 1000, 1000), 0.73741184});
        this.add(new Object[] {new Table(TableExamples.NARROW_MIDDLE_ROW_3x3, 500, 500), 0.7329896});
    }
}
