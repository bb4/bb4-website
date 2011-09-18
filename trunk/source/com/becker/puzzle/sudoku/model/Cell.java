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


    /** and, most importantly, the intersection of these.   */
    private Candidates candidates_;

    /**
     * Constructor.
     */
    public Cell(int value, BigCell parent, ValuesList values) {
        setOriginalValue(value);
        parentBigCell_ = parent;
        candidates_ = new Candidates(values);
    }

    public void setRowCells(CellArray rowCells) {
        rowCells_ = rowCells;
    }

    public void setColCells(CellArray colCells) {
        colCells_ = colCells;
    }

    public int getValue() {
        return value_;
    }

    public boolean isOriginal() {
        return original_;
    }

    /**
     * once the puzzle is started, you can only assign positive values to values of cells.
     * @param value
     */
    public void setValue(int value) {
        assert(value > 0);
        value_ = value;
        original_ = false;
        candidates_ = null;
        parentBigCell_.remove(value_);
        rowCells_.remove(value_);
        colCells_.remove(value_);
    }

    /**
     * set the value back to unset and add the old value to the list of candidates
     * The value should only be added bak to row/col/bigCell candidates if the value is not already set
     * for respective row/col/bigCell.
     */
    public void clearValue() {
        if (value_ == 0) return;

        int value = value_;
        value_ = 0;
        original_ = false;

        rowCells_.add(value);
        colCells_.add(value);
        parentBigCell_.add(value);

        if (candidates_ == null) {
            candidates_ = new Candidates();
        }
        candidates_.add(value);
    }

    /**
     * The value is not available if it is already set in another cell in the roe, col, orbigCell.
     * @param value  value to check
     * @return true if the value is available to restore in the cell.
     */
    public boolean isAvailable(int value) {
        return (parentBigCell_.isAvailable(value)
                && rowCells_.isAvailable(value)
                && colCells_.isAvailable(value));
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
            candidates_ = null;
        }
    }

    public Candidates getCandidates() {
        return candidates_;
    }

    /**
     * Only add to our candidates list if this cell has not yet been decided.
     * @param value
     */
    public void addCandidateValue(int value) {
        if (candidates_ != null) {
            candidates_.add(value);
        }
    }

    public void removeCandidateValue(int value) {
        assert value > 0;
        if (candidates_ != null) {
            candidates_.remove(value);
        }
    }

    /**
     * Intersect the parent big cell candidates with the row and column candidates.
     */
    public void updateCandidates() {

        if (candidates_ == null) {
            return;
        }
        candidates_.clear();
        candidates_.addAll(parentBigCell_.getCandidates());
        candidates_.retainAll(rowCells_.getCandidates());
        candidates_.retainAll(colCells_.getCandidates());

        if (candidates_.size() == 1) {
            int unique = candidates_.getFirst();
            setValue(unique);
        }
    }

    public String toString() {
         return "cell candidates = "+ candidates_;
    }
}
