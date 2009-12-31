package com.becker.game.twoplayer.pente;

import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.sound.MusicMaker;

/**
 *
 * @author Barry Becker
 */
public class PenteOptions extends TwoPlayerOptions {

    
    /** initial look ahead factor. */
    private static final int DEFAULT_LOOK_AHEAD = 4;

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 70;

    /** for any given ply never consider less taht this many moves. */
    private static final int DEFAULT_MIN_BEST_MOVES = 10;


    public PenteOptions() {
    }

    @Override
    protected int getDefaultLookAhead() {
        return DEFAULT_LOOK_AHEAD;
    }
    @Override
    protected int getDefaultPercentageBestMoves() {
        return DEFAULT_PERCENTAGE_BEST_MOVES;
    }
    
    @Override
    protected int getDefaultMinBestMoves() {
        return DEFAULT_MIN_BEST_MOVES;
    }

    
    @Override
    public int getMaxQuiescentDepth() {
        return this.getLookAhead() + 2;
    }
}
