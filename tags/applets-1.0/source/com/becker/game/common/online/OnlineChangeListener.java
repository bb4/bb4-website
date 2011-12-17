/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.common.online;



/**
 * Implemented by classes that need to be updated when something changes on the online game server.
 * For example, OnlineGameManagerPanel's implement this to update their table lists when they change on the server.
 * The Game Viewer should implement also so it can update when state changes on the server.
 *
 * @author Barry Becker Date: May 21, 2006
 */
public interface OnlineChangeListener {

    void handleServerUpdate(GameCommand cmd);
}
