package com.becker.simulation.fractals.algorithm;

import com.becker.common.concurrency.Parallelizer;
import com.becker.common.math.ComplexNumber;
import com.becker.common.math.ComplexNumberRange;
import com.becker.common.profile.ProfilerEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
