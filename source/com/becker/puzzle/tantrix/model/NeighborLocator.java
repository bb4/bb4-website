// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Box;
import com.becker.common.geometry.Location;

import java.util.HashMap;

/**
 *  Used to find neighboring locations in hex space.
 *
 *  @author Barry Becker
 */
public class NeighborLocator {

    private Location loc;
    /**
     * Constructor
     * @param location
     */
    public NeighborLocator(Location location) {
        assert location != null;
        this.loc = location;
    }

    /**
     * @param direction side to navigate to to find the neighbor. 0 is to the right.
     * @return the indicated neighbor of the specified tile.
     */
    public Location getNeighborLocation(int direction) {

        int row = loc.getRow();
        int col = loc.getCol();
        int offset = (row % 2 == 1) ? -1 : 0;
        Location nbrLoc = null;

        switch (direction) {
            case 0 : nbrLoc = new Location(row, col + 1); break;
            case 1 : nbrLoc = new Location(row - 1, col + offset + 1); break;
            case 2 : nbrLoc = new Location(row - 1, col + offset); break;
            case 3 : nbrLoc = new Location(row, col - 1); break;
            case 4 : nbrLoc = new Location(row + 1, col + offset); break;
            case 5 : nbrLoc = new Location(row + 1, col + offset + 1); break;
            default : assert false;
        }
        return nbrLoc;
    }
}
