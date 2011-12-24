// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.go.board.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to keep track of evaluating a measure of score passed only on values at positions.
 *
 * @author Barry Becker
 */
public final class PositionalScoreArray {

    /**
     * Keep a cache of the immutable score arrays.
     * We create them only when needed, and probably will never create more than one.
     */
    private static Map<Integer, PositionalScoreArray> scoreArrays = new HashMap<Integer, PositionalScoreArray>();

    /** a lookup table of scores to attribute to the board positions when calculating the worth */
    private final float[][] positionalScores_;

    /** we assign a value to a stone based on the line on which it falls when calculating worth. */
    private static final float[] LINE_VALS = {
       -0.5f,   // first line
        0.1f,   // second line
        1.0f,   // third line
        0.9f,   // fourth line
        0.1f    // fifth line
    };

    /**
     * Factory method that will retrieve a positional array of the right size.
     * It will create it if needed, but only the first time a given size is requested.
     * @param size
     */
    public static PositionalScoreArray getArray(int size) {
        if (scoreArrays.containsKey(size)) {
            return scoreArrays.get(size);
        }
        PositionalScoreArray array = new PositionalScoreArray(size, size);
        scoreArrays.put(size, array);
        return array;
    }

    public float getValue(int i, int j) {
       return positionalScores_[i][j];
    }
    
    /**
     * Construct the Go game controller.
     */
    private PositionalScoreArray(int numRows, int numCols) {
        positionalScores_ = createPositionalScoreArray(numRows, numCols);
    }

    /**
     * Create the lookup table of scores to attribute to the board positions when calculating the worth.
     * These weights are counted more heavily at te beginning of the game.
     * @return lookup of position scores.
     */
    private float[][] createPositionalScoreArray(int numRows, int numCols) {

        int row, col, rowmin, colmin;
        float[][] positionalScore = new float[numRows + 1][numCols + 1];

        for ( row = 1; row <= numRows; row++ ) {
            rowmin = Math.min( row, numRows - row + 1 );
            for ( col = 1; col <= numCols; col++ ) {
                colmin = Math.min( col, numCols - col + 1 );
                // default neutral value
                positionalScore[row][col] = 0.0f;

                int lineNo = Math.min(rowmin, colmin);
                if (lineNo < LINE_VALS.length) {
                    if (rowmin == colmin)  {
                        // corners get emphasized
                        positionalScore[row][col] = 1.5f * (LINE_VALS[lineNo - 1]);
                    }
                    else {
                        positionalScore[row][col] = LINE_VALS[lineNo - 1];
                    }
                }
            }
        }
        return positionalScore;
    }
}