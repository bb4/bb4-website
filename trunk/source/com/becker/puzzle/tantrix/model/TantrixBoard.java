// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Box;
import com.becker.common.geometry.Location;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *  Immutable representation of the current state of the tantrix puzzle.
 *
 *  @author Barry Becker
 */
public class TantrixBoard {

    /** starting position. must be odd I believe. */
    static final Location INITIAL_LOCATION = new Location(21, 21);

    /** The 'tantrix'. Map of locations to currently placed tiles. */
    Tantrix tantrix;

    /** color of the loop path */
    private PathColor primaryColor;

    /** tiles that have not yet been placed on the tantrix */
    private HexTileList unplacedTiles;

    /** number of tiles in the puzzle */
    private byte numTiles;

    /**
     * Constructor that creates a new tantrix instance when placing a move.
     * If the new tile to be placed is in the edge row of the grid, then
     * we need to increase the size of the grid by one in that direction and
     * also only render the inside cells.
     * @param board current tantrix state.
     * @param placement new piece to add to the tantrix and its positioning.
     */
    public TantrixBoard(TantrixBoard board, TilePlacement placement) {
        initializeFromOldBoard(board);

        boolean removed = this.unplacedTiles.remove(placement.getTile());
        assert(removed) : "Did not remove " + placement.getTile() + " from " + unplacedTiles;
        tantrix = tantrix.placeTile(placement);
    }

    public TantrixBoard(HexTileList initialTiles) {

        HexTileList tileList = (HexTileList) initialTiles.clone();

        numTiles = (byte) tileList.size();

        primaryColor = new HexTiles().getTile(numTiles).getPrimaryColor();
        HexTile tile = tileList.remove(0);
        unplacedTiles = (HexTileList) tileList.clone();

        tantrix = new Tantrix(tantrix, new TilePlacement(tile, INITIAL_LOCATION, Rotation.ANGLE_0));

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
     * Create a board with the specified tile placements (nothing unplaced).
     * @param tiles  specific placements to initialize the board with.
     */
    public TantrixBoard(TilePlacementList tiles, PathColor primaryColor) {
        this.numTiles = (byte)tiles.size();
        this.primaryColor = primaryColor;
        this.unplacedTiles = new HexTileList();
        this.tantrix = new Tantrix(tiles);
    }

    /**
     * If the new location is in the 0 row or column, we will return 1, to indicate
     * that everything has been shifted down and to the right one.
     * @return return the offset (1) if the tantrix was extended. or 0.
     */
    private void initializeFromOldBoard(TantrixBoard board) {

        this.primaryColor = board.primaryColor;
        this.unplacedTiles = (HexTileList) board.unplacedTiles.clone();
        this.numTiles = board.numTiles;
        this.tantrix = new Tantrix(board.tantrix);
    }

    /**
     * @return true if the puzzle is solved.
     */
    public boolean isSolved() {
        return new SolutionVerifier(this).isSolved();
    }

    /**
     * @param currentPlacement where we are now
     * @param direction side to navigate to to find the neighbor. 0 is to the right.
     * @return the indicated neighbor of the specified tile.
     */
    TilePlacement getNeighbor(TilePlacement currentPlacement, byte direction) {
        return tantrix.getNeighbor(currentPlacement, direction);
    }

    /**
     * Take the specified tile and place it where indicated.
     * @param placement the placement containing the new tile to place.
     * @return the new immutable tantrix instance.
     */
    public TantrixBoard placeTile(TilePlacement placement) {
        return new TantrixBoard(this, placement);
    }

    public HexTileList getUnplacedTiles() {
        return unplacedTiles;
    }

    public TilePlacement getLastTile() {
        return tantrix.getLastTile();
    }

    public PathColor getPrimaryColor() {
        return primaryColor;
    }

    public int getNumTiles() {
        return numTiles;
    }

    /**
     * @return a list of all the tiles in the puzzle
     */
    public HexTileList getTiles() {
        HexTileList tiles = new HexTileList();
        tiles.addAll(getUnplacedTiles());
        for (Location loc: getTantrixLocations()) {
            tiles.add(getTilePlacement(loc).getTile());
        }
        return tiles;
    }

    public int getEdgeLength() {
        return tantrix.getEdgeLength();
    }

    /**
     * @return the position of the top left bbox corner
     */
    public Box getBoundingBox() {
        return tantrix.getBoundingBox();
    }

    public Set<Location> getTantrixLocations() {
        return tantrix.keySet();
    }

    /**
     * @param location
     * @return null of there is no placement at that location.
     */
    public TilePlacement getTilePlacement(Location location) {
        return tantrix.get(location);
    }

    public boolean isEmpty(Location loc) {
        return getTilePlacement(loc) == null;
    }

    /**
     * @param currentLocation
     * @param i direction to look in. [0 - 6]
     * @return the specified neighbor
     */
    public Location getNeighborLocation(Location currentLocation, int i) {
        assert currentLocation != null;
        return new NeighborLocator(currentLocation).getNeighborLocation(i);
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
