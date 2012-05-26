// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path;

import com.becker.common.geometry.Location;
import com.becker.common.math.MathUtil;
import com.becker.puzzle.tantrix.model.*;
import junit.framework.TestCase;

import static com.becker.puzzle.tantrix.TantrixTstUtil.*;

/**
 * @author Barry Becker
 */
public class TantrixPathTest extends TestCase {

    /** instance under test */
    private TantrixPath path;


    public void test2TilePathConstruction() {

        HexTile pivotTile = TILES.getTile(1);

        TilePlacement firstTilePlacement =
                new TilePlacement(TILES.getTile(2), loc(2, 1), Rotation.ANGLE_0);
        TilePlacement secondTilePlacement =
                new TilePlacement(TILES.getTile(3), loc(1, 2), Rotation.ANGLE_0);

        TilePlacementList tileList = new TilePlacementList();
        tileList.add(firstTilePlacement);
        tileList.add(secondTilePlacement);
        path = new TantrixPath(tileList, pivotTile.getPrimaryColor());

        assertEquals("Unexpected path tiles", tileList, path.getTilePlacements());
    }

    /** we expect an exception because the tiles passed to the constructor do not form a primary path */
    public void testNonLoopPathConstruction() {
        TantrixBoard board = place3UnsolvedTiles();

        path =  new TantrixPath(board.getTantrix(), board.getPrimaryColor());

        assertEquals("Unexpected length", 3, path.size());
    }

    /** we expect an exception because the tiles passed to the constructor do not form a primary path */
    public void testInvalidPathConstruction() {
        TantrixBoard board = place3NonPathTiles();
        try {
            new TantrixPath(board.getTantrix(), board.getPrimaryColor());
            fail("did not expect to get here");
        }
        catch (AssertionError e) {
            // success
        }
    }

    public void testIsLoop() {
        TantrixBoard board = place3SolvedTiles();
        TantrixPath path = new TantrixPath(board.getTantrix(), board.getPrimaryColor());
        assertTrue("Unexpectedly not a loop", path.isLoop());
    }

    public void testIsNotLoop() {
        TantrixBoard board = place3UnsolvedTiles();
        TantrixPath path = new TantrixPath(board.getTantrix(), board.getPrimaryColor());
        assertFalse("Unexpectedly a loop", path.isLoop());
    }

    public void testFindRandomNeighbor() {
        MathUtil.RANDOM.setSeed(0);
        TantrixBoard board = place3UnsolvedTiles();
        TantrixPath path = new TantrixPath(board.getTantrix(), board.getPrimaryColor());
        TantrixPath nbr = (TantrixPath) path.getRandomNeighbor(0.5);

        TilePlacementList tiles =
                new TilePlacementList(
                        new TilePlacement(TILES.getTile(2), new Location(22, 20), Rotation.ANGLE_300),
                        new TilePlacement(TILES.getTile(1), new Location(21, 21), Rotation.ANGLE_0),
                        new TilePlacement(TILES.getTile(3), new Location(22, 21), Rotation.ANGLE_240));
        TantrixPath expectedPath = new TantrixPath(tiles, PathColor.YELLOW);

        TantrixPath expNbr = new TantrixPath(tiles, board.getPrimaryColor());
        assertEquals("Unexpected random neighbor.", expectedPath, nbr);
    }
}