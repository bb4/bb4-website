package com.becker.optimization.parameter;


import com.becker.common.math.Vector;
import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;

/**
 * private utility class for maintaining the data vectors for the iteration.
 */
class HillClimbIteration {

    Vector delta;
    Vector fitnessDelta;
    Vector gradient;
    Vector oldGradient;

    /**
     * Constructor
     * @param params current parameters
     * @param oldGradient the old steepest ascent gradietn if we know it.
     */
    HillClimbIteration(NumericParameterArray params, Vector oldGradient) {
        delta = params.asVector();
        fitnessDelta = params.asVector();
        gradient = params.asVector();
        if (oldGradient != null) {
            this.oldGradient = oldGradient;
        }
        else {
            this.oldGradient = params.asVector();

            // initialize the old gradient to the unit vector (any random direction will do)
            for ( int i = 0; i < params.size(); i++) {
                this.oldGradient.set(i, 1.0);
            }
            this.oldGradient = this.oldGradient.normalize();
        }
    }

    /**
     * Compute the squares in one of the iteration directions and add it to the running sum.
     * @return the sum of squares in one of the iteration directions.
     */
    double incSumOfSqs(int i, double sumOfSqs, Optimizee optimizee,
                       ParameterArray params, ParameterArray testParams) {

        double fwdFitness;
        double bwdFitness;

        Parameter p = testParams.get( i );
        // increment forward.
        delta.set(i, p.incrementByEps(1));

        fwdFitness = findFitnessDelta(optimizee, params, testParams);

        // revert the increment
        p.incrementByEps(-1);
        // this checks the fitness on the other side (backwards).
        p.incrementByEps(-1);

        bwdFitness = findFitnessDelta(optimizee, params, testParams);

        fitnessDelta.set(i, fwdFitness - bwdFitness);
        return sumOfSqs + (fitnessDelta.get(i) * fitnessDelta.get(i)) / (delta.get(i) * delta.get(i));
    }

    /**
     * @return the incremental change in fitness
     */
    private double findFitnessDelta(Optimizee optimizee, ParameterArray params, ParameterArray testParams) {
        double incFintess;
        if (optimizee.evaluateByComparison()) {
            incFintess = optimizee.compareFitness( testParams, params );
        }
        else{
            incFintess = optimizee.evaluateFitness( testParams );
        }
        return incFintess;
    }

    /** update gradient */
    void updateGradient(double jumpSize, double gradLength) {
        for ( int i = 0; i < delta.size(); i++ ) {
            gradient.set(i, jumpSize * fitnessDelta.get(i) / (delta.get(i) * gradLength));
        }
    }
}