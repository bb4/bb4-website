package com.becker.optimization.parameter;

import com.becker.optimization.parameter.ui.DoubleParameterWidget;
import com.becker.optimization.parameter.ui.ParameterWidget;
import java.util.Random;

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
    
    @Override
    public void randomizeValue(Random rand) {
        value_ = getMinValue() + rand.nextDouble() * (getRange() + 1.0);
    }

    
    @Override
    public double getValue() {
        double value = value_;
        if (redistributionFunction_ != null) {
            double v = (value_ - minValue_) / (getRange() + 1.0);
            v = redistributionFunction_.getFunctionValue(v);
            value = v * (getRange() + 0.999999999) + minValue_;
        }
            
        return (int)value;
    }  

    @Override
    public boolean isIntegerOnly() {
        return true;
    }
    
    public Object getNaturalValue() {
        return Integer.valueOf((int)this.getValue());
    }
    
    @Override
    public Class getType() {
        return int.class; 
    }
    
    public ParameterWidget createWidget(ParameterChangeListener listener) {
        return new DoubleParameterWidget(this, listener);
    }
}
