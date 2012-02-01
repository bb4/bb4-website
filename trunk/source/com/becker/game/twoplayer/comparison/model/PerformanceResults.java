// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.model;

import com.becker.common.format.FormatUtil;

/**
 * Right now this just contains the name of the config and the
 * search options, but we may add the game weights too at some point.
 * 
 * @author Barry Becker
 */
public class PerformanceResults {
    
    /** true if player1 won */
    private boolean player1Won;
    
    /** true if the game ended up being a tie. */
    private boolean wasTie;
    
    /** the time in milliseconds that it took the game to run. */
    private long timeMillis;

    /** number of moves that were played. */
    private int numMoves;

    private double normalizedNumSeconds = 1.0;
    private double normalizedNumMoves = 1.0;
            
    /** How much the winning player won by */
    private double strengthOfWin;
    
    private long numP1NodesSearched;
    private long numP2NodesSearched;

    private boolean normalized;


    /** default constructor */
    public PerformanceResults() {
        this(false, false, 0, 0, 0);
    }

    /** Constructor */
    public PerformanceResults(boolean p1Won, boolean wasTie, double strengthOfWin,
                              int numMoves, long timeMillis) {
        this.player1Won = p1Won;
        this.wasTie = wasTie;
        this.strengthOfWin = strengthOfWin;
        this.numMoves = numMoves;
        this.timeMillis = timeMillis;
    }
    
    public Outcome getOutcome() {
        if (wasTie) {
            return Outcome.TIE;
        }
        else return player1Won ? Outcome.PLAYER1_WON : Outcome.PLAYER2_WON;
    }

    public boolean getPlayer1Won() {
        return this.player1Won;
    }

    public boolean getWasTie() {
        return wasTie;
    }

    public double getStrengthOfWin() {
        return strengthOfWin;
    }
    
    public double getNumSeconds() {
        return (double) timeMillis / 1000.0;
    }

    public int getNumMoves() {
        return numMoves;
    }
    
    public String getTimeFormatted() {
        double numSecs = getNumSeconds();
        int numMinutes = (int)(numSecs / 60);
        double seconds = numSecs - numMinutes * 60;
        String minFmt =  (numMinutes>0)? FormatUtil.formatNumber(numMinutes)  + " min " : "";
        return ( minFmt + seconds + " secs");
    }

    public double getNormalizedNumSeconds() {
        //assert normalized;
        return normalizedNumSeconds;
    }

    public double getNormalizedNumMoves() {
        //assert normalized;
        return normalizedNumMoves;
    }

    public void normalize(ResultMaxTotals maxTotals) {
        normalizedNumSeconds = getNumSeconds() / maxTotals.maxTotalTimeSeconds;
        normalizedNumMoves = (double)getNumMoves() / maxTotals.maxTotalMoves;
        normalized = true;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        if (getWasTie()) {
            bldr.append("it was a tie");
        }
        else  {
            bldr.append("player " + (getPlayer1Won()?"1":"2") + " won by ");
            bldr.append(getStrengthOfWin());
        }
        bldr.append(" in ").append(getTimeFormatted());
        bldr.append(" and " + getNumMoves() +" moves. ");
        bldr.append(" normalized time = "+normalizedNumSeconds);
        bldr.append(" normalized numMoves = "+normalizedNumMoves);
        return bldr.toString();
    }
}
