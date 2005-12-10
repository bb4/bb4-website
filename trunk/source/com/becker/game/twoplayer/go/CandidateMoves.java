package com.becker.game.twoplayer.go;

import com.becker.game.common.BoardPosition;

/**
 * @author Barry Becker
 *         Date: Aug 21, 2005
 */
class CandidateMoves {

    private static final int CANDIDATE_MOVE_OFFSET = 1;

    private int size_;

    // this is an auxilliary structure to help determine candidate moves
    private boolean[][] candidateMoves_;

    CandidateMoves(int size) {
        size_ = size;
        candidateMoves_ = new boolean[size_ + 1][size_ + 1];
    }


    /**
     * we start with a default list of good starting moves, and
     * add to it all moves within 2 spaces of those that are played.
     */
    public void reset()
    {
        //int numRows = getNumRows();
        //int numCols = getNumCols();
        int i,j;

        // this will fill a 2 stone wide strip on the 3rd and 4rth lines of the board.
        // this includes the star points and many others as candidates to consider
        for ( i = 3; i <= size_ - 2; i++ ) {
            candidateMoves_[i][3] = true;
            candidateMoves_[i][4] = true;
            candidateMoves_[i][size_ - 2] = true;
            candidateMoves_[i][size_ - 3] = true;
        }
        for ( j = 5; j <= size_ - 4; j++ ) {
            candidateMoves_[3][j] = true;
            candidateMoves_[4][j] = true;
            candidateMoves_[size_ - 2][j] = true;
            candidateMoves_[size_ - 3][j] = true;
        }
        // also make the center space a candidate move
        candidateMoves_[((size_ + 1) >> 1)][((size_ + 1) >> 1)] = true;
    }


    /**
     * this method splats a footprint of trues around the current moves.
     * later we look for empty spots that are true for candidate moves
     */
    public final void determineCandidateMoves(BoardPosition positions[][])
    {
        //  set the footprints
        int i,j;
        for ( i = 1; i <= size_; i++ )
            for ( j = 1; j <= size_; j++ )
                if ( !positions[i][j].isUnoccupied() )
                    addCandidateMoves( positions[i][j], positions);
    }

    /**
     * this method splats a footprint of trues around the specified move.
     * @param stone
     */
    private void addCandidateMoves( BoardPosition stone, BoardPosition positions[][] )
    {
        int i,j;
        boolean[][] b = candidateMoves_;

        int startrow = Math.max( stone.getRow() - CANDIDATE_MOVE_OFFSET, 1 );
        int stoprow = Math.min( stone.getRow() + CANDIDATE_MOVE_OFFSET, size_ );
        int startcol = Math.max( stone.getCol() - CANDIDATE_MOVE_OFFSET, 1 );
        int stopcol = Math.min( stone.getCol() + CANDIDATE_MOVE_OFFSET, size_ );
        // set the footprint
        for ( i = startrow; i <= stoprow; i++ ) {
            for ( j = startcol; j <= stopcol; j++ )  {
                GoBoardPosition pos = (GoBoardPosition) positions[i][j];
                // never add a stone (from either side to an unconditonally alive eye. There is no advantage to it.
                if (pos.isUnoccupied() && !(pos.getEye()!=null && pos.getEye().isUnconditionallyAlive())) {
                    b[i][j] = true;
                }
            }
        }
    }

    /**
     * In theory, all empties should be considered, but in practice we keep
     * a shorter list of reasonable moves lest things get intractable.
     *
     * @return true if this position is a reasonable next move
     */
    public final boolean isCandidateMove( int row, int col )
    {
        return candidateMoves_[row][col];
    }
}
