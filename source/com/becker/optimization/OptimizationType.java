package com.becker.optimization;


/**
 * Enum for the different possible Optimization Strategies.
 * There is an optimization strategy class corresponding to each of these types.
 * Detailed explanations for many of these algorithms can be found in
 *  How To Solve It: Modern Heuristics  by Michaelwics and Fogel
 *
 * @see com.becker.optimization.strategy.OptimizationStrategy
 * @see Optimizer
 *
 * @author Barry Becker
 */
public enum OptimizationType
{
    GLOBAL_SAMPLING ("Sparsely sample the space and return the best sample."),
    GLOBAL_HILL_CLIMBING ("Start with the best global sampling and hill climb from there."),
    HILL_CLIMBING ("Search method which always marches toward the direction of greatest improvement."),
    SIMULATED_ANNEALING ("Marches in the general direction of improvement, but can excape local optima."),
    TABU_SEARCH ("Uses memory of past solutions to avoid searching them again as it marches toward an optimal solution."),
    GENETIC_SEARCH ("Uses a genetic algorithm to search for the best solution."),
    STATE_SPACE ("Searches the state space to find an optima.");


    private String description_;

    /**
     * constructor for optimizatrion type enum
     *
     * @param description string description of the optimization strategy.
     */
    private OptimizationType(String description) {
       description_ = description;
    }

    public String getDescription() {
        return description_;
    }

}

