package com.becker.optimization.strategy;


import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;

/**
 * private utility class for maintaining the data vectors for the iteration.
 */
class HillClimbIteration {

    /**
     * approximate number of steps to take when marching across one of the parameter dimensions.
     * used to calculate the stepsize in a dimension direction.
     */
    private static final int NUM_STEPS = 30;

    double[] delta;
    double[] fitnessDelta;
    double[] gradient;
    double[] oldGradient;

    HillClimbIteration(ParameterArray params) {
        delta = params.createDoubleArray();
        fitnessDelta = params.createDoubleArray();
        gradient = params.createDoubleArray();
        oldGradient = params.createDoubleArray();

        // initialize the old gradient to the unit vector (any random direction will do)
        for ( int i = 0; i < params.size(); i++)
            oldGradient[i] = 1.0;
        oldGradient = ParameterArray.normalize(oldGradient);
    }

    /**
     * Compute the square in one of the iteration directions and add it to the running sum.
     * @return the sum of squares in one of the iteration directions.
     */
    double incSumOfSqs(int i, double sumOfSqs, Optimizee optimizee,
                       ParameterArray params, ParameterArray testParams) {

        double fwdFitness;
        double bwdFitness;

        Parameter p = testParams.get( i );
        // this does the increment and returns the amount incremented (forward).
        delta[i] = p.increment( NUM_STEPS, 1 );

        if (optimizee.evaluateByComparison())
            fwdFitness = optimizee.compareFitness( testParams, params );
        else
            fwdFitness = optimizee.evaluateFitness( testParams );

        // this checks the fitness on the other side (backwards).
        p.increment( NUM_STEPS, -1 );
        p.increment( NUM_STEPS, -1 );

        if (optimizee.evaluateByComparison())
            bwdFitness = optimizee.compareFitness( testParams, params );
        else
            bwdFitness = optimizee.evaluateFitness( testParams );

        fitnessDelta[i] = fwdFitness - bwdFitness;
        return sumOfSqs + (fitnessDelta[i] * fitnessDelta[i]) / (delta[i] * delta[i]);
    }

    /** update gradient */
    void updateGradient(double jumpSize, double gradLength) {
        for ( int i = 0; i < delta.length; i++ ) {
            gradient[i] = jumpSize * fitnessDelta[i] / (delta[i] * gradLength);
        }
    }
}