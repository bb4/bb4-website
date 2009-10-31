package com.becker.game.twoplayer.pente;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.parameter.ParameterArray;


/**
 * Evaluates the value a recent move.
 * It does this by comparing the change in worth of the line patterns
 * vertically, horizontally, and diagonally through the new move's position.
 *
 * @author Barry Becker
*/
public class MoveEvaluator 
{

    private static final char P1_SYMB = 'X';
    private static final char P2_SYMB = 'O';

    protected TwoPlayerBoard board_;
    protected Patterns patterns_;

    public enum Direction {VERTICAL, HORIZONTAL, DOWN_DIAGONAL, UP_DIAGONAL};

    /**
     *  Constructor
     */
    public MoveEvaluator(TwoPlayerBoard board, Patterns patterns)
    {
        patterns_ = patterns;
        board_ = board;
    }


    /**
     *  Statically evaluate the board position.
     *  @return the lastMoves value modified by the value add of the new move.
     *   a large positive value means that the move is good from the specified players viewpoint
     */
    public int worth( Move lastMove, ParameterArray weights )
    {
        TwoPlayerMove move = (TwoPlayerMove)lastMove;
        int row = move.getToRow();
        int col = move.getToCol();
        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        
        // look at every string that passes through this new move to see how the value is effected.
        int diff = findStraightValueDifference(row, col, numCols, weights, Direction.HORIZONTAL);
        diff +=  findStraightValueDifference(row, col, numRows, weights,  Direction.VERTICAL);

        diff +=  findUpDiagonalValueDifference(row, col, numRows, numCols, weights);
        diff +=  findDownDiagonalValueDifference(row, col, numRows, numCols, weights);

        return (lastMove.getValue() + diff);
    }

    /**
     * @return the difference in worth after making a move campared with before.
     */
    private int findStraightValueDifference(int row, int col,
                       int numMax, ParameterArray weights, Direction dir) {

        StringBuilder line = new StringBuilder();
        int winLength = patterns_.getWinRunLength();
        boolean isVertical = (dir == Direction.VERTICAL);

        int currentPos = isVertical? row :  col;
        int start = currentPos - winLength;
        if ( start < 1 ) {
            start = 1;
        }
        int stop = currentPos + winLength;
        if ( stop > numMax ) {
            stop = numMax;
        }

        for (int i = start; i <= stop; i++ ) {
            if (isVertical) {
                lineAppend( board_.getPosition( i, col), line );
            } else {
                lineAppend( board_.getPosition( row, i ), line );
            }
        }

        int position = currentPos - start;
        int diff = computeValueDifference( line, position, weights );
        //worthDebug(dir, line, position, diff);
        return diff;
    }

    /**
     * @return weight difference for upward diagonal ( / ).
     */
    private int findUpDiagonalValueDifference(int row, int col,
                                                                 int numRows, int numCols,
                                                                ParameterArray weights) {

        StringBuilder line = new StringBuilder();
        int winLength = patterns_.getWinRunLength();

        int startc = col - winLength; 
        int startr = row + winLength;
        if ( startc < 1 ) {
            startr += startc - 1;
            startc = 1;
        }
        if ( startr > numRows ) {
            startc += startr - numRows;
            startr = numRows;
        }
        int stopc = col + winLength;
        int stopr = row - winLength;
        if ( stopc > numCols ) {
            stopr +=  + stopc - numCols;
            stopc = numCols;
        }
        if ( stopr < 1 ) {
            stopc += stopr - 1;
        }
        line.setLength( 0 );
        for (int i = startc; i <= stopc; i++ )
            lineAppend( board_.getPosition( startr - i + startc, i ), line );

        int position = col - startc;
        int diff = computeValueDifference( line, position, weights );
        //worthDebug(Direction.UP_DIAGONAL, line, position, diff);
        return diff;
    }

    /**
     * @return weight difference for downward diagonal ( \ ).
     */
    private int findDownDiagonalValueDifference(int row, int col,
                                                                 int numRows, int numCols,
                                                                ParameterArray weights) {

        StringBuilder line = new StringBuilder();
        int winLength = patterns_.getWinRunLength();

        int startc = col - winLength;  
        int startr = row - winLength;
        if ( startc < 1 ) {
            startr += (1 - startc);
            startc = 1;
        }
        if ( startr < 1 ) {
            startc += (1 - startr);
            startr = 1;
        }
        int stopc = col + winLength;
        int stopr = row + winLength;
        if ( stopc > numCols ) {
            stopr += (numCols - stopc);
            //stopc = numCols;
        }
        if ( stopr > numRows ) {
            //stopc += (numRows - stopr);
            stopr = numRows;
        }
        line.setLength( 0 );
        for (int i = startr; i <= stopr; i++ )
            lineAppend( board_.getPosition( i, startc + i - startr ), line );

        int position = row - startr;
        int diff = computeValueDifference( line, position, weights );
        //worthDebug(Direction.DOWN_DIAGONAL, line, position, diff);
        return diff;
    }

