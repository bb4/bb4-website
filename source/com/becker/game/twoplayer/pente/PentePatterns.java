package com.becker.game.twoplayer.pente;

import com.becker.game.common.GameContext;

import java.io.*;

/**
 *  Encapsulates the domain knowledge for Pente.
 *  Its primary client is the PenteController class.
 *  These are key patterns that can occur in the game and are weighted
 *  by importance to let the computer play better.
 *
 *  @author Barry Becker
 */
public final class PentePatterns
{

    private static final int NUM_PATTERNS = 210;

    // This is how many in a row are needed to win
    // if M is five then the game is pente
    // @@ this should be configurable
    public static final int M = 5;

    // max length of the pattern to match. long patterns should be very rare
    protected static final int MAX_LINE_LENGTH = 2 * M + 1;

    // this table provides a quick way to look up a weight for a pattern.
    // it acts as a hashmap to a weight index. The pattern can be converted to
    // a lookup index using convertPatternToInt.There is a leadin 1 in from of
    // the binary hash - that's why we need 2^12 rather than 2^11.
    private static final int TABLE_SIZE = 4096; //2048;  2^11 or 2^12
    public static int weightIndexTable_[] = null;

    // use these only if reading from a file
    private static final String PATTERN_FILE =
            GameContext.GAME_ROOT + "pente/Pente.patterns1.dat";

    // this vector contains the key patterns to match
    // it is loaded from the PATTERN_FILE.
    private static String[] patterns_ = null;

    private static String[] patternString = {
        "_XX", "XXX", "_XXX", "X_XX", "_X_X", "_XX_", "_X_X_", "_X_XX", "_XX_X", "_XXX_",
        "_XXXX", "X_X_X", "X_XXX", "XX_XX", "XXXXX", "_X_X_X", "_X_XX_", "_X_XXX", "_XX_XX", "_XXX_X",
        "_XXXX_", "_XXXXX", "X_X_XX", "X_XX_X", "X_XXXX", "XX_XXX", "XXXXXX", "_X_X_X_", "_X_X_XX", "_X_XX_X",
        "_X_XXX_", "_X_XXXX", "_XX_X_X", "_XX_XX_", "_XX_XXX", "_XXX_XX", "_XXXX_X", "_XXXXX_", "_XXXXXX", "X_X_X_X",
        "X_X_XXX", "X_XX_X_", "X_XX_XX", "X_XXX_X", "X_XXXXX", "XX_X_XX", "XX_XXXX", "XXX_XXX", "XXXXXXX", "_X_X_X_X",
        "_X_X_XX_", "_X_X_XXX", "_X_XX_XX", "_X_XXX_X", "_X_XXXX_", "_X_XXXXX", "_XX_X_XX", "_XX_XX_X", "_XX_XXX_", "_XX_XXXX",
        "_XXX_X_X", "_XXX_XX_", "_XXX_XXX", "_XXXX_X_", "_XXXX_XX", "_XXXXX_X", "_XXXXXX_", "_XXXXXXX", "X_X_X_XX", "X_X_XX_X",
        "X_X_XXXX", "X_XX_XXX", "X_XXX_XX", "X_XXXX_X", "X_XXXXXX", "XX_X_XXX", "XX_XX_XX", "XX_XXXXX", "XXX_XXXX", "XXXXXXXX", "_X_X_X_X_",
        "_X_X_X_XX", "_X_X_XX_X", "_X_X_XXX_", "_X_X_XXXX", "_X_XX_X_X", "_X_XX_XX_", "_X_XX_XXX", "_X_XXX_X_", "_X_XXX_XX", "_X_XXXX_X", "_X_XXXXX_",
        "_X_XXXXXX", "_XX_X_X_X", "_XX_X_XX_", "_XX_X_XXX", "_XX_XX_XX", "_XX_XXX_X", "_XX_XXXX_", "_XX_XXXXX", "_XXX_X_XX", "_XXX_XX_X", "_XXX_XXX_",
        "_XXX_XXXX", "_XXXX_X_X", "_XXXX_XXX", "_XXXXX_XX", "_XXXXXX_X", "_XXXXXXX_", "_XXXXXXXX", "X_X_X_X_X", "X_X_X_XXX", "X_X_XX_XX", "X_X_XXX_X", "X_X_XXXXX", "X_XX_X_XX",
        "X_XX_XX_X", "X_XX_XXXX", "X_XXX_XXX", "X_XXXX_XX", "X_XXXXX_X", "X_XXXXXXX", "XX_X_X_XX", "XX_X_XXXX", "XX_XX_XX_", "XX_XX_XXX", "XX_XXX_XX", "XX_XXXXXX", "XXX_X_XXX",
        "XXX_XXXXX", "XXXX_XXXX", "XXXXXXXXX", "_X_X_X_X_X", "_X_X_X_XX_", "_X_X_X_XXX", "_X_X_XX_X_", "_X_X_XX_XX", "_X_X_XXX_X", "_X_X_XXXX_", "_X_X_XXXXX", "_X_XX_X_X_",
        "_X_XX_X_XX", "_X_XX_XX_X", "_X_XX_XXX_", "_X_XX_XXXX", "_X_XXX_X_X", "_X_XXX_XX_", "_X_XXX_XXX", "_X_XXXX_X_", "_X_XXXX_XX", "_X_XXXXX_X", "_X_XXXXXX_", "_X_XXXXXXX",
        "_XX_X_X_XX", "_XX_X_XX_X", "_XX_X_XXX_", "_XX_X_XXXX", "_XX_XX_X_X", "_XX_XX_XX_", "_XX_XX_XXX", "_XX_XXX_XX", "_XX_XXXX_X", "_XX_XXXXX_", "_XX_XXXXXX", "_XXX_X_X_X",
        "_XXX_X_XXX", "_XXX_XX_XX", "_XXX_XXX_X", "_XXX_XXXX_", "_XXX_XXXXX", "_XXXX_X_XX", "_XXXX_XX_X", "_XXXX_XXXX", "_XXXXX_X_X", "_XXXXX_XXX", "_XXXXXX_XX", "_XXXXXXX_X",
        "_XXXXXXXX_", "_XXXXXXXXX", "X_X_X_X_XX", "X_X_X_XX_X", "X_X_X_XXXX", "X_X_XX_X_X", "X_X_XX_XXX", "X_X_XXX_XX", "X_X_XXXX_X", "X_X_XXXXXX", "X_XX_X_XXX", "X_XX_XX_XX",
        "X_XX_XXX_X", "X_XX_XXXXX", "X_XXX_X_XX", "X_XXX_XXXX", "X_XXXX_XXX", "X_XXXXX_XX", "X_XXXXXX_X", "X_XXXXXXXX", "XX_X_X_XX_", "XX_X_X_XXX", "XX_X_XX_XX", "XX_X_XXXXX",
        "XX_XX_XXXX", "XX_XXX_XXX", "XX_XXXX_XX", "XX_XXXXXXX", "XXX_X_XXX_", "XXX_X_XXXX", "XXX_XX_XXX", "XXX_XXXXX_", "XXX_XXXXXX"
    };

