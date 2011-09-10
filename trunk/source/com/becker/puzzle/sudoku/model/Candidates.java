package com.becker.puzzle.sudoku.model;

import java.util.Arrays;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The numbers which have not yet been used in this big cell.
 * Consider implementing list and then returning a synchronized version only for rendering.
 * @author Barry Becker
 */
public class Candidates extends ConcurrentSkipListSet<Integer> {

    public Candidates() {}

    public Candidates(Candidates cands) {
        addAll(cands);
    }

    public Candidates copy() {
        return new Candidates(this);
    }

    /**
     * Convenient for testing.
     * @param values
     */
    public Candidates(Integer... values) {
        if (values.length > 0) {
            this.addAll(Arrays.asList(values));
        }
    }

    public Integer getFirst() {
        return this.iterator().next();
    }

    /**
     *  Intersect ourselves with the 3 specified sets.
     */
    public void findIntersectionCandidates(Candidates set1, Candidates set2, Candidates set3)  {

        this.addAll(set1);
        this.retainAll(set2);
        this.retainAll(set3);
    }
}
