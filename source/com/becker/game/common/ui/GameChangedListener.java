package com.becker.game.common.ui;

import java.util.EventListener;

/**
 * This interface must be implemented by any class that wants to receive GameChangedEvents
 *
 * @see com.becker.game.common.ui.GameChangedListener
 *
 * @author Barry Becker
 */
public interface GameChangedListener extends EventListener
{
    public void gameChanged( GameChangedEvent evt );
}
