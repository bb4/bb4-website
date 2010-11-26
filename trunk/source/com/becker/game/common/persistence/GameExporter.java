package com.becker.game.common.persistence;

import com.becker.common.Location;
import com.becker.game.common.GameController;
import com.becker.game.common.Move;



/**
 * Export the state of a game to a file.
 *
 * @author Barry Becker Date: Oct 28, 2006
 */
public abstract class GameExporter {

    protected GameController controller_;


    protected GameExporter(GameController controller) {
        controller_ = controller;
    }

    /**
     * save the current state of the game to a file.
     * You must override if you want it to work.
     */
    public abstract void saveToFile( String fileName, AssertionError ae);

    /**
     * Convert a moew to SGF format
     * @param move the move to format
     * @return the sgf (smart game format) representation for the move.
     */
    protected abstract String getSgfForMove(Move move);
    
    
    /**
     * append the board position to the buffer in the form [<c><r>]
     * Where c and r are the column and row letters whose range depends on the game.
     */
    protected void serializePosition(Location pos, StringBuilder buf) {
        buf.append( '[' );
        buf.append( (char) ('a' + pos.getCol() - 1) );
        buf.append( (char) ('a' + pos.getRow() - 1) );
        buf.append( ']' );
    }
}
