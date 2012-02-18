/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.twoplayer.pente.Patterns;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Creates lines. Easy to override and create mock in tests.
 * @author Barry Becker
 */
public class LineFactory {

    public LineFactory() {}

    public Line createLine(Patterns patterns, ParameterArray weights) {
        return new Line(patterns, weights);
    }
}
