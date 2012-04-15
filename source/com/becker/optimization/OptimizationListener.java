/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization;

import com.becker.optimization.parameter.ParameterArray;


/**
 * Called when the optimizer has improved
 * @author Barry Becker
 */
public interface OptimizationListener {

    void optimizerChanged(ParameterArray params);
}
