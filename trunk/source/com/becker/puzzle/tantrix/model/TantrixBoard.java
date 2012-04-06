// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Box;
import com.becker.common.geometry.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *  Immutable representation of the current stat of the tantrix puzzle.
 *
 *  @author Barry Becker
 */
public class TantrixBoard {

    static final byte HEX_SIDES = 6;
    /** starting position. must be odd?*/
    static final Location INITIAL_LOCATION = new Location(21, 21);

    /** The 'tantrix'. Map of locations to currently placed tiles. */
    private Map<Location, TilePlacement> tantrix;

    /** color of the loop path */
    private PathColor primaryColor;

    /** the last tile placed */
    private TilePlacement lastTile;

    /** tiles that have not yet been placed on the board */
    private HexTileList unplacedTiles;

    /** number of tiles in the puzzle */
    private byte numTiles;

    /**
     * Constructor that creates a new board instance when placing a move.
     * If the new tile to be placed is in the edge row of the grid, then
     * we need to increase the size of the grid by one in that direction and
     * also only render the inside cells.
     * @param board current board state.
     * @param placement new piece to add to the board and its positioning.
     */
    public TantrixBoard(TantrixBoard board, TilePlacement placement) {
        initializeFromOldBoard(board);

        boolean removed = this.unplacedTiles.remove(placement.getTile());
        lastTile = placement;
        assert(removed) : "Did not remove " + placement.getTile() + " from " + unplacedTiles;

        setTilePlacement(placement);
    }

    /**
     * If the new location is in the 0 row or column, we will return 1, to indicate
     * that everything has been shifted down and to the right one.
     * @return return the offset (1) if the board was extended. or 0.
     */
    private void initializeFromOldBoard(TantrixBoard board) {

        this.primaryColor = board.primaryColor;
        this.lastTile = board.lastTile;
        this.unplacedTiles = (HexTileList) board.unplacedTiles.clone();
        this.numTiles = board.numTiles;

        createPlacementArray();
        tantrix.putAll(board.tantrix);
    }

    public TantrixBoard(HexTileList initialTiles) {

        HexTileList tileList = (HexTileList) initialTiles.clone();

        createPlacementArray();
        numTiles = (byte) tileList.size();

        primaryColor = new HexTiles().getTile(numTiles).getPrimaryColor();
        HexTile tile = tileList.remove(0);
        unplacedTiles = (HexTileList) tileList.clone();
        lastTile = new TilePlacement(tile, INITIAL_LOCATION, Rotation.ANGLE_0);

        this.setTilePlacement(lastTile);

        /* this shows all the tiles
        //tantrix.put(new Location(2, 2),
        //        new TilePlacement(new HexTiles().getTile(4), new Location(2, 2), Rotation.ANGLE_120));
        for (byte i = 0; i < tileList.size(); i++) {
            byte row = (byte) (i / 8);
            byte col = (byte) (i % 8);
            tantrix.put(new Location(row, col),
                    new TilePlacement(tileList.get(i), new Location(row, col), Rotation.ANGLE_120));
        }*/
    }

    /**
     * The puzzle is solved if there is a loop of the primary color
     * and all secondary colors match. Since a tile can only be placed in
     * a valid position, we only need to check if there is a complete loop.
     * @return true if solved.
     */
    public boolean isSolved() {
        if (!unplacedTiles.isEmpty()) {
            return false;
        }

        int numVisited = 0;
        TilePlacement currentTile = lastTile;
        TilePlacement previousTile = null;
        TilePlacement nextTile = null;

        do {
            nextTile = findNeighborTile(currentTile, previousTile);
            previousTile = currentTile;
            currentTile = nextTile;
            numVisited++;
        } while (currentTile != null && !currentTile.equals(lastTile));

        return (numVisited == numTiles && lastTile.equals(currentTile));
    }

    /**
     * Loop through the edges until we find the primary color.
     * If it does not direct us back to where we came from then go that way.
     * @param currentPlacement where we are now
     * @param previousTile where we were
     * @return the next tile in the path if there is one. Otherwise null.
     */
    private TilePlacement findNeighborTile(TilePlacement currentPlacement, TilePlacement previousTile) {

        for (byte i = 0; i < HEX_SIDES; i++) {
            PathColor color = currentPlacement.getPathColor(i);
            if (color == primaryColor) {
                TilePlacement nbr = getNeighbor(currentPlacement, i);
                if (nbr != null && !nbr.equals(previousTile)) {
                    return nbr;
                }
            }
        }
        return null;
    }

    /**
     * @param currentPlacement where we are now
     * @param direction side to navigate to to find the neighbor. 0 is to the right.
     * @return the indicated neighbor of the specified tile.
     */
    TilePlacement getNeighbor(TilePlacement currentPlacement, byte direction) {

        if (currentPlacement == null) return null;
        Location loc = getNeighborLocation(currentPlacement, direction);
        return getTilePlacement(loc);
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

    /**
     * Take the specified tile and place it where indicated.
     * @param placement the placement containing the new tile to place.
     * @return the new immutable board instance.
     */
    public TantrixBoard placeTile(TilePlacement placement) {
        return new TantrixBoard(this, placement);
    }

    public HexTileList getUnplacedTiles() {
        return unplacedTiles;
    }

    public TilePlacement getLastTile() {
        return lastTile;
    }

    public PathColor getPrimaryColor() {
        return primaryColor;
    }

    public int getEdgeLength() {
        return (int) Math.ceil(Math.sqrt(tantrix.size()) + 1);
    }

    /**
     * @return the position of the top left bbox corner
     */
    public Box getBoundingBox() {
        Box bbox = new Box(lastTile.getLocation(), lastTile.getLocation());

        for (Location loc : tantrix.keySet())  {
            bbox.expandBy(loc);
        }
        return bbox;
    }

    public Map<Location, TilePlacement> getTantrix() {
        return Collections.unmodifiableMap(tantrix);
    }

    /**
     * @return the placement at the specified location.
     */
    public TilePlacement getTilePlacement(int row, int col) {
        return tantrix.get(new Location(row, col));
    }

    public TilePlacement getTilePlacement(Location location) {
        return tantrix.get(location);
    }

    private void setTilePlacement(TilePlacement placement) {
        tantrix.put(placement.getLocation(), placement);
    }

    /**
     * return to original state before attempting solution.
     * Non original values become 0.
     */
    private void createPlacementArray() {
        tantrix = new HashMap<Location, TilePlacement>();
    }


    public String toString() {
        StringBuilder bldr = new StringBuilder("\n");
        for (Location loc: tantrix.keySet()) {
            TilePlacement placement = tantrix.get(loc);

             bldr.append(placement);
             bldr.append(" ");
        }

        return bldr.toString();
    }
}
