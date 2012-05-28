// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path.permuting;

import com.becker.common.geometry.Location;
import com.becker.optimization.parameter.PermutedParameterArray;
import com.becker.puzzle.tantrix.model.PathColor;
import com.becker.puzzle.tantrix.model.TilePlacement;
import com.becker.puzzle.tantrix.model.TilePlacementList;
import com.becker.puzzle.tantrix.solver.path.TantrixPath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    /**
     * The list of tiles that are passed in must be a continuous primary path,
     *  but it is not required that it be a loop, or that any of the secondary colors match.
     * @param path ordered path tiles.
     */
    public PathPermuter(TantrixPath path) {
        path_ = path;
    }

    /**
     * try the seven cases and take any that are valid.
     * @return no more than 7 permuted path cases.
     */
    public List<TantrixPath> findPermutedPaths(int pivotIndex) {

        pivotTile = path_.getTilePlacements().get(pivotIndex);

        TantrixPath subPath1 = path_.subPath(pivotIndex - 1, 0);
        TantrixPath subPath2 =  path_.subPath(pivotIndex + 1, path_.size() - 1);

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
        TantrixPath subPath1RevSwapped = swapper.mutate(subPath1Reversed);
        TantrixPath subPath2RevSwapped = swapper.mutate(subPath2Reversed);

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
        if (path != null) {
            pathPermutations.add(path);
        }
    }

    /**
     * Combine supPath1 and subPath2 to make a new path. SubPath1 needs to be reversed when adding.
     * @param subPath1
     * @param subPath2
     * @return null if the resulting permuted path is not valid (i.e. has overlaps)
     */
    private TantrixPath createPermutedPath(TantrixPath subPath1, TantrixPath subPath2) {

        // add tiles from the first path in reverse order
        TilePlacementList tiles = new TilePlacementList();
        for (TilePlacement p : subPath1.getTilePlacements()) {
            tiles.addFirst(p);
        }

        tiles.add(pivotTile);
        tiles.addAll(subPath2.getTilePlacements());
        TantrixPath path = null;
        if (isValid(tiles)) {
            assert (TantrixPath.hasOrderedPrimaryPath(tiles, path_.getPrimaryPathColor())) :
                    "out of order path tiles \nsubpath1" + subPath1 + "\npivot="+ pivotTile + "\nsubpath2=" + subPath2 + "\norigPath="+ path_;

           /*
            subpath1[
            [tileNum=4 colors: [B, Y, R, B, R, Y] at (row=21, column=22) ANGLE_0],
            [tileNum=1 colors: [R, B, R, B, Y, Y] at (row=22, column=21) ANGLE_300],
            [tileNum=2 colors: [B, Y, Y, B, R, R] at (row=23, column=22) ANGLE_180]]

            pivot=[tileNum=3 colors: [B, B, R, R, Y, Y] at (row=20, column=21) ANGLE_120]

            subpath2=[[tileNum=5 colors: [R, B, B, R, Y, Y] at (row=21, column=21) ANGLE_60]]

            origPath=[
            [tileNum=5 colors: [R, B, B, R, Y, Y] at (row=21, column=22) ANGLE_120],
            [tileNum=3 colors: [B, B, R, R, Y, Y] at (row=20, column=21) ANGLE_120],
            [tileNum=2 colors: [B, Y, Y, B, R, R] at (row=21, column=21) ANGLE_180],
            [tileNum=1 colors: [R, B, R, B, Y, Y] at (row=20, column=20) ANGLE_300],
            [tileNum=4 colors: [B, Y, R, B, R, Y] at (row=19, column=21) ANGLE_0]]
          */

            path = new TantrixPath(tiles, path_.getPrimaryPathColor());
        }
        return path;
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
