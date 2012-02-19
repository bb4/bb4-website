// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.pente.Patterns;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Represents a run of symbols to be evaluated on the board.
 * @author Barry Becker
 */
public class LineEvaluator {

    private static final int BACK = -1;
    private static final int FORWARD = 1;
    
    protected Patterns patterns_;
    private ParameterArray weights_;

    /**
     * Constructor
     * @param patterns patterns to lookout for.
     * @param weights weights amount to weight different patterns found in line.
     */
    public LineEvaluator(Patterns patterns, ParameterArray weights) {
        patterns_ = patterns;
        weights_ = weights;  
    }
    
    public int getMinInterestingLength() {
        return patterns_.getMinInterestingLength();
    }
    
    /**
     * Evaluate a line (vertical, horizontal, or diagonal).
     * 
     * @param line the line to evaluate
     * @param player1Perspective if true, then the first player just moved.
     * @param pos the position that was just played (symbol).
     * @param minpos starting pattern index in line (usually 0).
     * @param maxpos last pattern index position in line (usually one less than the line magnitude).
     * @return the worth of a (vertical, horizontal, left diagonal, or right diagonal) line.
     */
    public int evaluate(StringBuilder line, boolean player1Perspective, int pos, int minpos, int maxpos) {

        assert pos >= minpos && pos <= maxpos;
        int length = maxpos - minpos + 1;
        if ( length < patterns_.getMinInterestingLength() )  {
            return 0; // not an interesting pattern.
        }

        char opponentSymb = player1Perspective ? GamePiece.P2_SYMB : GamePiece.P1_SYMB;
        
        if ( (line.charAt( pos ) == opponentSymb)
                && !(pos == minpos) && !(pos == maxpos) ) {
            // first check for a special case where there was a blocking move in the
            // middle. In this case we break the string into an upper and lower
            // half and evaluate each separately.
            return (evaluate(line, player1Perspective, pos, minpos, pos)
                    + evaluate(line, player1Perspective, pos, pos, maxpos));
        }
        return getWeight(line, opponentSymb, pos, minpos, maxpos);
    }


    /**
     * In general, we march from the position pos in the middle towards the ends of the
     * string. Marching stops when we encounter one of the following
     * conditions:
     *  - 2 blanks in a row (@@ we may want to allow this)
     *  - an opponent's blocking piece
     *  - the end of a line.
     * @param minpos first symbol in the sting to evaluate
     * @param maxpos last symbol in the sting to evaluate
     * @return the index to use for getting the weight based on the pattern formed by this line.
     */
    private int getWeightIndex(StringBuilder line, char opponentSymb, int pos, int minpos, int maxpos) {

        String pattern = getPattern(line, opponentSymb, pos, minpos, maxpos);
        return patterns_.getWeightIndexForPattern(pattern);
    }
    
    protected String getPattern(StringBuilder line, char opponentSymb, int pos, int minpos, int maxpos) {

        int start = getEndPosition(line, opponentSymb, pos, minpos, BACK);
        int stop = getEndPosition(line, opponentSymb, pos, maxpos, FORWARD);
        return line.substring(start, stop + 1);
    }

    /**
     * @return the weight for the pattern if its a recognizable pattern, else return 0.
     */
    private int getWeight(StringBuilder line, char opponentSymb, int pos, int minpos, int maxpos) {

        int index = getWeightIndex(line, opponentSymb, pos, minpos, maxpos);

        if (index >= 0) {
            int weight = (int)weights_.get(index).getValue();
            return (opponentSymb == GamePiece.P2_SYMB) ? weight : -weight;
        } else {
            return 0;
        }
    }
    
    /**
     * March in the direction specified until we hit 2 blanks, an opponent piece, 
     * or the end of the line.
     * @return end position
     */
    protected int getEndPosition(StringBuilder line, char opponentSymb, int pos, int extremePos, int direction) {
        int end;
        end = pos;
        if ( (line.charAt( pos ) == opponentSymb) && (pos == extremePos) )  {
            end -= direction;
        }                                                                             
        else {
            while ( (direction * end < direction * extremePos) && (line.charAt( end + direction ) != opponentSymb)
                  && !next2Unoccupied(line, end, direction) ) {
                end += direction;
            }
        }
        return end;
    }

    private boolean next2Unoccupied(StringBuilder line, int position, int dir) {
        return (line.charAt( position ) == Patterns.UNOCCUPIED
             && line.charAt( position + dir ) == Patterns.UNOCCUPIED);
    } 
}
