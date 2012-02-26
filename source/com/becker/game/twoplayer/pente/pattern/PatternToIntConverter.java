// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.pattern;

/**
 * Responsible for Converting pattern strings to an integer.
 *
 * @author Barry Becker
 */
class PatternToIntConverter {

    public PatternToIntConverter() { }

    /**
     * each pattern can be represented as a unique integer.
     * this integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable
     * @return integer identifier for pattern.
     */
    int convertPatternToInt( String pattern ) {
        StringBuilder buf = new StringBuilder( pattern );
        return convertPatternToInt( buf, 0, pattern.length()-1 );
    }

    /**
     * Each pattern can be represented as a unique integer.
     * This integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable.
     * @return integer representation of pattern
     */
    int convertPatternToInt( CharSequence pattern, int minpos, int maxpos ) {

        int power = 1;
        int sum = 0;

        for ( int i = maxpos; i >= minpos; i-- ) {
            if ( pattern.charAt( i ) != Patterns.UNOCCUPIED )   {
                sum += power;
            }
            // power doubles every step through the loop.
            power += power;
        }
        return sum + power;
    }
}
