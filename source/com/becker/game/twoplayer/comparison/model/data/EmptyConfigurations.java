// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.model.data;

import com.becker.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfig;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfigList;

/**
 * User must add there own configurations manually.
 * 
 * @author Barry Becker
 */
public class EmptyConfigurations extends SearchOptionsConfigList {


    public EmptyConfigurations()  {
        initialize();
    }

    protected void initialize() {

    }


}
