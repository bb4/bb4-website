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
    Tantrix tantrix;


    public void testFindBorderForFirstTileOfThree() {
        tantrix = new TantrixBoard(threeTiles).tantrix;
        borderFinder = new BorderFinder(tantrix, PathColor.YELLOW);

        Set<Location> positions = borderFinder.findBorderPositions();
        assertEquals("Unexpected number of border locations.", 6, positions.size());
    }

    public void testFindBorderForTwoOfThreeTilesA() {
        tantrix = place2of3TilesA().tantrix;
        borderFinder = new BorderFinder(tantrix, PathColor.YELLOW);

        Set<Location> positions = borderFinder.findBorderPositions();
        assertEquals("Unexpected number of border locations.", 8, positions.size());
    }

    public void testFindBorderForTwoOfThreeTilesB() {
        tantrix = place2of3TilesB().tantrix;
        borderFinder = new BorderFinder(tantrix, PathColor.YELLOW);
        System.out.println(tantrix);
        Set<Location> positions = borderFinder.findBorderPositions();
        System.out.println(positions);
        assertEquals("Unexpected number of border locations.", 8, positions.size());
    }

    public void testFindBorderForThreeSolvedTiles() {
        tantrix = place3SolvedTiles().tantrix;
        borderFinder = new BorderFinder(tantrix, PathColor.YELLOW);

        Set<Location> positions = borderFinder.findBorderPositions();

        assertEquals("Unexpected number of border locations.", 9, positions.size());
    }


}