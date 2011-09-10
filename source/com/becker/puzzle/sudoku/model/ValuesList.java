package com.becker.puzzle.sudoku.model;

import com.becker.common.math.MathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *  The list of values in a bigCell (or row or column).
 *
 *  @author Barry Becker
 */
public class ValuesList extends ArrayList<Integer> {

    /**
     * Constructor
     */
    public ValuesList(int sizeSq) {
        for (int i = 1; i <= sizeSq; i++) {
            add(i);
        }
    }

    private ValuesList(Candidates cands) {
        super(cands.size());
        this.addAll(cands);
    }

    public static ValuesList createShuffledList(Candidates cands) {
        ValuesList randomList = new ValuesList(cands);
        randomList.addAll(cands);
        Collections.shuffle(randomList, MathUtil.RANDOM);
        return randomList;
    }

}
