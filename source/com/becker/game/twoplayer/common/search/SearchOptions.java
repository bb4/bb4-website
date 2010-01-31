package com.becker.game.twoplayer.common.search;

import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Encapsulate two player options here to keep the TwoPlayerController class mush simpler.
 *
 * @author Barry Becker
 * Date: Aug 20, 2005
 */
public class SearchOptions
 {
    /** if true then use alpha beta pruning */
    private static final boolean ALPHA_BETA = true;

    /** if true then use quiescent search */
    private static final boolean QUIESCENCE = false;

    /** Number of moves to look ahead while searching for the best move. */
    private static final int DEFAULT_LOOK_AHEAD = 3;

    /** Percentage of best moves to consider at each search ply. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 60;

    /** No matter what the percentBestMoves is we should not prune if less than this number. */
    private static final int DEFAULT_MIN_BEST_MOVES = 10;

    /** never search more than this many additional plys during quiescent search. */
    private static final int DEFAULT_MAX_QUIESCENT_DEPTH = 8;

    private boolean alphaBeta_ = ALPHA_BETA;
    private boolean quiescence_ = QUIESCENCE;

    // the default search method.
    private SearchStrategyType strategyMethod_ = SearchStrategyType.MINIMAX;
    private int lookAhead_;
    private int bestPercentage_;
    private int minBestMoves_;
    private int maxQuiescentDepth_ = DEFAULT_MAX_QUIESCENT_DEPTH;


    /**
     * Default Constructor
     */
    public SearchOptions() {
        lookAhead_ = getDefaultLookAhead();
        bestPercentage_ = getDefaultPercentageBestMoves();
        minBestMoves_ = getDefaultMinBestMoves();
    }

    /**
     * Constructor
     * @param defaultLookAhead default number of moves to look ahead.
     * @param defaultBestPercentage default number of best moves to consider at each ply.
     * @param minBestMoves we will never consider fewer than this many moves when searching.
     */
    public SearchOptions(int defaultLookAhead, int defaultBestPercentage, int minBestMoves) {
        lookAhead_ = defaultLookAhead;
        bestPercentage_ = defaultBestPercentage;
        minBestMoves_ = minBestMoves;
    }

    /**
     * Constructor
     */
    public SearchOptions(int defaultLookAhead, int defaultBestPercentage, int minBestMoves, int maxQuiescentDepth) {
        this(defaultLookAhead, defaultBestPercentage, minBestMoves);
        maxQuiescentDepth_ = maxQuiescentDepth;
    }


    protected int getDefaultLookAhead() {
        return DEFAULT_LOOK_AHEAD;
    }
    protected int getDefaultPercentageBestMoves() {
        return DEFAULT_PERCENTAGE_BEST_MOVES;
    }
    protected int getDefaultMinBestMoves() {
        return DEFAULT_MIN_BEST_MOVES;
    }

    /**
     * @return the strategy method currently being used.
     */
    public SearchStrategyType getSearchStrategyMethod()
    {
        return strategyMethod_;
    }

    /**
     * @param method the desired search strategy for evaluating the game tree.
     * (eg MINIMAX, NEGAMAX, etc)
     */
    public final void setSearchStrategyMethod(SearchStrategyType method )
    {
        strategyMethod_ = method;
    }

    /**
     * @return the amount of lookahead (number of plys) used by the search strategy
     */
    public final int getLookAhead()
    {
        return lookAhead_;
    }

    /**
     * @param look the number of plys to look ahaead.
     */
    public final void setLookAhead( int look )
    {
        lookAhead_ = look;
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
     *
     * @return  never return fewer than this many best moves.
     */
    public int getMinBestMoves() {
        return minBestMoves_;
    }

    /**
     * @return true if alpha-beta pruning is being employed by the search strategy.
     */
    public final boolean getAlphaBeta()
    {
        return alphaBeta_;
    }

    /**
     * @param ab set whether of not to use alpha-beta pruning
     */
    public final void setAlphaBeta( boolean ab )
    {
        alphaBeta_ = ab;
    }

    /**
     * @return whether or not the quiescent search option is being used by the search strategy
     */
    public final boolean getQuiescence()
    {
        return quiescence_;
    }

    public final void setQuiescence( boolean quiescence )
    {
        quiescence_ = quiescence;
    }
  
    public int getMaxQuiescentDepth() {
        return maxQuiescentDepth_;
    }

    /**
     * @param searchable  something that can be searched.
     * @return the search strategy to use given a searchable object.
     */
    public SearchStrategy getSearchStrategy(Searchable searchable, ParameterArray weights) {

        return getSearchStrategyMethod().createStrategy(searchable, weights);
    }
}