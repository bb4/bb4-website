package com.becker.game.multiplayer.trivial;

import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.board.Board;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.becker.game.multiplayer.trivial.player.TrivialPlayer;

/**
 * Representation of a Trivial Game Board
 *
 * @author Barry Becker
 */
public class TrivialTable extends Board {

    /** 
     * constructor
     *  @param numRows num rows
     *  @param numCols num cols
     */
    public TrivialTable( int numRows, int numCols )  {
        setSize( numRows, numCols );
    }

    /** Copy constructor */
    public TrivialTable(TrivialTable table) {
        super(table);
    }

    public TrivialTable copy() {
        return new TrivialTable(this);
    }

    /**
     * A trivial game has no real limit so we just reutnr a huge number.
     * @return max number of trivial rounds allowed.
     */
    public int getMaxNumMoves() {
        return 1000000;
    }

    // size of a players marker
    private static final double RADIUS = 0.65;
    
    /**
     * place the players around the trival table
     * @param players
     * @param controller
     */
    public void initPlayers(PlayerList players, TrivialController controller) {
        double angle = 0.6 * Math.PI;
        double angleIncrement = 2.0 * Math.PI / (players.size());
        double rowRad = getNumRows() >> 1;
        double colRad = getNumCols() >> 1;
        reset();
        
        for (Player p : players) {
            TrivialPlayer tp = null;
            if (p.isSurrogate()) {
                tp = (TrivialPlayer) ((SurrogateMultiPlayer) p).getPlayer();
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
    @Override
    protected boolean makeInternalMove( Move move ) {
        return true;
    }


    /**
     * For Trivial, undoing a move means turning time back a round and
     * restoring the state of the game one full round earlier
     * @@ todo
     */
    @Override
    protected void undoInternalMove( Move move ) {
        GameContext.log(0,  "undo not implemented yet for Trivial." );
    }

}
