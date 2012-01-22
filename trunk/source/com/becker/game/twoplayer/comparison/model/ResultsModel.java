// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.model;

/**
 * @author Barry Becker
 */
public class ResultsModel {
    
    private int size;
    
    /** matrix of performance results based on the grid of options to compare. */
    PerformanceResultsPair[][] resultsGrid;
    
    
    public ResultsModel(int size) {
        this.size = size;
        resultsGrid = new PerformanceResultsPair[size][size];
    }
    
    public void setResults(int i, int j, PerformanceResultsPair results) {
        resultsGrid[i][j] = results;
    }

    public PerformanceResultsPair getResults(int i, int j) {
        return resultsGrid[i][j];
    }
    
    /** Once all the results have been recorded, we should go through and normalize them. */
    public void normalize()  {
        double maxTotalTime = findMaxTotalTimeSeconds();
        int maxTotalMoves = findMaxTotalMoves();
        ResultMaxTotals maxTotals = new ResultMaxTotals(maxTotalTime, maxTotalMoves);

        updateNormalizedValues(maxTotals);
    }

    private void updateNormalizedValues(ResultMaxTotals maxTotals) {
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                resultsGrid[i][j].normalize(maxTotals);
            }
        }
    }
      
    private double findMaxTotalTimeSeconds() {
        double maxTime = 0;
       
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                double time = resultsGrid[i][j].getTotalNumSeconds();
                if (time > maxTime)  {
                   maxTime = time;
                }
            }
        }
        return maxTime;
    }

    private int findMaxTotalMoves() {
        int maxNumMoves = 0;

        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                int numMoves = resultsGrid[i][j].getTotalNumMoves();
                if (numMoves > maxNumMoves)  {
                    maxNumMoves = numMoves;
                }
            }
        }
        return maxNumMoves;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                bldr.append(resultsGrid[i][j].toString());
                bldr.append("\n") ;
            }
            bldr.append("\n");
        }
        return bldr.toString();
    }
}
