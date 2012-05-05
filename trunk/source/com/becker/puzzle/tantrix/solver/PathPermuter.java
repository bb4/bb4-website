// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;

import com.becker.common.geometry.Location;
import com.becker.optimization.parameter.PermutedParameterArray;
import com.becker.puzzle.tantrix.model.*;

import java.util.*;

/**
 * Given a TantrixPath and a pivot tile index, find the permuted paths.
 * Since there are 8 total ways to permute and the path already represents one of them,
 * the permuter will never return more than 7 valid permuted paths.
 *
 * @author Barry Becker
 */
public class PathPermuter extends PermutedParameterArray {

    private TantrixPath path_;
    private TilePlacement pivotTile;
    private PathEvaluator evaluator;


    /**
     * The list of tiles that are passed in must be a continuous primary path,
     *  but it is not required that it be a loop, or that any of the secondary colors match.
     * @param path ordered path tiles.
     */
    public PathPermuter(TantrixPath path, PathEvaluator evaluator) {
        path_ = path;
        this.evaluator = evaluator;
    }

    /**
     * try the seven cases and take any that are valid.
     * @return no more than 7 permuted path cases.
     */
    public List<TantrixPath> findPermutedPaths(int pivotIndex) {

        pivotTile = path_.getTilePlacements().get(pivotIndex);

        TantrixPath subPath1 = path_.subPath(pivotIndex - 1, 0);
        TantrixPath subPath2 = path_.subPath(pivotIndex + 1, path_.size() - 1);

        return createPermutedPathList(subPath1, subPath2);
    }

    /**
     * @param subPath1 path coming out of pivot tile
     * @param subPath2 the other path coming out of pivot tile.
     * @return list of permuted paths.
     */
    private List<TantrixPath> createPermutedPathList(TantrixPath subPath1, TantrixPath subPath2) {
        PathColor primaryColor = path_.getPrimaryPathColor();
        SubPathMutator swapper = new SubPathSwapper(pivotTile, primaryColor);
        SubPathMutator reverser = new SubPathReverser(pivotTile, primaryColor);

        TantrixPath subPath1Reversed = reverser.mutate(subPath1);
        TantrixPath subPath2Reversed = reverser.mutate(subPath2);
        TantrixPath subPath1Swapped = swapper.mutate(subPath1);
        TantrixPath subPath2Swapped = swapper.mutate(subPath2);
        TantrixPath subPath1RevSwapped = reverser.mutate(subPath1Swapped);
        TantrixPath subPath2RevSwapped = reverser.mutate(subPath2Swapped);

        List<TantrixPath> pathPermutations = new ArrayList<TantrixPath>();

        addIfNotNull(createPermutedPath(subPath1, subPath2Reversed), pathPermutations);
        addIfNotNull(createPermutedPath(subPath1Reversed, subPath2), pathPermutations);
        addIfNotNull(createPermutedPath(subPath1Reversed, subPath2Reversed), pathPermutations);

        addIfNotNull(createPermutedPath(subPath2Swapped, subPath1Swapped), pathPermutations);
        addIfNotNull(createPermutedPath(subPath2Swapped, subPath1RevSwapped), pathPermutations);
        addIfNotNull(createPermutedPath(subPath2RevSwapped, subPath1Swapped), pathPermutations);
        addIfNotNull(createPermutedPath(subPath2RevSwapped, subPath1RevSwapped), pathPermutations);
        return pathPermutations;
    }

    private void addIfNotNull(TantrixPath path, List<TantrixPath> pathPermutations) {
        if (path != null) pathPermutations.add(path);
    }

    /**
     * @param subPath1
     * @param subPath2
     * @return null if the resulting permuted path is not valid (i.e. has overlaps)
     */
    private TantrixPath createPermutedPath(TantrixPath subPath1, TantrixPath subPath2) {
        TilePlacementList tiles = new TilePlacementList(subPath1.getTilePlacements());
        tiles.add(pivotTile);
        tiles.addAll(subPath2.getTilePlacements());
        return isValid(tiles) ? new TantrixPath(tiles, path_.getPrimaryPathColor(), evaluator) : null;
    }

    /**
     * @param tiles
     * @return true if no overlapping tiles.
     */
    private boolean isValid(TilePlacementList tiles) {
        Set<Location> tileLocations = new HashSet<Location>();
        for (TilePlacement placement : tiles) {
            if (tileLocations.contains(placement.getLocation())) {
                return false;
            }
            tileLocations.add(placement.getLocation());
        }
        return true;
    }
}
