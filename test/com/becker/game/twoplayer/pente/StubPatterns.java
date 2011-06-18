package com.becker.game.twoplayer.pente;

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
        initializePatterns();
    }

     /**
      * This is how many in a row are needed to win
      */
    @Override
    public int getWinRunLength() {
        return 2;
    }

    @Override
    protected int getNumPatterns() {
        return 2;
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
}