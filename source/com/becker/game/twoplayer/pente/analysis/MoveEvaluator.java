/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.pente.Patterns;
import com.becker.game.twoplayer.pente.analysis.differencers.ValueDifferencer;
import com.becker.game.twoplayer.pente.analysis.differencers.ValueDifferencerFactory;
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
    private ValueDifferencerFactory differencerFactory_;
    
    private ValueDifferencer vertDifferencer;
    private ValueDifferencer horzDifferencer;
    private ValueDifferencer upDiagDifferencer;
    private ValueDifferencer downDiagDifferencer;

    /**
     * Constructor
     */
    public MoveEvaluator(TwoPlayerBoard board, Patterns patterns) {
        patterns_ = patterns;
        board_ = board;
        setValueDifferencerFactory(
                new ValueDifferencerFactory(board_, patterns_, new LineFactory()));
    }

    /**
     * Used for testing to inject something that will create mock differencers.
     */
    public void setValueDifferencerFactory(ValueDifferencerFactory factory) {
        differencerFactory_ = factory;
        
        vertDifferencer = differencerFactory_.createValueDifferencer(Direction.VERTICAL);
        horzDifferencer = differencerFactory_.createValueDifferencer(Direction.HORIZONTAL);
        upDiagDifferencer = differencerFactory_.createValueDifferencer(Direction.UP_DIAGONAL);
        downDiagDifferencer = differencerFactory_.createValueDifferencer(Direction.DOWN_DIAGONAL);
    }
    
    /**
     * Statically evaluate the board position.
     * @return the lastMoves value modified by the value add of the new move.
     *  a large positive value means that the move is good from the specified players viewpoint
     */
    public int worth( Move lastMove, ParameterArray weights ) {
        TwoPlayerMove move = (TwoPlayerMove)lastMove;
        int row = move.getToRow();
        int col = move.getToCol();
        assert board_.getPosition(row, col).getPiece() != null :
                "There must be a piece where the last move was played ("+row+", "+col+")";
        
        // look at every string that passes through this new move to see how the value is effected.
        int diff;
        diff = horzDifferencer.findValueDifference(row, col, weights);
        diff += vertDifferencer.findValueDifference(row, col, weights);
        diff += upDiagDifferencer.findValueDifference(row, col, weights);
        diff += downDiagDifferencer.findValueDifference(row, col, weights);

        return (lastMove.getValue() + diff);
    }
}
