// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;

import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.model.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Reverse a subpath.
 *
 * @author Barry Becker
 */
public class SubPathReverser extends SubPathMutator {

    public SubPathReverser(TilePlacement pivotTile, PathColor primaryColor) {
        super(pivotTile, primaryColor);
    }

    /**
     * Only one tile in the subPath is touching the pivotTile. When we are done reversing,
     * the tile at the other end of the path will be touching the pivotTile at the same location.
     * @param subPath the subpath to reverse relative to the pivot tile.
     * @return the whole path rotated and translated so that the other end is connected at
     *   the same point on the pivot tile.
     */
     @Override
     public TantrixPath mutate(TantrixPath subPath) {

         TilePlacementList tiles = new TilePlacementList();
         TilePlacementList subPathTiles = subPath.getTilePlacements();
         TilePlacement lastTile = subPathTiles.getLast();
         Map<Integer, Location> outgoing = lastTile.getOutgoingPathLocations(primaryColor);
         int directionToPrev = (subPathTiles.size() > 1) ?
                 findOutgoingDirection(lastTile, subPathTiles.get(subPathTiles.size()-2).getLocation()) :
                 findOutgoingDirection(lastTile, pivotTile.getLocation());
         outgoing.remove(directionToPrev); // now only one outgoing path - the one that is free.

         Location newLocation = subPathTiles.getFirst().getLocation();
         int startDir = findOutgoingDirection(pivotTile, newLocation);
         int numRotations = 3 + outgoing.keySet().iterator().next() - startDir;

         Location origLocation = pivotTile.getLocation();
         Rotation tileRotation = lastTile.getRotation().rotateBy(numRotations);
         TilePlacement previousTilePlacement = new TilePlacement(lastTile.getTile(), newLocation, tileRotation);
         tiles.add(previousTilePlacement);

         // this part is almost the same as in swapper
         for (int i = subPathTiles.size()-2; i >= 0; i--) {
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

         return new TantrixPath(tiles, primaryColor, subPath.getEvaluator());
    }
}
