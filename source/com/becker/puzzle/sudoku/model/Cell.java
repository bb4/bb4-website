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
    private BigCell parent_;

    /** and, most importantly, the intersection of these.    */
    private Candidates candidates_;


    public Cell(int value, BigCell parent) {
        setOriginalValue(value);
        parent_ = parent;
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
        Candidates bigCellSet = parent_.getCandidates();

        //System.out.println("rowCands=" + rowCandidates + " colCands=" + colCandidates);
        for (Integer candidate : bigCellSet)  {
            if (rowCandidates.contains(candidate) && colCandidates.contains(candidate)) {
               candidates_.add(candidate);
           }
        }
    }


    public void checkAndSetLoanRangers(CandidatesArray candArray,
                                       CandidatesArray candArray1, CandidatesArray candArray2,
                                       Candidates cands1, Candidates cands2) {

        if (getCandidates() == null) return;

        Candidates candsCopy = getCandidates().copy();

        int i=0;
        //System.out.println("starting with "+ candsCopy);
        while (i<candArray.size() && candsCopy.size() > 0) {

            Candidates c = candArray.get(i++);
            if (c != null)  {
                //System.out.println("   removing "+ c);
                candsCopy.removeAll(c);
            }
        }
        //System.out.println("ending up with " + candsCopy);

        if (candsCopy.size() == 1) {
            //System.out.println("setting " + candsCopy.getFirst());
            int unique = candsCopy.getFirst();
            setValue(unique);
            updateCandidateListsAfterSet(unique, candArray, candArray1, candArray2);
            parent_.getCandidates().remove(unique);
            cands1.remove(unique);
            cands2.remove(unique);
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

        int unique = parent_.getUniqueValueForCell(this, rowCandidates, colCandidates);

        if (unique > 0) {
            // set it and remove from appropriate candidate sets
            setValue(unique);
            updateCandidateListsAfterSet(unique, rowCandidates, colCandidates);
        }
    }

    public void updateCandidateListsAfterSet(int unique, Candidates rowCandidates, Candidates colCandidates) {
        assert(candidates_.contains(unique));
        candidates_.clear();
        candidates_ = null;
        boolean removed1 = parent_.getCandidates().remove(unique);
        boolean removed2 = rowCandidates.remove(unique);
        boolean removed3 = colCandidates.remove(unique);

        //assert (removed1) : "Invalid Puzzle: Could not remove " + unique + " from " + parent_.getCandidates();
        //assert (removed2) : "Invalid Puzzle: Could not remove " + unique + " from " + rowCandidates;
        //assert (removed3) : "Invalid Puzzle: Could not remove " + unique + " from " + colCandidates;
    }

    public void updateCandidateListsAfterSet(int unique,
                                             CandidatesArray candArray1,
                                             CandidatesArray candArray2,
                                             CandidatesArray candArray3) {
        assert(candidates_.contains(unique));
        candidates_.clear();
        candidates_ = null;

        for (int i=0; i<candArray1.size(); i++) {
            candArray1.get(i).remove(unique);
        }
        for (int i=0; i<candArray2.size(); i++) {
            candArray2.get(i).remove(unique);
        }
        for (int i=0; i<candArray3.size(); i++) {
            candArray3.get(i).remove(unique);
        }
    }

}
