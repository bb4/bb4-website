/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.optimizees;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import static com.becker.optimization.optimizees.AnalyticFunctionConsts.*;

/**
 * An enum for different sorts of analytic functions that we might want to test.
 * Different types of 3d planar functions that all have the same maximum.
 *
 * @author Barry Becker
 */
public enum AnalyticVariation {

    PARABOLA {
        /**
         * Smooth inverted parabola.
         */
        @Override
        public double evaluateFitness(ParameterArray a) {
            return  1000 + ((1.0 - Math.pow(AnalyticFunctionConsts.P1 - a.get(0).getValue(), 2)
                                 - Math.pow(AnalyticFunctionConsts.P2 - a.get(1).getValue(), 2)));
        }

        @Override
        public double getErrorTolerancePercent(OptimizationStrategyType opt) {
            return getErrorTolerancePercent(opt, new double[] {
                    GLOB_SAMP_TOL, BASE_TOLERANCE, BASE_TOLERANCE, BASE_TOLERANCE,  0,  GLOB_SAMP_TOL,   GLOB_SAMP_TOL, BASE_TOLERANCE
            });
        }
    },
    SINUSOIDAL {
        /**
         * This version introduces a bit of sinusoidal noise.
         * @param a the position on the parabolic surface given the specified values of p1 and p2
         * @return fitness value
         */
        @Override
        public double evaluateFitness(ParameterArray a) {
            return PARABOLA.evaluateFitness(a) + 0.5 * Math.cos(a.get(0).getValue() * a.get(1).getValue() - 2.0);
        }

        @Override
        public double getErrorTolerancePercent(OptimizationStrategyType opt) {
            return getErrorTolerancePercent(opt, new double[] {
                    GLOB_SAMP_TOL, RELAXED_TOL, 0.01, BASE_TOLERANCE,  RELAXED_TOL, 0.042, 0.042, BASE_TOLERANCE
            });
        }
    },
    ABS_SINUSOIDAL {
        /**
         * This version introduces a bit of absolute value sinusoidal noise.
         * This means it will not be second order differentiable, making this type of search harder.
         * @param a the position on the parabolic surface given the specified values of p1 and p2
         * @return fitness value
         */
        @Override
        public double evaluateFitness(ParameterArray a) {
            return PARABOLA.evaluateFitness(a) + 0.5 * Math.abs(Math.cos(a.get(0).getValue() * a.get(1).getValue() - 2.0));
        }

        @Override
        public double getErrorTolerancePercent(OptimizationStrategyType opt) {
            return getErrorTolerancePercent(opt, new double[] {
                    GLOB_SAMP_TOL, 0.0128, 0.01, BASE_TOLERANCE,  RELAXED_TOL, 0.03,  0.03,  BASE_TOLERANCE
            });
        }
    },
    STEPPED  {

        /**
         *  This version introduces a bit of step function noise.
         */
        @Override
        public double evaluateFitness(ParameterArray a) {
            return PARABOLA.evaluateFitness(a) - 0.2 * Math.round( Math.abs((P1
                    - a.get(0).getValue())) * Math.abs((P2 - a.get(1).getValue())));
        }

        @Override
        public double getErrorTolerancePercent(OptimizationStrategyType opt) {
            return getErrorTolerancePercent(opt, new double[] {
                    GLOB_SAMP_TOL, BASE_TOLERANCE, BASE_TOLERANCE, BASE_TOLERANCE, RELAXED_TOL,  0.042,  0.042, BASE_TOLERANCE
            });
        }
    };


    /**
     * Evaluate fitness for the analytics function.
     * @param a the position on the parabolic surface given the specified values of p1 and p2
     * @return fitness value
     */
    public abstract double evaluateFitness(ParameterArray a);

    /**
     * Error tolerance for each search strategy and variation of the problem.
     * @param opt optimization strategy.
     * @return error tolerance percent
     */
    public abstract double getErrorTolerancePercent(OptimizationStrategyType opt);

    protected double getErrorTolerancePercent(OptimizationStrategyType opt, double[] percentValues) {

        double percent = 0;
        switch (opt) {
            case GLOBAL_SAMPLING : percent = percentValues[0]; break;
            case GLOBAL_HILL_CLIMBING : percent = percentValues[1]; break;
            case HILL_CLIMBING : percent = percentValues[2]; break;
            case SIMULATED_ANNEALING : percent = percentValues[3]; break;
            case TABU_SEARCH: percent = percentValues[4]; break;
            case GENETIC_SEARCH : percent = percentValues[5]; break;
            case CONCURRENT_GENETIC_SEARCH : percent = percentValues[6]; break;
            case STATE_SPACE: percent = percentValues[7]; break;
        }
        return percent;
    }
}