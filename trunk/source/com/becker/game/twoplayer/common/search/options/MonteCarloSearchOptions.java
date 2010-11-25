package com.becker.game.twoplayer.common.search.options;

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

    private int maxSimulations_ = DEFAULT_MAX_SIMULATIONS;

    private double exploreExploitRatio_ = DEFAULT_EXPLORE_EXPLOIT_RATIO;

    /**
     * Default Constructor
     */
    public MonteCarloSearchOptions() {}

    /**
     * Constructor
     * @param maxSimulations default number simulations to run.
     */
    public MonteCarloSearchOptions(int maxSimulations, double exploreExploitRatio) {
        maxSimulations_ = maxSimulations;
        exploreExploitRatio_ = exploreExploitRatio;
    }



    /**
     * @return the max number of simulations to make while searching.
     */
    public int getMaxSimulations() {
        return maxSimulations_;
    }

    /**
     * @param maxSim the new max number of simulations.
     */
    public void setMaxSimulations( int maxSim) {
        maxSimulations_ = maxSim;
    }



    /**
     * The larger this is (bigger than 1) the closer to uniform search we get (i.e exploration).
     * The smaller it is (less than 1) the more selective the search becomes (i.e. we exploit the known good moves).
     * There needs to be a balance.
     * @return the ratio of exploraration to exploitation.
     */
    public double getExploreExploitRatio() {
         return exploreExploitRatio_;
    }

    /**
     * @param ratio the ratio of exploraration to exploitation.
     */
    public void setExploreExploitRatio(double ratio) {
         exploreExploitRatio_ = ratio;
    }
}