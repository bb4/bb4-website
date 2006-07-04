package com.becker.optimization;



/**
 *  This interface needs to be implemented for any object you wish to have optimized
 *  Optimization of a class is done by the Optimizer class (following the delegation design patter)
 *
 *  @author Barry Becker
 */
public interface Optimizee
{

    /**
     *
     * If true is returned then compareFitness will be used and evaluateFitness will not
     * otherwise the reverse will be true.
     * @return return true if we evaluate the fitness by comparison
     */
    boolean evaluateByComparison();

    /**
     *  attributes a measure of fitness to the specified set of parameters.
     *  This method must return a value greater than or equal to 0.
     *  This method is used if evalueateByComparison returns false.
     *  Higher values imply better fitness.
     *  @param params the set of parameters to misc
     *  @return the fitness measure. The higher the better
     */
    double evaluateFitness( ParameterArray params );

    /**
     * Compares two sets of parameters.  Used if evaluateByCompariuson returns true.
     * @return the amount that params1 are better than params2. May be negative if params2 are better than params1.
     */
    double compareFitness( ParameterArray params1, ParameterArray params2 );

    /**
     *
     * @return the number of parameters in the search space.
     */
    int getNumParameters();
}
