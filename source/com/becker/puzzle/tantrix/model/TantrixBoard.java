// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;


/**
 *  Immutable representation of the current stat of the tantrix puzzle.
 *
 *  @author Barry Becker
 */
public class TantrixBoard {

    static final byte MAX_SIZE = 9;
    static final byte HEX_SIDES = 6;

    /** The number of positions is n^2 * n^2.   */
    protected int n_ = MAX_SIZE;

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
     * Copy constructor
     */
    public TantrixBoard(TantrixBoard b) {

        this(b.getEdgeLength());
        this.primaryColor = b.primaryColor;
        this.lastTile = b.lastTile;
        this.unplacedTiles = (HexTileList) b.unplacedTiles.clone();
        this.numTiles = b.numTiles;

        for (int i=0; i < n_; i++) {
           for (int j=0; j < n_; j++) {
               setTilePlacement(b.getTilePlacement(i, j));
           }
        }
    }

    /**
     * Constructor that creates a new board instance when placing a move.
     * TODO - if the new tile to be placed is in the edge row of the grid
     * we need to increase the size of the grid by one in that direction
     * also only render the inside rows.
     * @param board
     * @param placement
     */
    public TantrixBoard(TantrixBoard board, TilePlacement placement) {
        this(board);
        boolean removed = this.unplacedTiles.remove(placement.getTile());
        assert(removed);
        this.setTilePlacement(placement);
    }

    public TantrixBoard(HexTileList tileList) {

        n_ = (int)Math.ceil(Math.sqrt(tileList.size())) + 1;
        reset();
        numTiles = (byte) tileList.size();
        HexTile tile = tileList.remove(0);
        unplacedTiles = (HexTileList) tileList.clone();

        lastTile = new TilePlacement(tile, new Location(n_/2, n_/2), Rotation.ANGLE_0);

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
     * Private Constructor
     */
    private TantrixBoard(int size) {
        assert(size > 1 && size < MAX_SIZE);
        n_ = size;
        reset();
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

        while (currentTile != null && currentTile != lastTile) {

            previousTile = currentTile;
            currentTile = findNeighborTile(currentTile, previousTile);
            numVisited++;
        }

        return (numVisited == numTiles && currentTile == lastTile);
    }

    /**
     * Loop through the edges until we find the primary color.
     * If it does not direct us back to where we came from then go that way.
     * @param currentPlacement
     * @return the next tile in the path if there is one. Otherwise null
     */
    private TilePlacement findNeighborTile(TilePlacement currentPlacement, TilePlacement previousTile) {

        for (byte i=0; i<HEX_SIDES; i++) {
            PathColor color = currentPlacement.getPathColor(i);
            if (color == primaryColor) {
                TilePlacement nbr = getNeighbor(currentPlacement, i);
                if (nbr != previousTile) {
                    return nbr;
                }
            }
        }
        return null;
    }

    /**
     * @param currentTile where we are now
     * @param direction side to navigate to to find the neighbor. 0 is to the right.
     * @return the indicated neighbor of the specified tile.
     */
    TilePlacement getNeighbor(TilePlacement currentTile, byte direction) {

        if (currentTile == null) return null;
        return getTilePlacement(getNeighborLocation(currentTile, direction));
    }

    /**
     * @param currentTile where we are now
     * @param direction side to navigate to to find the neighbor. 0 is to the right.
     * @return the indicated neighbor of the specified tile.
     */
    Location getNeighborLocation(TilePlacement currentTile, byte direction) {

        assert (currentTile != null);
        int offset = currentTile.isOnOddRow()? -1 : 0;
        Location loc = currentTile.getLocation();
        Location nbrLoc = null;

        switch (direction) {
            case 0 : nbrLoc = new Location(loc.getRow(), loc.getCol() + 1); break;
            case 1 : nbrLoc = new Location(loc.getRow() - 1, loc.getCol() + offset + 1); break;
            case 2 : nbrLoc = new Location(loc.getRow() - 1, loc.getCol() + offset); break;
            case 3 : nbrLoc = new Location(loc.getRow(), loc.getCol() - 1); break;
            case 4 : nbrLoc = new Location(loc.getRow() + 1, loc.getCol() + offset); break;
            case 5 : nbrLoc = new Location(loc.getRow() + 1, loc.getCol() + offset +1); break;
            default : assert false;
        }
        return nbrLoc;
    }

    /**
     * Take the specified tile and place it where indicated.
     * @param placement
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
    private void reset() {
        tiles = new TilePlacement[n_][n_];
        for (int i=0; i<n_; i++)  {
           for (int j=0; j<n_; j++) {
               tiles[i][j] = null; //new TilePlacement(new Location(i, j));
           }
        }
    }

    /**
     * @return retrieve the edge size of the board.
     */
    public final int getEdgeLength() {
        return n_;
    }

    /**
     * @return the cell in the bigCellArray at the specified location.
     */
    public TilePlacement getTilePlacement(int row, int col) {
        return tiles[row][col];
    }

    public TilePlacement getTilePlacement(Location location) {
        byte row = location.getRow();
        byte col = location.getCol();
        if (row<0 || row >= n_ || col <0 || col>=n_) return null;
        return tiles[location.getRow()][location.getCol()];
    }

    private void setTilePlacement(TilePlacement tile) {
        if (tile != null) {
            Location loc = tile.getLocation();
            tiles[loc.getRow()][loc.getCol()] = tile;
        }
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder("\n");
        for (int row=0; row < n_; row++) {
            for (int col=0; col < n_; col++) {
                bldr.append(getTilePlacement(row, col));
                bldr.append(" ");
            }
            bldr.append("\n");
        }

        return bldr.toString();
    }
}
