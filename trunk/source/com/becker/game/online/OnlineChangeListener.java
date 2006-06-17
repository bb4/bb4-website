package com.becker.game.online;



/**
 * Implemented by classes that need to be updated when something changes on the online game server.
 * For example, OnlineGameDialog's implement this to update their table lists when they change on the server.
 *
 * @author Barry Becker Date: May 21, 2006
 */
public interface OnlineChangeListener {

    void handleServerUpdate(GameCommand cmd);
}
