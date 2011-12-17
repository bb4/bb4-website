/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente;

/**
 *  Encapsulates the domain knowledge for Pente.
 *  Its primary client is the PenteController class.
 *  These are key patterns that can occur in the game and are weighted
 *  by importance to let the computer play better.
 *
 *  @author Barry Becker
 */
public class PentePatterns extends Patterns
{
    public static final int WIN_RUN_LENGTH = 5;

    /** total number of patterns used. */
    private static final int NUM_PATTERNS = 210;

    /** String patter/weight pairs. Easier to read than parallel arrays. */
    private static final Pair[] PAIRS = {
            new Pair("_XX", 1),
            new Pair("XXX", 2),
            new Pair("_XXX", 3),
            new Pair("X_XX", 3),
            new Pair("_X_X", 4),
            new Pair("_XX_", 5),
            new Pair("_X_X_", 5),
            new Pair("_X_XX", 5),
            new Pair("_XX_X", 5),
            new Pair("_XXX_", 6),
            new Pair("_XXXX", 7),
            new Pair("X_X_X", 4),
            new Pair("X_XXX", 7),
            new Pair("XX_XX", 7),
            new Pair("XXXXX", 9),
            new Pair("_X_X_X", 5),
            new Pair("_X_XX_", 6),
            new Pair("_X_XXX", 7),
            new Pair("_XX_XX", 7),
            new Pair("_XXX_X", 7),
            new Pair("_XXXX_", 8),
            new Pair("_XXXXX", 9),
            new Pair("X_X_XX", 5),
            new Pair("X_XX_X", 5),
            new Pair("X_XXXX", 7),
            new Pair("XX_XXX", 7),
            new Pair("XXXXXX", 10),
            new Pair("_X_X_X_", 5),
            new Pair("_X_X_XX", 5),
            new Pair("_X_XX_X", 6),
            new Pair("_X_XXX_", 7),
            new Pair("_X_XXXX", 7),
            new Pair("_XX_X_X", 6),
            new Pair("_XX_XX_", 7),
            new Pair("_XX_XXX", 7),
            new Pair("_XXX_XX", 7),
            new Pair("_XXXX_X", 8),
            new Pair("_XXXXX_", 9),
            new Pair("_XXXXXX", 10),
            new Pair("X_X_X_X", 5),
            new Pair("X_X_XXX", 7),
            new Pair("X_XX_X_", 6),
            new Pair("X_XX_XX", 7),
            new Pair("X_XXX_X", 8),
            new Pair("X_XXXXX", 9),
            new Pair("XX_X_XX", 4), // was 5
            new Pair("XX_XXXX", 7),
            new Pair("XXX_XXX", 7),
            new Pair("XXXXXXX", 11),
            new Pair("_X_X_X_X", 6),
            new Pair("_X_X_XX_", 6),
            new Pair("_X_X_XXX", 7),
            new Pair("_X_XX_XX", 7),
            new Pair("_X_XXX_X", 8),
            new Pair("_X_XXXX_", 8),
            new Pair("_X_XXXXX", 9),
            new Pair("_XX_X_XX", 6),
            new Pair("_XX_XX_X", 7),
            new Pair("_XX_XXX_", 7),
            new Pair("_XX_XXXX", 7),
            new Pair("_XXX_X_X", 7),
            new Pair("_XXX_XX_", 7),
            new Pair("_XXX_XXX", 7),
            new Pair("_XXXX_X_", 8),
            new Pair("_XXXX_XX", 8),
            new Pair("_XXXXX_X", 9),
            new Pair("_XXXXXX_", 10),
            new Pair("_XXXXXXX", 11),
            new Pair("X_X_X_XX", 6),
            new Pair("X_X_XX_X", 6),
            new Pair("X_X_XXXX", 7),
            new Pair("X_XX_XXX", 7),
            new Pair("X_XXX_XX", 8),
            new Pair("X_XXXX_X", 8),
            new Pair("X_XXXXXX", 10),
            new Pair("XX_X_XXX", 7),
            new Pair("XX_XX_XX", 8),
            new Pair("XX_XXXXX", 9),
            new Pair("XXX_XXXX", 7),
            new Pair("XXXXXXXX", 11),
            new Pair("_X_X_X_X_", 6),
            new Pair("_X_X_X_XX", 6),
            new Pair("_X_X_XX_X", 6),
            new Pair("_X_X_XXX_", 7),
            new Pair("_X_X_XXXX", 7),
            new Pair("_X_XX_X_X", 6),
            new Pair("_X_XX_XX_", 7),
            new Pair("_X_XX_XXX", 7),
            new Pair("_X_XXX_X_", 8),
            new Pair("_X_XXX_XX", 8),
            new Pair("_X_XXXX_X", 8),
            new Pair("_X_XXXXX_", 9),
            new Pair("_X_XXXXXX", 10),
            new Pair("_XX_X_X_X", 6),
            new Pair("_XX_X_XX_", 6),
            new Pair("_XX_X_XXX", 7),
            new Pair("_XX_XX_XX", 8),
            new Pair("_XX_XXX_X", 8),
            new Pair("_XX_XXXX_", 8),
            new Pair("_XX_XXXXX", 9),
            new Pair("_XXX_X_XX", 7),
            new Pair("_XXX_XX_X", 7),
            new Pair("_XXX_XXX_", 7),
            new Pair("_XXX_XXXX", 7),
            new Pair("_XXXX_X_X", 8),
            new Pair("_XXXX_XXX", 8),
            new Pair("_XXXXX_XX", 9),
            new Pair("_XXXXXX_X", 10),
            new Pair("_XXXXXXX_", 11),
            new Pair("_XXXXXXXX", 11),
            new Pair("X_X_X_X_X", 6),
            new Pair("X_X_X_XXX", 7),
            new Pair("X_X_XX_XX", 7),
            new Pair("X_X_XXX_X", 8),
            new Pair("X_X_XXXXX", 9),
            new Pair("X_XX_X_XX", 6),
            new Pair("X_XX_XX_X", 7),
            new Pair("X_XX_XXXX", 7),   // was 8
            new Pair("X_XXX_XXX", 8),
            new Pair("X_XXXX_XX", 8),
            new Pair("X_XXXXX_X", 9),
            new Pair("X_XXXXXXX", 11),
            new Pair("XX_X_X_XX", 6),
            new Pair("XX_X_XXXX", 7),
            new Pair("XX_XX_XX_", 8),
            new Pair("XX_XX_XXX", 8),
            new Pair("XX_XXX_XX", 8),
            new Pair("XX_XXXXXX", 10),
            new Pair("XXX_X_XXX", 8),
            new Pair("XXX_XXXXX", 9),
            new Pair("XXXX_XXXX", 7),
            new Pair("XXXXXXXXX", 11),
            new Pair("_X_X_X_X_X", 6),
            new Pair("_X_X_X_XX_", 6),
            new Pair("_X_X_X_XXX", 7),
            new Pair("_X_X_XX_X_", 6),
            new Pair("_X_X_XX_XX", 7),
            new Pair("_X_X_XXX_X", 8),
            new Pair("_X_X_XXXX_", 8),
            new Pair("_X_X_XXXXX", 9),
            new Pair("_X_XX_X_X_", 6),
            new Pair("_X_XX_X_XX", 6),
            new Pair("_X_XX_XX_X", 7),
            new Pair("_X_XX_XXX_", 7),
            new Pair("_X_XX_XXXX", 7),
            new Pair("_X_XXX_X_X", 8), // was 7
            new Pair("_X_XXX_XX_", 8),
            new Pair("_X_XXX_XXX", 8),
            new Pair("_X_XXXX_X_", 8),
            new Pair("_X_XXXX_XX", 8),
            new Pair("_X_XXXXX_X", 9),
            new Pair("_X_XXXXXX_", 10),
            new Pair("_X_XXXXXXX", 11),
            new Pair("_XX_X_X_XX", 6),
            new Pair("_XX_X_XX_X", 6),
            new Pair("_XX_X_XXX_", 7),
            new Pair("_XX_X_XXXX", 7),
            new Pair("_XX_XX_X_X", 7),
            new Pair("_XX_XX_XX_", 8),
            new Pair("_XX_XX_XXX", 8),
            new Pair("_XX_XXX_XX", 8),
            new Pair("_XX_XXXX_X", 8),
            new Pair("_XX_XXXXX_", 9),
            new Pair("_XX_XXXXXX", 10),
            new Pair("_XXX_X_X_X", 7),
            new Pair("_XXX_X_XXX", 8),
            new Pair("_XXX_XX_XX", 8),
            new Pair("_XXX_XXX_X", 8),
            new Pair("_XXX_XXXX_", 8),
            new Pair("_XXX_XXXXX", 9),
            new Pair("_XXXX_X_XX", 8),
            new Pair("_XXXX_XX_X", 8),
            new Pair("_XXXX_XXXX", 8),
            new Pair("_XXXXX_X_X", 9),
            new Pair("_XXXXX_XXX", 9),
            new Pair("_XXXXXX_XX", 10),
            new Pair("_XXXXXXX_X", 11),
            new Pair("_XXXXXXXX_", 11),
            new Pair("_XXXXXXXXX", 11),
            new Pair("X_X_X_X_XX", 6),
            new Pair("X_X_X_XX_X", 6),
            new Pair("X_X_X_XXXX", 7),
            new Pair("X_X_XX_X_X", 6),  // was 7
            new Pair("X_X_XX_XXX", 7),
            new Pair("X_X_XXX_XX", 8),
            new Pair("X_X_XXXX_X", 8),
            new Pair("X_X_XXXXXX", 9),
            new Pair("X_XX_X_XXX", 7),
            new Pair("X_XX_XX_XX", 8),
            new Pair("X_XX_XXX_X", 8),
            new Pair("X_XX_XXXXX", 9),
            new Pair("X_XXX_X_XX", 8),
            new Pair("X_XXX_XXXX", 8),
            new Pair("X_XXXX_XXX", 8),
            new Pair("X_XXXXX_XX", 9),
            new Pair("X_XXXXXX_X", 10),
            new Pair("X_XXXXXXXX", 11),
            new Pair("XX_X_X_XX_", 6),
            new Pair("XX_X_X_XXX", 7),
            new Pair("XX_X_XX_XX", 7),
            new Pair("XX_X_XXXXX", 9),
            new Pair("XX_XX_XXXX", 8),
            new Pair("XX_XXX_XXX", 8),
            new Pair("XX_XXXX_XX", 8),
            new Pair("XX_XXXXXXX", 11),
            new Pair("XXX_X_XXX_", 8),
            new Pair("XXX_X_XXXX", 8),
            new Pair("XXX_XX_XXX", 8),
            new Pair("XXX_XXXXX_", 9),
            new Pair("XXX_XXXXXX", 10),
    };


    /**
     * Constructor.
     */
    public PentePatterns() {}

    /**
     * This is how many in a row are needed to win
     * if M is five then the game is pente
     */
    @Override
    public int getWinRunLength() {
        return WIN_RUN_LENGTH;
    }

    @Override
    public int getMinInterestingLength() {
        return 3;
    }
    
    @Override
    protected int getNumPatterns() {
        return NUM_PATTERNS;
    }

    @Override
    protected String getPatternString(int i) {
        return PAIRS[i].pattern;
    }

    @Override
    protected int getWeightIndex(int i) {
        return PAIRS[i].weight;
    }

    private static class Pair {
        String pattern;
        int weight;
        
        Pair(String pattern, int weight) {
            this.pattern = pattern;
            this.weight = weight;
        }
    }

}
