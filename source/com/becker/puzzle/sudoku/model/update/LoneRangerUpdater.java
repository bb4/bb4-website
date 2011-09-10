package com.becker.puzzle.sudoku.model.update;

import com.becker.puzzle.sudoku.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 *  Lone rangers are cells that have a candidate (among others) that is unique when
 *  compared with the other candidates in other cells in that row, column, or bigCell.
 *
 *  @author Barry Becker
 */
public class LoneRangerUpdater extends AbstractUpdater {

    /**
     * Constructor
     */
    public LoneRangerUpdater(Board b) {
        super(b);
    }

    /**
     * Lone rangers are cells than have a unique candidate. For example, consider these
     * candidates for cells in a row (or column, or bigCell).
     * 23 278 13 28 238 23
     * Then the second cell has 7 as a lone ranger.
     */
    @Override
    public void updateAndSet() {

        checkForLoneRangers();
    }

    /** needs more work */
    private void checkForLoneRangers() {

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

                cell.checkAndSetLoneRangers(bigCellCands, rowCellCands, colCellCands, rowCands, colCands);
                cell.checkAndSetLoneRangers(rowCellCands, bigCellCands, colCellCands, rowCands, colCands);
                cell.checkAndSetLoneRangers(colCellCands, bigCellCands, rowCellCands, rowCands, colCands);
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
