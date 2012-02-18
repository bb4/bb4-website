// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.GameContext;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.pente.Patterns;
import com.becker.game.twoplayer.pente.PentePatterns;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Represents a run of symbols to be evaluated on the board.
 * @author Barry Becker
 */
public class LineEvaluator {

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
        if ( length < patterns_.getMinInterestingLength() )
            return 0; // not an interesting pattern.

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
     * @return the weight for the pattern if its a recognizable patter, else return 0.
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
    protected int getWeightIndex(StringBuilder line, char opponentSymb, int pos, int minpos, int maxpos) {

        int start = getStartPosition(line, opponentSymb, pos, minpos);
        int stop = getStopPosition(line, opponentSymb, pos, maxpos);
        return patterns_.getWeightIndexForPattern(line, start, stop);
    }

    /**
     * March forward until we hit 2 blanks, an opponent piece, or the end of the line.
     * @return stop position
     */
    protected int getStopPosition(StringBuilder line, char opponentSymb, int pos, int maxpos) {
        int stop;
        stop = pos;
        if ( (line.charAt( pos ) == opponentSymb) && (pos == maxpos) )  {
            stop--;
        }
        else {
            while ( stop < maxpos && (line.charAt( stop + 1 ) != opponentSymb)
                  && !next2Unoccupied(line, stop, 1) ) {
                stop++;
            }
        }
        return stop;
    }

    /**
     * March backward until we hit 2 blanks, an opponent piece, or the end of the line.
     * @return start position
     */
    protected int getStartPosition(StringBuilder line, char opponentSymb, int pos, int minpos) {
        int start = pos;
        if ( (line.charAt( pos ) == opponentSymb) && (pos == minpos) )  {
            start++;
        }
        else {
            while ( start > minpos && (line.charAt( start - 1 ) != opponentSymb)
                  && !next2Unoccupied(line, start, -1) ) {
                start--;
            }
        }
        return start;
    }

    private boolean next2Unoccupied(StringBuilder line, int position, int dir) {
        return (line.charAt( position ) == Patterns.UNOCCUPIED && line.charAt( position + dir ) == Patterns.UNOCCUPIED);
    } 
}
