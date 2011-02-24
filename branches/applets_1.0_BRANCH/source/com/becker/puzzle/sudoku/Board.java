package com.becker.puzzle.sudoku;

import java.util.*;

/**
 *  The Board describes the physical layout of the puzzle.
 *  Assumes an M*N grid.
 *  Legal positions are [1, numRows_][1, numCols_]
 *
 *  @author Barry Becker
 */
public class Board
{
    /** the internal data structures representing the game board. */
    protected BigCell bigCells_[][] = null;

    /** The number of Cells in the board is n^2 * n^2, but there are n * n big cells.   */
    protected int n_;
    protected int nn_;  // n times n

    static final int MAX_SIZE = 9;

    // row and col candidates for every row and col.
    protected Set<Integer>[] rowCandidates_;
    protected Set<Integer>[] colCandidates_;

    /** all the values in the big cell 1...nn_ */
    private final List<Integer> valuesList_;

    private int numIterations_;
    private Random random_ = new Random();


    public Board(int size) {
        assert(size > 1 && size < MAX_SIZE);
        n_ = size;
        nn_ = size * size;

        bigCells_ = new BigCell[n_][n_];
        for (int i=0; i<n_; i++) {
           for (int j=0; j<n_; j++) {
               bigCells_[i][j] = new BigCell(n_, this);
           }
        }

        initRowColCandidateLists();

        valuesList_ = new ArrayList<Integer>();
        for (int i = 1; i <= nn_; i++) {
            valuesList_.add(i);
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
     * copy constructor
     * @param b the board to copy.
     */
    public Board(Board b) {
        this(b.getBaseSize());
        for (int i=0; i<nn_; i++) {
           for (int j=0; j<nn_; j++) {
               getCell(i,j).setOriginalValue(b.getCell(i,j).getValue());
           }
        }
    }

    /**
     * return to original state before attempting solution.
     * Non original values become 0.
     */
    public void reset() {
        initRowColCandidateLists();

        for (int i=0; i<n_; i++) {
           for (int j=0; j<n_; j++) {
               bigCells_[i][j].getCandidates().clear();
           }
        }

        for (int i=0; i<nn_; i++) {
           for (int j=0; j<nn_; j++) {
               Cell c = getCell(i,j);
               if (!c.isOriginal()) {
                   c.clearValue();
               }
           }
        }
    }

    /**
     * @return  retrieve the base size of the board (sqrt(edge length)).
     */
    public final int getBaseSize()
    {
        return n_;
    }

    /**
     * @return  retrieve the edge size of the board.
     */
    public final int getEdgeLength()
    {
        return nn_;
    }


    /**
     * returns null if there is no game piece at the position specified.
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final BigCell getBigCell( int row, int col )
    {
        assert ( row >= 0 && row < n_ && col >= 0 && col < n_);
        return bigCells_[row][col];
    }

    /**
     * returns null if there is no game piece at the position specified.
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final Cell getCell( int row, int col )
    {
        assert ( row >= 0 && row < nn_ && col >= 0 && col < nn_);
        return bigCells_[row / n_][col / n_].getCell(row % n_, col % n_);
    }

    /**
     * returns null if there is no game piece at the position specified.
     * @return the piece at the specified position. Returns null if there is no piece there.
     */
    public final Cell getCell( int position )
    {
        return getCell(position / nn_, position % nn_);
    }

    /**
     * @return true if the board has been successfully solved.
     */
    public boolean solved() {

        if (isFilledIn()) {
            for (int row=0; row < nn_; row++) {
                for (int col=0; col < nn_; col++) {
                    Cell c = this.getCell(row, col);
                    if (c.getCandidates() != null) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @return true if all the cells have been filled in with a value (even if not a valid solution).
     */
    public boolean isFilledIn() {

        for (int row = 0; row < nn_; row++) {
            for (int col = 0; col < nn_; col++) {
                Cell c = this.getCell(row, col);
                if (c.getValue() <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return cell candidates in random order for specified position.
     */
    public List<Integer> getCellCandidates(int position) {
        Set<Integer> candidates = findCellCandidates(position);
        List<Integer>  randomCandidates = new ArrayList<Integer>(candidates.size());
        randomCandidates.addAll(candidates);
        Collections.shuffle(randomCandidates, random_);
        return randomCandidates;
    }

    private Set<Integer> findCellCandidates(int position) {
        return findCellCandidates(position / nn_, position % nn_);
    }

    /**
     * update candidate lists for a specific cell
     * @return cell candidates
     */
    public Set<Integer> findCellCandidates(int row, int col) {
        updateRowCandidates(row);
        updateColCandidates(col);
        getBigCell(row/n_, col/n_).updateCandidates();

        // find the cell candidates (intersection of above lists)
        Cell c = getCell(row, col);
        c.updateCandidates(getRowCandidates(row), getColCandidates(col));
        return c.getCandidates();
    }

    /**
     * update candidate lists for all cells
     */
    public void updateCellCandidates() {
        updateRowCandidates();
        updateColCandidates();
        updateBigCellCandidates();

        // find the cell candidates (intersection of above lists)
        for (int row=0; row < nn_; row++) {
            for (int col=0; col < nn_; col++) {
                getCell(row, col).updateCandidates(getRowCandidates(row), getColCandidates(col));
            }
        }
    }

    private void updateRowCandidates() {
        for (int row = 0; row < nn_; row++) {
            updateRowCandidates(row);
        }
    }

    private void updateRowCandidates(int row) {
        Set<Integer> rowCandidates = rowCandidates_[row];
        rowCandidates.clear();
        rowCandidates.addAll(valuesList_);
        for (int j=0; j < nn_; j++) {
            int v = getCell(row, j).getValue();
            if (v > 0)  {
                rowCandidates.remove(v);
            }
        }
    }

    private void updateColCandidates() {
        for (int col = 0; col < nn_; col++) {
            updateColCandidates(col);
        }
    }

    private void updateColCandidates(int col) {
        Set<Integer> colCands = colCandidates_[col];
        colCands.clear();
        colCands.addAll(valuesList_);
        for (int j = 0; j < nn_; j++) {
            int v = this.getCell(j, col).getValue();
            if (v > 0 )  {
                colCands.remove(v);
            }
        }
    }


    private void updateBigCellCandidates() {
        for (int i=0; i<n_; i++) {
            for (int j=0; j<n_; j++) {
                getBigCell(i, j).updateCandidates();
            }
        }
    }

    public void checkAndSetUniqueValues() {
        for (int row=0; row < nn_; row++) {
            for (int col=0; col < nn_; col++) {
                getCell(row, col).checkAndSetUniqueValues(getRowCandidates(row), getColCandidates(col));
            }
        }
    }

    /**
     * @return the complete set of allowable values (1,... nn);
     */
    protected List<Integer> getValuesList() {
        return valuesList_;
    }

    protected Set<Integer> getRowCandidates(int row)  {
        return rowCandidates_[row];
    }

    protected Set<Integer> getColCandidates(int col)  {
        return colCandidates_[col];
    }

    public int getNumIterations() {
        return numIterations_;
    }

    public void setNumIterations(int numIterations) {
        numIterations_ = numIterations;
    }

    /**
     *  create and initialize the row and col candidate lists
     */
    private void initRowColCandidateLists() {
        rowCandidates_ = new Set[nn_];
        colCandidates_ = new Set[nn_];
        for (int i=0; i < nn_; i++) {
            rowCandidates_[i] = new HashSet<Integer>();
            colCandidates_[i] = new HashSet<Integer>();
        }
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
