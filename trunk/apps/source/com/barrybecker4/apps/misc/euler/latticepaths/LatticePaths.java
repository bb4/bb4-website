// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.euler.latticepaths;

import com.barrybecker4.common.math.MathUtil;

/**
 * @author Barry Becker
 */
public class LatticePaths {


    public long getNumPaths(int a, int b) {
        return MathUtil.combination(2 * a, b).longValue();
    }

    public static void main(String[] args) {
        System.out.println("Num lattice paths for 20, 20 grid is " + new LatticePaths().getNumPaths(20, 20));
    }

}
