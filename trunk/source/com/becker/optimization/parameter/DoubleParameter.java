package com.becker.optimization.parameter;

import com.becker.optimization.parameter.redistribution.GaussianRedistribution;
import com.becker.optimization.parameter.redistribution.UniformRedistribution;
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
    public DoubleParameter( double val, double minVal, double maxVal, String paramName ) {
        super(val, minVal, maxVal, paramName, false);    
    }
    
    public static DoubleParameter createGaussianParameter(double val, double minVal, double maxVal,
                                                         String paramName, double normalizedMean, double stdDeviation) {
        DoubleParameter param = new DoubleParameter(val, minVal, maxVal, paramName);
        param.setRedistributionFunction(new GaussianRedistribution(normalizedMean, stdDeviation));    
        return param;
    }
    
    public static DoubleParameter createUniformParameter(double val, double minVal, double maxVal,
                                                         String paramName, double[] specialValues,
                                                         double[] specialValueProbabilities) {
        DoubleParameter param = new DoubleParameter(val, minVal, maxVal, paramName);
        param.setRedistributionFunction(
                new UniformRedistribution(specialValues, specialValueProbabilities));   
        return param;
    }

    public Parameter copy() {
        DoubleParameter p =  new DoubleParameter( getValue(), getMinValue(), getMaxValue(), getName() );
        p.setRedistributionFunction(redistributionFunction_);
        return p;
    }

    public Object getNaturalValue() {
        return this.getValue();
    }
    
    @Override
    public boolean isIntegerOnly() {
        return false;
    }
    
    @Override
    public Class getType() {
        return float.class; 
    }
    
    public ParameterWidget createWidget(ParameterChangeListener listener) {
        return new DoubleParameterWidget(this, listener);
    }
}
