package com.becker.optimization.parameter;

import com.becker.optimization.parameter.ui.ParameterWidget;
import com.becker.optimization.parameter.ui.StringParameterWidget;
import java.util.ArrayList;
import java.util.List;

/**
 *  represents a general parameter to an algorithm
 *
 *  @author Barry Becker
 */
public class StringParameter extends IntegerParameter
{
    private List<String> values_;

    public StringParameter( int index, List<String> values, String paramName)
    {        
        super(index, 0, values.size(), paramName);  
        values_ = values;
    }
    
    public StringParameter( Enum val, Enum[] enumValues , String paramName)
    {        
        super(val.ordinal(), 0, enumValues.length, paramName);    
        List<String> values = new ArrayList<String>(enumValues.length);
        int i = 0;
        for (Enum v: enumValues) {
            values.add(v.toString());
        }
        values_ = values;        
    }


    public Parameter copy()
    {
        return new StringParameter( (int)getValue(), values_, getName() );
    }

    public Object getNaturalValue() {
        return values_.get((int)getValue());
    }
    
   public List<String> getStringValues() {
       return values_;
    }
   
   public Class getType() {
        return String.class; 
    }
   
   public ParameterWidget createWidget(ParameterChangeListener listener) {
        return new StringParameterWidget(this, listener);
    }
}
