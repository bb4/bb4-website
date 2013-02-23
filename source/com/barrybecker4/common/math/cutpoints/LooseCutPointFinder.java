// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.math.cutpoints;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.common.math.Range;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class LooseCutPointFinder extends AbstractCutPointFinder {

    @Override
    protected void addPoints(List<Double> positions, Range roundedRange, Range finalRange, double d) {

        double stop = roundedRange.getMax() + 0.5 * d;
        for (double x = roundedRange.getMin(); x < stop; x += d) {
            positions.add(checkSmallNumber(x));
        }
    }

}