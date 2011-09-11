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

    private CellArray rowCells;
    private CellArray colCells;

    /** and, most importantly, the intersection of these.    */
    private Candidates candidates_;

    /**
     * Constructor.
     */
    public Cell(int value, BigCell parent) {
        setOriginalValue(value);
        parentBigCell_ = parent;
    }


    public int getValue() {
        return value_;
    }

    public boolean isOriginal() {
        return original_;
    }

    public BigCell getParentBigCell() {
        return parentBigCell_;
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
    }

    public void clearValue() {
        value_ = 0;
        original_ = false;
        candidates_ = new Candidates();
    }

    /**
     * once the puzzle is started, you can only assign positive values to values of cells.
     * @param value
     */
    public void setOriginalValue(int value) {
        assert(value >= 0);
        value_ = value;

        // if set to 0 initially, then it is a value that needs to be filled in.
        original_ = value > 0;

        if (original_)  {
            candidates_ = null;
        }
        else {
            candidates_ = new Candidates();
        }
    }

    public Candidates getCandidates() {
        if (original_) {
            assert(candidates_ == null) : candidates_ +" not null";
        }
        return candidates_;
    }

    /**
     * Intersect the parent big cell candidates with the row and column candidates.
     */
    public void updateCandidates(Candidates rowCandidates, Candidates colCandidates) {

        if (candidates_ == null)
            return;
        candidates_.clear();
        Candidates bigCellSet = parentBigCell_.getCandidates();

        //System.out.println("rowCands=" + rowCandidates + " colCands=" + colCandidates);
        for (Integer candidate : bigCellSet)  {
            if (rowCandidates.contains(candidate) && colCandidates.contains(candidate)) {
               candidates_.add(candidate);
           }
        }
    }


    /**
     * @@ Perhaps make a separate pass for setting all the cells that only have one value in the
     * candidate set first.
     * This should improve performance.
     */
    public void checkAndSetUniqueValues(Candidates rowCandidates, Candidates colCandidates) {

        if (candidates_ == null) {
            // nothing to do, the final value is already determined.
            return;
        }

        int unique = parentBigCell_.getUniqueValueForCell(this, rowCandidates, colCandidates);

        if (unique > 0) {
            // set it and remove from appropriate candidate sets
            setValue(unique);
            updateCandidateListsAfterSet(unique, rowCandidates, colCandidates);
        }
    }

    public void updateCandidateListsAfterSet(int unique, Candidates rowCandidates, Candidates colCandidates) {
        //assert(candidates_.size() == 1 && candidates_.contains(unique));
        //candidates_.clear();
        //candidates_ = null;
        boolean removed1 = parentBigCell_.getCandidates().remove(unique);
        boolean removed2 = rowCandidates.remove(unique);
        boolean removed3 = colCandidates.remove(unique);

        //assert (removed1) : "Invalid Puzzle: Could not remove " + unique + " from " + parentBigCell_.getCandidates();
        //assert (removed2) : "Invalid Puzzle: Could not remove " + unique + " from " + rowCandidates;
        //assert (removed3) : "Invalid Puzzle: Could not remove " + unique + " from " + colCandidates;
    }

    public void updateCandidateListsAfterSet(int unique,
                                             CandidatesArray candArray1,
                                             CandidatesArray candArray2,
                                             CandidatesArray candArray3) {
        //assert(candidates_.contains(unique));
        //candidates_.clear();
        //candidates_ = null;

        for (int i=0; i<candArray1.size(); i++) {
            Candidates cands1 = candArray1.get(i);
            cands1.remove(unique);
            //assert(cands1.size() > 1) :  cands1;
        }
        for (int i=0; i<candArray2.size(); i++) {
            Candidates cands2 = candArray2.get(i);
            cands2.remove(unique);
            //assert(cands2.size() > 1) :  cands2;
        }
        for (int i=0; i<candArray3.size(); i++) {
            Candidates cands3 = candArray3.get(i);
            cands3.remove(unique);
            //assert(cands3.size() > 1) :  cands3;
        }
    }

}
