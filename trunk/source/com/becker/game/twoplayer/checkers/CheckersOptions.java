package com.becker.game.twoplayer.checkers;

import com.becker.game.twoplayer.common.TwoPlayerOptions;

/**
 *
 * @author Barry Becker
 */
public class CheckersOptions extends TwoPlayerOptions {

    public CheckersOptions() {}

    @Override
    protected int getDefaultLookAhead() {
        return 4;
    }

    @Override
    protected int getDefaultPercentageBestMoves() {
        return 100;
    }
}
