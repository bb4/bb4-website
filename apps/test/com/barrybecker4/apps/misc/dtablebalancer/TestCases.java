// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

import com.barrybecker4.apps.misc.dtablebalancer.balancers.NoopBalancer;

import java.util.ArrayList;

/**
 * @author Barry Becker
 */
public class TestCases extends ArrayList<Object[]> {

    TestCases() {
        this.add(new Object[] {"Uniform 2 by 2 (200x200)",
                new Table(TableExamples.UNIFORM_2x2, 200, 200),
                new NoopBalancer(),
                5000.0,
                1.0,
                1.0
        });
        this.add(new Object[] {"Uniform 2 by 2 (100x100)",
                new Table(TableExamples.UNIFORM_2x2, 100, 100),
                new NoopBalancer(),
                1250.0,
                1.0,
                1.0
        });
        this.add(new Object[] {"Uniform 2 by 2 (130x130)",
                new Table(TableExamples.UNIFORM_2x2, 130, 130),
                new NoopBalancer(),
                2112.5,
                1.0,
                1.0
        });
        this.add(new Object[] {"Uniform 3 by 3 (300x300)",
                new Table(TableExamples.UNIFORM_3x3, 300, 300),
                new NoopBalancer(),
                100.0,
                1.0,
                1.0
        });
        this.add(new Object[] {"Narrow middle column 3 by 3 (1000x1000)",
                new Table(TableExamples.NARROW_MIDDLE_COLUMN_3x3, 1000, 1000),
                new NoopBalancer(),
                1108.89,
                0.73741184,
                0.73741184
        });
        this.add(new Object[] {"Narrow middle row 3 by 3 (500x500)",
                new Table(TableExamples.NARROW_MIDDLE_ROW_3x3, 500, 500),
                new NoopBalancer(),
                275.56,
                0.7329896,
                0.7329896
        });
        this.add(new Object[] {"Random 3 by 3 (500x500)",
                new Table(TableExamples.RANDOM_3x3, 500, 500),
                new NoopBalancer(),
                137.78,
                0.4183,
                0.4183
        });
        this.add(new Object[] {"Random 4 by 4 (600x600)",
                new Table(TableExamples.RANDOM_4x4, 600, 600),
                new NoopBalancer(),
                47.07113,
                0.36833159,
                0.36833159
        });
        this.add(new Object[] {"Race by Native (1200x1200)",
                new Table(TableExamples.RACE_BY_NATIVE_4x4, 1200, 1200),
                new NoopBalancer(),
                54.38066,
                0.159441,
                0.159441
        });
        this.add(new Object[] {"Race by Native (100x100)",
                new Table(TableExamples.RACE_BY_NATIVE_4x4, 100, 100),
                new NoopBalancer(),
                0.3776435,
                0.159441,
                0.159441
        });
        this.add(new Object[] {"Race by Native (70x70)",
                new Table(TableExamples.RACE_BY_NATIVE_4x4, 70, 70),
                new NoopBalancer(),
                0.174622356,
                0.15046,
                0.15046
        });
    }
}
