package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.multiplayer.poker.player.*;
import java.util.List;

/**
 * Representation of a Poker Game Board
 *
 * @author Barry Becker
 */
public class PokerTable extends Board
{

    /** constructor
     *  @param numRows num rows
     *  @param numCols num cols
     */
    public PokerTable( int numRows, int numCols )
    {
        setSize( numRows, numCols );
    }


    /**
     *  reset the board to its initial state
     */
    public void reset()
    {
        super.reset();
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new BoardPosition( i, j, null);
            }
        }
    }


    public void setSize( int numRows, int numCols )
    {
        numRows_ = numRows;
        numCols_ = numCols;
        rowsTimesCols_ = numRows_ * numCols_;
        // we don't use the 0 edges of the board
        positions_ = new BoardPosition[numRows_ + 1][numCols_ + 1];
        reset();
    }

    /**
     * A poker game has no real limit so we just reutnr a huge number.
     * @return max number of poker rounds allowed.
     */
    public int getMaxNumMoves()
    {
        return 1000000;
    }

    // size of a players marker
    private static final double RADIUS = 0.65;
    
    /**
     * place the players around the poker table
     * @param players
     * @param controller
     */
    public void initPlayers(List<PokerPlayer> players, PokerController controller) {
        double angle = 0.6 * Math.PI;
        double angleIncrement = 2.0 * Math.PI / (players.size());
        double rowRad = getNumRows() >> 1;
        double colRad = getNumCols() >> 1;

        for (final PokerPlayer p : players) {

            int row = (int) (0.93 * rowRad + (RADIUS * rowRad) * (Math.sin(angle)));
            int col = (int) (0.9 * colRad + (RADIUS * colRad) * (Math.cos(angle)));

            BoardPosition position = getPosition(row, col);
            position.setPiece(p.getPiece());
            p.getPiece().setLocation(position.getLocation());
            angle += angleIncrement;
        }
    }

    /**
     * given a move specification, execute it on the board
     * This applies the results for all the battles for one year (turn).
     *
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    protected boolean makeInternalMove( Move move )
    {
        //PokerTurn gmove = (PokerTurn)move;
        return true;
    }


    /**
     * For Poker, undoing a move means turning time back a round and
     * restoring the state of the game one full round earlier
     * @@ todo
     */
    protected void undoInternalMove( Move move )
    {
        GameContext.log(0,  "undo no implemented yet for poker." );
        //clear(positions_[move.getToRow()][move.getToCol()]);
    }

}
