package com.becker.puzzle.sudoku;

import java.util.*;

/**
 * @author Barry Becker Date: Jul 3, 2006
 */
public class Cell {

    // must be a number between 1 and nn_
    private int value_;

    // true if part of the original specification.
    private boolean original_;

    // the BigCell to which I belong
    private BigCell parent_;

    // and, most importantly, the intersection of these.
    private List candidates_ = null;


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
        candidates_ = new LinkedList();
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
            candidates_ = new LinkedList();
        }
    }

    public List getCandidates() {
        if (original_)
            assert(candidates_ == null) : candidates_ +" not null";
        return candidates_;
    }

    public void updateCandidates(List rowCandidates, List colCandidates) {
        if (candidates_ == null)
            return;
        candidates_.clear();
        List bcList = parent_.getCandidates();
        for (Object candidate : bcList)  {
            //System.out.println("Checking to see if " + candidate + " is also in "
            //                   + getRowCandidates(row)
            //                  + " or " + getColCandidates(col));
            if (rowCandidates.contains(candidate) && colCandidates.contains(candidate)) {
               candidates_.add(candidate);
           }
        }
    }

    /**
     * @@ Perhaps make a separate pass for setting all the cells that only have one value in candidate list first.
     * This should improve performance.
     */
    public void checkAndSetUniqueValues(List rowCandidates, List colCandidates) {

        if (candidates_ == null) {
            // nothing to do, the final value is already determined.
            return;
        }

        int unique = parent_.getUniqueValueForCell(this);
        if (unique > 0) {
            // set it and remove from appropriate candidate lists
            //System.out.println("setting val of "+ row+ "  "+col+" to "+ unique );
            setValue(unique);
            assert(candidates_.contains(unique));
            candidates_.clear();
            candidates_ = null;
            boolean removed1 = parent_.getCandidates().remove((Integer) unique);
            boolean removed2 = rowCandidates.remove((Integer) unique);
            boolean removed3 = colCandidates.remove((Integer) unique);

            assert (removed1) : "Invalid Puzzle: Could not remove " + unique + " from " + parent_.getCandidates();
            assert (removed2) : "Invalid Puzzle: Could not remove " + unique + " from " + rowCandidates;
            assert (removed3) : "Invalid Puzzle: Could not remove " + unique + " from " + colCandidates;
        }
    }

}
