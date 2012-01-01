// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente;

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
public class PentePlayerOptions extends TwoPlayerPlayerOptions {

    
    /** initial look ahead factor. */
    private static final int DEFAULT_LOOK_AHEAD = 6;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 80;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. not used. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 0;

    /** for any given ply never consider less that this many moves. */
    private static final int DEFAULT_MIN_BEST_MOVES = 5;


    public PentePlayerOptions(String name, Color color) {
       super(name, color);
    }

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions(new BruteSearchOptions(DEFAULT_LOOK_AHEAD, DEFAULT_LOOK_AHEAD + 2),
                                 new BestMovesSearchOptions(DEFAULT_PERCENTAGE_BEST_MOVES, 
                                                            DEFAULT_MIN_BEST_MOVES,
                                                            DEFAULT_PERCENT_LESS_THAN_BEST_THRESH),
                                 new MonteCarloSearchOptions(4000, 1.0, 25));
    }
}
