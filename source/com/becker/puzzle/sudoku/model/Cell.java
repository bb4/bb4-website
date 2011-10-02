package com.becker.puzzle.sudoku.model;

/**
 * @author Barry Becker
 */
public class Cell {

    /** must be a number between 1 and nn_  */
    private int value_;

    /** true if part of the original specification.  */
    private boolean original_;

    /** the BigCell to which I belong   */
    private BigCell parentBigCell_;
    private CellArray rowCells_;
    private CellArray colCells_;

    /**
     * Constructor.
     */
    public Cell(int value) {
        setOriginalValue(value);
    }

    public void setParent(BigCell parent) {
        parentBigCell_ = parent;
    }

    public int getValue() {
        return value_;
    }

    public boolean isOriginal() {
        return original_;
    }

    public boolean isParent(BigCell bigCell) {
        return bigCell == parentBigCell_;
    }

    /**
     * once the puzzle is started, you can only assign positive values to values of cells.
     * @param value the value to set permanently in the cell (at least until cleared).
     */
    public void setValue(int value) {
        assert(value > 0);
        value_ = value;
        original_ = false;

        parentBigCell_.remove(value_);
        rowCells_.remove(value_);
        colCells_.remove(value_);
    }

    /**
     * Set the value back to unset and add the old value to the list of candidates
     * The value should only be added back to row/col/bigCell candidates if the value is not already set
     * for respective row/col/bigCell.
     * Clear value should be the inverse of setValue.
     */
    public void clearValue() {
        if (value_ == 0) return;

        int value = value_;
        value_ = 0;
        original_ = false;

        rowCells_.add(value);
        colCells_.add(value);
        parentBigCell_.add(value);
    }

    /**
     * Once the puzzle is started, you can only assign positive values to values of cells.
     * @param value
     */
    public void setOriginalValue(int value) {
        assert(value >= 0);
        value_ = value;

        // if set to 0 initially, then it is a value that needs to be filled in.
        original_ = value > 0;

        if (original_)  {
            parentBigCell_.remove(value);
            rowCells_.remove(value);
            colCells_.remove(value);
        }
    }

    /**
     * Intersect the parent big cell candidates with the row and column candidates.
     * [If after doing the intersection, we have only one value, then set it on the cell. ]
     */
    public Candidates getCandidates() {

        if (value_ > 0) return null;
        Candidates candidates = new Candidates();
        candidates.addAll(parentBigCell_.getCandidates());
        candidates.retainAll(rowCells_.getCandidates());
        candidates.retainAll(colCells_.getCandidates());
        return candidates;
    }

    void setRowCells(CellArray rowCells) {
        rowCells_ = rowCells;
    }

    void setColCells(CellArray colCells) {
        colCells_ = colCells;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;
        return value_ == cell.value_;
    }

    @Override
    public int hashCode() {
        return value_;
    }

    public String toString() {
         return "Cell value=" + getValue();
    }
}
