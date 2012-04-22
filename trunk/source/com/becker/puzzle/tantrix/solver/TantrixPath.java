// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;

import com.becker.common.geometry.Location;
import com.becker.common.math.MathUtil;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.PermutedParameterArray;
import com.becker.puzzle.redpuzzle.model.PieceList;
import com.becker.puzzle.tantrix.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of tiles representing a primary color path that is used when searching to find a tantrix solution.
 * It has some unique properties.
 * For example, when finding a random neighbor, we select a tile at random and then consider all the
 * 7 other permutations of attaching the current path segments on either side. If any of those give a path
 * with a higher score that is what we use for the permuted path.
 *
 * @author Barry Becker
 */
public class TantrixPath extends PermutedParameterArray {

    private TilePlacementList tiles_;


    /**
     * The list of tiles that are passed in must be a continuous primary path,
     *  but it is not required that it be a loop, or that any of the secondary colors match.
     * @param tiles ordered path tiles.
     */
    public TantrixPath(TilePlacementList tiles) {
        tiles_ = tiles;
    }

    public TantrixPath(TantrixBoard board) {

        TantrixBoard myBoard = new TantrixBoard(board.getTiles());
        RandomPathGenerator gen = new RandomPathGenerator(myBoard);
        TantrixPath path = gen.generateRandomPath();
        this.tiles_ = path.tiles_;
    }

    @Override
    public TantrixPath copy() {
        TantrixPath copy = new TantrixPath(tiles_);

        copy.setFitness(this.getFitness());
        return copy;
    }

    public TilePlacementList getTilePlacements() {
        return tiles_;
    }

    public TantrixPath subPath(int startIndex, int endIndex) {
        TilePlacementList pathTiles = new TilePlacementList();
        for (int i=startIndex; i<=endIndex; i++) {
            pathTiles.add(this.tiles_.get(i));
        }
        return new TantrixPath(pathTiles);
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
    @Override
    public PermutedParameterArray getRandomNeighbor(double radius) {

        List<TantrixPath> pathPermutations = findPermutedPaths();
        return selectBestPath(pathPermutations);
    }

    /**
     * try the seven cases and take the one that works best
     * @return 7 permuted path cases.
     */
    private List<TantrixPath> findPermutedPaths() {

        int pivotIndex = 1 + MathUtil.RANDOM.nextInt(tiles_.size()-2);
        TilePlacement pivotTile = tiles_.get(pivotIndex);

        TantrixPath subPath1 = subPath(0, pivotIndex-1);
        TantrixPath subPath2 = subPath(pivotIndex+1, tiles_.size()-1);

        TantrixPath subPath1Reversed = subPath1.reverse(pivotTile);
        TantrixPath subPath2Reversed = subPath2.reverse(pivotTile);

        List<TantrixPath> pathPermutations = new ArrayList<TantrixPath>();
        pathPermutations.add(createPermutedPath(subPath1, pivotTile, subPath2Reversed));
        pathPermutations.add(createPermutedPath(subPath1Reversed, pivotTile, subPath2));
        pathPermutations.add(createPermutedPath(subPath1Reversed, pivotTile, subPath2Reversed));

        pathPermutations.add(createPermutedPath(subPath2, pivotTile, subPath1));
        pathPermutations.add(createPermutedPath(subPath2, pivotTile, subPath1Reversed));
        pathPermutations.add(createPermutedPath(subPath2Reversed, pivotTile, subPath1));
        pathPermutations.add(createPermutedPath(subPath2Reversed, pivotTile, subPath1Reversed));

        return pathPermutations;
    }

    /**
     * @param paths list of paths to evaluate.
     * @return the path with the best score. In other words the path which is closest to a valid solution.
     */
    private TantrixPath selectBestPath(List<TantrixPath> paths) {
        PathEvaluator evaluator = new PathEvaluator();

        double bestScore = -1;
        TantrixPath bestPath = null;

        for (TantrixPath path : paths) {
            double score = evaluator.evaluateFitness(path);
            if (score > bestScore) {
                bestPath = path;
            }
        }
        return bestPath;
    }

    private TantrixPath reverse(TilePlacement pivotTile) {
        return this;
    }

    private TantrixPath createPermutedPath(TantrixPath subPath1, TilePlacement placement, TantrixPath subPath2) {
       return subPath1;
    }

    /**
     * @return get a completely random solution in the parameter space.
     */
    @Override
    public ParameterArray getRandomSample() {

        TantrixBoard board = new TantrixBoard(new HexTileList(tiles_));
        RandomPathGenerator gen = new RandomPathGenerator(board);
        return gen.generateRandomPath();
    }

    /**
     * @return the number of parameters in the array.
     */
    @Override
    public int size() {
        return tiles_.size();
    }

    @Override
    public String toString() {
        return tiles_.toString();
    }

    /**
     * @return  the parameters in a string of Comma Separated Values.
     */
    @Override
    public String toCSVString() {
        return toString();
    }
}
