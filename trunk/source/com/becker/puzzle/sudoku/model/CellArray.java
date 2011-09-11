package com.becker.puzzle.sudoku.model;

import java.util.Arrays;

/**
 *  An array of cells for a row or column in the puzzle.
 *
 *  @author Barry Becker
 */
public class CellArray {

    /** candidate sets for a row or col.   */
    private Cell[] cells_;

    /**
     * Constructor
     * @param size this size of the row (small grid dim squared).
     */
    private CellArray(int size) {

        cells_ = new Cell[size];
    }

    public static CellArray createRowCellArray(int row, Board board) {
        CellArray cells = new CellArray(board.getEdgeLength());
        for (int i=0; i<board.getEdgeLength(); i++) {
            cells.cells_[i] = board.getCell(row, i);
        }
        return cells;
    }

    public static CellArray createColCellArray(int col, Board board) {
        CellArray cells = new CellArray(board.getEdgeLength());
        for (int i=0; i<board.getEdgeLength(); i++) {
            cells.cells_[i] = board.getCell(i, col);
        }
        return cells;
    }

    public Cell get(int i) {
        return cells_[i];
    }

    public int size() {
        return cells_.length;
    }

    public void updateArray(int entry, ValuesList values) {

        Candidates cands = get(entry).getCandidates();
        cands.clear();
        cands.addAll(values);

        for (int j=0; j < size(); j++) {
            int v = get(j).getValue();
            if (v > 0)  {
                cands.remove(v);
            }
        }
    }



    public String toString() {
       return Arrays.toString(cells_);
    }

}
