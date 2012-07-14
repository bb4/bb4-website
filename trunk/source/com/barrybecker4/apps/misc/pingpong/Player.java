/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.pingpong;

/**
 *
 */
public class Player implements Runnable {

      PingPongTable myTable;   // Table where they play
      String myOpponent;


      public Player(String opponent, PingPongTable table) {
          myTable  = table;
          myOpponent = opponent;
      }


      public void run() {
          while (myTable.hit(myOpponent));
     }
}
