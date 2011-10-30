/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.twoplayer.pente.Patterns;
import com.becker.optimization.parameter.ParameterArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a run of symbols to be evaluated on the board.
 * but also stores the patterns checked for later analysis.
 * @author Barry Becker
 */
public class LineRecorder extends Line {

    /** recorded set of patterns that we got weights for */
    List<String> patternsChecked_;

    /**
     * Constructor
     * @param patterns patterns to lookout for.
     * @param weights weights amount to weight different patterns found in line.
     */
    public LineRecorder(Patterns patterns, ParameterArray weights) {
        super(patterns, weights);
        patternsChecked_ = new LinkedList<String>();
    }


    public List<String> getPatternsChecked() {
        return patternsChecked_;
    }

    @Override
    protected int getWeightIndex(char opponentSymb, int pos, int minpos, int maxpos) {

        int start = getStartPosition(opponentSymb, pos, minpos);  
        int stop = getStopPosition(opponentSymb, pos, maxpos);
        patternsChecked_.add(line.substring(start, stop + 1));
        return patterns_.getWeightIndexForPattern(line, start, stop);
    }

}