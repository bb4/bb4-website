package com.becker.optimization;

import com.becker.common.MultiArray;


/**
 *  Hill climbing optimization strategy.
 *
 * @author Barry Becker
 */
public class GlobalSampleStrategy extends OptimizationStrategy
{

    private static final int DEFAULT_SAMPLE_REATE = 3;
    //the user should set this explicitly.
    int samplingRate_ = DEFAULT_SAMPLE_REATE;


    /**
     * Constructor
     * No log file specified in this constructor. (use this version if running in unsigned applet).
     * @param optimizee the thing to be optimized.
     */
    public GlobalSampleStrategy( Optimizee optimizee )
    {
        super(optimizee);
    }

    /**
     * Constructor
     * @param optimizee the thing to be optimized.
     * @param optimizationLogFile the file that will record the results
     */
    public GlobalSampleStrategy( Optimizee optimizee, String optimizationLogFile )
    {
        super(optimizee, optimizationLogFile);
    }


    /**
     * @param samplingRate the rate at which to sample along each dimension when trying guesses globally.
     */
    public void setSamplingRate(int samplingRate)
    {
        if (samplingRate<1 || samplingRate>1000) {
           assert false: "invalid sampling rate "+samplingRate;
        }
        samplingRate_ = samplingRate;
    }



    /**
     * Sparsely sample the global space and return the best of the samples.
     * If the number of dimensions is large, you must use a very small number of samples per dimension
     * since the number of samples tested is equal to samplesPerDim ^ numDims.
     * For example if you have 8 dimensions and samplesPerDim = 4, then the
     * number of samples checked will be 4^8 = 65,536
     * If the number of samples for a dimension is 3 then the samples look like the following:
     *  Min|--X----X----X--|Max
     *
     * Doing this sampling before pursuing a search strategy increases the chance
     * that you will find the global maxima. It does not guarantee it, because the space
     * you are sampling may have a high frequency of peaks and valleys.
     *
     * @param params the params to compare evaluation against if we evaluate BY_COMPARISON
     * @return best solution found using global sampling
     */
    public ParameterArray doOptimization( ParameterArray params )
    {
        int numDims = params.size();
        double bestFitness = -100000000;
        ParameterArray testParams = params.copy();
        ParameterArray bestParams = params.copy();

        int i;
        int[] dims = new int[numDims];
        for ( i = 0; i < testParams.size(); i++ ) {
            dims[i] = samplingRate_;
        }
        MultiArray samples = new MultiArray( dims );

        for ( i = 0; i < samples.getNumValues(); i++ ) {
            int[] index = samples.getIndexFromRaw( i );
            String hashKey = samples.getIndexKey( index );
            for ( int j = 0; j < testParams.size(); j++ ) {
                Parameter p = testParams.get( j );
                double increment = (p.maxValue - p.minValue) / (samplingRate_ + 1.0);
                p.value = increment / 2.0 + index[j] * increment;
            }
            double fitness = 0.0;
            if (optimizee_.evaluateByComparison())
                fitness = optimizee_.compareFitness( testParams, params );
            else
                fitness = optimizee_.evaluateFitness( testParams );
            System.out.println( "key = " + hashKey + "  fitness=" + fitness );
            if ( fitness > bestFitness ) {
                bestFitness = fitness;
                bestParams = testParams.copy();
            }
        }
        return bestParams;
    }


}
