// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path.permuting;

import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.TantrixTstUtil;
import com.becker.puzzle.tantrix.model.*;
import com.becker.puzzle.tantrix.solver.path.TantrixPath;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;
import static com.becker.puzzle.tantrix.TantrixTstUtil.*;

/**
 * @author Barry Becker
 */
public class PathPermuterTest extends TestCase {

    /** instance under test */
    private PathPermuter permuter;


    public void testPermut3TilePath() {
        TantrixBoard board = TantrixTstUtil.place3UnsolvedTiles();
        permuter = new PathPermuter(new TantrixPath(board));

        List<TantrixPath> permutedPathList = permuter.findPermutedPaths(1);
        assertEquals("Unexpected number of permuted paths.", 7, permutedPathList.size());

        // for each of the 7 permuted paths, we expect that tile 2 will be the middle/pivot tile.
        Location upperLeft = new Location(21, 21);
        Location upperRight = new Location(21,22);
        TilePlacement pivot = new TilePlacement(TILES.getTile(2), new Location(22, 21), Rotation.ANGLE_0);
        HexTile tile1 = TILES.getTile(1);
        HexTile tile3 = TILES.getTile(3);

        List<TantrixPath> expPathList =  Arrays.asList(
            createPath(new TilePlacement(tile3, upperLeft, Rotation.ANGLE_0), pivot, new TilePlacement(tile1, upperRight, Rotation.ANGLE_300)),
            createPath(new TilePlacement(tile3, upperLeft, Rotation.ANGLE_60), pivot, new TilePlacement(tile1, upperRight, Rotation.ANGLE_0)),
            createPath(new TilePlacement(tile3, upperLeft, Rotation.ANGLE_60), pivot, new TilePlacement(tile1, upperRight, Rotation.ANGLE_300)), // complete loop!
            createPath(new TilePlacement(tile1, upperLeft, Rotation.ANGLE_60), pivot, new TilePlacement(tile3, upperRight, Rotation.ANGLE_300)),
            createPath(new TilePlacement(tile1, upperLeft, Rotation.ANGLE_60), pivot, new TilePlacement(tile3, upperRight, Rotation.ANGLE_0)),
            createPath(new TilePlacement(tile1, upperLeft, Rotation.ANGLE_0), pivot, new TilePlacement(tile3, upperRight, Rotation.ANGLE_300)),  // complete loop!
            createPath(new TilePlacement(tile1, upperLeft, Rotation.ANGLE_0), pivot, new TilePlacement(tile3, upperRight, Rotation.ANGLE_0))
        );

        assertEquals("Unexpect permuted paths.", expPathList, permutedPathList);
    }


    private TantrixPath createPath(TilePlacement placement1, TilePlacement placement2, TilePlacement placement3) {
        return  new TantrixPath(new TilePlacementList(placement1, placement2, placement3), PathColor.YELLOW);
    }
}
