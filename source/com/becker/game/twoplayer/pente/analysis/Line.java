/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
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
public class Line {

    /** contains the symbols in the line (run) */
    protected StringBuilder line;

    protected Patterns patterns_;
    private ParameterArray weights_;

    /**
     * Constructor
     * @param patterns patterns to lookout for.
     * @param weights weights amount to weight different patterns found in line.
     */
    public Line(Patterns patterns, ParameterArray weights) {
        patterns_ = patterns;
        weights_ = weights;
        line = new StringBuilder();
    }

    /**
     * Extend the line by an additional position.
     * @param pos the position to extend ourselves by.
     */
    public void append(BoardPosition pos) {
        assert (pos != null): "Cannot append at null board position.";
        if ( pos.getPiece() == null )  {
            line.append( PentePatterns.UNOCCUPIED );
        }
        else {
            line.append( pos.getPiece().getSymbol() );
        }
    }

    /**
     * We return the difference in value between how the board looked before the
     * move was played (from both points of view) to after the move was played
     * (from both points of view. Its important that we look at it from both
     * sides because creating a near win is noticed from the moving players point of view
     * while blocks are noted from the opposing viewpoint.
     *
     * @param position position in the string to compute value difference for.
     * @return the difference in worth after making a move compared with before.
     *
     */
    public int computeValueDifference(int position) {

        char symb = line.charAt( position ); // the last move made
        boolean player1Perspective = (symb == GamePiece.P1_SYMB);

        int len = line.length();
        if ( len < patterns_.getMinInterestingLength() ) {
            return 0; // not an interesting pattern.
        }

        line.setCharAt( position, PentePatterns.UNOCCUPIED );
        int maxpos = len - 1;

        int oldScore = evalLine(player1Perspective, position, 0, maxpos);
        oldScore += evalLine(!player1Perspective, position, 0, maxpos);

        line.setCharAt( position, symb );
        int newScore = evalLine(player1Perspective, position, 0, maxpos);
        newScore += evalLine(!player1Perspective, position, 0, maxpos);

        return (newScore - oldScore);
    }

    /**
     * Evaluate a line (vertical, horizontal, or diagonal).
     * Public for testing.
     * @param player1Perspective if true, then the first player just moved.
     * @param pos the position that was just played (symbol).
     * @param minpos starting pattern index in line (usually 0).
     * @param maxpos last pattern index position in line (usually one less than the line magnitude).
     * @return the worth of a (vertical, horizontal, left diagonal, or right diagonal) line.
     */
    public int evalLine(boolean player1Perspective, int pos, int minpos, int maxpos) {

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
            return (evalLine( player1Perspective, pos, minpos, pos)
                    + evalLine( player1Perspective, pos, pos, maxpos));
        }
        return getWeight(opponentSymb, pos, minpos, maxpos);
    }

    /**
     * @return the weight for the pattern if its a recognizable patter, else return 0.
     */
    private int getWeight(char opponentSymb, int pos, int minpos, int maxpos) {

        int index = getWeightIndex(opponentSymb, pos, minpos, maxpos);
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
    protected int getWeightIndex(char opponentSymb, int pos, int minpos, int maxpos) {

        int start = getStartPosition(opponentSymb, pos, minpos);
        int stop = getStopPosition(opponentSymb, pos, maxpos);
        return patterns_.getWeightIndexForPattern(line, start, stop);
    }

    /**
     * March forward until we hit 2 blanks, an opponent piece, or the end of the line.
     * @return stop position
     */
    protected int getStopPosition(char opponentSymb, int pos, int maxpos) {
        int stop;
        stop = pos;
        if ( (line.charAt( pos ) == opponentSymb) && (pos == maxpos) )  {
            stop--;
        }
        else {
            while ( stop < maxpos && (line.charAt( stop + 1 ) != opponentSymb)
                  && !next2Unoccupied(stop, 1) ) {
                stop++;
            }
        }
        return stop;
    }

    /**
     * March backward until we hit 2 blanks, an opponent piece, or the end of the line.
     * @return start position
     */
    protected int getStartPosition(char opponentSymb, int pos, int minpos) {
        int start = pos;
        if ( (line.charAt( pos ) == opponentSymb) && (pos == minpos) )  {
            start++;
        }
        else {
            while ( start > minpos && (line.charAt( start - 1 ) != opponentSymb)
                  && !next2Unoccupied(start, -1) ) {
                start--;
            }
        }
        return start;
    }

    private boolean next2Unoccupied(int position, int dir) {
        return (line.charAt( position ) == Patterns.UNOCCUPIED && line.charAt( position + dir ) == Patterns.UNOCCUPIED);
    }

    /**
     * debugging aid
     */
    public void worthDebug( String dir, int pos, int diff ) {
        GameContext.log( 0,  dir + " "  + line + "  Pos: " + pos + "  difference:" + diff );
    }

    @Override
    public String toString() {
        return line.toString();
    }
}
