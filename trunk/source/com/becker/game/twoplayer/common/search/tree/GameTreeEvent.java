package com.becker.game.twoplayer.common.search.tree;


import com.becker.game.common.*;

import java.awt.*;

/**
 * This event gets fired whenever the structure of the game tree is
 * changed during search..
 *
 * @see com.becker.game.twoplayer.common.search.GameTreeChangedListener
 *
 * @author Barry Becker
 */
public final class GameTreeEvent extends AWTEvent
{
    private static final int GAME_CHANGED_EVENT = AWTEvent.RESERVED_ID_MAX + 4004;
    private final Move move_;
    private final GameControllerInterface controller_;
    private static final long serialVersionUID = 0L;

    /**
     * constructor
     * @param mv the most recently played move
     * @param controller
     */
    public GameTreeEvent(Move mv, GameControllerInterface controller, Object source )
    {
        super(source, GAME_CHANGED_EVENT );
        move_ = mv;
        controller_ = controller;
    }

    /**
     * @return the game controller for the viewer that sent the evernt.
     */
    public GameControllerInterface getController()
    {
        return controller_;
    }

    /**
     * @return the move that just caused the game changed event to fire.
     */
    public Move getMove()
    {
        return move_;
    }

}


