// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.model;

/**
 * Right now this just contains the name of the config and the
 * search options, but we may add the game weights too at some point.
 * 
 * @author Barry Becker
 */
public class PerformanceResultsPair {
    
    /** true if player1 won */
    private PerformanceResults p1FirstResults;
    
    private PerformanceResults p2FirstResults;

    /** default constructor */
    public PerformanceResultsPair() {
        this.p1FirstResults = new PerformanceResults();
        this.p2FirstResults = new PerformanceResults();
        this.normalize(new ResultMaxTotals(1.0, 1));
    }
    
    /** constructor */
    public PerformanceResultsPair(PerformanceResults p1FirstResults, PerformanceResults p2FirstResults) {
        this.p1FirstResults = p1FirstResults;
        this.p2FirstResults = p2FirstResults;
    }
    
    public Outcome[] getOutcomes() {
        return new Outcome[] {p1FirstResults.getOutcome(), p2FirstResults.getOutcome()};
    }

    public double getTotalNumSeconds() {
        return p1FirstResults.getNumSeconds() + p2FirstResults.getNumSeconds();
    }
    
    public int getTotalNumMoves() {
        return p1FirstResults.getNumMoves() + p2FirstResults.getNumMoves();
    }

    public void normalize(ResultMaxTotals maxTotals) {
        p1FirstResults.normalize(maxTotals);
        p2FirstResults.normalize(maxTotals);
    }
    
    public double[] getNormalizedTimes() {
        return new double[] {
            p1FirstResults.getNormalizedNumSeconds(), p2FirstResults.getNormalizedNumSeconds()
        };
    }

    public double[] getNormalizedNumMoves() {
        return new double[] {
            p1FirstResults.getNormalizedNumMoves(), p2FirstResults.getNormalizedNumMoves()
        };
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder("Results pair");

        bldr.append(" Player1 first: ").append(p1FirstResults.toString()).append("\n");
        bldr.append(" Player2 first: ").append(p2FirstResults.toString()).append("\n");

        return bldr.toString();
    }
}
