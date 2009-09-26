package com.becker.game.twoplayer.chess;

import com.becker.game.twoplayer.common.TwoPlayerOptions;

/**
 *
 * @author Barry Becker
 */
public class ChessOptions extends TwoPlayerOptions {

    public ChessOptions() {}

     @Override
    protected int getDefaultLookAhead() {
        return 2;
    }

    @Override
    protected int getDefaultPercentageBestMoves() {
        return 80;
    }
}
