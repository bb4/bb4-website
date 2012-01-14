// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.model;

import com.becker.common.format.FormatUtil;
import com.becker.game.twoplayer.common.search.options.SearchOptions;

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
            
    /** How much the winning player won by */
    private double strengthOfWin;
    
    private long numP1NodesSearched;
    private long numP2NodesSearched;
    
    
    public PerformanceResults(boolean p1Won, boolean wasTie, double strengthOfWin) {
        this.player1Won = p1Won;
        this.wasTie = wasTie;
        this.strengthOfWin = strengthOfWin;
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
    
    public String getTimeFormatted() {
        double numSecs = getNumSeconds();
        int numMinutes = (int)(numSecs / 60);
        double seconds = numSecs - numMinutes * 60;
        String minFmt =  (numMinutes>0)? FormatUtil.formatNumber(numMinutes)  + " min " : "";
        return ( minFmt + seconds + " secs");
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
        return bldr.toString();
    }
        
}
