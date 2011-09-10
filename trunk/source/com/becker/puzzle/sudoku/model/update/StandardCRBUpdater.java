package com.becker.puzzle.sudoku.model.update;

import com.becker.puzzle.sudoku.model.Board;
import com.becker.puzzle.sudoku.model.CandidatesArray;

/**
 *  CRB stands for Column, Row, Big Cell.
 *  We scan each for unique values. When we find one we set it permanently in the cell.
 *
 *  @author Barry Becker
 */
public class StandardCRBUpdater extends AbstractUpdater {

    /**
     * Constructor
     */
    public StandardCRBUpdater(Board b) {
        super(b);
    }

    /**
     * update candidate lists for all cells then set the unique values that are determined.
     */
    @Override
    public void updateAndSet() {

        updateCellCandidates();
        checkAndSetUniqueValues();
    }

    protected void updateCellCandidates() {
        for (int row = 0; row < board.getEdgeLength(); row++) {
            board.getRowCandidates().updateRow(row, board);
        }
        for (int col = 0; col < board.getEdgeLength(); col++) {
            board.getColCandidates().updateCol(col, board);
        }
        board.getBigCells().update(board);
    }

    /**
     * Takes the intersection of the three sets: row, col, bigCell candidates.
     */
    private void checkAndSetUniqueValues() {
        checkAndSetUniqueValues(board.getRowCandidates(), board.getColCandidates());
    }

    protected void checkAndSetUniqueValues(CandidatesArray rowCands, CandidatesArray colCands) {
        for (int row = 0; row < board.getEdgeLength(); row++) {
            for (int col = 0; col < board.getEdgeLength(); col++) {
                board.getCell(row, col).checkAndSetUniqueValues(rowCands.get(row), colCands.get(col));
            }
        }
    }
}
