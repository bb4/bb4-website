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
    private static final int DEFAULT_MAX_SIMULATIONS = 2000;

    /** Ratio of exploration to exploitation of good moves. */
    private static final double DEFAULT_EXPLORE_EXPLOIT_RATIO = 1.0;

    /** Default numbe rof random moves to look-ahead when playing a random game. */
    private static final int DEFAULT_RANDOM_LOOKAHEAD = 20;

    private int maxSimulations_ = DEFAULT_MAX_SIMULATIONS;

    private double exploreExploitRatio_ = DEFAULT_EXPLORE_EXPLOIT_RATIO;

    private int randomLookAhead_ = DEFAULT_RANDOM_LOOKAHEAD;

    /**
     * Default Constructor
     */
    public MonteCarloSearchOptions() {}

    /**
     * Constructor
     * @param maxSimulations default number simulations to run.
     * @param exploreExploitRatio
     * @param randomLookAhead amount to look ahead during random games.
     */
    public MonteCarloSearchOptions(int maxSimulations, double exploreExploitRatio, int randomLookAhead) {
        maxSimulations_ = maxSimulations;
        exploreExploitRatio_ = exploreExploitRatio;
        randomLookAhead_ = randomLookAhead;
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

    /**
     * @return number of moves to look ahead randomly when playing a random game.
     */
    public int getRandomLookAhead() {
        return randomLookAhead_;
    }

    /**
     * @param randomLookAhead
     */
    public void setRandomLookAhead(int randomLookAhead) {
        randomLookAhead_ = randomLookAhead;
    }

}