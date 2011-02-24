package com.becker.optimization.parameter;

import com.becker.common.util.Util;

import java.util.List;
import java.util.Random;

/**
 *  represents a 1 dimensional array of parameters
 *
 *  @author Barry Becker
 */
public class ParameterArray implements Comparable<ParameterArray>
{

    protected Parameter[] params_ = null;

    // default number of steps to go from the min to the max
    private static final int STEPS = 10;
    private int numSteps_ = STEPS;
    protected static final Random RANDOM = new Random(123);

    // assign a fitness (evaluation value) to this set of parameters
    private double fitness_ = 0;

    /**
     *  Constructor
     * @param params an array of params to initialize with.
     */
    public ParameterArray( Parameter[] params )
    {
        params_ = params;
    }

    /**
     * constructor if all the params are DoubleParameters
     * @param vals the values for each parameter.
     * @param minVals the minimum value allowed for each parameter respectively.
     * @param maxVals the maximum value allowed for each parameter respectively.
     * @param names the display name for each parameter in the array.
     */
    public ParameterArray( double[] vals, double[] minVals, double[] maxVals, String names[])
    {
        int len = vals.length;
        params_ = new Parameter[len];
        for (int i=0; i<len; i++)
        {
            params_[i] = new DoubleParameter(vals[i], minVals[i], maxVals[i], names[i]);
        }
    }
    
    /**
     * Use this construcotr if you have mixed types of parameters.
     * @param params
     */
    public ParameterArray(List<Parameter> params) {
         int len = params.size();
        params_ = new Parameter[len];
        for (int i=0; i<len; i++)
        {
            params_[i] = params.get(i);
        }
    }

    protected ParameterArray() {}

    /**
     * @return the number of parameters in the array.
     */
    public int size()
    {
        return params_.length;
    }

    public void setFitness(double value)
    {
        fitness_ = value;
    }

    public double getFitness()
    {
        return fitness_;
    }

    /**
     * @return a copy of ourselves.
     */
    public ParameterArray copy()
    {
        Parameter[] newParams = new Parameter[params_.length];
        for ( int k = 0; k < params_.length; k++ ) {
            newParams[k] = params_[k].copy();
        }

        ParameterArray pa = new ParameterArray( newParams );
        pa.setFitness(fitness_);
        return pa;
    }

    /**
     * @return the ith parameter in the array.
     */
    public Parameter get( int i )
    {
        return params_[i];
    }

    /**
     * @return the distance between this parameter array and another.
     * sqrt(sum of squares)
     */
    public double distance( ParameterArray pa )
    {
        assert ( params_.length == pa.size() );
        double sumOfSq = 0.0;
        for ( int k = 0; k < params_.length; k++ ) {
            double dif = pa.get( k ).getValue() - params_[k].getValue();
            sumOfSq += dif * dif;
        }
        return Math.sqrt( sumOfSq );
    }

    /**
     * add a vector of deltas to the parameters.
     * @param vec must be the same size as the parameter list.
     */
    public void add( double[] vec )
    {
        assert ( vec.length == params_.length): "Parameter vec has length " + vec.length + ", expecting " + params_.length ;
        for ( int i = 0; i < params_.length; i++ ) {
            params_[i].setValue(params_[i].getValue() + vec[i]);
            if ( params_[i].getValue() > params_[i].getMaxValue() ) {
                System.out.println( "Warning param " + params_[i].getName() +
                        " is exceeding is maximum value. It is being pegged to that maximum of " + params_[i].getMaxValue() );
                params_[i].setValue(params_[i].getMaxValue());
            }
            if ( params_[i].getValue() < params_[i].getMinValue() ) {
                System.out.println( "Warning param " + params_[i].getName() +
                        " is exceeding is minimum value. It is being pegged to that minimum of " + params_[i].getMinValue() );
                params_[i].setValue(params_[i].getMinValue());
            }
        }
    }


