/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.parameter;

import com.becker.common.format.FormatUtil;
import com.becker.optimization.Improvement;
import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.types.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *  represents a 1 dimensional array of parameters
 *
 *  @author Barry Becker
 */
public abstract class ParameterArray implements Comparable<ParameterArray> {

    protected Parameter[] params_;

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
        return pa;
    }

    protected abstract ParameterArray createInstance();

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
    public abstract List<ParameterArray> findGlobalSamples(int requestedNumSamples);

    /**
     * Try to find a parameterArray that is better than what we have now by evaluating using the optimizee passed in.
     * @param optimizee something that can evaluate parameterArrays.
     * @param jumpSize how far to move in the direction of improvement
     * @param lastImprovement the improvement we had most recently. May be null if none.
     * @param cache set of parameters that have already been tested. This is important for cases where the
     *   parameters are discrete and not continuous.
     * @return the improvement which contains the improved parameter array and possibly a revised jumpSize.
     */
    public abstract Improvement findIncrementalImprovement(Optimizee optimizee, double jumpSize,
                                                           Improvement lastImprovement, Set<ParameterArray> cache);

    /**
     * @return the distance between this parameter array and another.
     * sqrt(sum of squares)
     */
    public abstract double distance( ParameterArray pa );

    /**
     * @paramm radius the size of the (1 std deviation) gaussian neighborhood to select a random nbr from
     *     (relative to each parameter range).
     * @return the random nbr.
     */
    public abstract ParameterArray getRandomNeighbor(double radius);

    /**
     * @return get a completely random solution in the parameter space.
     */
    public abstract ParameterArray getRandomSample();

    public String toString() {
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
            sb.append(FormatUtil.formatNumber(params_[i].getValue())).append(", ");
        }
        sb.append(FormatUtil.formatNumber(params_[params_.length - 1].getValue()) );
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

        return Arrays.equals(params_, that.params_);
    }

    @Override
    public int hashCode() {
        return params_ != null ? Arrays.hashCode(params_) : 0;
    }
}
