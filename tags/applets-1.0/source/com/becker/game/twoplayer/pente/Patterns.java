/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente;

/**
 * Encapsulates the domain knowledge for n in a row game.
 * These are key patterns that can occur in the game and are weighted
 * by importance to let the computer play better.
 *
 * Do not add duplicate patterns or patterns that are the reverse of other patterns.
 *
 * @author Barry Becker
 */
public abstract class Patterns {

    /**
     * This table provides a quick way to look up a weight for a pattern.
     * it acts as a hash map to a weight index. The pattern can be converted to
     * a lookup index using convertPatternToInt.There is a leading 1 in front of
     * the binary hash - that's why we need 2^12 rather than 2^11.
     */
    private static final int TABLE_SIZE = 4096;
    protected int weightIndexTable_[] = null;


    /** a blank space on the game board. */
    public static final char UNOCCUPIED = '_';

    /**
     * Constructor.
     */
    public Patterns() {
        initTable();
        initializePatterns();
    }

    /**
     * @return how many in a row are needed to win. If M is five then the game is pente
     */
    public abstract int getWinRunLength();

    /**
     * @return patterns shorter than this are not interesting and have weight 0
     */
    public abstract int getMinInterestingLength();

    /**
     * @return total number of patterns represented
     */
    protected abstract int getNumPatterns();

    /**
     * Initialize all the pente patterns.
     */
    protected void initializePatterns() {

        for ( int i = 0; i < getNumPatterns(); i++ ) {
            setPatternWeightInTable( getPatternString(i), getWeightIndex(i));
        }
    }

    protected void initTable() {
        weightIndexTable_ = new int[TABLE_SIZE];
        for ( int i = 0; i < TABLE_SIZE; i++ ) {
            weightIndexTable_[i] = -1;
        }
    }

    protected abstract String getPatternString(int i);
    
    protected abstract int getWeightIndex(int i);

    /**
     * @param pattern  pattern to get the weight index for.
     * @param minpos index of first character in pattern
     * @param maxpos index of last character position in pattern.
     * @return weight index
     */
    public int getWeightIndexForPattern(StringBuilder pattern, int minpos, int maxpos) {
        return weightIndexTable_[convertPatternToInt(pattern, minpos, maxpos)];
    }
    
    /**
     * each pattern can be represented as a unique integer.
     * this integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable
     * @return integer identifier for pattern.
     */
    protected int convertPatternToInt( String pattern ) {

        StringBuilder buf = new StringBuilder( pattern );
        return convertPatternToInt( buf, 0, pattern.length()-1 );
    }

    /**
     * Each pattern can be represented as a unique integer.
     * this integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable
     * @return integer representation of pattern
     */
    private int convertPatternToInt( StringBuilder pattern, int minpos, int maxpos ) {

        int power = 1;
        int sum = 0;

        for ( int i = maxpos; i >= minpos; i-- ) {
            if ( pattern.charAt( i ) != UNOCCUPIED )
                sum += power;
            power += power;  // doubles every step through the loop.
        }
        return sum + power;
    }

    protected void setPatternWeightInTable( String pattern, int wtIndex ) {

        int hash = convertPatternToInt( pattern );
        weightIndexTable_[hash] = wtIndex;

        // also add the reversed pattern
        StringBuilder reverse = new StringBuilder( pattern );
        reverse.reverse();

        hash = convertPatternToInt(reverse, 0, reverse.length()-1);
        weightIndexTable_[hash] = wtIndex;
    }
}
