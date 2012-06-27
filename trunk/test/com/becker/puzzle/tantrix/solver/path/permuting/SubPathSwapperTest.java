// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path.permuting;

import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.model.PathColor;
import com.becker.puzzle.tantrix.model.Rotation;
import com.becker.puzzle.tantrix.model.TilePlacement;
import com.becker.puzzle.tantrix.model.TilePlacementList;
import com.becker.puzzle.tantrix.solver.path.TantrixPath;

import static com.becker.puzzle.tantrix.TantrixTstUtil.TILES;
import static com.becker.puzzle.tantrix.TantrixTstUtil.loc;

/**
 * @author Barry Becker
 */
public class SubPathSwapperTest extends SubPathMutatorBase {

    @Override
    public SubPathMutator createMutator(PathColor primaryColor) {
        return new SubPathSwapper(primaryColor);
    }

    @Override
    protected void verifyMutated1TilePath(TantrixPath resultPath) {
        assertEquals("Unexpected result for " + mutator,
                1, resultPath.size());

        TilePlacement first = new TilePlacement(TILES.getTile(2), loc(2, 0), Rotation.ANGLE_300);
        TilePlacementList expList = new TilePlacementList(first);
        assertEquals("Unexpected swap.", expList, resultPath.getTilePlacements());
    }

    @Override
    protected void verifyMutated2TilePath(TantrixPath resultPath) {
        assertEquals("unexpected size.", 2, resultPath.size());

        TilePlacement first = new TilePlacement(TILES.getTile(2), loc(2, 0), Rotation.ANGLE_300);
        TilePlacement second = new TilePlacement(TILES.getTile(3), loc(2, 1), Rotation.ANGLE_300);

        TilePlacementList expList = new TilePlacementList(first, second);

        assertEquals("Unexpected swap.", expList, resultPath.getTilePlacements());
    }

    @Override
    protected void verifyMutated3TilePath(TantrixPath resultPath) {
        assertEquals("unexpected size.", 3, resultPath.size());

        TilePlacement first =
                new TilePlacement(TILES.getTile(1), new Location(19, 20), Rotation.ANGLE_240);
        TilePlacement second =
                new TilePlacement(TILES.getTile(5), new Location(19, 21), Rotation.ANGLE_0);
        TilePlacement third =
                new TilePlacement(TILES.getTile(2), new Location(19, 22), Rotation.ANGLE_240);

        TilePlacementList expList = new TilePlacementList(first, second, third);
        assertEquals("Unexpected reversal.", expList, resultPath.getTilePlacements());
    }

    @Override
    protected void verifyMutated3aTilePath(TantrixPath resultPath) {
        assertEquals("unexpected size.", 3, resultPath.size());

        TilePlacement first =
                new TilePlacement(TILES.getTile(2), new Location(23, 22), Rotation.ANGLE_240);
        TilePlacement second =
                new TilePlacement(TILES.getTile(1), new Location(23, 21), Rotation.ANGLE_0);
        TilePlacement third =
                new TilePlacement(TILES.getTile(4), new Location(22, 20), Rotation.ANGLE_60);  // was 23, 22

        TilePlacementList expList = new TilePlacementList(first, second, third);
        assertEquals("Unexpected reversal.", expList, resultPath.getTilePlacements());
    }
}