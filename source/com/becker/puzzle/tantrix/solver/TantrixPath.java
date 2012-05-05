// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;

import com.becker.common.geometry.Location;
import com.becker.common.math.MathUtil;
import java.util.Map;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.PermutedParameterArray;
import com.becker.puzzle.tantrix.model.*;

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
    private PathColor primaryPathColor_;
    private PathEvaluator evaluator_;



    /**
     * The list of tiles that are passed in must be a continuous primary path,
     *  but it is not required that it be a loop, or that any of the secondary colors match.
     * @param tiles ordered path tiles.
     * @param primaryColor
     */
    public TantrixPath(TilePlacementList tiles, PathColor primaryColor, PathEvaluator evaluator) {
        tiles_ = tiles;
        primaryPathColor_ = primaryColor;
        evaluator_ = evaluator;
    }

    public TantrixPath(TantrixBoard board) {

        TantrixBoard myBoard = new TantrixBoard(board.getTiles());
        RandomPathGenerator gen = new RandomPathGenerator(myBoard);
        TantrixPath path = gen.generateRandomPath();
        this.tiles_ = path.tiles_;
        this.primaryPathColor_ = board.getPrimaryColor();
    }

    @Override
    public TantrixPath copy() {
        TantrixPath copy = new TantrixPath(tiles_, primaryPathColor_, evaluator_);

        copy.setFitness(this.getFitness());
        return copy;
    }

    public TilePlacementList getTilePlacements() {
        return tiles_;
    }

    public PathEvaluator getEvaluator() {
        return evaluator_;
    }

    /**
     * The start index is not necessarily smaller than the end index.
     * @param startIndex  tile to add first
     * @param endIndex  tile to add last
     * @return sub path
     */
    public TantrixPath subPath(int startIndex, int endIndex) {
        TilePlacementList pathTiles = new TilePlacementList();
        if (startIndex <= endIndex) {
            for (int i = startIndex; i <= endIndex; i++) {
                pathTiles.add(this.tiles_.get(i));
            }
        }
        else  {
            for (int i = startIndex; i >= endIndex; i--) {
                pathTiles.add(this.tiles_.get(i));
            }
        }
        return new TantrixPath(pathTiles, primaryPathColor_, evaluator_);
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
     * @return get a completely random solution in the parameter space.
     */
    @Override
    public ParameterArray getRandomSample() {

        TantrixBoard board = new TantrixBoard(new HexTileList(tiles_));
        RandomPathGenerator gen = new RandomPathGenerator(board);
        return gen.generateRandomPath();
    }

    public PathColor getPrimaryPathColor() {
        return primaryPathColor_;
    }

    /**
     * Its a loop if the beginning of the path connects with the end.
     * Having the distance between beginning and end be 0 is a prerequisite and quicker to compute.
     * @return true if the path is a complete loop (ignoring secondary paths)
     */
    public boolean isLoop() {

        double distance = getEndPointDistance();

        if (distance == 0) {
            Map<Integer, Location> outgoing = tiles_.getFirst().getOutgoingPathLocations(primaryPathColor_);
            if (outgoing.containsValue(tiles_.getLast().getLocation())) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return  the distance between the path end points. A distance of 0 does not automatically mean there is a loop.
     */
    public double getEndPointDistance() {
        Location end1 = tiles_.get(0).getLocation();
        Location end2 = tiles_.get(0).getLocation();
        return HexUtil.distanceBetween(end1, end2);
    }

    /**
     * try the seven cases and take the one that works best
     * @return 7 permuted path cases.
     */
    private List<TantrixPath> findPermutedPaths() {

        int pivotIndex = 1 + MathUtil.RANDOM.nextInt(tiles_.size()-2);
        PathPermuter permuter = new PathPermuter(this, evaluator_);
        return permuter.findPermutedPaths(pivotIndex);
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
            }
        }
        return bestPath;
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
