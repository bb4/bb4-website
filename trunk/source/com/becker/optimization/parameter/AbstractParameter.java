package com.becker.optimization.parameter;

import com.becker.common.util.Util;
import java.util.Random;

/**
 *  represents a general parameter to an algorithm
 *
 *  @author Barry Becker
 */
public abstract class AbstractParameter implements Parameter
{

    protected double value_ = 0.0;
    protected double minValue_ = 0.0;
    protected double maxValue_ = 0.0;
    private double range_ = 0.0;
    private String name_ = null;
    private boolean integerOnly_ = false;

    /**
     *  Constructor
     * @param val the initial or assign parameter value
     * @param minVal the minimum value that this parameter is allowed to take on
     * @param maxVal the maximum value that this parameter is allowed to take on
     * @param paramName of the parameter
     */
    public AbstractParameter( double val, double minVal, double maxVal, String paramName )
    {
        value_ = val;
        minValue_ = minVal;
        maxValue_ = maxVal;
        range_ = maxVal - minVal;
        name_ = paramName;
        integerOnly_ = false;
    }

    public AbstractParameter( double val, double minVal, double maxVal, String paramName, boolean intOnly )
    {
        this(val, minVal, maxVal, paramName);
        integerOnly_ = intOnly;
    }

    public boolean isIntegerOnly() {
        return integerOnly_;
    }

    /**
     * increments the parameter based on the number of steps to get from one end of the range to the other.
     * If we are already at the max end of the range, then we increment in a negative direction.
     * @param numSteps of steps to get from one end of the range to the other
     * @param direction 1 for forward, -1 for backward.
     * @return the size of the increment taken
     */
    public double increment( int numSteps, int direction )
    {
        double increment = direction * (getMaxValue() - getMinValue()) / numSteps;
        if (isIntegerOnly()) {
            increment = Math.max((int) increment, 1);
        }
        if ( (getValue()+increment > getMaxValue()) || (getValue()+increment < getMinValue()) ) {
            value_ = (getValue() - increment);
            return -increment;
        }
        else {
            value_ = (getValue() + increment);
            return increment;
        }
    }

    /**
     * 
     * @param r  the size of the (1 std deviation) gaussian neighborhood to select a random nbr from
     *     r is relative to each parameter range (in other words scaled by it).
     */
    public void tweakValue(double r, Random rand)
    {           
        if (r == 0 ) {
            return;  // no change in the param.
        }
        double prevValue = getValue();
        double change = rand.nextGaussian() * r * getRange();
        value_ = (getValue() + change);
        if (getValue() > getMaxValue()) {
              value_ = getMaxValue();
        }
        else if (getValue() < getMinValue()) {
             value_ = getMinValue();
        }
        if (prevValue == getValue() &&
            (prevValue == getMinValue() || prevValue == getMaxValue())) {
            value_ = (prevValue - change);
        }
   }

    public String toString()
    {
        StringBuffer sa = new StringBuffer( getName() );
        sa.append( " =" );
        sa.append( Util.formatNumber(getValue()) );
        sa.append( " [" );
        sa.append( Util.formatNumber(getMinValue()) );
        sa.append( ", " );
        sa.append( Util.formatNumber(getMaxValue()) );
        sa.append( ']' );
        return sa.toString();
    }
    
    public Class getType() {
        if (isIntegerOnly()) {
            return int.class; // Integer.TYPE;  //int.class;
        }
        else {
            return float.class; // Float.TYPE; //  float.class;
        }
    }
    
    public void setValue(double value) {
        this.value_ = value;
    }

    public double getValue() {
        if (isIntegerOnly())  {
            return Math.round(value_);
        }
        return value_;
    }  

    public double getMinValue() {
        return minValue_;
    }

    public double getMaxValue() {
        return maxValue_;
    }

    public double getRange() {
        return range_;
    }

    public String getName() {
        return name_;
    }
    
}
