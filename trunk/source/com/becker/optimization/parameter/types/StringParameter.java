/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.parameter.types;

import com.becker.optimization.parameter.ParameterChangeListener;
import com.becker.optimization.parameter.redistribution.DiscreteRedistribution;
import com.becker.optimization.parameter.ui.ParameterWidget;
import com.becker.optimization.parameter.ui.StringParameterWidget;

import java.util.ArrayList;
import java.util.List;

/**
 *  represents a general parameter to an algorithm
 *
 *  @author Barry Becker
 */
public class StringParameter extends IntegerParameter {
    private List<String> values_;

    public StringParameter( int index, List<String> values, String paramName) {
        super(index, 0, values.size()-1, paramName);  
        values_ = values;
    }
    
    public StringParameter( Enum val, Enum[] enumValues , String paramName) {
        super(val.ordinal(), 0, enumValues.length-1, paramName);    
        List<String> values = new ArrayList<String>(enumValues.length);

        for (Enum v: enumValues) {
            values.add(v.toString());
        }
        values_ = values;        
    }
    
    public static StringParameter createDiscreteParameter(int index, List<String> values, String paramName,
                                                          int[] discreteSpecialValues,
                                                          double[] specialValueProbabilities) {
       StringParameter param = new StringParameter(index, values, paramName);
        param.setRedistributionFunction(
                new DiscreteRedistribution(values.size(), discreteSpecialValues, specialValueProbabilities));    
        return param;
    }
    
    public static StringParameter createDiscreteParameter(Enum val, Enum[] enumValues, String paramName,
                                                          Enum[] specialEnumValues, double[] specialValueProbabilities) {
       StringParameter param = new StringParameter(val, enumValues, paramName);
       int[] discSpecialValues = new int[specialEnumValues.length];
       for (int i=0; i<specialEnumValues.length; i++ ) {
           Enum e = specialEnumValues[i];
           discSpecialValues[i] = e.ordinal();
       }
       param.setRedistributionFunction(
                new DiscreteRedistribution(enumValues.length, discSpecialValues, specialValueProbabilities));    
        return param;
    }

    @Override
    public Parameter copy() {
        StringParameter p = new StringParameter( (int)getValue(), values_, getName() );
        p.setRedistributionFunction(redistributionFunction_);
        return p;
    }

    @Override
    public Object getNaturalValue() {
        return values_.get((int)getValue());
    }
    
    public List<String> getStringValues() {
       return values_;
    }

    @Override
    protected boolean isOrdered() {
        return false;
    }
   
   @Override
   public Class getType() {
        return String.class; 
    }
   
   @Override
   public ParameterWidget createWidget(ParameterChangeListener listener) {
        return new StringParameterWidget(this, listener);
    }
}