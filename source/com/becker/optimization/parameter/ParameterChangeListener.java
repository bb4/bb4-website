package com.becker.optimization.parameter;

/**
 * Implemented by classes that do something when a parameter gets changed.
 * @author Barry Becker
 */
public interface ParameterChangeListener {

    void parameterChanged(Parameter param);
}
