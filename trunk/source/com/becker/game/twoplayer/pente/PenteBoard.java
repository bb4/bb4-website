package com.becker.game.twoplayer.pente;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerBoard;

/**
 * Representation of a Pente Game Board
 *
 * @author Barry Becker
 */
public class PenteBoard extends TwoPlayerBoard
{

    // this is an auxilliary structure to help determine candidate moves
    private boolean[][] candidateMoves_ = null;

    /** constructor
     *  @param numRows num rows
     *  @param numCols num cols
     */
    public PenteBoard( int numRows, int numCols )
    {
        setSize( numRows, numCols );
    }

    // reset the board to its initial state
    public void reset()
    {
        super.reset();
        for ( int i = 1; i <= getNumRows(); i++ )
            for ( int j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new BoardPosition( i, j, null);
            }
        initCandidateMoves();
    }

    private void initCandidateMoves()
    {
        // the candidateMoves has a border on all sides
        for ( int i = 0; i <= getNumRows() + 1; i++ )
            for ( int j = 0; j <= getNumCols() + 1; j++ ) {
                candidateMoves_[i][j] = false;
            }
    }

    // must call reset() after changing the size
    public void setSize( int numRows, int numCols )
    {
        numRows_ = numRows;
        numCols_ = numCols;
        rowsTimesCols_ = numRows_ * numCols_;
        // we don't use the 0 edges of the board
        positions_ = new BoardPosition[numRows_ + 1][numCols_ + 1];
        candidateMoves_ = new boolean[numRows_ + 2][numCols_ + 2];
        reset();
    }

    public int getMaxNumMoves()
    {
        return rowsTimesCols_;
    }

    // for pente, undoing a move is just changing that space back to a blank
    protected void undoInternalMove( Move move )
    {
        TwoPlayerMove m = (TwoPlayerMove)move;
        positions_[m.getToRow()][m.getToCol()].clear();
    }

    /**
     * this method splats a footprint of trues around the current moves.
     * later we look for empty spots that are true for candidate moves
     */
    public void determineCandidateMoves()
    {
        boolean[][] b = candidateMoves_;
        // first clear out what we had before
        initCandidateMoves();

        //  set the footprints
        int i,j;
        for ( i = 1; i <= getNumRows(); i++ )
            for ( j = 1; j <= getNumCols(); j++ ) {
                if ( positions_[i][j].isOccupied() ) {
                    b[i - 1][j - 1] = true;
                    b[i - 1][j] = true;
                    b[i - 1][j + 1] = true;
                    b[i][j - 1] = true;
                    b[i][j + 1] = true;
                    b[i + 1][j - 1] = true;
                    b[i + 1][j] = true;
                    b[i + 1][j + 1] = true;
                }
            }
    }

    /**
     * We consider only those spaces bordering on non-empty spaces.
     * In theory all empties should be considered, but in practice only
     * those bordering existing moves are likely to be favorable.
     *
     * @return true if this position is a possible next move
     */
    public boolean isCandidateMove( int row, int col )
    {
        //System.out.println("boolb="+candidateMoves_[row][col] +"b="+positions_[row][col]);
        return (candidateMoves_[row][col] && positions_[row][col].isUnoccupied());
    }
}
