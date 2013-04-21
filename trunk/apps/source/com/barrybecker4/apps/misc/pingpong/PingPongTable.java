/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.pingpong;

import com.barrybecker4.common.concurrency.ThreadUtil;

/**
 * Table at which the ping pong players play.
 * see http://www.javaworld.com/jw-04-1996/jw-04-synch.html?page=1
 */
public class PingPongTable {

    /** state variable identifying whose turn it is.*/
    private String playerToPlay = null;

    private static final String DONE = "DONE";
    private static final int TIMEOUT_DURATION = 1500;

    /**
     * One player hits the ball to his opponent.
     * @param opponent opposite player that we are hitting the ball to.
     * @return true if we should keep playing, else terminate.
     */
    public synchronized boolean hit(String opponent) {

        String currentPlayer = Thread.currentThread().getName();

        // Initialize with whichever thread gets here first
        if (playerToPlay == null) {
            playerToPlay = currentPlayer;
            return true;
        }

        if (playerToPlay.compareTo(DONE) == 0)  {
            return false;
        }
        if (opponent.compareTo(DONE) == 0) {
            playerToPlay = DONE;
            notifyAll();
            return false;
        }

        if (currentPlayer.equals(playerToPlay)) {
            System.out.println("PING!  " + currentPlayer + " hit it to " + opponent);
            playerToPlay = opponent;
            // random pause before moving on
            ThreadUtil.sleep((int) (500 * Math.random()));
            notifyAll();
        }
        else {
            try {
                long t1 = System.currentTimeMillis();
                wait(TIMEOUT_DURATION);
                if ((System.currentTimeMillis() - t1) > TIMEOUT_DURATION) {
                    System.out.println("*** TIMEOUT! " + currentPlayer + " is waiting for " + playerToPlay + " to play.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
       }
       return true;
    }

    /**
     * stop playing ping pong. Causes all players to quit their threads.
     */
    public void stop() {
        hit(DONE);
    }
}
