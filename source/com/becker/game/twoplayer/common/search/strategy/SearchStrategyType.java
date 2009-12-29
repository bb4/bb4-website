package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.*;
import com.becker.game.common.*;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Currently supported search method strategy
 *
 * @author Barry Becker  Date: Mar 10, 2007
 */
public enum SearchStrategyType {

    
    MINIMAX("MINIMAX_SEARCH") {
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new MiniMaxStrategy(s, weights);
        }
        @Override
        public boolean sortAscending(boolean player1, boolean playerOnesPerspective) {
            return  ( player1 == playerOnesPerspective);
        }
    },
    NEGAMAX("NEGAMAX_SEARCH") {
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new NegaMaxStrategy(s, weights);
        }
         @Override
         public boolean sortAscending(boolean player1, boolean playerOnesPerspective) {
            return false;
        }
    },
    NEGASCOUT("NEGASCOUT_SEARCH") {
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new NegaScoutStrategy(s, weights);
        }
        @Override
        public boolean sortAscending(boolean player1, boolean playerOnesPerspective) {
            return  false;
        }
    },
    NEGASCOUT_W_MEMORY("NEGASCOUT_W_MEMORY_SEARCH") {
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new NegaScoutMemoryStrategy(s, weights);
        }
        @Override
        public boolean sortAscending(boolean player1, boolean playerOnesPerspective) {
            return  false;
        }
    },
    MTD("MTD_SEARCH"){
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new MtdStrategy(s, weights);
        }
        @Override
        public boolean sortAscending(boolean player1, boolean playerOnesPerspective) {
            return  false;
        }
    };

    private String labelKey_;

    /**
     * Constructor for eye type enum.
     *
     * @param labelKey message key
     */
    private SearchStrategyType(String labelKey) {
        labelKey_ = labelKey;
    }


    /**
     * @return localized description.
     */
    public String getLabel() {
        return GameContext.getLabel(labelKey_);
    }

    /**
     * Factory method for creating the search strategy to use.
     * Do not call the constructor directly.
     *
     * @return the search method to use
     */
    public abstract SearchStrategy createStrategy(Searchable s, ParameterArray weights);

    /**
     * How to sort when determining subset of best moves to try.
     * Negamax and NegaScout type algorithms always maximize - so want a
     * descending order. For minimax, it depends on which player is moving.
     *
     * @param player1 true if player 1's turn.
     * @param playerOnesPerspective true if scores are from player 1's perspective.
     * @return true if we should short the nodes in ascending order (best move first).
     */
    public abstract boolean sortAscending(boolean player1, boolean playerOnesPerspective);
}
