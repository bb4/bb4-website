// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.blockade;

import com.becker.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.becker.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;

import java.awt.*;

/**
 *
 * @author Barry Becker
 */
public class BlockadePlayerOptions extends TwoPlayerPlayerOptions {

    /** initial look ahead factor. */
    private static final int DEFAULT_LOOK_AHEAD = 3;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 50;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 0;

    /** for any given ply never consider less that this many moves. */
    private static final int DEFAULT_MIN_BEST_MOVES = 5;


    public BlockadePlayerOptions(String name, Color color) {
        super(name, color);
    }

    @Override
    protected SearchOptions createDefaultSearchOptions() {

        return new SearchOptions(new BruteSearchOptions(DEFAULT_LOOK_AHEAD),
                                new BestMovesSearchOptions(DEFAULT_PERCENTAGE_BEST_MOVES,
                                                           DEFAULT_MIN_BEST_MOVES,
                                                           DEFAULT_PERCENT_LESS_THAN_BEST_THRESH),
                                new MonteCarloSearchOptions(50, 1.0, 10));
    }



}
