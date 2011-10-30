/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.strategy;

import com.becker.common.format.FormatUtil;
import com.becker.optimization.Optimizee;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Genetic Algorithm (evolutionary) optimization strategy.
 * Many different strategies are possible to alter the population for each successive iteration.
 * The 2 primary ones that I use here are unary mutation and cross-over.
 * See Chapter 6 in "How to Solve it: Modern Heuristics" for more info.
 *
 * @author Barry Becker
 */
public class GeneticSearchStrategy extends OptimizationStrategy {

    /** The population size will be this number raised to the number of dimensions power (up to POP_MAX). */
    private static final int POPULATION_SIZE_PER_DIM = 4;
    /** but never exceed this amount  */
    private static final int POPULATION_MAX = 4000;

    // the amount to decimate the parent population by on each iteration
    private static final double CULL_FACTOR = 0.9;
    private static final double NBR_RADIUS = 0.03;
    private static final double NBR_RADIUS_SHRINK_FACTOR = 0.9;
    private static final double NBR_RADIUS_SOFTENER = 5.0;
    private static final double INITIAL_RADIUS = 1.5;


    /** this prevents us from running forever.  */
    private static final int MAX_ITERATIONS = 100;

    /** stop when the avg population score does not improve by better than this  */
    private static final double DEFAULT_IMPROVEMENT_EPS = 0.000000000001;

    /** radius to look for neighbors in  */
    private double nbrRadius_ = NBR_RADIUS;

    /** this is the number of members to be maintained in the population at any time. */
    private int populationSize_;

    /** If crossover breeding of genetic material is used. */
    private boolean useCrossOver_ = false;

    /** if we don't improve by at least this amount between iterations, terminate.  */
    protected double improvementEpsilon_ = DEFAULT_IMPROVEMENT_EPS;


    /**
     * Constructor
     * use a hardcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     * @param optimizee the thing to be optimized.
     */
    public GeneticSearchStrategy( Optimizee optimizee ) {
        super(optimizee);
    }

    /**
     *
     * @param useCrossOver if true then create new population members using genetic crossover between parents.
     */
    public void setUseCrossOver(boolean useCrossOver) {
         useCrossOver_ = useCrossOver;
    }

    public void setImprovementEpsilon(double eps) {
        improvementEpsilon_ = eps;
    }


    /**
     * finds a local maxima using a genetic algorithm (evolutionary) search.
     * We stop iterating as soon as the average evaluation score of the population
     * does not significantly improve.
     *
     * @param params the initial value for the parameters to optimize.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return the optimized params.
     */
     @Override
     public ParameterArray doOptimization( ParameterArray params, double fitnessRange) {

         ParameterArray lastBest;

         populationSize_ = Math.min(POPULATION_MAX, (int)Math.pow(POPULATION_SIZE_PER_DIM, params.size()));

         // create an initial population based on params and POPULATION_SIZE-1 other random candidate solutions.
         List<ParameterArray> population = new LinkedList<ParameterArray>();
         population.add(params);
         for (int i=1; i<populationSize_; i++) {
             population.add(i, params.getRandomNeighbor(INITIAL_RADIUS));
         }

         // EVALUATE POPULATION
         lastBest = evaluatePopulation(population, params);

         return findNewBest(params, lastBest, population);
     }

