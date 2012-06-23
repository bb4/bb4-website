// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path;

import com.becker.common.math.MathUtil;
import com.becker.optimization.parameter.PermutedParameterArray;
import com.becker.puzzle.tantrix.model.TilePlacementList;
import com.becker.puzzle.tantrix.solver.path.permuting.PathPivotPermuter;
import com.becker.puzzle.tantrix.solver.path.permuting.SameTypeTileMixer;

import java.util.*;

/**
 * A list of tiles representing a primary color path that is used when searching to find a tantrix solution.
 * It has some unique properties.
 * For example, when finding a random neighbor, we select a tile at random and then consider all the
 * 7 other permutations of attaching the current path segments on either side. If any of those give a path
 * with a higher score that is what we use for the permuted path.
 *
 * @author Barry Becker
 */
public class PathPermutationGenerator  {

    private TantrixPath path;
    private PathEvaluator evaluator_ = new PathEvaluator();
    //private static Set<TantrixPath> cache = new HashSet<TantrixPath>();

    /**
     * Constructor
     * @path path to permute
     */
    public PathPermutationGenerator(TantrixPath path) {
        this.path = path;
    }

    /**
     * We want to find a potential solution close to the one that we have,
     * with minimal disturbance of the pieces that are already fit, but yet improved from what we had.
     * The main criteria for quality of the path is
     *  1) How close the ends of the path are to each other. Perfection achieved when we have a closed loop.
     *  2) Better if more matching secondary path colors
     *  3) Fewer inner spaces and a bbox with less area.
     *
     * @param radius proportional to the amount of variation. This might be a little difficult for tantrix.
     *   If the radius is small, or there is a closed loop, consider swapping pieces who's
     *   primary path have the same shape. If the radius is large, we could perhaps do random permutation from
     *   more than one spot.
     * @return the random nbr (potential solution).
     */
    public PermutedParameterArray getRandomNeighbor(double radius) {

        System.out.println("finding rand nbr for rad="+ radius);
        List<TantrixPath> pathPermutations = findPermutedPaths(radius);

        assert (!pathPermutations.isEmpty()) :
                "Could not find any permutations of " + this;
        //System.out.println("selecting from among " + pathPermutations.size() +" paths");
        return selectPath(pathPermutations);
    }

    /**
     * try the seven cases and take the one that works best
     * @param radius the larger the radius the wider the variance of the random paths returned.
     * @return 7 permuted path cases.
     */
    private List<TantrixPath> findPermutedPaths(double radius) {

        List<TantrixPath> permutedPaths = new ArrayList<TantrixPath>();
        PathPivotPermuter permuter = new PathPivotPermuter(path);
        TilePlacementList tiles = path.getTilePlacements();
        int numTiles = path.size();

        if (radius >= 0.4) {
            for (int i = 1; i < numTiles - 1; i++) {
                addAllPermutedPaths(permuter.findPermutedPaths(i, i), permutedPaths);
            }
        }
        else if (radius >= 0.1) {
            // to avoid trying too many paths, increment by something more than one if many tiles.
            int inc = 1 + path.size()/4;
            // n^2 * 7 permuted paths will be added.
            for (int pivot1 = 1; pivot1 < numTiles-1; pivot1+=rand(inc)) {
                for (int pivot2 = pivot1; pivot2 < numTiles-1; pivot2+=rand(inc)) {
                    addAllPermutedPaths(permuter.findPermutedPaths(pivot1, pivot2), permutedPaths);
                }
            }
        }
        else if (permutedPaths.isEmpty()) {
            List<PathType> types = Arrays.asList(PathType.values());
            Collections.shuffle(types, MathUtil.RANDOM);
            Iterator<PathType> typeIter = types.iterator();

            do {
                SameTypeTileMixer mixer = new SameTypeTileMixer(typeIter.next(), path);
                addAllPermutedPaths(mixer.findPermutedPaths(), permutedPaths);
            } while (permutedPaths.isEmpty() && typeIter.hasNext());
        }

        // as a last resort use this.
        if (permutedPaths.isEmpty()) {
            int pivotIndex1 = 1 + MathUtil.RANDOM.nextInt(tiles.size()-2);
            int pivotIndex2 = 1 + MathUtil.RANDOM.nextInt(tiles.size()-2);
            return permuter.findPermutedPaths(pivotIndex1, pivotIndex2);
        }
         /*
        else if (radius > 0) {//0.01) {
            int halfTiles = tiles_.size()/2;
            int proportion = (int)Math.ceil(5.0 * radius * (halfTiles));
            int pivotIndex1 = 1 + MathUtil.RANDOM.nextInt(proportion);
            int pivotIndex2 = tiles_.size() - 2 - MathUtil.RANDOM.nextInt(proportion);
            //System.out.println("radius="+radius+" numTiles=" + tiles_.size() + " pivotIndex1=" + pivotIndex1 + " pivotIndex2=" + pivotIndex2);
            return permuter.findPermutedPaths(pivotIndex1, pivotIndex2);
        } */

        // if radius very small, swap non-fitting tiles of the same shape?
        return permutedPaths;
    }

    private int rand(int inc) {
        if (inc <= 1) return 1;
        else return 1 + MathUtil.RANDOM.nextInt(inc);
    }

    /**
     * Check first that it is not in our global cache of paths already considered.
     */
    private void addAllPermutedPaths(List<TantrixPath> pathsToAdd, List<TantrixPath> permutedPaths) {

        for (TantrixPath p : pathsToAdd) {
            addPermutedPath(p, permutedPaths);
        }
    }

    /**
     * Check first that it is not in our global cache of paths already considered.
     */
    private void addPermutedPath(TantrixPath pathToAdd, List<TantrixPath> permutedPaths) {

        //if (!cache.contains(pathToAdd)) {
            permutedPaths.add(pathToAdd);
            //cache.add(pathToAdd);
            //System.out.println("csize=" + cache.size());
        //}
    }

    /**
     * @param paths list of paths to evaluate.
     * @return the path with the best score. In other words the path which is closest to a valid solution.
     */
    private TantrixPath selectBestPath(List<TantrixPath> paths) {

        double bestScore = -1;
        TantrixPath bestPath = null;

        for (TantrixPath path : paths) {
            double score = evaluator_.evaluateFitness(path);
            if (score > bestScore) {
                bestPath = path;
                bestScore = score;
            }
        }
        return bestPath;
    }

    /**
     * Skew toward selecting the best, but don't always select the best because then we
     * might always return the same random neighbor.
     * @param paths list of paths to evaluate.
     * @return the path with the best score. In other words the path which is closest to a valid solution.
     */
    private TantrixPath selectPath(List<TantrixPath> paths) {

        double totalScore = 0;
        List<Double> scores = new ArrayList<Double>(paths.size() + 1);

        for (TantrixPath path : paths) {
            double score = evaluator_.evaluateFitness(path);
            if (score >= PathEvaluator.SOLVED_THRESH)  {
               return path;
            }
            totalScore += score;
            scores.add(score);
        }
        scores.add(10000.0);

        double r = MathUtil.RANDOM.nextDouble() * totalScore;

        double total = 0;
        int ct = 0;
        do {
           total += scores.get(ct++);
        } while (r > total);

        return paths.get(ct-1);
    }
}
