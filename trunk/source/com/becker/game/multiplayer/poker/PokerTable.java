package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.becker.game.multiplayer.poker.player.PokerPlayer;

/**
 * Representation of a Poker Game Board
 *
 * @author Barry Becker
 */
public class PokerTable extends Board {

    /** size of a players marker  */
    private static final double RADIUS = 0.65;


    /**
     * Constructor
     * @param numRows num rows
     * @param numCols num cols
     */
    public PokerTable( int numRows, int numCols )  {
        setSize( numRows, numCols );
    }

    public PokerTable(PokerTable table) {
    }

    public PokerTable copy() {
        return new PokerTable(this);
    }

    /**
     *  reset the board to its initial state
     */
    @Override
    public void reset() {
        super.reset();
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new BoardPosition( i, j, null);
            }
        }
    }


    @Override
    public void setSize( int numRows, int numCols ) {
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
    public int getMaxNumMoves() {
        return 1000000;
    }


    /**
     * place the players around the poker table
     * @param players
     */
    public void initPlayers(PlayerList players) {
        double angle = 0.6 * Math.PI;
        double angleIncrement = 2.0 * Math.PI / (players.size());
        double rowRad = getNumRows() >> 1;
        double colRad = getNumCols() >> 1;
        reset();
        for (final Player p : players) {

            PokerPlayer pp = null;
            if (p.isSurrogate()) {
                pp = (PokerPlayer) ((SurrogateMultiPlayer) p).getPlayer();
            }
            else {
                pp = (PokerPlayer)p;
            }
            int row = (int) (0.93 * rowRad + (RADIUS * rowRad) * (Math.sin(angle)));
            int col = (int) (0.9 * colRad + (RADIUS * colRad) * (Math.cos(angle)));

            BoardPosition position = getPosition(row, col);
            position.setPiece(pp.getPiece());
            pp.getPiece().setLocation(position.getLocation());
            angle += angleIncrement;
        }
    }

    /**
     * given a move specification, execute it on the board
     *
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    @Override
    protected boolean makeInternalMove( Move move ) {
        //PokerTurn gmove = (PokerTurn)move;
        return true;
    }


    /**
     * For Poker, undoing a move means turning time back a round and
     * restoring the state of the game one full round earlier
     * @@ todo
     */
    @Override
    protected void undoInternalMove( Move move ) {
        GameContext.log(0,  "undo no implemented yet for poker." );
        //clear(positions_[move.getToRow()][move.getToCol()]);
    }

}
