package com.becker.game.twoplayer.common.search;

import com.becker.game.common.*;

/**
 * @author Barry Becker Date: Mar 10, 2007
 */
public enum SearchStrategyType {

    // currently supported search method strategy
    // @@ ad ability to have plugable strategies.
    MINIMAX("MINIMAX_SEARCH"),
    NEGAMAX("NEGAMAX_SEARCH"),
    NEGASCOUT("NEGASCOUT_SEARCH"),
    NEGASCOUT_W_MEMORY("NEGASCOUT__W_MEMORY_SEARCH"),
    MTD("MTD_SEARCH");

    private String label_;

    /**
     * constructor for eye type enum
     *
     * @param label message key
     */
    private SearchStrategyType(String label) {
        label_ = label;
    }


    /**
     * @return localized description
     */
    public String getLabel() {
        return GameContext.getLabel(label_);
    }

}
