package com.becker.game.multiplayer.trivial;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.multiplayer.common.online.SurrogatePlayer;
import com.becker.game.multiplayer.trivial.player.*;
import java.util.List;

/**
 * Representation of a Trivial Game Board
 *
 * @author Barry Becker
 */
public class TrivialTable extends Board
{

    /** 
     * constructor
     *  @param numRows num rows
     *  @param numCols num cols
     */
    public TrivialTable( int numRows, int numCols )
    {
        setSize( numRows, numCols );
    }


    /**
     *  reset the board to its initial state
     */
    @Override
    public void reset()
    {
        super.reset();
        for ( int i = 0; i <= getNumRows(); i++ ) {
            for ( int j = 0; j <= getNumCols(); j++ ) {
                positions_[i][j] = new BoardPosition( i, j, null);
            }
        }
    }


    @Override
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
     * A trivial game has no real limit so we just reutnr a huge number.
     * @return max number of trivial rounds allowed.
     */
    public int getMaxNumMoves()
    {
        return 1000000;
    }

    // size of a players marker
    private static final double RADIUS = 0.65;
    
    /**
     * place the players around the trival table
     * @param players
     * @param controller
     */
    public void initPlayers(List<? extends Player> players, TrivialController controller) {
        double angle = 0.6 * Math.PI;
        double angleIncrement = 2.0 * Math.PI / (players.size());
        double rowRad = getNumRows() >> 1;
        double colRad = getNumCols() >> 1;
        reset();
        
        for (Player p : players) {
            TrivialPlayer tp = null;
            if (p.isSurrogate()) {
                tp = (TrivialPlayer) ((SurrogatePlayer) p).getPlayer();
            }
            else {
                tp = (TrivialPlayer)p;
            }
            int row = (int) (0.93 * rowRad + (RADIUS * rowRad) * (Math.sin(angle)));
            int col = (int) (0.9 * colRad + (RADIUS * colRad) * (Math.cos(angle)));

            BoardPosition position = getPosition(row, col);
            position.setPiece(tp.getPiece());
            tp.getPiece().setLocation(position.getLocation());
            angle += angleIncrement;
        }
    }

    /**
     * given a move specification, execute it on the board
     *
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    protected boolean makeInternalMove( Move move )
    {
        return true;
    }


    /**
     * For Trivial, undoing a move means turning time back a round and
     * restoring the state of the game one full round earlier
     * @@ todo
     */
    protected void undoInternalMove( Move move )
    {
        GameContext.log(0,  "undo not implemented yet for Trivial." );
    }

}
