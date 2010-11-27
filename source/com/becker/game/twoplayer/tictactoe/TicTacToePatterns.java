package com.becker.game.twoplayer.tictactoe;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.pente.Patterns;

/**
 *  Encapsulates the domain knowledge for TicTacToe.
 *  These are key patterns that can occur in the game and are weighted
 *  by importance to let the computer play better.
 *
 *  @author Barry Becker
 */
public final class TicTacToePatterns extends Patterns
{

    public static final int WIN_RUN_LENGTH = 3;

    private static final int NUM_PATTERNS = 5;

    private static final String[] patternString = {
        "_X", "_X_", "_XX", "X_X", "XXX"
    };

    /** index into weights array. @see TicTacTowWeights */
    private static int[] weightIndex = {
          0,     1,    2,     3,     4
    };

    public TicTacToePatterns() {
        initialize();
    }

     /**
      * This is how many in a row are needed to win
      */
    @Override
    public int getWinRunLength() {
        return WIN_RUN_LENGTH;
    }

    @Override
    protected int getNumPatterns() {
        return NUM_PATTERNS;
    }

    @Override
    public int getMinInterestingLength() {
        return 2;
    }

    @Override
    protected String getPatternString(int i) {
        return patternString[i];
    }

    @Override
    protected int getWeightIndex(int i) {
        return weightIndex[i];
    }

     @Override
     protected String getPatternFile() {
        return  GameContext.GAME_ROOT + "tictactoe/TicTacToe.patterns1.dat";
    }

    @Override
    protected String getExportFile() {
        return GameContext.GAME_ROOT + "tictactoe/TicTacToe.export.dat";
    }
}