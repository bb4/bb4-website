package com.becker.game.online;

/**
 * @author Barry Becker Date: May 14, 2006
 */
public interface OnlineGameServerInterface {


    /**
     *
     * @param cmdLine command and its arguments in a form that can be parsed.
     * @param response the response from the server to be interpreted by the client.
     * @return true if successfully handled
     */
    boolean handleCommand(String cmdLine, StringBuffer response);

    /**
     *
     * @return  the port on which to connect to the server on
     */
    //int getPort();
}
