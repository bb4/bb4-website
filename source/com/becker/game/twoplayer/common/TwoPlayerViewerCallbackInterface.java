package com.becker.game.twoplayer.common;

import com.becker.game.common.Move;
import com.becker.game.common.ViewerCallbackInterface;

/**
 * The TwoPlayerController communicates with the viewer via this interface.
 * Alernatively we could use RMI or events, but for now the minimal interface is
 * defined here and called directly by the controller.
 *
 * @author Barry Becker
 */
public interface TwoPlayerViewerCallbackInterface  extends ViewerCallbackInterface
{

    /**
     * Called when the controller has found the next computer move and needs to make the viewer aware of it.
     * @param m the computers move
     */
    public void computerMoved(Move m);

    /**
     * Used when the computer is playing against itself, and you want the game to show up in the viewer.
     */
    public void showComputerVsComputerGame();


    /**
     * Currently this does not actually step forward just one search step, but instead
     * stops after PROGRESS_STEP_DELAY more milliseconds of seaching.
     */
    public void step();

    /**
     * resume searching for the next move at full speed.
     */
    public void continueProcessing();

}
