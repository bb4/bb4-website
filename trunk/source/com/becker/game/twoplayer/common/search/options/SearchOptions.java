package com.becker.game.twoplayer.common.search.options;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Encapsulate two player search options here to keep the TwoPlayerController class much simpler.
 * While some options are used for all strategies,
 * some Search strategies with different SearchAttributes use different sets of options.
 * 
 * @author Barry Becker
 */
public class SearchOptions {

    /** the default search method. */
    private SearchStrategyType strategyMethod_ = SearchStrategyType.MINIMAX;

    private BestMovesSearchOptions bestMovesOptions_;
    private BruteSearchOptions bruteOptions_;
    private MonteCarloSearchOptions monteCarloOptions_;


    /**
     * Default Constructor
     */
    public SearchOptions() {

        bestMovesOptions_ = new BestMovesSearchOptions();
        bruteOptions_ = new BruteSearchOptions();
        monteCarloOptions_ = new MonteCarloSearchOptions();
    }

    /**
     * Constructor
     * @param bruteOptions brute forst search oiptions to use.
     * @param bestMovesOptions for finding best moves out of reasonable set of next moves
     */
    public SearchOptions(BruteSearchOptions bruteOptions, BestMovesSearchOptions bestMovesOptions) {
        bruteOptions_ = bruteOptions;
        bestMovesOptions_ = bestMovesOptions;
        monteCarloOptions_ = new MonteCarloSearchOptions();
    }

    /**
     * @return the strategy method currently being used.
     */
    public SearchStrategyType getSearchStrategyMethod() {
        return strategyMethod_;
    }

    /**
     * @param method the desired search strategy for evaluating the game tree.
     * (eg MINIMAX, NEGAMAX, etc)
     */
    public final void setSearchStrategyMethod(SearchStrategyType method ) {
        strategyMethod_ = method;
    }

    public BruteSearchOptions getBruteSearchOptions() {
        return bruteOptions_;
    }

    public BestMovesSearchOptions getBestMovesSearchOptions() {
        return bestMovesOptions_;
    }

    public MonteCarloSearchOptions getMonteCarloSearchOptions() {
        return monteCarloOptions_;
    }

    /**
     * @param searchable  something that can be searched.
     * @return the search strategy to use given a searchable object.
     */
    public SearchStrategy getSearchStrategy(Searchable searchable, ParameterArray weights) {

        return getSearchStrategyMethod().createStrategy(searchable, weights);
    }
}