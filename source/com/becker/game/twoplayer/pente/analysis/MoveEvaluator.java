/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.pente.Patterns;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Evaluates the value a recent move.
 * It does this by comparing the change in worth of the line patterns
 * vertically, horizontally, and diagonally through the new move's position.
 *
 * @author Barry Becker
*/
public class MoveEvaluator  {

    private TwoPlayerBoard board_;
    private Patterns patterns_;
    private LineFactory lineFactory_;

    /**
     * Constructor
     */
    public MoveEvaluator(TwoPlayerBoard board, Patterns patterns) {
        patterns_ = patterns;
        board_ = board;
        lineFactory_ = new LineFactory();
    }

    /**
     * Used for debugging and testing to inject something that will create mock lines.
     */
    public void setLineFactory(LineFactory factory) {
        lineFactory_ = factory;
    }
    
    /**
     *  Statically evaluate the board position.
     *  @return the lastMoves value modified by the value add of the new move.
     *   a large positive value means that the move is good from the specified players viewpoint
     */
    public int worth( Move lastMove, ParameterArray weights ) {
        TwoPlayerMove move = (TwoPlayerMove)lastMove;
        int row = move.getToRow();
        int col = move.getToCol();
        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        assert board_.getPosition(row, col).getPiece() != null :
                "There must be a piece where the last move was played ("+row+", "+col+")";
        
        // look at every string that passes through this new move to see how the value is effected.
        int diff = findStraightValueDifference(row, col, numCols, weights, Direction.HORIZONTAL);
        diff +=  findStraightValueDifference(row, col, numRows, weights, Direction.VERTICAL);

        diff +=  findUpDiagonalValueDifference(row, col, numRows, numCols, weights);
        diff +=  findDownDiagonalValueDifference(row, col, numRows, numCols, weights);

        return (lastMove.getValue() + diff);
    }

    /**
     * @return the difference in worth after making a move compared with before.
     */
    private int findStraightValueDifference(int row, int col,
                       int numMax, ParameterArray weights, Direction dir) {

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

        Line line = lineFactory_.createLine(patterns_, weights);
        for (int i = start; i <= stop; i++ ) {
            if (isVertical) {
                line.append(board_.getPosition( i, col));
            } else {
                line.append(board_.getPosition( row, i ));
            }
        }

        int position = currentPos - start;
        return line.computeValueDifference(position);
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
        Line line = lineFactory_.createLine(patterns_, weights);
        for (int i = startc; i <= stopc; i++ )
            line.append( board_.getPosition( startr - i + startc, i ) );

        int position = col - startc;
        int diff = line.computeValueDifference(position);
        //line.worthDebug(Direction.UP_DIAGONAL.name(), position, diff);
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

        Line line = lineFactory_.createLine(patterns_, weights);
        for (int i = startr; i <= stopr; i++ )
            line.append( board_.getPosition( i, startc + i - startr ));

        int position = row - startr;
        int diff = line.computeValueDifference(position);
        //line.worthDebug(Line.Direction.DOWN_DIAGONAL.name(), position, diff);
        return diff;
    }
}
