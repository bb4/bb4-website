package com.becker.optimization.parameter;

import com.becker.common.math.MultiArray;
import com.becker.common.math.Vector;
import com.becker.common.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *  represents a 1 dimensional array of parameters
 *
 *  @author Barry Becker
 */
public class ParameterArray implements Comparable<ParameterArray> {

    protected Parameter[] params_;

    /** default number of steps to go from the min to the max */
    private static final int STEPS = 10;
    private int numSteps_ = STEPS;
    protected static final Random RANDOM = new Random(123);

    /** assign a fitness (evaluation value) to this set of parameters */
    private double fitness_ = 0;


    /** Default constructor */
    protected ParameterArray() {}

    /**
     *  Constructor
     * @param params an array of params to initialize with.
     */
    public ParameterArray( Parameter[] params ) {
        params_ = params;
    }

    /**
     * constructor if all the params are DoubleParameters
     * @param vals the values for each parameter.
     * @param minVals the minimum value allowed for each parameter respectively.
     * @param maxVals the maximum value allowed for each parameter respectively.
     * @param names the display name for each parameter in the array.
     */
    public ParameterArray( double[] vals, double[] minVals, double[] maxVals, String names[]) {
        int len = vals.length;
        params_ = new Parameter[len];
        for (int i=0; i<len; i++)  {
            params_[i] = new DoubleParameter(vals[i], minVals[i], maxVals[i], names[i]);
        }
    }

    /**
     * Use this constructor if you have mixed types of parameters.
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


    /**
     * @return the number of parameters in the array.
     */
    public int size() {
        return params_.length;
    }

    public void setFitness(double value) {
        fitness_ = value;
    }

    public double getFitness() {
        return fitness_;
    }

    /**
     * @return a copy of ourselves.
     */
    public ParameterArray copy() {
        Parameter[] newParams = new Parameter[params_.length];
        for ( int k = 0; k < params_.length; k++ ) {
            newParams[k] = params_[k].copy();
        }

        ParameterArray pa = createInstance();
        pa.params_ = newParams;
        pa.setFitness(fitness_);
        pa.setNumSteps(numSteps_);
        return pa;
    }

    protected ParameterArray createInstance() {
        return new ParameterArray();
    }

    /**
     * @return the ith parameter in the array.
     */
    public Parameter get( int i ) {
        return params_[i];
    }

    /**
     * Globally sample the parameter space with a uniform distribution.
     * @param requestedNumSamples approximate number of samples to retrieve.
     *   If the problem space is small and requestedNumSamples is large, it may not be possible to return this
     *   many unique samples.
     * @return some number of unique samples.
     */
    public List<ParameterArray> findGlobalSamples(int requestedNumSamples) {
        int numDims = size();
        System.out.println("reqNumSamples=" + requestedNumSamples);
        int i;
        int[] dims = new int[numDims];
        int numSamples = 1;
        int samplingRate = (int)Math.pow((double)requestedNumSamples, 1.0/numDims);
        for ( i = 0; i < numDims; i++ ) {
            dims[i] = samplingRate;
            numSamples *= dims[i];
        }
        System.out.println("dims="+Arrays.toString(dims));
        MultiArray samples = new MultiArray( dims );
        List<ParameterArray> globalSamples = new ArrayList<ParameterArray>(numSamples);
        int ct = 0;

        for ( i = 0; i < samples.getNumValues(); i++ ) {
            int[] index = samples.getIndexFromRaw( i );
            ParameterArray nextSample = this.copy();

            for ( int j = 0; j < nextSample.size(); j++ ) {
                Parameter p = nextSample.get( j );
                double increment = (p.getMaxValue() - p.getMinValue()) / (samplingRate + 1.0);
                p.setValue(increment / 2.0 + index[j] * increment);
            }

            globalSamples.add(nextSample);
        }
        return globalSamples;
    }

    /**
     * @return the distance between this parameter array and another.
     * sqrt(sum of squares)
     */
    public double distance( ParameterArray pa )  {
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
    public void add( Vector vec ) {

        assert ( vec.size() == params_.length): "Parameter vec has magnitude " + vec.size()+ ", expecting " + params_.length ;
        for ( int i = 0; i < params_.length; i++ ) {
            params_[i].setValue(params_[i].getValue() + vec.get(i));
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
     public ParameterArray getRandomNeighbor(double radius) {
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
     public ParameterArray getRandomSample() {
         ParameterArray nbr = this.copy();
         for ( int k = 0; k < params_.length; k++ ) {
             Parameter newPar = nbr.get(k);
             newPar.setValue(newPar.getMinValue() + RANDOM.nextDouble() * newPar.getRange());
             assert (newPar.getValue() < newPar.getMaxValue() && newPar.getValue() > newPar.getMinValue()):
                     "newPar "+newPar.getValue()+" not between "+newPar.getMinValue()+" and  "+newPar.getMaxValue();
         }

         return nbr;
     }


    /**
     *
     * @return a new double array the same magnitude as the parameter list
     */
    public Vector asVector()  {
        return new Vector(this.size());
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
        StringBuilder sb = new StringBuilder("fitness = "+this.getFitness()+'\n');
        sb.append("parameter[0] = ").append(params_[0].toString());
        for ( int i = 1; i < params_.length; i++ ) {
            sb.append( '\n' );
            sb.append("parameter[").append(i).append("] = ").append(params_[i].toString()).append("; ");
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
            sb.append(Util.formatNumber(params_[i].getValue())).append(", ");
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
        return (diff > 0)? 1 :  0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterArray that = (ParameterArray) o;

        if (!Arrays.equals(params_, that.params_)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return params_ != null ? Arrays.hashCode(params_) : 0;
    }
}
