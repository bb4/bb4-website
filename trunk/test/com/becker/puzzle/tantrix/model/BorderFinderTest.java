// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;
import junit.framework.TestCase;
import java.util.Set;

import static com.becker.puzzle.tantrix.model.TantrixTstUtil.*;

/**
 * @author Barry Becker
 */
public class BorderFinderTest extends TestCase {

    /** instance under test */
    BorderFinder borderFinder;
    TantrixBoard board;


    public void testFindBorderForFirstTileOfThree() {
        board = new TantrixBoard(threeTiles);
        borderFinder = new BorderFinder(board);

        Set<Location> positions = borderFinder.findBorderPositions();

        assertEquals("Unexpected number of border locations.", 6, positions.size());
    }

    public void testFindBorderForTwoOfThreeTilesA() {
        board = place2of3TilesA();
        borderFinder = new BorderFinder(board);

        Set<Location> positions = borderFinder.findBorderPositions();

        assertEquals("Unexpected number of border locations.", 8, positions.size());
    }

    public void testFindBorderForTwoOfThreeTilesB() {
        board = place2of3TilesB();
        borderFinder = new BorderFinder(board);

        Set<Location> positions = borderFinder.findBorderPositions();

        assertEquals("Unexpected number of border locations.", 8, positions.size());
    }

    public void testFindBorderForThreeSolvedTiles() {
        board = place3SolvedTiles();
        borderFinder = new BorderFinder(board);

        Set<Location> positions = borderFinder.findBorderPositions();

        assertEquals("Unexpected number of border locations.", 9, positions.size());
    }


}