    private static int[] weightIndex = {
        1, 2, 3, 3, 4, 5, 5, 5, 5, 6,
        7, 4, 6, 7, 9, 5, 6, 7, 7, 7,
        8, 9, 5, 5, 7, 7, 10, 5, 5, 6,
        7, 7, 6, 7, 7, 7, 8, 9, 10, 5,
        7, 6, 7, 8, 9, 5, 7, 7, 11, 6,
        6, 7, 7, 8, 8, 9, 6, 7, 7, 7,
        7, 7, 7, 8, 8, 9, 10, 11, 6, 6,
        7, 7, 8, 8, 10, 7, 8, 9, 7, 11,
        6, 6, 6, 7, 7, 6, 7, 7, 8, 8,
        8, 9, 10, 6, 6, 7, 8, 8, 8, 9,
        7, 7, 7, 7, 8, 8, 9, 10, 11, 11,
        6, 7, 7, 8, 9, 6, 7, 8, 8, 8,
        9, 11, 6, 7, 8, 8, 8, 10, 8, 9,
        7, 11, 6, 6, 7, 6, 7, 8, 8, 9,
        6, 6, 7, 7, 7, 7, 8, 8, 8, 8,
        9, 10, 11, 6, 6, 7, 7, 7, 8, 8,
        8, 8, 9, 10, 7, 8, 8, 8, 8, 9,
        8, 8, 8, 9, 9, 10, 11, 11, 11, 6,
        6, 7, 7, 7, 8, 8, 9, 7, 8, 8,
        9, 8, 8, 8, 9, 10, 11, 6, 7, 7,
        9, 8, 8, 8, 11, 8, 8, 8, 9, 10
    };
    public static final char UNOCCUPIED = '_';

    public static void initialize()
    {
        weightIndexTable_ = new int[TABLE_SIZE];
        for ( int i = 0; i < TABLE_SIZE; i++ )
            weightIndexTable_[i] = 0;

        // since applets cannot read files, I've moved the pattern data into the class Patterns.
        // only use this method if you need to read the data from a file.
        //readPatternFile();
        // only use this when changing the format
        //writePatternFile();

        // use this initialization code if not reading from a file using the above
        //System.out.println("there are "+Patterns.patternString.length+" patterns. ("+Patterns.NUM_PATTERNS+")");
        for ( int i = 0; i < PentePatterns.NUM_PATTERNS; i++ ) {
            setPatternWeightInTable( PentePatterns.patternString[i], PentePatterns.weightIndex[i] );
        }
    }

