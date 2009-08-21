package com.becker.game.twoplayer.common.search;

import com.becker.game.common.*;

/**
 * Currently supported search method strategy
 *
 * @author Barry Becker  Date: Mar 10, 2007
 */
public enum SearchStrategyType {

    
    MINIMAX("MINIMAX_SEARCH") {
        public SearchStrategy createStrategy(Searchable s) { return new MiniMaxStrategy(s); }
    },
    NEGAMAX("NEGAMAX_SEARCH") {
        public SearchStrategy createStrategy(Searchable s) { return new NegaMaxStrategy(s); }
    },
    NEGASCOUT("NEGASCOUT_SEARCH") {
        public SearchStrategy createStrategy(Searchable s) { return new NegaScoutStrategy(s); }
    },
    NEGASCOUT_W_MEMORY("NEGASCOUT_W_MEMORY_SEARCH") {
        public SearchStrategy createStrategy(Searchable s) { return new NegaScoutMemoryStrategy(s); }
    },
    MTD("MTD_SEARCH"){
        public SearchStrategy createStrategy(Searchable s) { return new MtdStrategy(s); }
    };

    private String labelKey_;

    /**
     * Constructor for eye type enum.
     *
     * @param label message key
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
     * @return the search method to use
     */
    public abstract SearchStrategy createStrategy(Searchable s);

}