    /**
     * @paramm radius the size of the (1 std deviation) gaussian neighborhood to select a random nbr from
     *     (relative to each parameter range).
     * @return the random nbr.
     */
     public ParameterArray getRandomNeighbor(double radius)
     {
         ParameterArray nbr = this.copy();
         for ( int k = 0; k < params_.length; k++ ) {
             Parameter par = nbr.get(k);
             par.tweakValue(radius, RANDOM);
         }

         return nbr;
     }

    /**
     * @return get a completely random solution in the parameter space.
     */
     public ParameterArray getRandomSolution()
     {
         ParameterArray nbr = this.copy();
         for ( int k = 0; k < params_.length; k++ ) {
             Parameter newPar = nbr.get(k);
             newPar.setValue(newPar.getMinValue() + RANDOM.nextDouble() * newPar.getRange());
             assert (newPar.getValue() < newPar.getMaxValue() && newPar.getValue() > newPar.getMinValue()):
                     "newPar "+newPar.getValue()+" not between "+newPar.getMinValue()+" and  "+newPar.getMaxValue();
         }

         return nbr;
     }


    // ------------ these methods deal with 1 dimensional double vectors that match the length of the params array ----
    /**
     *
     * @return a new double array the same length as the parameter list
     */
    public double[] createDoubleArray()
    {
        return new double[this.size()];
    }

    /**
     * @return the dot product of 2 vectors which each must have the same length as the number of parameters.
     */
    public static double dot( double[] vec1, double[] vec2 )
    {
        assert (vec1.length == vec2.length):
                "vec1 has length " + vec1.length + ", and vec2 has length "
                + vec2.length + " expected both to have the same length ";
        double dotProduct = 0.0;
        for ( int i = 0; i < vec1.length; i++ ) {
            dotProduct += vec1[i] * vec2[i];
        }
        return dotProduct;
    }

    /**
     * @param vec vector to find length of.
     * @return length of the vector.
     */
    public static double length( double vec[] )
    {
        double sumSq = 0;
        for (final double newVar : vec) {
            sumSq += newVar * newVar;
        }
        return Math.sqrt( sumSq );

    }

    /**
     * @param vec
     * @return a vector in the same direction as vec, but with unit length.
     */
    public static double[] normalize( double vec[] )
    {
        double len = length( vec );
        for ( int i = 0; i < vec.length; i++ ) {
            vec[i] /= len;
        }
        return vec;
    }

    /**
     * @param vec
     * @return string form of vec.
     */
    public String vecToString( double[] vec )
    {
        StringBuilder buf = new StringBuilder( vec[0] + " " );
        for ( int i = 1; i < params_.length; i++ ) {
            buf.append( vec[i] + " " );
        }
        return buf.toString();
    }

    public void setNumSteps( int numSteps )
    {
        numSteps_ = numSteps;
    }

    public int getNumSteps()
    {
        return numSteps_;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder("fitness="+this.getFitness()+'\n');
        sb.append( "parameter[0] = " + params_[0].toString() );
        for ( int i = 1; i < params_.length; i++ ) {
            sb.append( '\n' );
            sb.append( "parameter[" + i + "] = " + params_[i].toString() + "; " );
        }
        return sb.toString();
    }

    /**
     * @return  the parameters in a string of Comma Separated Values.
     */
    public String toCSVString()
    {
        StringBuilder sb = new StringBuilder("");
        for ( int i = 0; i < params_.length-1; i++ ) {
            sb.append(  Util.formatNumber(params_[i].getValue()) + ", " );
        }
        sb.append(Util.formatNumber(params_[params_.length-1].getValue()) );
        return sb.toString();
    }

    /**
     * Natural ordering based on the fitness evaluation assigned to this parameter array.
     * @param p the parameter array to compare ourselves too.
     * @return -1 if we are less than p, 1 if greater than p, 0 if equal.
     */
    public int compareTo(ParameterArray p) {
        double diff = this.getFitness() - p.getFitness();
        if (diff < 0)
            return -1;
        if (diff > 0)
            return 1;
        else
            return 0;
    }
}
