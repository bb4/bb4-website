/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.pingpong;

/**
 *  Ping pong player
 */
public class Player implements Runnable {

    /** Table where they play */
    PingPongTable table;
    String opponent;


    public Player(String opponent, PingPongTable table) {
        this.table = table;
        this.opponent = opponent;
    }


    public void run() {
        while (table.hit(opponent)) {};
   }
}
