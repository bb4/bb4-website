// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;

import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.model.*;

import java.util.*;

/**
 * Swap a subpath from on outgoing primary path to the other on the pivot tile.
 *
 * @author Barry Becker
 */
public class SubPathSwapper extends SubPathMutator {

    public SubPathSwapper(TilePlacement pivotTile, PathColor primaryColor) {
        super(pivotTile, primaryColor);
    }

    /**
     * Only one tile in the subPath is touching the pivotTile. When we are done swapping,
     * the same path tile will be rotated and translated (as well as all the tiles connected to it) so that
     * it connects to the other outgoing path on the pivot tile.
     * @param subPath the subpath to swap to the other outgoing path on the pivot tile.
     * @return the whole path rotated and translated so that the same end is connected at
     *   the a different point the pivot tile. There is only one other valid point that it can connect to.
     */
    @Override
    public TantrixPath mutate(TantrixPath subPath) {
        TilePlacementList tiles = new TilePlacementList();
        TilePlacementList subPathTiles = subPath.getTilePlacements();
        TilePlacement firstTile = subPathTiles.get(0);
        Location firstTileLocation = firstTile.getLocation();
        int numRotations = findRotationsToSwapLocation(firstTileLocation);

        Location newLocation = HexNeighborLocator.getNeighbor(pivotTile.getLocation(), numRotations);
        Location origLocation = pivotTile.getLocation();

        Rotation tileRotation = firstTile.getRotation().rotateBy(numRotations);
        TilePlacement previousTilePlacement = new TilePlacement(firstTile.getTile(), newLocation, tileRotation);
        tiles.add(previousTilePlacement);

        for (int i=1; i<subPathTiles.size(); i++) {
            TilePlacement currentTile = subPathTiles.get(i);

            newLocation = findOtherOutgoingLocation(previousTilePlacement, origLocation);

            tileRotation = currentTile.getRotation().rotateBy(numRotations);
            TilePlacement currentTilePlacement = new TilePlacement(currentTile.getTile(), newLocation, tileRotation);
            assert fits(currentTilePlacement, previousTilePlacement) :
                " current=" + currentTilePlacement +" did not fit with " + previousTilePlacement;

            tiles.add(currentTilePlacement);
            origLocation = previousTilePlacement.getLocation();
            previousTilePlacement = currentTilePlacement;
        }

        return new TantrixPath(tiles, primaryColor);
    }

    /**
     * There are two outgoing paths from the pivot tile. The firstTileLocation is at one of them. We want the other one.
     * @param firstTileLocation
     * @return the number of rotations to get to the swap location.
     */
    private int findRotationsToSwapLocation(Location firstTileLocation) {
        Map<Integer, Location> outgoingPathLocations = pivotTile.getOutgoingPathLocations(primaryColor);
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
}
