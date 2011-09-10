package com.becker.puzzle.sudoku.model;

import com.becker.puzzle.sudoku.model.update.LoneRangerUpdater;
import com.becker.puzzle.sudoku.model.update.StandardCRBUpdater;

/**
 *  The Board describes the physical layout of the puzzle.
 *
 *  @author Barry Becker
 */
public class Board {

    /** The number of Cells in the board is n^2 * n^2, but there are n * n big cells.   */
    protected int n_;
    protected int nn_;  // n times n

    static final int MAX_SIZE = 9;

    // row and col candidates for every row and col.
    protected CandidatesArray rowCandidates_;
    protected CandidatesArray colCandidates_;

    /** the internal data structures representing the game board. */
    protected BigCellArray bigCells_;

    /** all the values in the big cells or rows/cols 1...nn_ */
    private final ValuesList valuesList_;

    private int numIterations_;

    /**
     * Constructor
     */
    public Board(int size) {
        assert(size > 1 && size < MAX_SIZE);
        n_ = size;
        nn_ = size * size;
        valuesList_ = new ValuesList(nn_);

        bigCells_ = new BigCellArray(n_);
        rowCandidates_ = new CandidatesArray(nn_);
        colCandidates_ = new CandidatesArray(nn_);
    }

    /**
     * copy constructor
     */
    public Board(Board b) {
        this(b.getBaseSize());
        for (int i=0; i<nn_; i++) {
           for (int j=0; j<nn_; j++) {
               getCell(i, j).setOriginalValue(b.getCell(i, j).getValue());
           }
        }
    }

    public Board(int[][] initialData) {
        this((int) Math.sqrt(initialData.length));

        assert(initialData.length == nn_ && initialData[0].length == nn_);

        for (int i=0; i<nn_; i++) {
           for (int j=0; j<nn_; j++) {
               getCell(i, j).setOriginalValue(initialData[i][j]);
           }
        }
    }

    /**
     * return to original state before attempting solution.
     * Non original values become 0.
     */
    public void reset() {
        rowCandidates_ = new CandidatesArray(nn_);
        colCandidates_ = new CandidatesArray(nn_);
        bigCells_.reset();
    }

    public CandidatesArray getRowCandidates() {
        return rowCandidates_;
    }

    public CandidatesArray getColCandidates() {
        return colCandidates_;
    }

    public BigCellArray getBigCells() {
        return bigCells_;
    }

    /**
     * @return retrieve the base size of the board (sqrt(edge magnitude)).
     */
    public final int getBaseSize() {
        return n_;
    }

    /**
     * @return  retrieve the edge size of the board.
     */
    public final int getEdgeLength() {
        return nn_;
    }

    /**
     * @return the bigCell at the specified location.
     */
    public final BigCell getBigCell( int row, int col ) {

        return bigCells_.getBigCell(row, col);
    }

    /**
     * @param row 0-nn_-1
     * @param col 0-nn_-1
     * @return the cell in the bigCellArray at the specified location.
     */
    public final Cell getCell( int row, int col ) {
        return bigCells_.getCell(row, col);
    }

    /**
     * @param position a number between 0 and nn_^2
     * @return the cell at the specified position.
     */
    public final Cell getCell( int position ) {
        return getCell(position / nn_, position % nn_);
    }

    /**
     * @return true if the board has been successfully solved.
     */
    public boolean solved() {

        return bigCells_.isFilledIn() && bigCells_.hasNoCandidates();
    }

    /**
     * @return cell candidates in random order for specified position.
     */
    public ValuesList getShuffledCellCandidates(int position) {
        return ValuesList.createShuffledList(findCellCandidates(position));
    }

    private Candidates findCellCandidates(int position) {
        //System.out.println("position="+position + " div=" + position / nn_);
        return findCellCandidates(position / nn_, position % nn_);
    }

    /**
     * update candidate lists for a specific cell
     * @return cell candidates
     */
     Candidates findCellCandidates(int row, int col) {

        rowCandidates_.updateRow(row, this);
        colCandidates_.updateCol(col, this);
        getBigCell(row / n_, col / n_).updateCandidates(this);

        // find the cell candidates (intersection of above lists)
        Cell c = getCell(row, col);
        //System.out.println("rowCandidates_.get("+row+")=" + rowCandidates_.get(row) + " col=" + col);
        c.updateCandidates(rowCandidates_.get(row), colCandidates_.get(col));
        return c.getCandidates();
    }

    /**
     * update candidate lists for all cells then set the unique values that are determined.
     * Next check for loan rangers.
     */
    public void updateAndSet() {

        new StandardCRBUpdater(this).updateAndSet();
        new LoneRangerUpdater(this).updateAndSet();
    }

    /**
     * @return the complete set of allowable values (1,... nn);
     */
    protected ValuesList getValuesList() {
        return valuesList_;
    }

    public int getNumIterations() {
        return numIterations_;
    }

    public void setNumIterations(int numIterations) {
        numIterations_ = numIterations;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        for (int row=0; row < nn_; row++) {
            for (int col=0; col < nn_; col++) {
                bldr.append(getCell(row, col).getValue());
                bldr.append(" ");
            }
            bldr.append("\n");
        }
        return bldr.toString();
    }
}
