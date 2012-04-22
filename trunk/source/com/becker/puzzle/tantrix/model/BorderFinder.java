// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Box;
import com.becker.common.geometry.Location;

import java.util.*;

import static com.becker.puzzle.tantrix.model.TantrixBoard.HEX_SIDES;

/**
 * Finds the set of moves on the border of the current 'tantrix'.
 * The 'tantrix' is the set of currently played consistent tiles.
 *
 * @author Barry Becker
 */
public class BorderFinder {

    private Tantrix tantrix;
    private PathColor primaryColor;
    private Set<Location> visited;
    private int maxHalfPathLength;
    private Box boundingBox;
    //private int pruneCt;
    //private int ppruneCt;

    /**
     * Constructor
     */
    public BorderFinder(Tantrix tantrix, int numTiles, PathColor primaryColor) {
        this.tantrix = tantrix;
        this.primaryColor = primaryColor;
        this.maxHalfPathLength = (numTiles + 1)/2;
        boundingBox = tantrix.getBoundingBox();
    }

    /**
     * Travel the primary path in both directions, adding all adjacent empty placements
     * as long as they do not push either boundingBox dimension beyond maxHalfPathLength.
     * @return list of legal next placements
     */
    public Set<Location> findBorderPositions() {
        Set<Location> positions = new LinkedHashSet<Location>();
        visited = new HashSet<Location>();

        TilePlacement lastPlaced = tantrix.getLastTile();

        Queue<TilePlacement> searchQueue = new LinkedList<TilePlacement>();
        searchQueue.add(lastPlaced);
        visited.add(lastPlaced.getLocation());

        while (!searchQueue.isEmpty()) {
            TilePlacement placement = searchQueue.remove();
            positions.addAll(findEmptyNeighborLocations(placement));
            searchQueue.addAll(findPrimaryPathNeighbors(placement));
        }

        return positions;
    }

    /**
     * @return all the empty neighbor positions next to the specified placement
     */
    private List<Location> findEmptyNeighborLocations(TilePlacement placement) {
        List<Location> emptyNbrLocations = new LinkedList<Location>();
        for (byte i=0; i<HEX_SIDES; i++) {

            Location nbrLoc = tantrix.getNeighborLocation(placement, i);
            if (tantrix.get(nbrLoc) == null) {
                Box newBox = new Box(boundingBox, nbrLoc);
                if (newBox.getMaxDimension() <= maxHalfPathLength) {
                    emptyNbrLocations.add(nbrLoc);
                    boundingBox = newBox;
                }
            }
        }
        return emptyNbrLocations;
    }

    /**
     * @return the one or two neighbors that can be found by following the primary path.
     */
    private List<TilePlacement> findPrimaryPathNeighbors(TilePlacement previous) {

        List<TilePlacement> pathNbrs = new LinkedList<TilePlacement>();
        for (byte i=0; i<HEX_SIDES; i++) {
            PathColor color = previous.getPathColor(i);
            if (color == primaryColor) {
                TilePlacement nbr = tantrix.getNeighbor(previous, i);
                if (nbr != null && !visited.contains(nbr.getLocation())) {
                    Box newBox = new Box(boundingBox, nbr.getLocation());
                    if (newBox.getMaxDimension() < maxHalfPathLength) {
                        pathNbrs.add(nbr);
                        visited.add(nbr.getLocation());
                        boundingBox = newBox;
                    }
                }
            }
        }
        return pathNbrs;
    }
}
