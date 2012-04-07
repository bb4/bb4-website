// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

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

    Tantrix board;
    PathColor primaryColor;
    Set<Location> visited;

    /**
     * Constructor
     */
    public BorderFinder(Tantrix board, PathColor primaryColor) {
        this.board = board;
        this.primaryColor = primaryColor;
    }

    /**
     * Travel the primary path in both directions, adding all adjacent
     * empty placements.
     * @return list of legal next placements
     */
    public Set<Location> findBorderPositions() {
        Set<Location> positions = new LinkedHashSet<Location>();
        visited = new HashSet<Location>();

        TilePlacement lastPlaced = board.getLastTile();

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
     * @return the one or two neighbors that can be found by following the primary path.
     */
    private List<TilePlacement> findPrimaryPathNeighbors(TilePlacement previous) {

        List<TilePlacement> pathNbrs = new LinkedList<TilePlacement>();
        for (byte i=0; i<HEX_SIDES; i++) {
            PathColor color = previous.getPathColor(i);
            if (color == primaryColor) {
                TilePlacement nbr = board.getNeighbor(previous, i);
                if (nbr != null && !visited.contains(nbr.getLocation())) {
                    pathNbrs.add(nbr);
                    visited.add(nbr.getLocation());
                }
            }
        }
        return pathNbrs;
    }

    /**
     * @return all the empty neighbor positions next to the specified placement
     */
    private List<Location> findEmptyNeighborLocations(TilePlacement placement) {
        List<Location> emptyNbrLocations = new LinkedList<Location>();
        for (byte i=0; i<HEX_SIDES; i++) {

            Location nbrLoc = board.getNeighborLocation(placement, i);
            if (board.get(nbrLoc) == null) {
                emptyNbrLocations.add(nbrLoc);
            }
        }
        return emptyNbrLocations;
    }
}
