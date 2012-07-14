/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.IGameController;
import com.barrybecker4.game.common.Move;

import java.awt.*;

/**
 * This event gets fired whenever the Game state changes.
 *
 * @see GameChangedListener
 *
 * @author Barry Becker
 */
public final class GameChangedEvent extends AWTEvent {

    private static final int GAME_CHANGED_EVENT = AWTEvent.RESERVED_ID_MAX + 4003;
    private final Move move_;
    private final IGameController controller_;
    private static final long serialVersionUID = 0L;

    /**
     * constructor
     * @param mv the most recently played move
     * @param controller
     */
    public GameChangedEvent(Move mv, IGameController controller, Object source ) {
        super(source, GAME_CHANGED_EVENT );
        move_ = mv;
        controller_ = controller;
    }

    /**
     * @return the game controller for the viewer that sent the event.
     */
    public IGameController getController() {
        return controller_;
    }

    /**
     * @return the move that just caused the game changed event to fire.
     */
    public Move getMove() {
        return move_;
    }

}


