// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Box;
import com.becker.common.geometry.Location;

/**
 *  Immutable representation of the current stat of the tantrix puzzle.
 *
 *  @author Barry Becker
 */
public class TantrixBoard {

    static final byte HEX_SIDES = 6;

    /** The number of positions is n^2 * n^2. */
    private int n_ = 3;

    private TilePlacement[][] tiles;

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
        n_ =  board.getEdgeLength();
        int offset = initializeFromOldBoard(board, placement.getLocation());
        Location loc = placement.getLocation();
        assert new Box(0, 0, n_-1, n_-1).contains(loc) :
                "Location out of bounds: " + loc + " offset="+ offset + " \n" + this;

        boolean removed = this.unplacedTiles.remove(placement.getTile());
        if (offset != 0) {
            Location oldLoc = placement.getLocation();
            Location newLoc = new Location(oldLoc.getRow() + offset, oldLoc.getCol() + offset);
            placement = new TilePlacement(placement.getTile(), newLoc, placement.getRotation());
        }
        lastTile = placement;
        assert(removed) : "Did not remove " + placement.getTile() + " from " + unplacedTiles;

        setTilePlacement(placement);
    }

    /**
     * If the new location is in the 0 row or column, we will return 1, to indicate
     * that everything has been shifted down and to the right one.
     * @return return the offset (1) if the board was extended. or 0.
     */
    private int initializeFromOldBoard(TantrixBoard board, Location location) {

        this.primaryColor = board.primaryColor;
        this.lastTile = board.lastTile;
        this.unplacedTiles = (HexTileList) board.unplacedTiles.clone();
        this.numTiles = board.numTiles;
        int oldN = n_;
        byte offset = adjustRangeAndReturnOffset(location);
        createPlacementArray();
        for (int i = 0; i < oldN; i++) {
            for (int j = 0; j < oldN; j++) {
                setTilePlacement(board.getTilePlacement(i, j), offset);
            }
        }
        return offset;
    }

    /**
     * Adjusts n_ if needed
     * @return the amount to offset the row/col position of the tiles when copying them over.
     */
    private byte adjustRangeAndReturnOffset(Location location)  {

        byte offset = 0;
        int oldDim = n_;
        if (location != null) {
            if (location.getRow() == 0 || location.getCol() == 0) {
                n_++;
                offset = 1;
            }
            else if (location.getRow() == (n_-1) || location.getCol() == (n_-1)) {
                n_++;
            }
        }
        if (n_ > oldDim)
            System.out.println("n increased to " + n_ + " because of "+ location + " offset=" + offset);
        return offset;
    }

    public TantrixBoard(HexTileList initialTiles) {

        HexTileList tileList = (HexTileList) initialTiles.clone();
        n_ = (int)Math.ceil(Math.sqrt(tileList.size())) + 5;
        createPlacementArray();
        numTiles = (byte) tileList.size();
        HexTile tile = tileList.remove(0);
        unplacedTiles = (HexTileList) tileList.clone();
        primaryColor = tile.getPrimaryColor();

        Location initialLoc = new Location(n_/2, n_/2);
        lastTile = new TilePlacement(tile, initialLoc, Rotation.ANGLE_0);

        this.setTilePlacement(lastTile);
        /* // this shows all the tiles
        for (byte i = 0; i < tileList.size(); i++) {
            byte row = (byte) (i / n_);
            byte col = (byte) (i % n_);
            tiles[row][col] =
                    new TilePlacement(tileList.get(i), new Location(row, col), Rotation.ANGLE_0);
        } */
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
        TilePlacement previousTile;

        do {
            previousTile = currentTile;
            currentTile = findNeighborTile(currentTile, previousTile);
            numVisited++;
        } while (currentTile != null && !currentTile.equals(lastTile));

        return (numVisited == numTiles && currentTile.equals(lastTile));
    }

    /**
     * Loop through the edges until we find the primary color.
     * If it does not direct us back to where we came from then go that way.
     * @param currentPlacement
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

        int offset = (currentLocation.getRow() % 2 == 0) ? -1 : 0;
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

    /**
     * return to original state before attempting solution.
     * Non original values become 0.
     */
    private void createPlacementArray() {
        tiles = new TilePlacement[n_][n_];
    }

    /**
     * @return retrieve the edge size of the board.
     */
    public final int getEdgeLength() {
        return n_;
    }

    /**
     * @return the placement at the specified location.
     */
    public TilePlacement getTilePlacement(int row, int col) {
        return tiles[row][col];
    }

    public TilePlacement getTilePlacement(Location location) {
        byte row = location.getRow();
        byte col = location.getCol();
        if (row<0 || row >= n_ || col<0 || col>=n_) {
            return null;
        }
        return tiles[row][col];
    }

    private void setTilePlacement(TilePlacement tile) {
        setTilePlacement(tile, (byte) 0);
    }

    private void setTilePlacement(TilePlacement placement, byte offset) {
        if (placement == null) return;
        Location loc = placement.getLocation();
        int row = loc.getRow() + offset;
        int col = loc.getCol() + offset;
        assert (row < n_ && col < n_) : "n_="+ n_ +" loc=" +row + "," +col;
        assert tiles[row][col] == null;

        tiles[row][col] =
            new TilePlacement(placement.getTile(), new Location(row, col), placement.getRotation());
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder("\n");
        for (int row=0; row < n_; row++) {
            for (int col=0; col < n_; col++) {
                TilePlacement placement = getTilePlacement(row, col);
                if (placement == null) {
                    bldr.append("[  ----- ]");
                } else {
                    bldr.append(placement);
                }
                bldr.append(" ");
            }
            bldr.append("\n");
        }

        return bldr.toString();
    }
}
