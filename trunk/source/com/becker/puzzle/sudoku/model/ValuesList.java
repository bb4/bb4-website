package com.becker.puzzle.sudoku.model;

import com.becker.common.math.MathUtil;
import sun.awt.SunHints;

import java.util.*;

/**
 *  The list of values in a bigCell (or row or column).
 *
 *  @author Barry Becker
 */
public class ValuesList extends ArrayList<Integer> {

    public ValuesList() {}

    /**
     * Constructor
     */
    public ValuesList(int sizeSq) {
        for (int i = 1; i <= sizeSq; i++) {
            add(i);
        }
    }

    private ValuesList(Candidates cands) {
        this.addAll(cands);
    }

    public static ValuesList createShuffledList(Candidates cands) {
        ValuesList randomList = new ValuesList(cands);
        Collections.shuffle(randomList, MathUtil.RANDOM);
        return randomList;
    }
}
