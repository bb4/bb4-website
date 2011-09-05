package com.becker.puzzle.sudoku.model;

import ca.dj.jigo.sgf.tokens.SourceToken;

import java.util.*;

/**
 *  An array of sets of integers representing the candidates for the cells in a row or column.
 *
 *  @author Barry Becker
 */
public class CandidatesArray {

    /** candidate sets for a row or col.   */
    private Candidates[] candidates_;


    /**
     * Constructor
     * @param size this size of the row (small grid dim squared).
     */
    public CandidatesArray(int size) {

        candidates_ = new Candidates[size];

        for (int i=0; i < size; i++) {
            candidates_[i] = new Candidates();
        }
    }

    public CandidatesArray(Candidates[] cands) {
        candidates_ = cands;
    }

    public Candidates get(int i) {
        return candidates_[i];
    }

    public void updateRow(int row, Board board) {

        Candidates cands = get(row);
        cands.clear();
        cands.addAll(board.getValuesList());
        //System.out.println("update row =" + row);
        for (int j=0; j < board.getEdgeLength(); j++) {
            int v = board.getCell(row, j).getValue();

            if (v > 0)  {
                cands.remove(v);
            }
        }
    }

    public void updateCol(int col, Board board) {

        Candidates cands = get(col);
        cands.clear();
        cands.addAll(board.getValuesList());
        //System.out.println("update col =" + col);
        for (int i=0; i < board.getEdgeLength(); i++) {
            int v = board.getCell(i, col).getValue();
            //System.out.print("v = "+ v +"; " );
            if (v > 0)  {
                cands.remove(v);
            }
        }
        //System.out.println("col "+col+"cands after remove="+ cands);
    }


    public String toString() {
       return Arrays.toString(candidates_);
    }

}
