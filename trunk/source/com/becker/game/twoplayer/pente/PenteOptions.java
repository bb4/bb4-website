package com.becker.game.twoplayer.pente;

import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;

/**
 *
 * @author Barry Becker
 */
public class PenteOptions extends TwoPlayerOptions {

    
    /** initial look ahead factor. */
    private static final int DEFAULT_LOOK_AHEAD = 4;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 50;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. not used. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 0;

    /** for any given ply never consider less taht this many moves. */
    private static final int DEFAULT_MIN_BEST_MOVES = 4;


    public PenteOptions() {
    }

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions(new BruteSearchOptions(DEFAULT_LOOK_AHEAD, DEFAULT_LOOK_AHEAD + 2),
                                 new BestMovesSearchOptions(DEFAULT_PERCENTAGE_BEST_MOVES, 
                                                            DEFAULT_MIN_BEST_MOVES,
                                                            DEFAULT_PERCENT_LESS_THAN_BEST_THRESH));
    }
}
