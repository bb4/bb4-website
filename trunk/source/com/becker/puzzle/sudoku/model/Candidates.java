package com.becker.puzzle.sudoku.model;

import org.omg.PortableInterceptor.SUCCESSFUL;

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

    public Candidates(ValuesList values) {
        this.addAll(values);
    }

    public Integer getFirst() {
        return this.iterator().next();
    }
}
