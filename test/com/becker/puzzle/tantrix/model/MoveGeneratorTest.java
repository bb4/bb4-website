// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;
import junit.framework.TestCase;
import java.util.List;

import static com.becker.puzzle.tantrix.model.TantrixTstUtil.*;

/**
 * @author Barry Becker
 */
public class MoveGeneratorTest extends TestCase {

    /** instance under test */
    MoveGenerator generator;
    TantrixBoard board;


    public void testMoveGenerationFromTwoOfThreeTilesA() {
        board = place2of3Tiles_OneThenTwo();
        generator = new MoveGenerator(board);

        List<TilePlacement> moves = generator.generateMoves();

        assertEquals("Unexpected number of next moves.", 1, moves.size());
        assertEquals("Unexpected first next move.",
            new TilePlacement(TILES.getTile(3), new Location(22, 20), Rotation.ANGLE_120), moves.get(0));
        //assertEquals("Unexpected second next move.",
        //    new TilePlacement(TILES.getTile(3), new Location(1, 0), Rotation.ANGLE_0), moves.get(1));
    }

    public void testMoveGenerationFromTwoOfThreeTilesB() {
        board = place2of3Tiles_OneThenThree();
        generator = new MoveGenerator(board);

        List<TilePlacement> moves = generator.generateMoves();

        System.out.println("moves = " + moves);
        assertEquals("Unexpected number of next moves.", 1, moves.size());
    }

    public void testMoveGenerationFromFirstTileOfThree() {
        board = new TantrixBoard(threeTiles);
        generator = new MoveGenerator(board);

        List<TilePlacement> moves = generator.generateMoves();

        System.out.println("moves = " + moves);
        assertEquals("Unexpected number of next moves.", 8, moves.size());
    }


    public void testPlacementDoesNotFit0() {
        board = place2of3Tiles_OneThenThree();

        TilePlacement tile2 = new TilePlacement(TILES.getTile(2), loc(2, 0), Rotation.ANGLE_0);
        generator = new MoveGenerator(board);
        assertFalse("Unexpectedly fit.", generator.fits(tile2));
    }

    public void testPlacementDoesNotFit60() {
        board = place2of3Tiles_OneThenThree();

        TilePlacement tile2 = new TilePlacement(TILES.getTile(2), loc(2, 0), Rotation.ANGLE_60);
        generator = new MoveGenerator(board);
        assertFalse("Unexpectedly fit.", generator.fits(tile2));
    }

    public void testPlacementFits() {
        board = place2of3Tiles_OneThenThree();

        TilePlacement tile2 = new TilePlacement(TILES.getTile(2), loc(2, 0), Rotation.ANGLE_300);
        generator = new MoveGenerator(board);
        assertTrue("Unexpectedly did not fit.", generator.fits(tile2));
    }

    public void testTile2PlacementFits() {
        board = place1of3Tiles_startingWithTile2();

        TilePlacement tile2 = new TilePlacement(TILES.getTile(3), loc(0, 0), Rotation.ANGLE_60);
        generator = new MoveGenerator(board);
        System.out.println(board);

        assertTrue("Unexpectedly fit.", generator.fits(tile2));
    }

    public void testile2PlacementDoesNotFits() {
        board = place1of3Tiles_startingWithTile2();

        TilePlacement tile2 = new TilePlacement(TILES.getTile(3), loc(0, 0), Rotation.ANGLE_300);
        generator = new MoveGenerator(board);
        System.out.println(board);

        assertFalse("Unexpectedly did not fit.", generator.fits(tile2));
    }
}

