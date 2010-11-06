package com.becker.game.common.ui;

import java.util.EventListener;

/**
 * This interface must be implemented by any class that wants to receive GameChangedEvents
 *
 * @author Barry Becker
 */
public interface GameChangedListener extends EventListener
{
    void gameChanged( GameChangedEvent evt );
}
