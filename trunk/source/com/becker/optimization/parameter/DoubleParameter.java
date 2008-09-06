package com.becker.optimization.parameter;

import com.becker.optimization.parameter.ui.DoubleParameterWidget;
import com.becker.optimization.parameter.ui.ParameterWidget;

/**
 *  represents a double (i.e. floating point) parameter to an algorithm
 *
 *  @author Barry Becker
 */
public class DoubleParameter extends AbstractParameter
{

    /**
     *  Constructor
     * @param val the initial or assign parameter value
     * @param minVal the minimum value that this parameter is allowed to take on
     * @param maxVal the maximum value that this parameter is allowed to take on
     * @param paramName of the parameter
     */
    public DoubleParameter( double val, double minVal, double maxVal, String paramName )
    {
        super(val, minVal, maxVal, paramName, false);    
    }

    public Parameter copy()
    {
        return new DoubleParameter( getValue(), getMinValue(), getMaxValue(), getName() );
    }

    public Object getNaturalValue() {
        return Double.valueOf(this.getValue());
    }
    
    public boolean isIntegerOnly() {
        return false;
    }
    
    public Class getType() {
        return float.class; 
    }
    
    public ParameterWidget createWidget(ParameterChangeListener listener) {
        return new DoubleParameterWidget(this, listener);
    }
}
