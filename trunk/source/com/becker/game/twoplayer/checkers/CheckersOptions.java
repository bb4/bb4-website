package com.becker.game.twoplayer.checkers;

import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;

/**
 * @author Barry Becker
 */
public class CheckersOptions extends TwoPlayerOptions {

    public CheckersOptions() {}

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions(new BruteSearchOptions(4), new BestMovesSearchOptions(100, 10, 0));
    }
}
