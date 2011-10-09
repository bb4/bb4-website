package com.becker.puzzle.sudoku.model.update;

import com.becker.puzzle.sudoku.model.board.Candidates;

import java.util.*;

/**
 *  An array of sets of integers representing the candidates for the cells in a row or column.
 *
 *  @author Barry Becker
 */
public class CandidatesArray {

    /** candidate sets for a row or col.   */
    private Candidates[] candidates_;

    public CandidatesArray(Candidates[] cands) {
        candidates_ = cands;
    }

    public Candidates get(int i) {
        return candidates_[i];
    }

    public int size() {
        return candidates_.length;
    }

    public String toString() {
       return Arrays.toString(candidates_);
    }

}
