// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.model.data;

import com.becker.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.twoplayer.common.ui.dialogs.searchoptions.MonteCarloOptionsPanel;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfig;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfigList;
import groovyjarjarantlr.Lookahead;

import java.util.ArrayList;

/**
 * A default list of search config options so we do not have to enter them every time.
 * 
 * @author Barry Becker
 */
public class DefaultSearchConfigurations extends SearchOptionsConfigList {

    private static final int DEFAULT_LOOK_AHEAD = 3;
    private static final int DEFAULT_QUIESCENT_LOOK_AHEAD = 5;

    public DefaultSearchConfigurations()  {
        initialize();
    }

    protected void initialize() {
        add(new SearchOptionsConfig("Minimax", new SearchOptions(SearchStrategyType.MINIMAX)));
        add(new SearchOptionsConfig("Negamax", new SearchOptions(SearchStrategyType.NEGAMAX)));
        add(new SearchOptionsConfig("Negascout", new SearchOptions(SearchStrategyType.NEGASCOUT)));
        add(new SearchOptionsConfig("Negamax w/mem", new SearchOptions(SearchStrategyType.NEGAMAX_W_MEMORY)));
        add(new SearchOptionsConfig("Negascout w/mem", new SearchOptions(SearchStrategyType.NEGASCOUT_W_MEMORY)));
        add(new SearchOptionsConfig("UCT", new SearchOptions(SearchStrategyType.UCT)));
    }
    
    private BruteSearchOptions createBruteOptions() {
        return new BruteSearchOptions(DEFAULT_LOOK_AHEAD, DEFAULT_QUIESCENT_LOOK_AHEAD);
    }

    private BestMovesSearchOptions createBestMoveOptions() {
        return new BestMovesSearchOptions(100, 40, 20);
    }

    private MonteCarloSearchOptions createMonteCarloOptions() {
        return new MonteCarloSearchOptions(200, 0.9, 10);
    }
    
}
