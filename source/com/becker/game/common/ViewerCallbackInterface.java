package com.becker.game.common;

/**
 * The GameController communicates with the viewer via this interface.
 * Alernatively we could use RMI or events, but for now the minimal interface is
 * defined here and called directly by the controller.
 *
 * @author Barry Becker
 */
public interface ViewerCallbackInterface
{

    /**
     * return the game to its original state.
     */
   void reset();
}
