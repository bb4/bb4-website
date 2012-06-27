/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.search.SearchAttribute;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Arrays;
import java.util.List;

import static com.becker.game.twoplayer.common.search.SearchAttribute.*;
/**
 * Currently supported search strategies.
 *
 * @author Barry Becker
 */
public enum SearchStrategyType {
    
    MINIMAX("MINIMAX_SEARCH",
            new SearchAttribute[] {BRUTE_FORCE}) {
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new MiniMaxStrategy(s, weights);
        }
    },
    NEGAMAX("NEGAMAX_SEARCH",
            new SearchAttribute[] {BRUTE_FORCE}) {
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new NegaMaxStrategy(s, weights);
        }
    },
    NEGAMAX_W_MEMORY("NEGAMAX_W_MEMORY_SEARCH",
            new SearchAttribute[] {BRUTE_FORCE, MEMORY}) {
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new NegaMaxMemoryStrategy(s, weights);
        }
    },
    NEGASCOUT("NEGASCOUT_SEARCH",
            new SearchAttribute[] {BRUTE_FORCE, ASPIRATION}) {
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new NegaScoutStrategy(s, weights);
        }
    },
    NEGASCOUT_W_MEMORY("NEGASCOUT_W_MEMORY_SEARCH",
            new SearchAttribute[] {BRUTE_FORCE, MEMORY, ASPIRATION}) {
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new NegaScoutMemoryStrategy(s, weights);
        }
    },   
    MTD_NEGASCOUT("MTD_NEGASCOUT_SEARCH",
            new SearchAttribute[] {BRUTE_FORCE, MEMORY, ASPIRATION}){
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new MtdStrategy(new NegaScoutMemoryStrategy(s, weights));
        }
    },
    MTD_NEGAMAX("MTD_NEGAMAX_SEARCH",
            new SearchAttribute[] {BRUTE_FORCE, MEMORY, ASPIRATION}){
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new MtdStrategy(new NegaMaxMemoryStrategy(s, weights));
        }
    },
    UCT("UCT_SEARCH",
            new SearchAttribute[] {MONTE_CARLO}){
        @Override
        public SearchStrategy createStrategy(Searchable s, ParameterArray weights) {
            return new UctStrategy(s, weights);
        }
    };


    private String labelKey_;
    private List<SearchAttribute> attributes_;

    /**
     * Constructor for eye type enum.
     *
     * @param labelKey message key
     */
    SearchStrategyType(String labelKey, SearchAttribute[] attributes) {
        labelKey_ = labelKey;
        attributes_ =  Arrays.asList(attributes);
    }


    /**
     * @return localized description.
     */
    public String getLabel() {
        return GameContext.getLabel(labelKey_);
    }

    /**
     * @return localized description.
     */
    public String getTooltip() {
        return GameContext.getLabel(labelKey_ + "_TIP");
    }


    /**
     * @param attribute
     * @return true if this search type has the specified attribute.
     */
    public boolean hasAttribute(SearchAttribute attribute) {
        return attributes_.contains(attribute);
    }

    /**
     * Factory method for creating the search strategy to use.
     * Do not call the constructor directly.
     *
     * @return the search method to use
     */
    public abstract SearchStrategy createStrategy(Searchable s, ParameterArray weights);
}