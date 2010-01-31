package com.becker.game.twoplayer.checkers;

import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.SearchOptions;

/**
 *
 * @author Barry Becker
 */
public class CheckersOptions extends TwoPlayerOptions {

    public CheckersOptions() {}

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions(4, 100, 10);
    }
}
