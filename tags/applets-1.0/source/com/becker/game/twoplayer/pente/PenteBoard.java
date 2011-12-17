/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente;

import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;

/**
 * Representation of a Pente Game Board.
 *
 * @author Barry Becker
 */
public class PenteBoard extends TwoPlayerBoard {

    /** this is an auxiliary structure to help determine candidate moves. should extract to sep class. */
    protected boolean[][] candidateMoves_;

    /** constructor
     *  @param numRows num rows
     *  @param numCols num cols
     */
    public PenteBoard( int numRows, int numCols ) {
        setSize( numRows, numCols );
    }

    /**
     * default constructor
     */
    public PenteBoard() {
        setSize( 30, 30 );
    }

    public PenteBoard(PenteBoard pb) {
        super(pb);
        candidateMoves_ = pb.candidateMoves_.clone();
    }

    @Override
    public PenteBoard copy() {
        return new PenteBoard(this);
    }

    /**
     *  must call reset() after changing the size.
     */
    @Override
    public void setSize( int numRows, int numCols ) {
        super.setSize(numRows, numCols);
        candidateMoves_ = createCandidateMoves();
    }

    private boolean[][] createCandidateMoves() {
        return new boolean[getNumRows() + 2][getNumCols() + 2];
    }

    /**
     * The candidateMoves has a border on all sides
     */
    protected void initCandidateMoves() {
        for ( int i = 0; i <= getNumRows() + 1; i++ )  {
            for ( int j = 0; j <= getNumCols() + 1; j++ ) {
                candidateMoves_[i][j] = false;
            }
        }
    }

    public int getMaxNumMoves() {
        return positions_.getNumBoardSpaces();
    }

    /**
     *  For pente, undoing a move is just changing that space back to a blank.
     */
    @Override
    protected void undoInternalMove( Move move ) {
        TwoPlayerMove m = (TwoPlayerMove)move;
        getPosition(m.getToRow(), m.getToCol()).clear();
    }

    /**
     * This method splats a footprint of trues around the current moves.
     * later we look for empty spots that are true for candidate moves.
     */
    public void determineCandidateMoves()  {
        boolean[][] b = candidateMoves_;
        // first clear out what we had before
        initCandidateMoves();

        // set the footprints
        int i,j;
        boolean hasCandidates = false;
        for ( i = 1; i <= getNumRows(); i++ ) {
            for ( j = 1; j <= getNumCols(); j++ ) {
                if ( getPosition(i, j).isOccupied() ) {
                    b[i - 1][j - 1] = true;
                    b[i - 1][j] = true;
                    b[i - 1][j + 1] = true;
                    b[i][j - 1] = true;
                    b[i][j + 1] = true;
                    b[i + 1][j - 1] = true;
                    b[i + 1][j] = true;
                    b[i + 1][j + 1] = true;
                    hasCandidates = true;
                }
            }
        }
        // edge case when no moves on the board - just use the center
        if (!hasCandidates) {
            b[getNumRows()/2+1][getNumCols()/2+1] = true;
        }
    }

    /**
     * We consider only those spaces bordering on non-empty spaces.
     * In theory all empties should be considered, but in practice only
     * those bordering existing moves are likely to be favorable.
     *
     * @return true if this position is a possible next move
     */
    public boolean isCandidateMove( int row, int col )  {
        return (candidateMoves_[row][col] && getPosition(row, col).isUnoccupied());
    }

    /**
     * Num different states.
     * This is used primarily for the Zobrist hash. You do not need to override if yo udo not use it.
     * States: player1, player2, empty.
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
        return 3;
    }
}
