// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.analysis.differencers;

import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.pente.Patterns;
import com.becker.game.twoplayer.pente.analysis.LineFactory;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Determines the difference in value between the most recent move
 * and how it was before in one specific direction
 *
 * @author Barry Becker
*/
public abstract class ValueDifferencer {

    protected TwoPlayerBoard board_;
    protected Patterns patterns_;
    protected LineFactory lineFactory_;
    protected int winLength;

    /**
     * Constructor
     */
    public ValueDifferencer(TwoPlayerBoard board, Patterns patterns, LineFactory factory) {
        patterns_ = patterns;
        board_ = board;
        lineFactory_ = factory;
        winLength = patterns_.getWinRunLength();
    }

    public abstract int findValueDifference(int row, int col, ParameterArray weights);

    /**
     * Used for debugging and testing to inject something that will create mock lines.
     */
    public void setLineFactory(LineFactory factory) {
        lineFactory_ = factory;
    }
}
