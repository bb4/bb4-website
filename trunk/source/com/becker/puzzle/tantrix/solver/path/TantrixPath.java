// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path;

import com.becker.common.geometry.Location;
import com.becker.common.math.MathUtil;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.PermutedParameterArray;
import com.becker.puzzle.tantrix.model.*;
import com.becker.puzzle.tantrix.solver.path.permuting.PathPermuter;

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
public class TantrixPath extends PermutedParameterArray {

    private TilePlacementList tiles_;
    private PathColor primaryPathColor_;
    private PathEvaluator evaluator_;


    /**
     * The list of tiles that are passed in must be a continuous primary path,
     * but it is not required that it be a loop, or that any of the secondary colors match.
     * @param tiles ordered path tiles.
     * @param primaryColor
     */
    public TantrixPath(TilePlacementList tiles, PathColor primaryColor) {

        assert primaryColor != null;
        primaryPathColor_ = primaryColor;
        tiles_ = reorder(tiles);
    }

    /**
     * The list of tiles that are passed in must be a continuous primary path,
     * but it is not required that it be a loop, or that any of the secondary colors match.
     * @param tantrix ordered path tiles.
     * @param primaryColor
     */
    public TantrixPath(Tantrix tantrix, PathColor primaryColor) {
        this(new TilePlacementList(tantrix), primaryColor);
    }


    /**
     * Creates a random path given a board state.
     * @param board
     */
    public TantrixPath(TantrixBoard board) {

        TantrixBoard myBoard = new TantrixBoard(board.getAllTiles());
        RandomPathGenerator gen = new RandomPathGenerator(myBoard);
        TantrixPath path = gen.generateRandomPath();
        this.tiles_ = path.tiles_;
        this.primaryPathColor_ = board.getPrimaryColor();
    }

    @Override
    public TantrixPath copy() {
        TantrixPath copy = new TantrixPath(tiles_, primaryPathColor_);
        copy.setFitness(this.getFitness());
        return copy;
    }

    public TilePlacementList getTilePlacements() {
        return tiles_;
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
        return new TantrixPath(pathTiles, primaryPathColor_);
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

        if (size() <= 2) return false;
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
        if (tiles_.isEmpty()) return 200.0;//Double.MAX_VALUE;
        if (tiles_.size() == 1) return 1.0;
        TilePlacement first = tiles_.getFirst();
        TilePlacement last = tiles_.getLast();
        Location end1 = first.getLocation();
        Location end2 = last.getLocation();

        // if they touch return distance of 0
        if (first.getOutgoingPathLocations(primaryPathColor_).containsValue(end2)
                && last.getOutgoingPathLocations(primaryPathColor_).containsValue(end1)) {
            return 0;
        }

        return HexUtil.distanceBetween(end1, end2);
    }

    /**
     * Attempt to reorder the tiles into a path if possible.
     * Throw an error if not. Should not change the order if the tiles are already arranged on a path.
     * @param list
     * @return the tiles in path order.
     */
    private TilePlacementList reorder(TilePlacementList list) {
        if (list.size() < 2) {
            return list;
        }
        Iterator<TilePlacement> placementIter = list.iterator();
        TilePlacement startingPoint = placementIter.next();
        Iterator<Location> outIter =
                startingPoint.getOutgoingPathLocations(primaryPathColor_).values().iterator();
        Set<Location> head = new HashSet<Location>();
        Set<Location> tail = new HashSet<Location>();

        // if the list is already ordered along the primary path, try to preserve that.
        Location next = outIter.next();
        if (next.equals(list.get(1).getLocation())) {
            head.add(next);
            tail.add(outIter.next());
        } else {
            head.add(outIter.next());
            tail.add(next);
        }

        TilePlacementList newList = new TilePlacementList();
        newList.add(startingPoint);
        placementIter.remove();

        do {
            while (placementIter.hasNext()) {
                TilePlacement nextPlacement = placementIter.next();
                if (head.contains(nextPlacement.getLocation())) {
                    newList.addFirst(nextPlacement);
                    head.clear();
                    head.addAll(nextPlacement.getOutgoingPathLocations(primaryPathColor_).values());
                    placementIter.remove();
                }
                else if (tail.contains(nextPlacement.getLocation())) {
                    newList.addLast(nextPlacement);
                    tail.clear();
                    tail.addAll(nextPlacement.getOutgoingPathLocations(primaryPathColor_).values());
                    placementIter.remove();
                }
            }
            System.out.println("num left = " + list.size() + "  ordered list=" + newList);
            placementIter = list.iterator();
        }
        while (placementIter.hasNext());

        return newList;
    }

    /**
     * try the seven cases and take the one that works best
     * @return 7 permuted path cases.
     */
    private List<TantrixPath> findPermutedPaths() {

        int pivotIndex = 1 + MathUtil.RANDOM.nextInt(tiles_.size()-2);
        PathPermuter permuter = new PathPermuter(this);
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
