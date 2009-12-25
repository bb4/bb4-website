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
    protected TwoPlayerBoard board_;
    protected Patterns patterns_;

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
        int diff = findStraightValueDifference(row, col, numCols, weights, Line.Direction.HORIZONTAL);
        diff +=  findStraightValueDifference(row, col, numRows, weights,  Line.Direction.VERTICAL);

        diff +=  findUpDiagonalValueDifference(row, col, numRows, numCols, weights);
        diff +=  findDownDiagonalValueDifference(row, col, numRows, numCols, weights);

        return (lastMove.getValue() + diff);
    }

    /**
     * @return the difference in worth after making a move campared with before.
     */
    private int findStraightValueDifference(int row, int col,
                       int numMax, ParameterArray weights, Line.Direction dir) {

        int winLength = patterns_.getWinRunLength();
        boolean isVertical = (dir == Line.Direction.VERTICAL);

        int currentPos = isVertical? row :  col;
        int start = currentPos - winLength;
        if ( start < 1 ) {
            start = 1;
        }
        int stop = currentPos + winLength;
        if ( stop > numMax ) {
            stop = numMax;
        }

        Line line = new Line(patterns_, weights);
        for (int i = start; i <= stop; i++ ) {
            if (isVertical) {
                line.append(board_.getPosition( i, col));
            } else {
                line.append(board_.getPosition( row, i ));
            }
        }

        int position = currentPos - start;
        int diff = line.computeValueDifference(position);
        //line.worthDebug(dir, position, diff);
        return diff;
    }

    /**
     * @return weight difference for upward diagonal ( / ).
     */
    private int findUpDiagonalValueDifference(int row, int col,
                                              int numRows, int numCols,
                                              ParameterArray weights) {
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
        Line line = new Line(patterns_, weights);
        for (int i = startc; i <= stopc; i++ )
            line.append( board_.getPosition( startr - i + startc, i ) );

        int position = col - startc;
        int diff = line.computeValueDifference( position);
        //line.worthDebug(Line.Direction.UP_DIAGONAL, position, diff);
        return diff;
    }

    /**
     * @return weight difference for downward diagonal ( \ ).
     */
    private int findDownDiagonalValueDifference(int row, int col,
                                                int numRows, int numCols,
                                                ParameterArray weights) {
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

        Line line = new Line(patterns_, weights);
        for (int i = startr; i <= stopr; i++ )
            line.append( board_.getPosition( i, startc + i - startr ));

        int position = row - startr;
        int diff = line.computeValueDifference(position);
        //line.worthDebug(Line.Direction.DOWN_DIAGONAL, position, diff);
        return diff;
    }
}
