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

    // row and col cells for every row and col.
    protected CellArrays rowCells_;
    protected CellArrays colCells_;

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
        reset();
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
        bigCells_ = new BigCellArray(n_, valuesList_);
        rowCells_ = CellArrays.createRowCellArrays(this);
        colCells_ = CellArrays.createColCellArrays(this);
        numIterations_ = 0;
    }

    public CellArrays getRowCells() {
        return rowCells_;
    }

    public CellArrays getColCells() {
        return colCells_;
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
        //System.out.println("position = " + position + " position / nn_=" + position / nn_ + " position % nn_=" + position % nn_);
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
        Candidates cands = findCellCandidates(position);
        if (cands == null) {
            return new ValuesList();
        }
        return ValuesList.createShuffledList(cands);
    }

    private Candidates findCellCandidates(int position) {
        //System.out.println("position="+position + " div=" + position / nn_);
        return getCandidates(position / nn_, position % nn_);
    }

    /**
     * find candidate lists for a specific cell.
     * @return cell candidates
     */
     Candidates getCandidates(int row, int col) {
        return getCell(row, col).getCandidates();
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
    public ValuesList getValuesList() {
        return valuesList_;
    }

    public int getNumIterations() {
        return numIterations_;
    }

    public void incrementNumIterations() {
        numIterations_++;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder("\n");
        for (int row=0; row < nn_; row++) {
            for (int col=0; col < nn_; col++) {
                bldr.append(getCell(row, col).getValue());
                bldr.append(" ");
            }
            bldr.append("\n");
        }
        bldr.append("rowCells=\n" + rowCells_);
        //bldr.append("colCells=\n" + colCells_);
        bldr.append("bigCells =\n" + getBigCells());
        return bldr.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;
        if (n_ != board.n_) return false;
        if (nn_ != board.nn_) return false;

        for (int row=0; row < nn_; row++) {
            for (int col=0; col < nn_; col++) {
                if (this.getCell(row, col).getValue() != board.getCell(row, col).getValue() ) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = n_;
        result = 31 * result + nn_;
        result = 31 * result + (rowCells_ != null ? rowCells_.hashCode() : 0);
        return result;
    }
}
