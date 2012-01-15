// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.execution;

import com.becker.game.twoplayer.comparison.model.PerformanceResults;
import com.becker.game.twoplayer.comparison.model.ResultsModel;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfig;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfigList;

/**
 * Run through the grid of game combinations and gather the performance results
 * @author Barry Becker
 */
public class PerformanceRunner {
        
    SearchOptionsConfigList optionsList;
    
    public PerformanceRunner(SearchOptionsConfigList optionsList)  {
         this.optionsList = optionsList;
    }

    /**
     * Run the NxN comparison and return the results.
     * @return model with all the results
     */
    public ResultsModel doComparisonRuns() {

        int size = optionsList.size();
        ResultsModel model = new ResultsModel(size);

        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                                
                PerformanceResults results = 
                        getResultsForComparison(i, j);
                model.setResults(i, j, results);
            }
        }
        return model;
    }
    
    private PerformanceResults getResultsForComparison(int i, int j) {

        SearchOptionsConfig config1 = optionsList.get(i);
        SearchOptionsConfig config2 = optionsList.get(j);

        // run with each as player1

        return new PerformanceResults(true, false, 10);
    }

}
