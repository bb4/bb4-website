// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.common;

import com.becker.game.common.player.PlayerOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;

import java.awt.*;

/**
 * Not a great name for this class. These are the options for a player in a two player game.
 * Since each player could be a computer player with different search options, the search options
 * are part of this class and not TwoPlayerOtpions.
 * 
 * @author Barry Becker
 */
public class TwoPlayerPlayerOptions extends PlayerOptions {

    private SearchOptions searchOptions_;

    /**
     * Default Constructor
     */
    public TwoPlayerPlayerOptions(String name, Color color) {
        super(name, color);
        searchOptions_ = createDefaultSearchOptions();
    }

    /**
     * Constructor
     * @param searchOptions search options to use.
     */
    protected TwoPlayerPlayerOptions(String name, Color color, SearchOptions searchOptions) {

        this(name, color);
        searchOptions_ = searchOptions;
    }

    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions();
    }

    public SearchOptions getSearchOptions() {
        return searchOptions_;
    }
}
