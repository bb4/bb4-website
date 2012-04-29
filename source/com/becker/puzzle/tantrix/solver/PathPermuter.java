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
        TantrixPath subPath2 = path_.subPath(pivotIndex + 1, path_.size() - 1);

        TantrixPath subPath1Reversed = reverse(subPath1);
        TantrixPath subPath2Reversed = reverse(subPath2);
        TantrixPath subPath1Swapped = swap(subPath1);
        TantrixPath subPath2Swapped = swap(subPath2);
        TantrixPath subPath1RevSwapped = reverse(subPath1Swapped);
        TantrixPath subPath2RevSwapped = reverse(subPath2Swapped);

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
        return isValid(tiles) ? new TantrixPath(tiles, path_.getPrimaryPathColor()) : null;
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

    /**
     * Only one tile in the subPath is touching the pivotTile. When we are done reversing,
     * the tile at the other end of the path will be touching the pivotTile at the same location.
     * @param subPath the subpath to reverse relative to the pivot tile.
     * @return the whole path rotated and translated so that the other end is connected at
     *   the same point on the pivot tile.
     */
    private TantrixPath reverse(TantrixPath subPath) {
        TilePlacementList tiles = new TilePlacementList(subPath.getTilePlacements());
        Collections.reverse(tiles);
        // not correct @@ . do this and fitness method.
        return new TantrixPath(tiles, path_.getPrimaryPathColor());
    }

    /**
     * Only one tile in the subPath is touching the pivotTile. When we are done swapping,
     * the same path tile will be rotated and translated (as well as all the tiles connected to it) so that
     * it connects to the other outgoing path on the pivot tile.
     * @param subPath the subpath to swap to the other outgoing path on the pivot tile.
     * @return the whole path rotated and translated so that the same end is connected at
     *   the a different point the pivot tile. There is only one other valid point that it can connect to.
     */
    private TantrixPath swap(TantrixPath subPath) {
        TilePlacementList tiles = new TilePlacementList();
        TilePlacementList subPathTiles = subPath.getTilePlacements();
        TilePlacement firstTile = subPathTiles.get(0);
        Location firstTileLocation = firstTile.getLocation();
        int numRotations = findRotationsToSwapLocation(firstTileLocation);
        Location swapLocation = new NeighborLocator(pivotTile.getLocation()).getNeighborLocation(numRotations);
        Location origLocation = pivotTile.getLocation();

        Rotation tileRotation = firstTile.getRotation().rotateBy(numRotations);
        TilePlacement previousTilePlacement = new TilePlacement(firstTile.getTile(), swapLocation, tileRotation);
        tiles.add(previousTilePlacement);

        for (int i=1; i<subPathTiles.size(); i++) {
            TilePlacement currentTile = subPathTiles.get(i);

            swapLocation = findSwapLocation(previousTilePlacement, origLocation);

            tileRotation = currentTile.getRotation().rotateBy(numRotations);
            TilePlacement currentTilePlacement = new TilePlacement(currentTile.getTile(), swapLocation, tileRotation);
            assert fits(currentTilePlacement, previousTilePlacement) :
                " current=" + currentTilePlacement +" did not fit with " + previousTilePlacement;

            tiles.add(currentTilePlacement);
            origLocation = previousTilePlacement.getLocation();
            previousTilePlacement = currentTilePlacement;
        }

        return new TantrixPath(tiles, path_.getPrimaryPathColor());
    }

    /**
     * Of the two outgoing path locations coming out from previousPlacement pick the one that is not the origLocation.
     * @param previousPlacement the tile most recently placed.
     * @param origLocation where the path came from before previous placement.
     * @return location for the current tile.
     */
    private Location findSwapLocation(TilePlacement previousPlacement, Location origLocation) {

       Map<Integer, Location> outgoingPathLocations = getOutgoingPathLocations(previousPlacement);
       Location loc = null;
       for (int rot : outgoingPathLocations.keySet()) {
           loc = outgoingPathLocations.get(rot);
           if (!loc.equals(origLocation)) {
               break;
           }
       }
       return loc;
    }

    private boolean fits(TilePlacement currentPlacement, TilePlacement previousPlacement) {
        Map<Integer, Location> outgoingPathLocations = getOutgoingPathLocations(currentPlacement);
        for (int direction : outgoingPathLocations.keySet()) {
            Location loc = outgoingPathLocations.get(direction);
            if (loc.equals(previousPlacement.getLocation())) {
                return true;
            }
        }
        return false;
    }


    /**
     * There are two outgoing paths from the pivot tile. The firstTileLocation is at one of them. We want the other one.
     * @param firstTileLocation
     * @return the number of rotations to get to the swap location.
     */
    private int findRotationsToSwapLocation(Location firstTileLocation) {
        Map<Integer, Location> outgoingPathLocations = getOutgoingPathLocations(pivotTile);
        Set<Integer> keys = outgoingPathLocations.keySet();
        for (int key : keys) {
            Location loc = outgoingPathLocations.get(key);
            if (!firstTileLocation.equals(loc)) {
                return key;
            }
        }
        assert false;
        return 0;
    }

    private Map<Integer, Location> getOutgoingPathLocations(TilePlacement placement) {
        Map<Integer, Location> outgoingPathLocations = new HashMap<Integer, Location>();
        for (int i=0; i< HexTile.NUM_SIDES; i++)  {
            if (path_.getPrimaryPathColor() == placement.getPathColor(i)) {
                outgoingPathLocations.put(i, new NeighborLocator(placement.getLocation()).getNeighborLocation(i));
            }
        }

        assert outgoingPathLocations.size() == 2: "There must always be two paths.";
        return outgoingPathLocations;
    }

}
