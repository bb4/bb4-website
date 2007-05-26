package com.becker.game.common;



/**
 * Export the state of a game to a file.
 *
 * @author Barry Becker Date: Oct 28, 2006
 */
public abstract class GameExporter {

    protected GameController controller_;


    public GameExporter(GameController controller) {
        controller_ = controller;
    }

    /**
     * save the current state of the game to a file.
     * You must override if you want it to work.
     */
    public abstract void saveToFile( String fileName, AssertionError ae);

    /**
     *
     * @param move
     * @return the sgf (smart game format) representation for the move.
     */
    protected abstract String getSgfForMove(Move move);
    
    
    /**
     * append the board position to the buffer in the form [<c><r>]
     * Where c and r are the column and row letters whose range depends on the game.
     */
    protected void serializePosition(BoardPosition pos, StringBuffer buf) {
        buf.append( '[' );
        buf.append( (char) ('a' + pos.getCol() - 1) );
        buf.append( (char) ('a' + pos.getRow() - 1) );
        buf.append( ']' );
    }
}
