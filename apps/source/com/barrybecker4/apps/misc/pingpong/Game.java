/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.pingpong;

import com.barrybecker4.common.concurrency.ThreadUtil;

/**
 * Ping pong game that uses multiple threads.
 * Its a good example of using wait and notify.
 * see http://www.javaworld.com/jw-04-1996/jw-04-synch.html?page=1
 */
public class Game {

    private PingPongTable table;


    private Game() {
        table = new PingPongTable();
    }

    private void startGame(String[] players, int duration) {
        int numPlayers = players.length;

        for (int i=0; i < numPlayers; i++) {
            int nextPlayer = (i + 1) % numPlayers;
            Thread t = new Thread(new Player(players[nextPlayer], table), players[i]);
            t.start();
        }

        ThreadUtil.sleep(duration);
        table.stop();
    }


    public static void main(String args[]) {

        final String[] PLAYERS= {"Alice", "Barry", "Bob", "Brian", "Joe", "Duy", "Shanna"};

        new Game().startGame(PLAYERS, 8000);
    }

}

