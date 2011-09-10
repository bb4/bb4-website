package com.becker.puzzle.sudoku.model.update;

import com.becker.puzzle.sudoku.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 *  The Board describes the physical layout of the puzzle.
 *
 *  @author Barry Becker
 */
public class LoanRangerUpdater extends AbstractUpdater {

    /**
     * Constructor
     */
    public LoanRangerUpdater(Board b) {
        super(b);
    }

    /**
     * update candidate lists for all cells then set the unique values that are determined.
     * Next check for loan rangers.
     */
    @Override
    public void updateAndSet() {

        checkForLoanRangers();
    }


    /** needs more work */
    private void checkForLoanRangers() {

        int n = board.getBaseSize();

        for (int row = 0; row < board.getEdgeLength(); row++) {
            for (int col = 0; col < board.getEdgeLength(); col++) {
                Cell cell = board.getCell(row, col);

                BigCell bigCell = board.getBigCell(row / n, col / n);
                CandidatesArray bigCellCands = bigCell.getCandidatesArrayExcluding(row % n, col % n);
                CandidatesArray rowCellCands = getCandidatesArrayForRowExcludingCol(row, col);
                CandidatesArray colCellCands = getCandidatesArrayForColExcludingRow(row, col);

                Candidates rowCands = board.getRowCandidates().get(row);
                Candidates colCands = board.getColCandidates().get(col);

                cell.checkAndSetLoanRangers(bigCellCands, rowCellCands, colCellCands, rowCands, colCands);
                cell.checkAndSetLoanRangers(rowCellCands, bigCellCands, colCellCands, rowCands, colCands);
                cell.checkAndSetLoanRangers(colCellCands, bigCellCands, rowCellCands, rowCands, colCands);
            }
        }
    }

    private CandidatesArray getCandidatesArrayForRowExcludingCol(int row, int col) {
        List<Candidates> cands = new ArrayList<Candidates>();

        for (int i = 0; i < board.getEdgeLength(); i++) {
           Candidates c = board.getCell(row, i).getCandidates();
           if ((i != col) && c != null) {
               cands.add(c);
           }
        }
        return new CandidatesArray(cands.toArray(new Candidates[cands.size()]));
    }

    private CandidatesArray getCandidatesArrayForColExcludingRow(int row, int col) {
        List<Candidates> cands = new ArrayList<Candidates>();

        for (int i = 0; i < board.getEdgeLength(); i++) {
           Candidates c = board.getCell(i, col).getCandidates();
           if ((i != row) && c != null) {
               cands.add(c);
           }
        }
        return new CandidatesArray(cands.toArray(new Candidates[cands.size()]));
    }
}
