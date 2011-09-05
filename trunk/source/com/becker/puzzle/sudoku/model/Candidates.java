package com.becker.puzzle.sudoku.model;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The numbers which have not yet been used in this big cell.
 * Consider implementing list and then returning a synchronized version only for rendering.
 * @author Barry Becker
 */
public class Candidates extends ConcurrentSkipListSet<Integer> {

    public Candidates() {}

    public Candidates(Integer... values) {
        if (values.length > 0) {
            this.addAll(Arrays.asList(values));
        }

    }

}
