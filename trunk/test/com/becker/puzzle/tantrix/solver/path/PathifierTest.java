// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path;

import com.becker.puzzle.tantrix.model.Rotation;
import com.becker.puzzle.tantrix.model.Tantrix;
import com.becker.puzzle.tantrix.model.TilePlacement;
import com.becker.puzzle.tantrix.model.TilePlacementList;
import junit.framework.TestCase;

import static com.becker.puzzle.tantrix.TantrixTstUtil.TILES;
import static com.becker.puzzle.tantrix.TantrixTstUtil.loc;

/**
 * @author Barry Becker
 */
public class PathifierTest extends TestCase {

    /** instance under test */
    private Pathifier pathifier;


    @Override
    public void setUp() {
        pathifier = new Pathifier(TILES.getTile(1).getPrimaryColor());
    }


    public void test2TilePathConstruction() {

        TilePlacement firstTilePlacement =
                new TilePlacement(TILES.getTile(2), loc(2, 1), Rotation.ANGLE_0);
        TilePlacement secondTilePlacement =
                new TilePlacement(TILES.getTile(3), loc(1, 2), Rotation.ANGLE_0);

        TilePlacementList tileList = new TilePlacementList();
        tileList.add(firstTilePlacement);
        tileList.add(secondTilePlacement);

        assertEquals("Unexpected tiles", tileList, pathifier.reorder(new Tantrix(tileList)));
    }

    public void test3TilePathConstruction() {

        TilePlacement firstTilePlacement =
                new TilePlacement(TILES.getTile(1), loc(1, 1), Rotation.ANGLE_0);
        TilePlacement secondTilePlacement =
                new TilePlacement(TILES.getTile(2), loc(2, 1), Rotation.ANGLE_0);
        TilePlacement thirdTilePlacement =
                new TilePlacement(TILES.getTile(3), loc(1, 2), Rotation.ANGLE_0);

        TilePlacementList tileList = new TilePlacementList();
        tileList.add(firstTilePlacement);
        tileList.add(secondTilePlacement);
        tileList.add(thirdTilePlacement);

        assertEquals("Unexpected tiles", tileList, pathifier.reorder(new Tantrix(tileList)));
    }

    /** We should get an error if there is no path that can be found from rearranging the tiles without rotation. */
    public void testOutOfOrder2TilePathConstruction() {

        TilePlacement firstTilePlacement =
                new TilePlacement(TILES.getTile(2), loc(2, 1), Rotation.ANGLE_60);
        TilePlacement secondTilePlacement =
                new TilePlacement(TILES.getTile(3), loc(1, 2), Rotation.ANGLE_120);

        TilePlacementList tileList = new TilePlacementList();
        tileList.add(firstTilePlacement);
        tileList.add(secondTilePlacement);

        try {
            pathifier.reorder(new Tantrix(tileList));
            fail("did not expect to get here");
        } catch (AssertionError e) {
            // success
        }
    }
}