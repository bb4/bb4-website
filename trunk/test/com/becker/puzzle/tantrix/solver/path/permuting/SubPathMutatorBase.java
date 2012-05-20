// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path.permuting;

import com.becker.puzzle.tantrix.model.*;
import com.becker.puzzle.tantrix.solver.path.TantrixPath;
import junit.framework.TestCase;

import static com.becker.puzzle.tantrix.TantrixTstUtil.TILES;
import static com.becker.puzzle.tantrix.TantrixTstUtil.loc;

/**
 * Base class for sub path mutator tests.
 * @author Barry Becker
 */
public abstract class SubPathMutatorBase extends TestCase {

    /** instance under test */
    protected SubPathMutator mutator;

    /** creates the mutator to test */
    protected abstract SubPathMutator createMutator(TilePlacement pivot, PathColor primaryColor);


    public void testMutating1TilePath() {

        HexTile pivotTile = TILES.getTile(1);

        TilePlacement pivotTilePlacement =
                new TilePlacement(pivotTile, loc(1, 1), Rotation.ANGLE_0);
        TilePlacement firstTilePlacement =
                new TilePlacement(TILES.getTile(2), loc(2, 1), Rotation.ANGLE_0);

        TilePlacementList tileList = new TilePlacementList();
        tileList.add(firstTilePlacement);
        TantrixPath path = new TantrixPath(tileList, pivotTile.getPrimaryColor());
        mutator = createMutator(pivotTilePlacement, pivotTile.getPrimaryColor());

        TantrixPath resultPath = mutator.mutate(path);

        verifyMutated1TilePath(resultPath);
    }

    public void testMutating2TilePath() {

        HexTile pivotTile = TILES.getTile(1);

        TilePlacement pivotTilePlacement =
                new TilePlacement(pivotTile, loc(1, 1), Rotation.ANGLE_0);
        TilePlacement firstTilePlacement =
                new TilePlacement(TILES.getTile(2), loc(2, 1), Rotation.ANGLE_0);
        TilePlacement secondTilePlacement =
                new TilePlacement(TILES.getTile(3), loc(1, 2), Rotation.ANGLE_0);

        TilePlacementList tileList = new TilePlacementList();
        tileList.add(firstTilePlacement);
        tileList.add(secondTilePlacement);
        TantrixPath path = new TantrixPath(tileList, pivotTile.getPrimaryColor());
        mutator = createMutator(pivotTilePlacement, pivotTile.getPrimaryColor());

        TantrixPath resultPath = mutator.mutate(path);
        verifyMutated2TilePath(resultPath);
    }

    protected abstract void verifyMutated1TilePath(TantrixPath resultPath);
    protected abstract void verifyMutated2TilePath(TantrixPath resultPath);


}