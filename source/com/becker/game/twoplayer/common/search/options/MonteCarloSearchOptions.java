package com.becker.game.twoplayer.common.search.options;

import com.becker.game.twoplayer.common.search.SearchWindow;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;

/**
 * The options for search strategies that use brute-force minimax search like MiniMax, NegaMax, NegaScout,
 * and alsot the memory and aspiration variations of these strategies.
 * These methods usually use a search window to do pruning of tree branches.
 *
 * @author Barry Becker
 */
public class MonteCarloSearchOptions {

    /** Number of moves to look ahead while searching for the best move. */
    private static final int DEFAULT_MAX_SIMULATIONS = 1000;

    private static final double DEFAULT_EXPLORE_EXPLOIT_RATIO = 1.0;

    private int maxSimulations_;


    /**
     * Default Constructor
     */
    public MonteCarloSearchOptions() {
        maxSimulations_ = getDefaultMaxSimulations();
    }

    /**
     * Constructor
     * @param maxSimulations default number simulations to run.
     */
    public MonteCarloSearchOptions(int maxSimulations) {
        maxSimulations_ = maxSimulations;
    }


    int getDefaultMaxSimulations() {
        return DEFAULT_MAX_SIMULATIONS;
    }

    /**
     * @return the max number of simulations to make while searching.
     */
    public final int getMaxSimulations() {
        return maxSimulations_;
    }

    /**
     * @param maxSim the new max number of simulations.
     */
    public final void setMaxSimulations( int maxSim) {
        maxSimulations_ = maxSim;
    }

    /**
     * The larger this is (bigger than 1) the closer to uniform search we get (i.e exploration).
     * The smaller it is (less than 1) the more selective the search becomes (i.e. we exploit the known good moves).
     * There needs to be a balance.
     * @return the ratio of exploraration to exploitation.
     */
    public final double getExploreExploitRatio() {
         return DEFAULT_EXPLORE_EXPLOIT_RATIO;
    }
}