/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.blockade;

import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.sound.MusicMaker;

/**
 *
 * @author Barry Becker
 */
public class BlockadeOptions extends TwoPlayerOptions {

    /** initial look ahead factor. */
    private static final int DEFAULT_LOOK_AHEAD = 3;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 50;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 0;

    /** for any given ply never consider less that this many moves. */
    private static final int DEFAULT_MIN_BEST_MOVES = 5;

    /** Default sound when moving */
    private static final String DEFAULT_TONE = MusicMaker.APPLAUSE;


    public BlockadeOptions() {
    }

    @Override
    protected SearchOptions createDefaultSearchOptions() {

        return new SearchOptions(new BruteSearchOptions(DEFAULT_LOOK_AHEAD),
                                new BestMovesSearchOptions(DEFAULT_PERCENTAGE_BEST_MOVES,
                                                           DEFAULT_MIN_BEST_MOVES,
                                                           DEFAULT_PERCENT_LESS_THAN_BEST_THRESH),
                                new MonteCarloSearchOptions(50, 1.0, 10));
    }

    @Override
    protected String getDefaultTone() {
        return DEFAULT_TONE;
    }

}