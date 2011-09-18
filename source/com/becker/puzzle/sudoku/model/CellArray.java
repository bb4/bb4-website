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

    /** the candidates for the cells in this row or column */
    private Candidates candidates_;

    /**
     * Constructor
     * @param size this size of the row (small grid dim squared).
     */
    private CellArray(int size) {

        candidates_ = new Candidates();
        cells_ = new Cell[size];
    }

    public static CellArray createRowCellArray(int row, Board board) {
        CellArray cells = new CellArray(board.getEdgeLength());
        cells.candidates_.addAll(board.getValuesList());
        for (int i=0; i<board.getEdgeLength(); i++) {
            Cell cell = board.getCell(row, i);
            cell.setRowCells(cells);
            cells.cells_[i] = cell;
            if (cell.getValue() > 0) {
                cells.remove(cell.getValue());
            }
        }
        return cells;
    }

    public static CellArray createColCellArray(int col, Board board) {
        CellArray cells = new CellArray(board.getEdgeLength());
        cells.candidates_.addAll(board.getValuesList());
        for (int i=0; i<board.getEdgeLength(); i++) {
            Cell cell = board.getCell(i, col);
            cell.setColCells(cells);
            cells.cells_[i] = cell;
            if (cell.getValue() > 0) {
                cells.remove(cell.getValue());
            }
        }
        return cells;
    }

    public Cell getCell(int i) {
        return cells_[i];
    }

    public Candidates getCandidates() {
        return candidates_;
    }

    public void remove(int unique) {
        candidates_.remove(unique);
        for (int j=0; j < size(); j++) {
            getCell(j).removeCandidateValue(unique);
        }
    }

    /**
     * We can only add the value if none of our cells already have it set.
     * @param value
     */
    public void add(int value) {

        for (int j=0; j < size(); j++) {
            Cell cell = getCell(j);
            if (cell.isAvailable(value))  {
                cell.addCandidateValue(value);
            }
        }
        candidates_.add(value);
    }

    public boolean isAvailable(int value) {
        for (int j=0; j < size(); j++) {
            if (getCell(j).getValue() == value) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return cells_.length;
    }

    /**
     * Make sure that the candidates for this cell are all values less the values from the other cells in this array.
     */
    public void updateEntries(int entry, ValuesList values) {

        Candidates cands = getCell(entry).getCandidates();
        if (cands == null) return;
        cands.clear();
        cands.addAll(values);

        for (int j=0; j < size(); j++) {
            int v = getCell(j).getValue();
            if (v > 0)  {
                cands.remove(v);
            }
        }
    }

    public String toString() {
       return "CellArray cells:" +Arrays.toString(cells_) + "    cands=" + candidates_ + "\n";
    }
}
