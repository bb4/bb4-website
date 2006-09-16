package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.multiplayer.poker.player.*;

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


    // must call reset() after changing the size
    public void setSize( int numRows, int numCols )
    {
        numRows_ = numRows;
        numCols_ = numCols;
        rowsTimesCols_ = numRows_ * numCols_;
        // we don't use the 0 edges of the board
        positions_ = new BoardPosition[numRows_ + 1][numCols_ + 1];
        reset();
    }

    public int getMaxNumMoves()
    {
        return rowsTimesCols_;
    }

    private static final double RADIUS = .65;
    /**
     * place the players around the poker table
     * @param players
     * @param controller
     */
    public void initPlayers(PokerPlayer[] players, PokerController controller) {
        double angle = 0.6 * Math.PI;
        double angleIncrement = 2.0 * Math.PI / (players.length);
        double rowRad = getNumRows()/2.0;
        double colRad = getNumCols()/2.0;

        for (int i=0; i<players.length; i++) {

            int row =  (int)(.93*rowRad + (RADIUS*rowRad) * (Math.sin(angle)));
            int col =  (int)(.9*colRad + (RADIUS*colRad) * (Math.cos(angle)));

            BoardPosition position = getPosition(row, col);
            position.setPiece(players[i].getPiece());
            players[i].getPiece().setLocation(position.getLocation());
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


    public void higlightPlanet() {

    }

    /**
     * For Poker, undoing a move means turning time back a year and
     * restoring the state of the game one full turn earlier
     * @@ todo
     */
    protected void undoInternalMove( Move move )
    {
        GameContext.log(0,  "undo no implemented yet." );
        //clear(positions_[move.getToRow()][move.getToCol()]);
    }


}
