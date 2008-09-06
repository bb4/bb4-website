package com.becker.optimization.parameter;

import com.becker.optimization.parameter.ui.DoubleParameterWidget;
import com.becker.optimization.parameter.ui.ParameterWidget;

/**
 *  represents an integer parameter to an algorithm
 *
 *  @author Barry Becker
 */
public class IntegerParameter extends AbstractParameter
{

    public IntegerParameter( int val, int minVal, int maxVal, String paramName )
    {
        super((double)val, (double)minVal, (double)maxVal, paramName, true);    
    }

    public Parameter copy()
    {
        return new IntegerParameter( (int)getValue(), (int)getMinValue(), (int)getMaxValue(), getName() );
    }

    public boolean isIntegerOnly() {
        return true;
    }
    
    public Object getNaturalValue() {
        return Integer.valueOf((int)this.getValue());
    }
    
    public Class getType() {
        return int.class; 
    }
    
    public ParameterWidget createWidget(ParameterChangeListener listener) {
        return new DoubleParameterWidget(this, listener);
    }
}