    /**
     * Find the new best candidate.
     * @return the new best candidate.
     */
    private ParameterArray findNewBest(ParameterArray params, ParameterArray lastBest,
                                       List<ParameterArray> population) {
        //
        ParameterArray currentBest;
        int ct = 0;
        double deltaFitness;
        ParameterArray recentBest = lastBest;

        // each iteration represents a new generation of the population.
        do {
            int keepSize = cullPopulation(population);
            replaceCulledWithKeeperVariants(population, keepSize);

            // EVALUATE POPULATION
            currentBest = evaluatePopulation(population, recentBest);

            deltaFitness = (currentBest.getFitness() - lastBest.getFitness());
            assert (deltaFitness >=0) : "We must never get worse in a new generation.";

            System.out.println(" ct="+ct+"  nbrRadius_="+nbrRadius_ +" population size=" + populationSize_
                               +" deltaFitness="+deltaFitness+"  currentBest = "+ currentBest.getFitness()
                               +"  lastBest="+ lastBest.getFitness());
            log(ct, currentBest.getFitness(), nbrRadius_, deltaFitness, params, "---");
            recentBest = currentBest.copy();

            if (listener_ != null) {
                // notify of our best candidate in this generation
                listener_.optimizerChanged(currentBest);
            }
            ct++;

        } while ( (deltaFitness > improvementEpsilon_ || optimizee_.getOptimalFitness() > 0 )
                && !isOptimalFitnessReached(currentBest)
                && (ct < MAX_ITERATIONS));

        if (isOptimalFitnessReached(currentBest)) {
            System.out.println("stopped because we found the optimal fitness.");
        }
        else if (deltaFitness <= improvementEpsilon_) {
            System.out.println("stopped because we made no IMPROVEMENT");
        }
        System.out.println("----------------------- done -------------------");
        log(ct, currentBest.getFitness(), 0, 0, currentBest, FormatUtil.formatNumber(ct));
        return currentBest;
    }

    /**
     * Remove all but the best candidates
     * @param population the whole population. It will be reduced in size.
     * @return the number of members that were retained.
     */
    private int cullPopulation(List<ParameterArray> population) {

        // sort the population according to the fitness of members.
        Collections.sort(population);
        Collections.reverse(population);

        // throw out the bottom CULL_FACTOR*populationSize_ members - keeping the cream of the crop.
        // then replace those culled with unary variations of those (now parents) that remain.
        // @@ add option to do cross-over variations too.
        int keepSize = Math.max(1,  (int)(populationSize_*(1.0 - CULL_FACTOR)));

        //printPopulation(population, 10);
        for (int j = populationSize_-1; j >= keepSize; j--) {
            population.remove( j );
        }
        return keepSize;
    }

    /**
     * Replace the members of the population that were removed with variations of the ones that we kept.
     * @param population
     * @param keepSize the number that were kept
     */
    private void replaceCulledWithKeeperVariants(List<ParameterArray> population, int keepSize) {

        int k = keepSize;
        while ( k < populationSize_ ) {

            // loop over the keepers until all replacements found
            int m = k % keepSize;

            // do the best one twice to avoid terminating too quickly
            // in the event that we got a very good fitness score on an early iteration.
            if (m == keepSize-1) {
                m = 0;
            }
            ParameterArray p = population.get(m);

            // add a permutation of one of the keepers
            // we multiply the radius by m because we want the worse ones to have
            // higher variability.
            population.add(p.getRandomNeighbor((m + NBR_RADIUS_SOFTENER)/NBR_RADIUS_SOFTENER * nbrRadius_));
            k++;
        }
        nbrRadius_ *= NBR_RADIUS_SHRINK_FACTOR;
    }


    /**
     * Evaluate the members of the population - either directly, or by
     * comparing them against the initial params value passed in (including params).
     * Note: this method assigns a fitness value to each member of the population.
     *
     * @param population the population to evaluate
     * @param params the best solution from the previous iteration
     * @return the new best solution.
     */
    protected ParameterArray evaluatePopulation(List<ParameterArray> population, ParameterArray params) {
        ParameterArray bestFitness = params;

        for (ParameterArray p : population) {

            double fitness;
            if (optimizee_.evaluateByComparison()) {
                fitness = optimizee_.compareFitness(p, params);
            } else {
                fitness = optimizee_.evaluateFitness(p);
            }

            p.setFitness(fitness);
            if (fitness > bestFitness.getFitness()) {
                bestFitness = p;
            }
        }
        return bestFitness.copy();
    }

    private static void printPopulation(List population) {
        printPopulation(population, population.size());
    }

    private static void printPopulation(List population, int limit)  {
        for (int i=0; i<population.size() && i<limit; i++)
            System.out.println( i + ": " + population.get(i));
        System.out.println( "" );
    }
}
