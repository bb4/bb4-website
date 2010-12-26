package com.becker.game.twoplayer.pente;

import com.becker.game.common.GameContext;

import java.io.*;

/**
 * Encapsulates the domain knowledge for n in a row game.
 * These are key patterns that can occur in the game and are weighted
 * by importance to let the computer play better.
 *
 * Do not add duplicate patterns or patterns that are the reverse of other patterns.
 *
 * @author Barry Becker
 */
public abstract class Patterns
{
    /**
     * This table provides a quick way to look up a weight for a pattern.
     * it acts as a hashmap to a weight index. The pattern can be converted to
     * a lookup index using convertPatternToInt.There is a leading 1 in front of
     * the binary hash - that's why we need 2^12 rather than 2^11.
     */
    private static final int TABLE_SIZE = 4096;
    private int weightIndexTable_[] = null;

    /**
     * This vector contains the key patterns to match
     * it is loaded from the PATTERN_FILE. (if used)
     */
    private String[] patterns_ = null;

    /** a blank space on the game board. */
    public static final char UNOCCUPIED = '_';

    /**
     * Constructor.
     */
    public Patterns() {
        initialize();
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
    protected void initialize() {
        weightIndexTable_ = new int[TABLE_SIZE];
        for ( int i = 0; i < TABLE_SIZE; i++ ) {
            weightIndexTable_[i] = -1;
        }

        // since reading files is not easy with applets, I've moved the pattern data into the class PentePatterns.
        // only use this method if you need to read the data from a file.
        //readPatternFile();
        // only use this when changing the format
        //writePatternFile();

        // use this initialization code if not reading from a file using the above
        for ( int i = 0; i < getNumPatterns(); i++ ) {
            setPatternWeightInTable( getPatternString(i), getWeightIndex(i));
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
     */
    private static int convertPatternToInt( String pattern )
    {
        StringBuilder buf = new StringBuilder( pattern );
        return convertPatternToInt( buf, 0, pattern.length()-1 );
    }

    /**
     * Each pattern can be represented as a unique integer.
     * this integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable
     * @return integer representation of pattern
     */
    private static int convertPatternToInt( StringBuilder pattern, int minpos, int maxpos )
    {
        int power = 1;
        int sum = 0;

        for ( int i = maxpos; i >= minpos; i-- ) {
            if ( pattern.charAt( i ) != UNOCCUPIED )
                sum += power;
            power += power;  // doubles every step thru the loop.
        }
        return sum + power;
    }

    private void setPatternWeightInTable( String pattern, int wtIndex )
    {
        int hash = convertPatternToInt( pattern );
        weightIndexTable_[hash] = wtIndex;

        // also add the reversed pattern
        StringBuilder reverse = new StringBuilder( pattern );
        reverse.reverse();

        hash = convertPatternToInt(reverse, 0, reverse.length()-1);
        weightIndexTable_[hash] = wtIndex;
    }

    protected abstract String getPatternFile();
    protected abstract String getExportFile();

    /**
     * the pattern file is fixed for pente
     * this method fills in pattern_ and weightTable_
     */
    protected void readPatternFile() {
        // Open a file of the given name.
        String patternFileName = getPatternFile();
        File file = new File( patternFileName );
        FileInputStream patternFile = null;
        int token;
        int wtIndex = 0;

        try {
            patternFile = new FileInputStream( file );
        } catch (FileNotFoundException e) {
            GameContext.log(0, "file " + patternFileName  + " not found." + e.getMessage() );
        }
        InputStreamReader iStreamReader = new InputStreamReader( patternFile );
        BufferedReader inData = new BufferedReader( iStreamReader );
        StreamTokenizer inStream = new StreamTokenizer( inData );
        inStream.commentChar( '#' );
        inStream.slashSlashComments( true );
        inStream.wordChars( '_', '_' + 1 );

        try {
            // the first entry in the file should be the number of patterns
            //token = inStream.nextToken();
            int numPatterns = (int) (inStream.nval);
            patterns_ = new String[numPatterns];
            String pattern;
            for ( int i = 0; i < numPatterns; i++ ) {
                token = inStream.nextToken(); // must be TT_WORD
                if ( token == StreamTokenizer.TT_WORD ) {
                    patterns_[i] = inStream.sval;
                }
                else
                    GameContext.log(0,  "unexpected token type = " + token + "   nval = " + inStream.nval );
                token = inStream.nextToken(); // must be TT_NUMBER
                if ( token == StreamTokenizer.TT_NUMBER ) {
                    wtIndex = (int) (inStream.nval);
                }
                else
                    GameContext.log(0, "unexpected token type = " + token + "   sval = " + inStream.sval );

                pattern = patterns_[i];
                setPatternWeightInTable( pattern, wtIndex );
            }
            iStreamReader.close();
        } catch (IOException e) {
            GameContext.log(0,  "error occurred while reading " +patternFileName );
        }

    }

    /**
     * allow exporting the patterns and weight indices in a different format.
     * ordinarily we do not export the patterns, but sometimes we might want to
     * change the format.
     */
    protected void writePatternFile()
    {
        // Open a file of the given name.
        String exportFile = getExportFile();
        File file = new File( exportFile );
        FileOutputStream patternFile = null;

        try {
            patternFile = new FileOutputStream( file );
        } catch (FileNotFoundException e) {
            GameContext.log(0, "can't open " + exportFile + " for write" );
            e.printStackTrace();
        }
        OutputStreamWriter oStreamWriter = new OutputStreamWriter( patternFile );
        BufferedWriter outData = new BufferedWriter( oStreamWriter );
        int i;

        try {

            int numPatterns = getNumPatterns();
            GameContext.log(0,"there are " + numPatterns + " patterns. " );
            for ( i = 0; i < numPatterns; i++ ) {
                outData.write( '\"' + patterns_[i] + "\", " );
            }
            outData.write( "\r\n" );
            for ( i = 0; i < numPatterns; i++ ) {
                int index = weightIndexTable_[convertPatternToInt( patterns_[i] )];
                outData.write( index + ", " );
            }
            outData.write( "\r\n" );
            outData.flush();
            patternFile.flush();
            outData.close();
            patternFile.close();
        } catch (IOException e) {
            GameContext.log(0, "error occurred while writing " + exportFile );
            e.printStackTrace();
        } 
    }
}
