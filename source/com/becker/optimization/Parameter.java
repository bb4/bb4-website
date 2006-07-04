package com.becker.optimization;

import com.becker.common.Util;

/**
 *  represents a general parameter to an algoritm
 *
 *  @author Barry Becker
 */
public class Parameter
{

    private double value_ = 0.0;
    private double minValue_ = 0.0;
    private double maxValue_ = 0.0;
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
    public Parameter( double val, double minVal, double maxVal, String paramName )
    {
        setValue(val);
        setMinValue(minVal);
        setMaxValue(maxVal);
        setRange(maxVal - minVal);
        setName(paramName);
        integerOnly_ = false;
    }

    public Parameter( double val, double minVal, double maxVal, String paramName, boolean intOnly )
    {
        this(val, minVal, maxVal, paramName);
        integerOnly_ = intOnly;
    }


    public Parameter copy()
    {
        Parameter p = new Parameter( this.getValue(), this.getMinValue(), this.getMaxValue(), this.getName() );
        return p;
    }

    public void setIntegerOnly(boolean intOnly)  {
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
            setValue(getValue() - increment);
            return -increment;
        }
        else {
            setValue(getValue() + increment);
            return increment;
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

    public double getValue() {
        if (isIntegerOnly())  {
            return Math.round(value_);
        }
        return value_;
    }

    public void setValue(double value) {
        this.value_ = value;
    }

    public double getMinValue() {
        return minValue_;
    }

    public void setMinValue(double minValue) {
        this.minValue_ = minValue;
    }

    public double getMaxValue() {
        return maxValue_;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue_ = maxValue;
    }

    public double getRange() {
        return range_;
    }

    public void setRange(double range) {
        this.range_ = range;
    }

    public String getName() {
        return name_;
    }

    public void setName(String name) {
        this.name_ = name;
    }
}
