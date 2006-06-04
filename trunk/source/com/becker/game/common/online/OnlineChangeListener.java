package com.becker.game.common.online;

/**
 * @author Barry Becker Date: May 21, 2006
 */
public interface OnlineChangeListener {

    void handleServerUpdate(GameCommand cmd);
}
