// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;


/**
 * @author Barry Becker
 */
public class TantrixTstUtil {

    public static final HexTiles TILES = new HexTiles();
    public static final HexTileList threeTiles = TILES.createOrderedList(3);

    private TantrixTstUtil() {}

    /** Places first tile in the middle and one of two remaining placed */
    public static TantrixBoard place2of3TilesA() {
        TantrixBoard board = new TantrixBoard(threeTiles);

        TilePlacement tile2 = new TilePlacement(TILES.getTile(2), new Location(2, 1), Rotation.ANGLE_0);
        board = new TantrixBoard(board, tile2);
        return board;
    }

    /** Places first tile in the middle and one of two remaining placed */
    public static TantrixBoard place2of3TilesB() {
        TantrixBoard board = new TantrixBoard(threeTiles);

        TilePlacement tile2 = new TilePlacement(TILES.getTile(3), new Location(2, 1), Rotation.ANGLE_120);
        board = new TantrixBoard(board, tile2);
        return board;
    }

    /** Places first tile in the middle */
    public static TantrixBoard place3UnsolvedTiles() {
        TantrixBoard board = new TantrixBoard(threeTiles);

        TilePlacement tile2 = new TilePlacement(TILES.getTile(2), new Location(2, 0), Rotation.ANGLE_0);
        TilePlacement tile3 = new TilePlacement(TILES.getTile(3), new Location(2, 1), Rotation.ANGLE_180);
        board = new TantrixBoard(board, tile2);
        board = new TantrixBoard(board, tile3);
        return board;
    }

    /** constructor places first tile in the middle */
    public static TantrixBoard place3SolvedTiles() {
        System.out.println("3 tiles =" + threeTiles);
        TantrixBoard board = new TantrixBoard(threeTiles);

        TilePlacement tile2 = new TilePlacement(TILES.getTile(2), new Location(2, 1), Rotation.ANGLE_60);
        TilePlacement tile3 = new TilePlacement(TILES.getTile(3), new Location(2, 0), Rotation.ANGLE_120);
        board = new TantrixBoard(board, tile2);
        board = new TantrixBoard(board, tile3);
        return board;
    }
}