    /**
     *  @return the difference in worth after making a move campared with before.
     *  We need to look at it from the point of view of both sides (p1 = +, p2 = -)
     */
    private int computeValueDifference( StringBuilder line, int position, ParameterArray weights )
    {
        char opponent = P2_SYMB;
        char symb = line.charAt( position ); // the last move made
        if ( symb == P2_SYMB )
            opponent = P1_SYMB;

        int len = line.length();
        if ( len < 3 ) {
            return 0; // not an interesting pattern.
        }

        line.setCharAt( position, PentePatterns.UNOCCUPIED );
        int oldScore = evalLine( line, symb, opponent, position, 0, len, weights );
        oldScore += evalLine( line, opponent, symb, position, 0, len, weights );

        line.setCharAt( position, symb );
        int newScore = evalLine( line, symb, opponent, position, 0, len, weights );
        newScore += evalLine( line, opponent, symb, position, 0, len, weights );

        return newScore - oldScore;
    }


    /**
     *Evaluate a line (vertical, horizontal, or diagonal.
     * @param line  the line of characters to evaluate.
     * @param symb  the current players symbol that just played.
     * @param opponent  symbol for the opponents's piece
     * @param pos the positionn that was just played (symbol).
     * @param minpos starting pattern index in line (usually 0).
     * @param maxpos last pattern index posisiton in line (usually the length).
     * @param weights amount to weight different patters found in line.
     * @return the worth of a (vertical, horizontal, left diagonal, or right diagonal) line.
     */
    private int evalLine( StringBuilder line, char symb, char opponentSymb,
                          int pos, int minpos, int maxpos, ParameterArray weights )
    {
        int len = maxpos - minpos;
        int ct = pos;
        if ( len < 3 )
            return 0; // not an interesting pattern.

        if ( (line.charAt( pos ) == opponentSymb)
                && !(pos == minpos) && !(pos == maxpos - 1) ) {
            // first check for a special case where there was a blocking move in the
            // middle. In this case we break the string into an upper and lower
            // half and evaluate each separately.
            return (evalLine( line, symb, opponentSymb, pos, minpos, pos + 1, weights )
                    + evalLine( line, symb, opponentSymb, pos, pos, maxpos, weights ));

        }
        // In general, we march from position in the middle towards the ends of the
        // string. Marching stops when we encounter one of the following
        // conditions:
        //  - 2 blanks in a row (@@ we may want to allow this)
        //  - an opponent's blocking piece
        //  - the end of a line.
        if ( (line.charAt( pos ) == opponentSymb) && (pos == minpos) )
            ct++;
        else {
            while ( ct > minpos && (line.charAt( ct - 1 ) != opponentSymb)
                    && !(line.charAt( ct ) == PentePatterns.UNOCCUPIED
                    && line.charAt( ct - 1 ) == PentePatterns.UNOCCUPIED) ) {
                ct--;
            }
        }
        int start = ct;
        ct = pos;
        if ( (line.charAt( pos ) == opponentSymb) && (pos == maxpos - 1) )
            ct--;
        else {
            while ( ct < (maxpos - 1) && (line.charAt( ct + 1 ) != opponentSymb)
                    && !(line.charAt( ct ) == PentePatterns.UNOCCUPIED
                    && line.charAt( ct + 1 ) == PentePatterns.UNOCCUPIED) ) {
                ct++;
            }
        }
        int stop = ct;
        int index = patterns_.getWeightIndexForPattern(line, start, stop + 1);

        if ( symb == P1_SYMB )
            return (int)weights.get(index).getValue();
        else
            return -(int)weights.get(index).getValue();
    }

    /**
     * debugging aid
     */
    private void worthDebug( Direction dir, StringBuilder line, int pos, int diff )
    {
        GameContext.log( 0,  dir + " "  + line + "  Pos: " + pos + "  difference:" + diff );
    }

    private static void lineAppend( BoardPosition pos, StringBuilder line )
    {
        assert (pos!=null): "pos "+pos+" was null!";
        if ( pos.getPiece() == null )
            line.append( PentePatterns.UNOCCUPIED );
        else if ( pos.getPiece().isOwnedByPlayer1() )
            line.append( P1_SYMB );
        else
            line.append( P2_SYMB );
    }

}
