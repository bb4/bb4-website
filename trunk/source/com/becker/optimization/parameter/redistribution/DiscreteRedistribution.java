package com.becker.optimization.parameter.redistribution;

/**
 * Integer case of UniformRedistributionFunction.
 * 
 * @author Barry Becker
 */
public class DiscreteRedistribution extends UniformRedistribution {

    private double numDiscretes;
    
    /**
     * If you have just a purely uniform distribution you do not need to add any redistribution function as that is the default.
     * Use this function thoug, if you have uniform except for a few special values.
     * If the sum of all special value probabilities is equal to one, then no non-special values are ever selected.
     * @param specialValues certain values that are more likely to occur than other regualr values. (must be in increasing order)
     * @param specialValueProbabilities sum of all special value probabilities must be less than or equal to one.
     */
    public DiscreteRedistribution(int numValues, int[] discreteSpecialValues, double[] discreteSpecialValueProbabilities) {
         
        int len = discreteSpecialValues.length;
        specialValues = new double[len];
        specialValueProbabilities = new double[len];
        numDiscretes = numValues;

        for (int i=0; i<len; i++) {
            assert discreteSpecialValues[i] < numDiscretes;
            specialValues[i] = ((double)(discreteSpecialValues[i])) / (double)(numValues-1);          
            specialValueProbabilities[i] = discreteSpecialValueProbabilities[i]; 
        }
            
        initializeFunction();       
    }
    
}
