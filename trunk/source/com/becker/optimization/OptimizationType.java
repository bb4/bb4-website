package com.becker.optimization;

import com.becker.common.EnumeratedType;
import com.becker.game.twoplayer.go.GoEye;
import com.becker.game.twoplayer.go.EyeType;

/**
 * Enum for the different possible Optimization Strategies.
 * There is an optimization strategy class corresponding to each of these types.
 * Detailed explainations for many of these algorithms can be found in
 *  How To Solve It: Modern Heuristics  by Michaelwics and Fogel
 *
 * @see OptimizationStrategy
 * @see Optimizer
 *
 * @author Barry Becker
 */
public final class OptimizationType  extends  EnumeratedType.BasicValue
{

    // Ordinals
    // different types of optimization algorithms.
    public static final int GLOBAL_SAMPLING_CODE = 1;
    public static final int GLOBAL_HILL_CLIMBING_CODE = 2;
    public static final int HILL_CLIMBING_CODE = 3;
    public static final int SIMULATED_ANNEALING_CODE = 4;
    public static final int TABU_SEARCH_CODE = 5;
    public static final int GENETIC_SEARCH_CODE = 6;
    public static final int STATE_SPACE_CODE = 7;



    // The enumerated values.
    public static final OptimizationType GLOBAL_SAMPLING =
            new OptimizationType(GLOBAL_SAMPLING_CODE, "Global Sampling",
                    "Sparsely sample the space and return the best sample.");
    public static final OptimizationType GLOBAL_HILL_CLIMBING =
            new OptimizationType(GLOBAL_HILL_CLIMBING_CODE, "Global Hill Climbing",
                    "Start with the best global sampling and hill climb from there.");
    public static final OptimizationType HILL_CLIMBING =
            new OptimizationType(HILL_CLIMBING_CODE , "Hill Climbing",
                    "Search method which always marches toward the direction of greatest improvement.");
    public static final OptimizationType SIMULATED_ANNEALING =
            new OptimizationType(SIMULATED_ANNEALING_CODE, "Simulated Annealing",
                    "Marches in the general direction of improvement, but can excape local optima.");
    public static final OptimizationType TABU_SEARCH =
            new OptimizationType(TABU_SEARCH_CODE, "Tabu Search",
                    "Uses memory of past solutions to avoid searching them again as it marches toward an optimal solution.");
    public static final OptimizationType GENETIC_SEARCH =
            new OptimizationType(GENETIC_SEARCH_CODE, "Genetic Algorithm Search",
                    "Uses a genetic algorithm to search for the best solution.");
    public static final OptimizationType STATE_SPACE =
            new OptimizationType(STATE_SPACE_CODE, "Sate Space Search",
                    "Searches the state space to find an optima.");

    /**
     * Contains all valid {@link com.becker.optimization.OptimizationType}s.
     */
    private static final EnumeratedType enumeration = new EnumeratedType(
            new OptimizationType[] {
                GLOBAL_SAMPLING, GLOBAL_HILL_CLIMBING, HILL_CLIMBING, SIMULATED_ANNEALING,
                TABU_SEARCH, GENETIC_SEARCH, STATE_SPACE
            }
    );

    /**
     * constructor for eye type enum
     *
     * @param ordinal ordered integer value for the eye type enum
     * @param name string name of the eye type (eg "False Eye")
     */
    private OptimizationType(final int ordinal, final String name) {
        super(name, ordinal, null);
    }

    /**
     * constructor for eye type enum
     *
     * @param ordinal ordered integer value for the eye type enum
     * @param name string name of the optimization strategy type (eg "Simulated Annealing")
     * @param description short description.
     */
    private OptimizationType(final int ordinal, final String name, final String description) {
            super(name, ordinal, description);
        }


    public EnumeratedType getEnumeratedType() {
        return enumeration;
    }

    /**
     * Looks up an {@link com.becker.optimization.OptimizationType}
     * @throws Error if the name is not a member of the enumeration
     */
    public static OptimizationType get(final String name, final boolean finf) {
        return (OptimizationType) enumeration.getValue(name, finf);
    }
}

