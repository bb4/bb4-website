/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.parameter;

import com.becker.optimization.parameter.types.Parameter;

/**
 * Implemented by classes that do something when a parameter gets changed.
 * @author Barry Becker
 */
public interface ParameterChangeListener {

    void parameterChanged(Parameter param);
}
