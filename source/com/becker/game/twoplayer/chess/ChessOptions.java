package com.becker.game.twoplayer.chess;

import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;

/**
 * @author Barry Becker
 */
public class ChessOptions extends TwoPlayerOptions {

    public ChessOptions() {}

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions(new BruteSearchOptions(2), 80, 10);
    }
}
