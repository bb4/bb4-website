/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.fractalexplorer.algorithm;

import com.becker.common.math.ComplexNumberRange;

import java.util.LinkedList;

/**
 * Maintains regions (ranges) in the complex space that were visited, so
 * we can back up.
 * @author Barry Becker
 */
public class History {


    /** range of bounding box in complex plane. */
    private LinkedList<ComplexNumberRange> stack;


    public History() {
        stack = new LinkedList<ComplexNumberRange>();
    }


    public void addRangeToHistory(ComplexNumberRange range)  {
        stack.push(range);
    }

    /**
     * Back up one step in the history
     * @return
     */
    public ComplexNumberRange popLastRange() {
         return stack.pop();
    }

}
