package com.becker.optimization.parameter;

import com.becker.common.math.MathUtil;
import com.becker.common.math.Vector;
import com.becker.optimization.Improvement;
import com.becker.optimization.Optimizee;

import java.lang.UnsupportedOperationException;
import java.util.*;

/**
 *  represents a 1 dimensional array of unique permuted parameters.
 *  There are no duplicates among the parameters, and this array holds them in some permuted order.
 *  This sort of parameter array could be used to represent the order of cities visited in
 *  the traveling salesman problem, for example.
 *
 *  @author Barry Becker
 */
public class PermutedParameterArray extends ParameterArray {

    /** Default constructor */
    protected PermutedParameterArray() {}

    /**
     *  Constructor
     * @param params an array of params to initialize with.
     */
    public PermutedParameterArray(Parameter[] params) {
        super(params);
    }

    /**
     * Use this constructor if you have mixed types of parameters.
     * @param params
     */
    public PermutedParameterArray(List<Parameter> params) {
        super(params);
    }

    @Override
    protected PermutedParameterArray createInstance() {
        return new PermutedParameterArray();
    }

    protected ParameterArray reverse() {
        ParameterArray paramCopy = this.copy();
        int len = size();

        for (int i=0; i<len/2; i++) {
            Parameter temp = paramCopy.params_[i];
            paramCopy.params_[i] = paramCopy.params_[len - i - 1];
            paramCopy.params_[len - i - 1] = temp;
        }
        return paramCopy;
    }
    /**
     * The distance computation will be quite different for this than a regular parameter array.
     * We want the distance to represent a measure of the amount of similarity between two permutations.
     * If there are similar runs between two permutations, then the distance should be relatively small.
     * N^2 operation, where N is the number of params.
     * @return the distance between this parameter array and another.
     */
    @Override
    public double distance( ParameterArray pa )  {
        assert ( params_.length == pa.size() );

        ParameterArray paReverse = ((PermutedParameterArray) pa).reverse();
        return Math.min(distanceAux(pa), distanceAux(paReverse));
    }

    public double distanceAux( ParameterArray pa )  {

        List<Integer> runLengths = new LinkedList<Integer>();
        int len = params_.length;
        int i = 0;
        int k;

        while (i<len) {

            Parameter param = get(i);

            // find the corresponding entry in the other parameter array. it must be there
            int j=0;
            while (j<len && !param.equals(pa.get(j)) ) {
                j++;
            }
            assert (j<len) : "Param "+  param +  " did not match any values in "+ pa;

            int ii = i;
            k = 1;
            boolean matchFound = false;
            boolean matched;
            do {
                ii = ++ii % len;
                j = ++j % len;
                k++;
                matched = this.get(ii).equals(pa.get(j));
                matchFound |= matched;
            } while (matched && k<=len);

            int runLength = k-1;

            if (matchFound) {
                runLengths.add(runLength);
            }
            i += runLength;
        }

        return calcDistance(runLengths);
    }

    /**
     * Find the distance between two permutations that have runs of the specified lengths.
     * @param runLengths list of run lengths.
     * @return the approximate distance between two permutations.
     */
    private double calcDistance(List<Integer> runLengths) {

        // careful this could overflow. If it does we may need to switch to BigInteger.
        double max = Math.pow(2, size());

        if (runLengths.isEmpty()) return max;

        double denom = 0;
        for (int run : runLengths) {
           denom += Math.pow(2, run-1);
        }
        return max / denom - 2.0;
    }

    /**
     * Create a new permutation that is not too distant from what we have now.
     * @paramm radius a indication of the amount of variation to use. 0 is none, 3 is a lot.
     *   Change Math.min(1, 10 * radius * N/100) of the entries, where N is the number of params
     * @return the random nbr.
     */
    @Override
    public PermutedParameterArray getRandomNeighbor(double radius) {

        if (size() <= 1) return this;

        int numToSwap = Math.max(1, (int)(10.0 * radius * size() / 100.0));

        PermutedParameterArray nbr = (PermutedParameterArray)this.copy();
        for ( int k = 0; k < numToSwap; k++ ) {
            int index1 = RANDOM.nextInt(size());
            int index2 = RANDOM.nextInt(size());
            while (index2 == index1) {
                index2 = RANDOM.nextInt(size());
            }
            Parameter temp =  nbr.params_[index1];
            nbr.params_[index1] = nbr.params_[index2];
            nbr.params_[index2] = temp;
        }

        return nbr;
    }

    /**
     * Globally sample the parameter space.
     * @param requestedNumSamples approximate number of samples to retrieve.
     *   If the problem space is small and requestedNumSamples is large, it may not be possible to return this
     *   many unique samples.
     * @return some number of unique samples.
     */
    @Override
    public List<ParameterArray> findGlobalSamples(int requestedNumSamples) {

        // Divide by 2 because it does not matter which param we start with.
        // See page 13 in How to Solve It.
        long numPermutations = MathUtil.factorial(size()) / 2;

        // if the requested number of samples is close to the total number of permutations,
        // then we could just enumerate the permutations.
        double closeFactor = 0.7;
        int numSamples = requestedNumSamples;

        if (requestedNumSamples > closeFactor *numPermutations) {

            System.out.println("Warning: samples requested approaching num permutations. Reducing to "
                    + closeFactor * numPermutations);
            numSamples = (int)(closeFactor * numPermutations);
        }

        List<ParameterArray> globalSamples = new ArrayList<ParameterArray>(numSamples);

        while (globalSamples.size() < numSamples) {

            ParameterArray nextSample = this.getRandomSample();
            if (!globalSamples.contains(nextSample)) {
                globalSamples.add(nextSample);
            }
        }
        return globalSamples;
    }

    /**
     * {@inheritDoc}
     * Try swapping parameters randomly until we find an improvement (if we can);
     */
    @Override
    public Improvement findIncrementalImprovement(Optimizee optimizee, double jumpSize,
                                                  Improvement lastImprovement, Set<ParameterArray> cache) {
        int maxTries = 1000;
        int numTries = 0;
        double fitnessDelta;
        Improvement improvement = new Improvement(this, 0, jumpSize);

        do {
            PermutedParameterArray nbr = getRandomNeighbor(jumpSize);
            fitnessDelta = 0;

            if (!cache.contains(nbr)) {
                cache.add(nbr);
                if (optimizee.evaluateByComparison()) {
                    fitnessDelta = optimizee.compareFitness(nbr, this);
                } else {
                    double fitness = optimizee.evaluateFitness(nbr);
                    fitnessDelta = fitness - getFitness();
                    nbr.setFitness(fitness);
                }

                if (fitnessDelta > 0) {
                    improvement = new Improvement(nbr, fitnessDelta, jumpSize);
                }
            }
            numTries++;

        }  while (fitnessDelta <= 0 && numTries < maxTries);

        return improvement;
    }

    /**
     * @return get a completely random solution in the parameter space.
     */
    @Override
    public ParameterArray getRandomSample() {

        List<Parameter> theParams = Arrays.asList(params_);
        Collections.shuffle(theParams, RANDOM);

        Parameter[] newParams = new Parameter[params_.length];
        for ( int k = 0; k < params_.length; k++ ) {
            Parameter newParam = theParams.get(k).copy();
            newParams[k] = newParam;
        }

        return new PermutedParameterArray(newParams);
    }

}
