package com.becker.optimization;

import com.becker.optimization.parameter.ParameterArray;


/**
 * Called when the optimizer has improved
 * @author Barry Becker
 */
public interface OptimizationListener {

    void optimizerChanged(ParameterArray params); 
}
