package com.becker.game.twoplayer.pente.test;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.pente.Patterns;

/**
 *  Simple patterns for use with unit tests.
 *
 *  @author Barry Becker
 */
public final class StubPatterns extends Patterns
{
    private static String[] patternString = {
        "_X", "XX"
    };

    /** index into weights array. */
    private static int[] weightIndex = {
        0, 1
    };

    public StubPatterns() {
        initialize();
    }

     /**
      * This is how many in a row are needed to win
      */
    public int getWinRunLength() {
        return 2;
    }

    protected int getNumPatterns() {
        return 2;
    }

    protected int getMinInterestingLength() {
        return 2;
    }


    protected String getPatternString(int i) {
        return patternString[i];
    }

    protected int getWeightIndex(int i) {
        return weightIndex[i];
    }

     @Override
     protected String getPatternFile() {
        return  null;
    }

    @Override
    protected String getExportFile() {
        return null;
    }
}