// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.model;

/**
 * @author Barry Becker
 */
public class ResultsModel {
    
    private int size;
    
    /** matrix of performance results based on the grid of options to compare. */
    PerformanceResults[][] resultsGrid;
    
    
    public ResultsModel(int size) {
        this.size = size;
        resultsGrid = new PerformanceResults[size][size];
    }
    
    public void setResults(int i, int j, PerformanceResults results) {
        resultsGrid[i][j] = results;
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
