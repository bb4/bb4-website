package com.becker.game.twoplayer.blockade;

import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.sound.MusicMaker;

/**
 *
 * @author Barry Becker
 */
public class BlockadeOptions extends TwoPlayerOptions {

    /** initial look ahead factor. */
    private static final int DEFAULT_LOOK_AHEAD = 2;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 70;

    /** for any given ply never consider less taht this many moves. */
    private static final int DEFAULT_MIN_BEST_MOVES = 10;

    private static final String DEFAULT_TONE = MusicMaker.APPLAUSE;


    public BlockadeOptions() {
    }

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions(new BruteSearchOptions(DEFAULT_LOOK_AHEAD),
                                 DEFAULT_PERCENTAGE_BEST_MOVES, DEFAULT_MIN_BEST_MOVES);
    }

    @Override
    protected String getDefaultTone() {
        return DEFAULT_TONE;
    }

}
