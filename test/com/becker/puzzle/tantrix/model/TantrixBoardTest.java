// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;
import com.becker.common.math.MathUtil;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class TantrixBoardTest extends TestCase {

    /** instance under test */
    TantrixBoard board;

    HexTiles tiles;
    HexTileList threeTiles;
    //HexTileList fiveTiles;


    @Override
    public void setUp() {
        MathUtil.RANDOM.setSeed(1);
        tiles = new HexTiles();
        threeTiles = tiles.createOrderedList(3);
        //fiveTiles = tiles.createOrderedList(5);
    }

    public void testBoardConstruction() {
        board = place3UnsolvedTiles();
        TilePlacement expLastPlaced =
                new TilePlacement(tiles.getTile(3), new Location(2, 1), Rotation.ANGLE_180);
        assertEquals("Unexpected last tile placed", expLastPlaced, board.getLastTile());
        assertEquals("Unexpected edge length", 4, board.getEdgeLength());
        assertEquals("Unexpected primary path color",
                tiles.getTile(1).getPrimaryColor(), board.getPrimaryColor());
        assertEquals("All the tiles should have been placed",
                0, board.getUnplacedTiles().size());
    }

    public void test3TilePlacement() {
        board = place3SolvedTiles();

        verifyPlacement(new Location(2, 2));
        verifyPlacement(new Location(3, 1));
        verifyPlacement(new Location(3, 2));
    }

    private void verifyPlacement(Location loc) {
        TilePlacement placement = board.getTilePlacement(loc);
        assertNotNull("Placement at " + loc + " was unexpectedly null", placement);
        assertEquals("Unexpected tiles at " + loc,
                loc, placement.getLocation());
    }

    public void testGetNeighborLocation() {
        board = place3UnsolvedTiles();
        assertEquals("Unexpected right neighbor",
                new Location(1, 2), board.getNeighborLocation(new Location(1, 1), 0));
        assertEquals("Unexpected bottom left neighbor",
                new Location(2, 1), board.getNeighborLocation(new Location(1, 1), 4));
        assertEquals("Unexpected bottom right neighbor",
                new Location(2, 2), board.getNeighborLocation(new Location(1, 1), 5));
    }

    public void testGetNeighborFromUnrotatedTile() {
        board = place3SolvedTiles();
        assertEquals("Unexpected right neighbor",
                null, board.getNeighbor(board.getTilePlacement(2, 2), (byte)0));

        TilePlacement bottomLeft = board.getTilePlacement(3, 1);
        assertEquals("Unexpected bottom left neighbor",
                bottomLeft, board.getNeighbor(board.getTilePlacement(2, 2), (byte)4));

        TilePlacement bottomRight = board.getTilePlacement(3, 2);
        assertEquals("Unexpected bottom right neighbor",
                bottomRight, board.getNeighbor(board.getTilePlacement(2, 2), (byte)5));
    }

    public void testGetNeighborFromRotatedTile() {
        board = place3SolvedTiles();
        assertEquals("Unexpected right neighbor",
                null, board.getNeighbor(board.getTilePlacement(3, 2), (byte)0));

        TilePlacement topLeft = board.getTilePlacement(2, 2);
        assertEquals("Unexpected top left neighbor",
                topLeft, board.getNeighbor(board.getTilePlacement(3, 2), (byte)2));

        TilePlacement left = board.getTilePlacement(3, 1);
        assertEquals("Unexpected left neighbor",
                left, board.getNeighbor(board.getTilePlacement(3, 2), (byte)3));
    }

    public void testIsNotSolved() {
        board = place3UnsolvedTiles();
        assertFalse("Unexpectedly solved", board.isSolved());
    }

    public void testIsSolved() {
        board = place3SolvedTiles();
        assertTrue("Unexpectedly not solved", board.isSolved());
    }

    /** constructor places first tile in the middle */
    private TantrixBoard place3UnsolvedTiles() {
        TantrixBoard board = new TantrixBoard(threeTiles);

        TilePlacement tile2 = new TilePlacement(tiles.getTile(2), new Location(2, 0), Rotation.ANGLE_0);
        TilePlacement tile3 = new TilePlacement(tiles.getTile(3), new Location(2, 1), Rotation.ANGLE_180);
        board = new TantrixBoard(board, tile2);
        board = new TantrixBoard(board, tile3);
        return board;
    }

    /** constructor places first tile in the middle */
    private TantrixBoard place3SolvedTiles() {
        TantrixBoard board = new TantrixBoard(threeTiles);

        TilePlacement tile2 = new TilePlacement(tiles.getTile(2), new Location(2, 1), Rotation.ANGLE_60);
        TilePlacement tile3 = new TilePlacement(tiles.getTile(3), new Location(2, 0), Rotation.ANGLE_120);
        board = new TantrixBoard(board, tile2);
        board = new TantrixBoard(board, tile3);
        return board;
    }
}