// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.twoplayer.pente.Patterns;
import com.becker.optimization.parameter.ParameterArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a run of symbols to be evaluated on the board.
 * @author Barry Becker
 */
public class StubLineEvaluator extends LineEvaluator {

    /** recorded set of patterns that we got weights for */
    List<String> patternsChecked_;

    /**
     * Constructor
     * @param patterns patterns to lookout for.
     * @param weights weights amount to weight different patterns found in line.
     */
    public StubLineEvaluator(Patterns patterns, ParameterArray weights) {
        super(patterns, weights);
        patternsChecked_ = new LinkedList<String>();
    }


    public List<String> getPatternsChecked() {
        return patternsChecked_;
    }

    @Override
    protected String getPattern(StringBuilder line, char opponentSymb, int pos, int minpos, int maxpos) {
        String pattern = super.getPattern(line, opponentSymb, pos, minpos, maxpos);
        patternsChecked_.add(pattern);
        return pattern;
    }
}
