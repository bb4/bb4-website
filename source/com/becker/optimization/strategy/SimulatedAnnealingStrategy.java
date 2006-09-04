package com.becker.optimization.strategy;

import com.becker.common.Util;
import com.becker.optimization.*;

/**
 * Simulated annealing optimization strategy.
 *
 * @author Barry Becker
 */
public class SimulatedAnnealingStrategy extends OptimizationStrategy
{

    // this is the number of iterations in the inner loop divided by the number of dimensions in the search space
    private static final int N = 5;
    private static final int NUM_TEMP_ITERATIONS = 8;
    // the amount to drop the temperature on each temperature iteration.
    private static final double TEMP_DROP_FACTOR = 0.5;

    // the client should really set the tempMax using setTemperatureMax before running.
    private static final double DEFAULT_TEMP_MAX = 1000;
    private double tempMax_ = DEFAULT_TEMP_MAX;


    /**
     * Constructor
     * use a harcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     * No log file specified in this constructor. (use this version if running in unsigned applet).
     * @param optimizee the thing to be optimized.
     */
    public SimulatedAnnealingStrategy( Optimizee optimizee )
    {
        super(optimizee);
    }

    /**
     * Constructor
     * @param optimizee the thing to be optimized.
     * @param optimizationLogFile the file that will record the results
     */
    public SimulatedAnnealingStrategy( Optimizee optimizee, String optimizationLogFile )
    {
        super(optimizee, optimizationLogFile);
    }


    /**
     *
     * @param tempMax the initial temperature at the start of the simulated annealing process (before cooling).
     */
    public void setMaxTemperature(double tempMax)
    {
        tempMax_ = tempMax;
    }



    /**
     * finds a local maxima.
     *
     *  The concept is based on the manner in which liquids freeze or metals recrystalize in the process of annealing.
     * In an annealing process a initially at high temperature and disordered liquid, is slowly cooled so that the system
     * is approximately in thermodynamic equilibrium at any point in the process. As cooling proceeds, the system becomes
     * more ordered and approaches a "frozen" ground state at T=0. Hence the process can be thought of as an adiabatic
     * approach to the lowest energy state. If the initial temperature of the system is too low or cooling is too fast,
     * the system may become quenched forming defects or freezing out in metastable states
     * (ie. trapped in a local minimum energy state).
     *
     * In many ways the algorithm is similar to hillclimbing.
     * The main differences are:
     *  - The next candidate solution is selected randomly within a gaussian neighborhood that shrinks
     *    with the temperature and within the current iteration.
     *  - You can actually make a move toward a solution that is worse. This allows the algorithm to
     *    move out of local optima.
     *
     * @param params the initial value for the parameters to optimize.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return the optimized params.
     */
     public ParameterArray doOptimization( ParameterArray params, double fitnessRange )
     {
         int ct = 0;
         double temperature = tempMax_;
         double tempMin = tempMax_ / Math.pow(2.0, NUM_TEMP_ITERATIONS);


         double currentFitness = 0.0;
         if (!optimizee_.evaluateByComparison())
             currentFitness = optimizee_.evaluateFitness(params);

         // store the best solution we found at any given temperature iteration and use that as the initial
         // start of the next temperature iteration.
         ParameterArray bestParams = params.copy();
         double bestFitness = currentFitness;

         do {  // temperature iteration (temperature drops each time through)
             params = bestParams;
             currentFitness = bestFitness;

             do {
                 // select a new point in the nbrhood of our current location
                 // The nbrhood we select from has a radius of r.
                 //double r = (tempMax/5.0+temperature) / (8.0*(N/5.0+ct)*tempMax);
                 double r = (temperature) / ((N+ct)*tempMax_);
                 ParameterArray newParams = params.getRandomNeighbor(r);
                 double dist = params.distance(newParams);

                 double deltaFitness = 0.0;
                 double newFitness = 0.0;
                 if (optimizee_.evaluateByComparison())
                     deltaFitness = optimizee_.compareFitness(newParams, params);
                 else {
                     newFitness = optimizee_.evaluateFitness(newParams);
                     deltaFitness = newFitness - currentFitness;
                 }

                 double probability = Math.pow(Math.E, deltaFitness/temperature);
                 if ((deltaFitness > 0) ||
                    (Math.random() < probability))  {
                     // we always select the solution if it has a better fitness,
                     // but we only select a worse solution if the second term evaluates to true.
                     if ((deltaFitness < 0) &&Math.random() < probability) {
                         System.out.println( "*** selected worse solution prob="+probability );
                     }
                     params = newParams;
                     currentFitness = newFitness;
                 }
                 if (currentFitness > bestFitness)    {
                     bestFitness = currentFitness;
                     bestParams = params.copy();
                 }

                 System.out.println("T="+temperature+" ct="+ct+" dist="+dist+" deltaFitness="+deltaFitness+"  currentFitness = "+currentFitness );
                 writeToLog(ct, currentFitness, r, dist, params, Util.formatNumber(temperature));

                 ct++;
             } while (ct < N * params.size() && !isOptimalFitnessReached(params));
             ct = 0;
             // keep halving the temperature until it reaches tempMin
             temperature *= TEMP_DROP_FACTOR;
         } while (temperature > tempMin);

         System.out.println("T="+temperature+"  currentFitness = "+bestFitness );
         writeToLog(ct, currentFitness, 0, 0, bestParams, Util.formatNumber(temperature));

         return bestParams;
     }

}
