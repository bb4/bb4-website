package com.becker.game.twoplayer.pente;

import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Represents a run of symbols to be evaluated on the board.
 * @author Barry Becker
 */
public class Line {

    public enum Direction {VERTICAL, HORIZONTAL, DOWN_DIAGONAL, UP_DIAGONAL}

    private static final char P1_SYMB = 'X';
    private static final char P2_SYMB = 'O';

    /** contains the symbols in the line (run) */
    StringBuilder line;

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
    public void append(BoardPosition pos)
    {
        assert (pos != null): "Cannot append at null board position.";
        if ( pos.getPiece() == null )
            line.append( PentePatterns.UNOCCUPIED );
        else if ( pos.getPiece().isOwnedByPlayer1() )
            line.append( P1_SYMB );
        else
            line.append( P2_SYMB );
    }

    /**
     *
     * @param position position in the string to compute value difference for.
     * @return the difference in worth after making a move compared with before.
     *  We need to look at it from the point of view of both sides (p1 = +, p2 = -)
     */
    public int computeValueDifference(int position)
    {
        char symb = line.charAt( position ); // the last move made
        boolean player1JustPlayed = (symb == P1_SYMB);

        int len = line.length();
        if ( len < patterns_.getMinInterestingLength() ) {
            return 0; // not an interesting pattern.
        }

        line.setCharAt( position, PentePatterns.UNOCCUPIED );
        int maxpos = len - 1;
        int oldScore = evalLine(player1JustPlayed, position, 0, maxpos);
        oldScore += evalLine(!player1JustPlayed, position, 0, maxpos);

        line.setCharAt( position, symb );
        int newScore = evalLine(player1JustPlayed, position, 0, maxpos);
        newScore += evalLine(!player1JustPlayed, position, 0, maxpos);

        return newScore - oldScore;
    }

    /**
     * Evaluate a line (vertical, horizontal, or diagonal).
     * Public for testing.
     * @param player1JustPlayed if true, then the first player just moved.
     * @param pos the position that was just played (symbol).
     * @param minpos starting pattern index in line (usually 0).
     * @param maxpos last pattern index position in line (usually one less than the line length).
     * @return the worth of a (vertical, horizontal, left diagonal, or right diagonal) line.
     */
    public int evalLine(boolean player1JustPlayed, int pos, int minpos, int maxpos)
    {
        assert pos >= minpos && pos <= maxpos;
        int length = maxpos - minpos + 1;
        if ( length < patterns_.getMinInterestingLength() )
            return 0; // not an interesting pattern.

        char opponentSymb = player1JustPlayed ? P2_SYMB : P1_SYMB;
        System.out.println("evaluating " + line.substring(minpos, maxpos +1));

        if ( (line.charAt( pos ) == opponentSymb)
                && !(pos == minpos) && !(pos == maxpos) ) {
            // first check for a special case where there was a blocking move in the
            // middle. In this case we break the string into an upper and lower
            // half and evaluate each separately.
            System.out.println("Interesting pattern: " + line + "  pos="+ pos);
            return (evalLine( player1JustPlayed, pos, minpos, pos)
                    + evalLine( player1JustPlayed, pos, pos, maxpos));
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
            return (opponentSymb == P2_SYMB) ? weight : -weight;
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
    private int getWeightIndex(char opponentSymb, int pos, int minpos, int maxpos) {

        int ct = pos;
        if ( (line.charAt( pos ) == opponentSymb) && (pos == minpos) )  {
            ct++;
        }
        else {
            while ( ct > minpos && (line.charAt( ct - 1 ) != opponentSymb)
                  && !(line.charAt( ct ) == Patterns.UNOCCUPIED && line.charAt( ct - 1 ) == Patterns.UNOCCUPIED) ) {
                ct--;
            }
        }
        int start = ct;
        ct = pos;
        if ( (line.charAt( pos ) == opponentSymb) && (pos == maxpos) )  {
            ct--;
        }
        else {
            while ( ct < maxpos && (line.charAt( ct + 1 ) != opponentSymb)
                  && !(line.charAt( ct ) == Patterns.UNOCCUPIED && line.charAt( ct + 1 ) == Patterns.UNOCCUPIED) ) {
                ct++;
            }
        }
        int stop = ct;
        System.out.println("getting wt index for " + line.substring(start, stop+1));
        int index = patterns_.getWeightIndexForPattern(line, start, stop);
        return index;
    }

    /**
     * debugging aid
     */
    public void worthDebug( Direction dir, int pos, int diff )
    {
        GameContext.log( 0,  dir + " "  + line + "  Pos: " + pos + "  difference:" + diff );
    }

    @Override
    public String toString() {
        return line.toString();
    }
}
