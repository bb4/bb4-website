package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.twoplayer.pente.Patterns;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Creates lines. Easy to override and create mock for in tests.
 * @author Barry Becker
 */
public class LineFactory {

    public LineFactory() {}

    public Line createLine(Patterns patterns, ParameterArray weights) {
        return new Line(patterns, weights);
    }
}
