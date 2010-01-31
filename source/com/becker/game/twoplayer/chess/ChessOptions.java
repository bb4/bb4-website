package com.becker.game.twoplayer.chess;

import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.SearchOptions;

/**
 * @author Barry Becker
 */
public class ChessOptions extends TwoPlayerOptions {

    public ChessOptions() {}

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions(2, 80, 10);
    }
}
