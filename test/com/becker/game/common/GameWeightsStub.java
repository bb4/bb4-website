/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.common;


import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.optimization.parameter.DoubleParameter;
import com.becker.optimization.parameter.NumericParameterArray;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Stub GameWeights for testing
 *
 * @author Barry Becker
 */
public class GameWeightsStub extends GameWeights {

    /** not really used. */
    private static final Parameter[] PARAMS = {new DoubleParameter(1, 0, 10, "paramName")};


    public GameWeightsStub() {
        super(new NumericParameterArray(PARAMS));
    }

}

