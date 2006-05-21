package com.becker.optimization;

import com.becker.common.Util;

/**
 *  represents a general parameter to an algoritm
 *
 *  @author Barry Becker
 */
public class Parameter
{

    public double value = 0.0;
    public double minValue = 0.0;
    public double maxValue = 0.0;
    public double range = 0.0;
    public String name = null;

    /**
     *  Constructor
     * @param val the initial or assign parameter value
     * @param minVal the minimum value that this parameter is allowed to take on
     * @param maxVal the maximum value that this parameter is allowed to take on
     * @param paramName of the parameter
     */
    public Parameter( double val, double minVal, double maxVal, String paramName )
    {
        value = val;
        minValue = minVal;
        maxValue = maxVal;
        range = maxVal - minVal;
        name = paramName;
    }

    public Parameter copy()
    {
        Parameter p = new Parameter( this.value, this.minValue, this.maxValue, this.name );
        return p;
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
        double increment = direction * (maxValue - minValue) / numSteps;
        if ( (value+increment > maxValue) || (value+increment < minValue) ) {
            value -= increment;
            return -increment;
        }
        else {
            value += increment;
            return increment;
        }
    }


    public String toString()
    {
        StringBuffer sa = new StringBuffer( name );
        sa.append( " =" );
        sa.append( Util.formatNumber(value) );
        sa.append( " [" );
        sa.append( Util.formatNumber(minValue) );
        sa.append( ", " );
        sa.append( Util.formatNumber(maxValue) );
        sa.append( ']' );
        return sa.toString();
    }
}