    /**
     * each pattern can be represented as a unique integer.
     * this integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable
     */
    private static int convertPatternToInt( String pattern )
    {
        StringBuffer buf = new StringBuffer( pattern );
        return convertPatternToInt( buf, 0, pattern.length() );
    }

    /**
     * each pattern can be represented as a unique integer.
     * this integer can be used like a hash for a quick lookup of the weight
     * in the weightIndexTable
     */
    public static int convertPatternToInt( StringBuffer pattern, int minpos, int maxpos )
    {
        int power = 1;
        int sum = 0;
        //int len = maxpos - minpos;

        for ( int i = maxpos - 1; i >= minpos; i-- ) {
            if ( pattern.charAt( i ) != UNOCCUPIED )
                sum += power;
            power += power;  // doubles every step thru the loop.
        }
        return sum + power;
    }

    private static void setPatternWeightInTable( String pattern, int wtIndex )
    {
        int hash = convertPatternToInt( pattern );
        //System.out.println("pattern = "+pattern+"  wtind= "+wtIndex+" hash = "+hash);
        weightIndexTable_[hash] = wtIndex;

        // also add the reversed pattern
        StringBuffer reverse = new StringBuffer( pattern );
        int len = pattern.length();
        for ( int j = 0; j < reverse.length(); j++ )
            reverse.setCharAt( j, pattern.charAt( len - j - 1 ) );
        hash = convertPatternToInt( new String( reverse ) );
        weightIndexTable_[hash] = wtIndex;
    }

    // the pattern file is fixed for pente
    // this method fills in pattern_ and weightTable_
    protected static void readPatternFile()
    {
        // Open a file of the given name.
        File file = new File( PATTERN_FILE );
        FileInputStream patternFile = null;
        int token;
        int wtIndex = 0;

        try {
            patternFile = new FileInputStream( file );
        } catch (FileNotFoundException e) {
            System.out.println( "file " + PATTERN_FILE + " not found" );
        }
        InputStreamReader iStreamReader = new InputStreamReader( patternFile );
        BufferedReader inData = new BufferedReader( iStreamReader );
        StreamTokenizer inStream = new StreamTokenizer( inData );
        inStream.commentChar( '#' );
        inStream.slashSlashComments( true );
        inStream.wordChars( '_', '_' + 1 );

        try {
            // the first entry in the file should be the number of patterns
            token = inStream.nextToken();
            int numPatterns = (int) (inStream.nval);
            patterns_ = new String[numPatterns];
            String pattern;
            for ( int i = 0; i < numPatterns; i++ ) {
                token = inStream.nextToken(); // must be TT_WORD
                if ( token == inStream.TT_WORD ) {
                    patterns_[i] = inStream.sval;
                }
                else
                    GameContext.log(0,  "unexpected token type = " + token + "   nval = " + inStream.nval );
                token = inStream.nextToken(); // must be TT_NUMBER
                if ( token == inStream.TT_NUMBER ) {
                    wtIndex = (int) (inStream.nval);
                }
                else
                    GameContext.log(0, "unexpected token type = " + token + "   sval = " + inStream.sval );

                pattern = patterns_[i];
                setPatternWeightInTable( pattern, wtIndex );

                //System.out.println("pattern "+i+"= "+patterns_[i]+"  wtind= "+wtIndex);
            }
            iStreamReader.close();
        } catch (IOException e) {
            GameContext.log(0,  "error occurred while reading " + PATTERN_FILE );
        }

    }

    /**
     * allow exporting the patterns and weight indices in a different format.
     * ordinarily we do not export the patterns, but sometimes we might want to
     * change the format.
     */
    protected static void writePatternFile()
    {
        // Open a file of the given name.
        String exportFile = GameContext.GAME_ROOT + "pente/Pente.export.dat";
        File file = new File( exportFile );
        FileOutputStream patternFile = null;

        try {
            patternFile = new FileOutputStream( file );
        } catch (FileNotFoundException e) {
            System.out.println( "can't open " + exportFile + " for write" );
        }
        OutputStreamWriter oStreamWriter = new OutputStreamWriter( patternFile );
        BufferedWriter outData = new BufferedWriter( oStreamWriter );
        int i;

        try {

            int numPatterns = PentePatterns.NUM_PATTERNS;
            System.out.println( "there are " + numPatterns + " patterns. " );
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
            System.out.println( "error occurred while reading " + PATTERN_FILE );
        }
    }
}
