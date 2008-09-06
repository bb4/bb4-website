package com.becker.optimization;

import com.becker.optimization.parameter.ParameterArray;



/**
 * Called when the optimizer has improved
 * @author Barry Becker Date: Jun 25, 2006
 */
public interface OptimizationListener {

    void optimizerChanged(ParameterArray params); 
}
