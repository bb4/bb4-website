// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.analysis.differencers;

import com.becker.game.common.board.BoardPosition;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.pente.Patterns;
import com.becker.game.twoplayer.pente.analysis.Direction;
import com.becker.game.twoplayer.pente.analysis.Line;
import com.becker.game.twoplayer.pente.analysis.LineFactory;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Determines the difference in value between the most recent move
 * and how it was before in the up/down or left right direction.
 *
 * @author Barry Becker
*/
public class StraightDifferencer extends ValueDifferencer {

    private int numMax;
    private boolean isVertical;

    /**
     * Constructor
     */
    public StraightDifferencer(TwoPlayerBoard board, Patterns patterns,
                               LineFactory lineFactory, Direction dir) {
        super(board, patterns, lineFactory);
        assert (dir == Direction.VERTICAL || dir == Direction.HORIZONTAL) : "unexpected direction" ;
        isVertical = (dir == Direction.VERTICAL);
    }

    /**
     * @return the difference in worth after making a move compared with before.
     */
    @Override
    public int findValueDifference(int row, int col, ParameterArray weights) {

        numMax = isVertical ? board_.getNumRows() : board_.getNumCols();
        int currentPos = isVertical ? row :  col;
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
                BoardPosition pos = board_.getPosition(i, col);
                assert pos != null : "invalid at i=" + i + " col=" + col + " start="+ start +" stop="+ stop +" numMax="+ numMax
                        + " isVert="+ isVertical + " winLength="+ winLength + " board.numRows=" + board_.getNumRows();
                line.append(board_.getPosition(i, col));
            } else {
                line.append(board_.getPosition(row, i));
            }
        }

        int position = currentPos - start;
        return line.computeValueDifference(position);
    }
}