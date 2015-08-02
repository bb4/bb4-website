// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

import com.barrybecker4.apps.misc.dtablebalancer.balancers.NoopBalancer;
import com.barrybecker4.apps.misc.dtablebalancer.balancers.SimpleBalancer;

import java.util.ArrayList;

/**
 * @author Barry Becker
 */
public class TestCases extends ArrayList<Object[]> {

    TestCases() {
        this.add(new Object[] {"Uniform 2 by 2 (200x200) (noop balancer)",
                new Table(TableExamples.UNIFORM_2x2, 200, 200),
                new NoopBalancer(),
                5000.0,
                1.0,
                1.0
        });
        this.add(new Object[] {"Uniform 2 by 2 (200x200)",
                new Table(TableExamples.UNIFORM_2x2, 200, 200),
                new SimpleBalancer(),
                5000.0,
                1.0,
                1.0
        });
        this.add(new Object[] {"Uniform 2 by 2 (100x100)",
                new Table(TableExamples.UNIFORM_2x2, 100, 100),
                new SimpleBalancer(),
                1250.0,
                1.0,
                1.0
        });
        this.add(new Object[] {"Uniform 2 by 2 (130x130)",
                new Table(TableExamples.UNIFORM_2x2, 130, 130),
                new SimpleBalancer(),
                2112.5,
                1.0,
                1.0
        });
        this.add(new Object[] {"Uniform 3 by 3 (300x300)",
                new Table(TableExamples.UNIFORM_3x3, 300, 300),
                new SimpleBalancer(),
                100.0,
                1.0,
                1.0
        });
        this.add(new Object[] {"Narrow middle column 3 by 3 (1000x1000)",                 //  5
                new Table(TableExamples.NARROW_MIDDLE_COLUMN_3x3, 1000, 1000),
                new SimpleBalancer(),
                1111.111111,
                0.73889,
                0.84232
        });
        this.add(new Object[] {"Narrow middle row 3 by 3 (500x500)",
                new Table(TableExamples.NARROW_MIDDLE_ROW_3x3, 500, 500),
                new SimpleBalancer(),
                277.7777777,
                0.738888889,
                0.842583948
        });
        this.add(new Object[] {"Random 3 by 3 (500x500)",                  //   7
                new Table(TableExamples.RANDOM_3x3, 500, 500),
                new SimpleBalancer(),
                138.888888888889,
                0.4216667,
                0.64144138
        });
        this.add(new Object[] {"Random 4 by 4 (600x600)",
                new Table(TableExamples.RANDOM_4x4, 600, 600),
                new SimpleBalancer(),
                47.07113,
                0.36833159,
                0.57406665
        });
        this.add(new Object[] {"Race by Native (1200x1200)",
                new Table(TableExamples.RACE_BY_NATIVE_4x4, 1200, 1200),
                new SimpleBalancer(),
                54.38066,
                0.159441,
                0.369373496
        });
        this.add(new Object[] {"Race by Native (100x100)",
                new Table(TableExamples.RACE_BY_NATIVE_4x4, 100, 100),
                new SimpleBalancer(),
                0.3776435,
                0.159441,
                0.36663332
        });
        this.add(new Object[] {"Race by Native (70x70)",                  // 11
                new Table(TableExamples.RACE_BY_NATIVE_4x4, 70, 70),
                new SimpleBalancer(),
                0.185045317,
                0.159441087,
                0.36544273
        });
    }
}
