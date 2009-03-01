/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.apps.misc.pingpong;

/**
 * see
 * http://www.javaworld.com/jw-04-1996/jw-04-synch.html?page=1 
 */
public class Game {
    
    // the players
    private static final String[] PLAYERS= {"Alice", "Barry", "Bob", "Brian", "Joe", "Duy", "Shanna"};

    public static void main(String args[]) {
        PingPongTable table = new PingPongTable();
        int numPlayers = PLAYERS.length;
        
        for (int i=0; i < numPlayers; i++) {
            int nextPlayer = (i + 1) % numPlayers;
            Thread t = new Thread(new Player(PLAYERS[nextPlayer], table), PLAYERS[i]);
            t.start();
        }
          
        pause(8000);       
        table.stop();        
    }
    
    
    public static final void pause(int millis) {
        try {
            Thread.currentThread().sleep(millis);
        } catch (InterruptedException e) { }
    }
}

                     