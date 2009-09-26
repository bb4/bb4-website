package com.becker.game.twoplayer.tictactoe;

import com.becker.game.common.GameContext;

import com.becker.game.twoplayer.pente.Patterns;

/**
 *  Encapsulates the domain knowledge for Pente.
 *  Its primary client is the PenteController class.
 *  These are key patterns that can occur in the game and are weighted
 *  by importance to let the computer play better.
 *
 *  @author Barry Becker
 */
public final class TicTacToePatterns extends Patterns
{

    public static final int WIN_RUN_LENGTH = 3;

    private static final int NUM_PATTERNS = 7;

    private static String[] patternString = {
        "X__", "__X", "_X_", "XX_", "_XX", "X_X", "XXX"
    };

    private static int[] weightIndex = {
        0, 0, 1, 2, 2, 3, 4
    };

    public TicTacToePatterns() {
        initialize();
    }

     /**
      * This is how many in a row are needed to win
      */
    public int getWinRunLength() {
        return WIN_RUN_LENGTH;
    }

    protected int getNumPatterns() {
        return NUM_PATTERNS;
    }

    protected String getPatternString(int i) {
        return patternString[i];
    }

    protected int getWeightIndex(int i) {
        return weightIndex[i];
    }

     protected String getPatternFile() {
        return  GameContext.GAME_ROOT + "tictactoe/TicTacToe.patterns1.dat";
    }

    protected String getExportFile() {
        return GameContext.GAME_ROOT + "tictactoe/TicTacToe.export.dat";
    }
}
