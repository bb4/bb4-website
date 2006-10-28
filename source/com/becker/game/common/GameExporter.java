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
     * @return the sgf representation for the move.
     */
    protected abstract String getSgfForMove(Move move);
}
