package com.becker.optimization;

import com.becker.common.Util;

import java.util.*;

/**
 * Genetic Algorithm (evolutionary) optimization strategy.
 * Many different strategies are possible to alter the population for each successive iteration.
 * The 2 primary ones that I use here are unary mutation and cross-over.
 * See Chapter 6 in "How to Solve it: Modern Heuristics" for more info.
 *
 * @author Barry Becker
 */
public class GeneticSearchStrategy extends OptimizationStrategy
{

    // The population size will be this number raised to the number of dimensions power.
    private static final int POPULATION_SIZE_PER_DIM = 4;
    // but never exceed this amount
    private static final int POPULATION_MAX = 100;

    // the amount to decimate the parent population by on each iteration
    private static final double CULL_FACTOR = 0.9;
    private static final double NBR_RADIUS = .02;
    private static final double NBR_RADIUS_SHRINK_FACTOR = .9;
    private static final double NBR_RADIUS_SOFTENER = 5.0;


    // this prevents us from running forever.
    private static final int MAX_ITERATIONS = 100;

    // stop when the avg population score does not improve by better than this
    private double improvementEps_ = .000000000001;
    //
    private double nbrRadius_ = NBR_RADIUS;

    // this is the number of members to be maintained in the population at any time.
    private int populationSize_;

    private boolean useCrossOver_ = false;


    /**
     * Constructor
     * use a harcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     * No log file specified in this constructor. (use this version if running in unsigned applet).
     * @param optimizee the thing to be optimized.
     */
    public GeneticSearchStrategy( Optimizee optimizee )
    {
        super(optimizee);
    }

    /**
     * Constructor
     * @param optimizee the thing to be optimized.
     * @param optimizationLogFile the file that will record the results
     */
    public  GeneticSearchStrategy( Optimizee optimizee, String optimizationLogFile )
    {
        super(optimizee, optimizationLogFile);
    }


    /**
     *
     * @param useCrossOver if true then create new population members using genetic crossover between parents.
     */
    public void setUseCrossOver(boolean useCrossOver)
    {
         useCrossOver_ = useCrossOver;
    }


    /**
     *
     * @param eps stop when things don't improve by more than this on successive iterations.
     */
    public void setImprovementEpsilon(double eps)
    {
        improvementEps_ = eps;
    }

    /**
     * finds a local maxima using a genetic algorithm (evolutionary) search.
     * We stop iterating as soon as the average evaluation score of the population
     * does not significantly improve.
     *
     * @param params the initial value for the parameters to optimize.
     * @return the optimized params.
     */
     public ParameterArray doOptimization( ParameterArray params )
     {
         int ct = 0;
         ParameterArray currentBestParams;
         ParameterArray lastBestParams;
         double deltaFitness = 0;

         populationSize_ = Math.min(POPULATION_MAX, (int)Math.pow(POPULATION_SIZE_PER_DIM, params.size()));

         // create an initial population based on params and POPULATION_SIZE-1 other random solutions.
         List population = new LinkedList();

         population.add(params);
         for (int i=1; i<populationSize_; i++) {
             population.add(i, params.getRandomNeighbor(1.6));
         }

         // EVALUATE POPULATION
         lastBestParams = evaluatePopulation(population, params);


         // each iteration represents a new generation of the population
         do {
             // sort the population according to the fitness of members.
             Collections.sort(population);
             Collections.reverse(population);

             printPopulation(population);

             // throw out the bottom CULL_FACTOR*populationSize_ members - keeping the cream of the crop.
             // then replace those culled with unary variations of those (now parents) that remain.
             // @@ add option to do cross-over variations too.
             int keepSize = Math.max(1,  (int)(populationSize_*(1.0 - CULL_FACTOR)));
             for (int j=populationSize_-1; j>=keepSize; j--) {
                 population.remove( j );
             }

             int k = keepSize;
             int m = 0;
             while ( k < populationSize_ ) {
                 // loop over the keepers until all replacements found
                 m = k % keepSize;
                 // do the best one 2wice to avoid  terminating
                 // too quickly in the event that we got a very good fitness score on an early iteration
                 if (m==keepSize-1)
                     m=0;
                 ParameterArray p = (ParameterArray)population.get(m);
                 // add a permutaion of one of the keepers
                 // we multiply the radius by k because we want the worse ones to have
                 // higher variability.
                 population.add(k, p.getRandomNeighbor((m+NBR_RADIUS_SOFTENER)/NBR_RADIUS_SOFTENER * nbrRadius_));
                 k++;
             }
             nbrRadius_ *= NBR_RADIUS_SHRINK_FACTOR;


             // EVALUATE POPULATION
             currentBestParams = evaluatePopulation(population, lastBestParams);

             deltaFitness = (currentBestParams.getFitness() - lastBestParams.getFitness());

             System.out.println(" ct="+ct+"  nbrRadius_="+nbrRadius_+" deltaFitness="+deltaFitness+"  currentFitness = "+ currentBestParams.getFitness());
             writeToLog(ct, currentBestParams.getFitness(), nbrRadius_, deltaFitness, params, "---");
             lastBestParams = currentBestParams;

             ct++;

         } while ( (deltaFitness > improvementEps_) && (ct < MAX_ITERATIONS));

         writeToLog(ct, currentBestParams.getFitness(), 0, 0, currentBestParams, Util.formatNumber(ct));

         return currentBestParams;
     }


    /**
     * Note: this method assigns a fitness value to each member of the population.
     *
     * @param population the population to evaluate
     * @param params the best solution from the previous iteration
     * @return the new best solution.
     */
    private ParameterArray evaluatePopulation(List population, ParameterArray params)
    {
        ParameterArray bestFitness = params;

        // now evaluate the members of the population - either directly, or by
        // comparing them against the initial params value passed in (including params).
        for (int i=0; i<population.size(); i++) {
            ParameterArray p = (ParameterArray)population.get(i);

            double fitness = 0;
            if (optimizee_.evaluateByComparison())
                fitness =  optimizee_.compareFitness(p, params);
            else
                fitness =  optimizee_.evaluateFitness(p);

            p.setFitness(fitness);
            if (p.getFitness() > bestFitness.getFitness())
                bestFitness = p;
        }
        return bestFitness;
    }

    private static final void printPopulation(List population)
    {
        for (int i=0; i<population.size(); i++)
            System.out.println( i+": "+ population.get(i).toString());
        System.out.println( "" );
    }
}
