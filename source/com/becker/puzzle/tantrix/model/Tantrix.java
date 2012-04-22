// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Box;
import com.becker.common.geometry.Location;

import java.util.HashMap;

/**
 *  Represents "The Tantrix". In other words the set of currently placed tiles.
 *  Immutable.
 *
 *  @author Barry Becker
 */
public class Tantrix extends HashMap<Location, TilePlacement>{

    /** the last tile placed */
    private TilePlacement lastTile;

    /**
     * Constructor that creates a new tantrix instance when placing a move.
     * If the new tile to be placed is in the edge row of the grid, then
     * we need to increase the size of the grid by one in that direction and
     * also only render the inside cells.
     * @param tantrix current tantrix.
     * @param placement new piece to add to the tantrix and its positioning.
     */
    public Tantrix(Tantrix tantrix, TilePlacement placement) {
        if (tantrix == null) {
            tantrix = new Tantrix();
        }
        initializeFromOldTantrix(tantrix);
        lastTile = placement;
        setTilePlacement(placement);
    }

    /**
     * Copy constructor
     * @param tantrix tantrix to copy.
     */
    public Tantrix(Tantrix tantrix) {
        putAll(tantrix);
    }

    /**
     * @param tiles tiles in the tantrix
     */
    public Tantrix(TilePlacementList tiles) {
        lastTile = tiles.getLast();
        for (TilePlacement p : tiles) {
            this.put(p.getLocation(), p);
        }
    }

    private Tantrix() {}

    /**
     * Initialize ourselves from the old version.
     */
    private void initializeFromOldTantrix(Tantrix tantrix) {
        //this.primaryColor = tantrix.primaryColor;
        this.lastTile = tantrix.lastTile;
        this.putAll(tantrix);
    }

    /**
     * Take the specified tile and place it where indicated.
     * @param placement the placement containing the new tile to place.
     * @return the new immutable tantrix instance.
     */
    public Tantrix placeTile(TilePlacement placement) {
        return new Tantrix(this, placement);
    }

    /**
     * @param currentPlacement where we are now
     * @param direction side to navigate to to find the neighbor. 0 is to the right.
     * @return the indicated neighbor of the specified tile.
     */
    TilePlacement getNeighbor(TilePlacement currentPlacement, byte direction) {

        if (currentPlacement == null) return null;
        Location loc = getNeighborLocation(currentPlacement, direction);
        return get(loc);
    }

    /**
     * @param currentTile where we are now
     * @param direction side to navigate to to find the neighbor. 0 is to the right.
     * @return the indicated neighbor of the specified tile.
     */
    Location getNeighborLocation(TilePlacement currentTile, byte direction) {
        assert (currentTile != null);
        return getNeighborLocation(currentTile.getLocation(), direction);
    }

    /**
     * @param currentLocation where we are now
     * @param direction side to navigate to to find the neighbor. 0 is to the right.
     * @return the indicated neighbor of the specified tile.
     */
    Location getNeighborLocation(Location currentLocation, int direction) {

        int offset = (currentLocation.getRow() % 2 == 1) ? -1 : 0;
        Location nbrLoc = null;

        switch (direction) {
            case 0 : nbrLoc = new Location(currentLocation.getRow(), currentLocation.getCol() + 1);
                break;
            case 1 : nbrLoc = new Location(currentLocation.getRow() - 1, currentLocation.getCol() + offset + 1);
                break;
            case 2 : nbrLoc = new Location(currentLocation.getRow() - 1, currentLocation.getCol() + offset);
                break;
            case 3 : nbrLoc = new Location(currentLocation.getRow(), currentLocation.getCol() - 1);
                break;
            case 4 : nbrLoc = new Location(currentLocation.getRow() + 1, currentLocation.getCol() + offset);
                break;
            case 5 : nbrLoc = new Location(currentLocation.getRow() + 1, currentLocation.getCol() + offset + 1);
                break;
            default : assert false;
        }
        return nbrLoc;
    }

    public TilePlacement getLastTile() {
        return lastTile;
    }

    public int getEdgeLength() {
        return getBoundingBox().getMaxDimension() + 1;
    }

    /**
     * @return the bounds of the current tantrix tiles.
     */
    public Box getBoundingBox() {
        Box bbox = new Box(lastTile.getLocation());

        for (Location loc : keySet())  {
            bbox.expandBy(loc);
        }
        return bbox;
    }

    /**
     * @return the placement at the specified location.
     */
    public TilePlacement getTilePlacement(int row, int col) {
        return get(new Location(row, col));
    }

    private void setTilePlacement(TilePlacement placement) {
        put(placement.getLocation(), placement);
    }


    public String toString() {
        StringBuilder bldr = new StringBuilder("\n");
        for (Location loc: keySet()) {
             TilePlacement placement = get(loc);
             bldr.append(placement);
             bldr.append(" ");
        }

        return bldr.toString();
    }
}
