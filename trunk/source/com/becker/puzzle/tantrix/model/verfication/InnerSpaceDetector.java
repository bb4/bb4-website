// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model.verfication;

import com.becker.common.geometry.Box;
import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.model.TantrixBoard;

import java.util.*;

import static com.becker.puzzle.tantrix.model.HexTile.NUM_SIDES;

/**
 * Used to determine if a candidate solution has empty spaces within the tantrix.
 * A val;id solution cannot have such spaces.
 *
 *  @author Barry Becker
 */
public class InnerSpaceDetector {

    TantrixBoard board;

    /**
     * Constructor.
     * @param board the tantrix state to test for solution.
     */
    public InnerSpaceDetector(TantrixBoard board) {
        this.board = board;
    }

    /**
     * Start with an empty position on the border of the bbox.
     * Do a seed fill to visit all the spaces connected to that.
     * Finally, if there are any empty spaces inside the bbox that are not visited,
     * then there are inner spaces and it is not a valid solution.
     * @return true if there are no inner empty spaces.
     */
    public boolean hasInnerSpaces() {

        Set<Location> seedEmpties = findEmptyBorderPositions();
        //System.out.println("seeds=" + seedEmpties);
        Set<Location> visited = findConnectedEmpties(seedEmpties);
        //System.out.println("all empties="+ visited);
        boolean hasInner = !allEmptiesVisited(visited);
        //System.out.println("found inner spaces == " + hasInner);
        return hasInner;
    }

    /**
     * @return all the empty positions on the border
     */
    private Set<Location> findEmptyBorderPositions() {

        Box bbox = board.getBoundingBox();
        Set<Location> empties = new HashSet<Location>();

        for (int i = bbox.getMinCol(); i <= bbox.getMaxCol(); i++) {
            Location loc = new Location(bbox.getMinRow(), i);
            if (board.isEmpty(loc))  {
                empties.add(loc);
            }
            loc = new Location(bbox.getMaxRow(), i);
            if (board.isEmpty(loc))  {
                empties.add(loc);
            }
        }

        for (int i = bbox.getMinRow() + 1; i < bbox.getMaxRow(); i++) {
            Location loc = new Location(i, bbox.getMinCol());
            if (board.isEmpty(loc))  {
                empties.add(loc);
            }
            loc = new Location(i, bbox.getMaxCol());
            if (board.isEmpty(loc))  {
                empties.add(loc);
            }
        }

        assert empties.size() > 0 : "We should have found at least one empty position on the border";
        return empties;
    }

    private Set<Location> findConnectedEmpties(Set<Location> seedEmpties) {
        Set<Location> visited = new HashSet<Location>();

        Queue<Location> searchQueue = new LinkedList<Location>();
        searchQueue.addAll(seedEmpties);
        visited.addAll(seedEmpties);

        while (!searchQueue.isEmpty()) {
            Location loc = searchQueue.remove();
            List<Location> nbrEmpties = findEmptyNeighborLocations(loc);
            for (Location empty : nbrEmpties) {
                if (!visited.contains(empty)) {
                    visited.add(empty);
                    searchQueue.add(empty);
                }
            }
        }

        return visited;
    }

    /**
     * @return all the empty neighbor positions next to the current one.
     */
    private List<Location> findEmptyNeighborLocations(Location loc) {
        List<Location> emptyNbrLocations = new LinkedList<Location>();
        Box bbox = board.getBoundingBox();

        for (byte i=0; i< NUM_SIDES; i++) {

            Location nbrLoc = board.getNeighborLocation(loc, i);
            if (board.isEmpty(nbrLoc) && bbox.contains(nbrLoc)) {
                emptyNbrLocations.add(nbrLoc);
            }
        }
        return emptyNbrLocations;
    }

    /**
     * @param visited set of visited empties.
     * @return true if any empties in the tantrix bbox are not visited
     */
    private boolean allEmptiesVisited(Set<Location> visited) {
        Box bbox = board.getBoundingBox();
        for (int i = bbox.getMinRow(); i < bbox.getMaxRow(); i++) {
            for (int j = bbox.getMinCol(); j <= bbox.getMaxCol(); j++)  {
                Location loc = new Location(i, j);
                if (board.isEmpty(loc) && !visited.contains(loc))  {
                    return false;
                }
            }
        }
        return true;
    }

}
