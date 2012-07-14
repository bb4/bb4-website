/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.apps.misc.pingpong;

import com.becker.common.concurrency.ThreadUtil;

/**
 * see
 * http://www.javaworld.com/jw-04-1996/jw-04-synch.html?page=1
 */
public class Game {

    // the players
    private static final String[] PLAYERS= {"Alice", "Barry", "Bob", "Brian", "Joe", "Duy", "Shanna"};

    private Game() {}

    public static void main(String args[]) {
        PingPongTable table = new PingPongTable();
        int numPlayers = PLAYERS.length;

        for (int i=0; i < numPlayers; i++) {
            int nextPlayer = (i + 1) % numPlayers;
            Thread t = new Thread(new Player(PLAYERS[nextPlayer], table), PLAYERS[i]);
            t.start();
        }

        ThreadUtil.sleep(8000);
        table.stop();
    }

}

