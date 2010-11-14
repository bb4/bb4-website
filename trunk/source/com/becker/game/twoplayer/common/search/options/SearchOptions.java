package com.becker.game.twoplayer.common.search.options;

import com.becker.game.twoplayer.common.search.SearchWindow;
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

    /** Percentage of best moves to consider at each search ply. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 60;

    /** No matter what the percentBestMoves is we should not prune if less than this number. */
    private static final int DEFAULT_MIN_BEST_MOVES = 10;


    // the default search method.
    private SearchStrategyType strategyMethod_ = SearchStrategyType.MINIMAX;

    private int bestPercentage_;
    private int minBestMoves_;

    private BruteSearchOptions bruteOptions_;
    private MonteCarloSearchOptions monteCarloOptions_;


    /**
     * Default Constructor
     */
    public SearchOptions() {
        bruteOptions_ = new BruteSearchOptions();
        monteCarloOptions_ = new MonteCarloSearchOptions();
        bestPercentage_ = getDefaultPercentageBestMoves();
        minBestMoves_ = getDefaultMinBestMoves();
    }

    /**
     * Constructor
     * @param bruteOptions brute forst search oiptions to use.
     * @param defaultBestPercentage default number of best moves to consider at each ply.
     * @param minBestMoves we will never consider fewer than this many moves when searching.
     */
    public SearchOptions(BruteSearchOptions bruteOptions, int defaultBestPercentage, int minBestMoves) {
        bruteOptions_ = bruteOptions;
        monteCarloOptions_ = new MonteCarloSearchOptions();
        bestPercentage_ = defaultBestPercentage;
        minBestMoves_ = minBestMoves;
    }

    int getDefaultPercentageBestMoves() {
        return DEFAULT_PERCENTAGE_BEST_MOVES;
    }
    
    int getDefaultMinBestMoves() {
        return DEFAULT_MIN_BEST_MOVES;
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

    public MonteCarloSearchOptions getMonteCarloSearchOptions() {
        return monteCarloOptions_;
    }
    
    /**
     * @return  the percentage of top moves considered at each ply
     */
    public final int getPercentageBestMoves()
    {
        return bestPercentage_;
    }

    /**
     * @param bestPercentage  the percentage of top moves considered at each ply
     */
    public final void setPercentageBestMoves( int bestPercentage )
    {
        bestPercentage_ = bestPercentage;
    }

    /**
     * @return  never return fewer than this many best moves.
     */
    public int getMinBestMoves() {
        return minBestMoves_;
    }

    /**
     * @param searchable  something that can be searched.
     * @return the search strategy to use given a searchable object.
     */
    public SearchStrategy getSearchStrategy(Searchable searchable, ParameterArray weights) {

        return getSearchStrategyMethod().createStrategy(searchable, weights);
    }
}