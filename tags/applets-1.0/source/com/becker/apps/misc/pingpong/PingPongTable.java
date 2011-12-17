/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.apps.misc.pingpong;

/**
 * see
 * http://www.javaworld.com/jw-04-1996/jw-04-synch.html?page=1 
 */
public class PingPongTable {
    
    // state variable identifying whose turn it is.
    private String whoseTurn = null;
    
    private static final String DONE = "DONE";
    private static final int TIMEOUT_DURATION = 1500;

    /**
     * One player hits the ball to his opponent.
     * @param opponent opposit e player
     * @return true if we should keep playing, else terminate.
     */
    public synchronized boolean hit(String opponent) {
          
        String currentPlayer = Thread.currentThread().getName();
    
        // Initialize with whichever thread gets here first
        if (whoseTurn == null) {
            whoseTurn = currentPlayer;
            return true;
        }
        
        if (whoseTurn.compareTo(DONE) == 0)
            return false; 
        if (opponent.compareTo(DONE) == 0) {
            whoseTurn = DONE;
            notifyAll();
            return false;
        }
    
        if (currentPlayer.equals(whoseTurn)) {
            System.out.println("PING!  "+currentPlayer+" hit it to " + opponent);
            whoseTurn = opponent;
            // random pause before moving on
            Game.pause((int)(500 * Math.random()));
            notifyAll();
       } else {
            try {
                long t1 = System.currentTimeMillis();
                wait(TIMEOUT_DURATION);
                if ((System.currentTimeMillis() - t1) > TIMEOUT_DURATION) {
                    System.out.println("****** TIMEOUT! "+currentPlayer+" is waiting for "+whoseTurn+" to play.");
                }
            } catch (InterruptedException e) { }
       }
       return true;
    }
        
    /**
     * stop playing ping pong
     */
    public void stop() {
        // cause the players to quit their threads.
        hit("DONE"); 
        Game.pause(100);
    }
}
