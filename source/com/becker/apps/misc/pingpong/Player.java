package com.becker.apps.misc.pingpong;

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
