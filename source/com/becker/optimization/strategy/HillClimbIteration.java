package com.becker.optimization.strategy;


import com.becker.common.math.Vector;
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

    Vector delta;
    Vector fitnessDelta;
    Vector gradient;
    Vector oldGradient;

    HillClimbIteration(ParameterArray params) {
        delta = params.asVector();
        fitnessDelta = params.asVector();
        gradient = params.asVector();
        oldGradient = params.asVector();

        // initialize the old gradient to the unit vector (any random direction will do)
        for ( int i = 0; i < params.size(); i++)
            oldGradient.set(i, 1.0);
        oldGradient = oldGradient.normalize();
    }

    double calcDotProduct() {
        double dotProduct = gradient.dot(oldGradient);
        double divisor = gradient.magnitude() * oldGradient.magnitude();
        return (divisor == 0.0) ? 1.0 : dotProduct / divisor;
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
        delta.set(i, p.increment(NUM_STEPS, 1));

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

        fitnessDelta.set(i, fwdFitness - bwdFitness);
        return sumOfSqs + (fitnessDelta.get(i) * fitnessDelta.get(i)) / (delta.get(i) * delta.get(i));
    }

    /** update gradient */
    void updateGradient(double jumpSize, double gradLength) {
        for ( int i = 0; i < delta.size(); i++ ) {
            gradient.set(i, jumpSize * fitnessDelta.get(i) / (delta.get(i) * gradLength));
        }
    }
}