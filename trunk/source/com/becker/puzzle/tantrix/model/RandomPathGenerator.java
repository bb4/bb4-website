// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;
import com.becker.common.math.MathUtil;
import com.becker.puzzle.tantrix.solver.TantrixPath;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.becker.puzzle.tantrix.model.TantrixBoard.HEX_SIDES;

/**
 * Generates random continuous primary color paths that do not necessarily match on secondary colors.
 *
 * @author Barry Becker
 */
public class RandomPathGenerator {

    private TantrixBoard initialBoard;
    private PathColor primaryColor;


    /**
     * Constructor
     */
    public RandomPathGenerator(TantrixBoard board) {
        this.initialBoard = board;
        primaryColor = board.getPrimaryColor();
    }

    /**
     * @return a random path.
     */
    public TantrixPath generateRandomPath() {
        TilePlacementList moves = new TilePlacementList();

        TantrixBoard currentBoard = initialBoard;

        while (!currentBoard.getUnplacedTiles().isEmpty())   {
            TilePlacement placement = generateRandomPathMove(currentBoard);
            currentBoard = currentBoard.placeTile(placement);
        }
        return new TantrixPath(moves);
    }

    /**
     * For each unplaced tile, find all valid placements given current configuration.
     * Valid placements must extend the primary path.
     * @return List of all valid tile placements for the current tantrix state.
     */
    public TilePlacement generateRandomPathMove(TantrixBoard board) {

        HexTileList unplacedTiles = board.getUnplacedTiles();
        HexTile tile = unplacedTiles.get(MathUtil.RANDOM.nextInt(unplacedTiles.size()));

        return findWeakPlacementForTile(board, tile);
    }


    /**
     * @return the first placement for the specified tile which matches the primary path
     *     but not necessarily the secondary paths.
     */
    private TilePlacement findWeakPlacementForTile(TantrixBoard board, HexTile tile) {

        TilePlacement lastPlaced = board.getLastTile();
        TilePlacementList validFits = getFittingPlacements(tile, lastPlaced.getLocation());

        assert validFits.size() == 2 : "There must be two ways for the primary path to fit";
        return validFits.get(MathUtil.RANDOM.nextInt(2));
    }

    /**
     * @param tile the tile to place.
     * @param loc the location to try and place it at.
     * @return the placement if one could be found, else null.
     */
    private TilePlacementList getFittingPlacements(HexTile tile, Location loc) {
        TilePlacement placement = new TilePlacement(tile, loc, Rotation.ANGLE_0);
        TilePlacementList validPlacements = new TilePlacementList();

        for (int i = 0; i < HEX_SIDES; i++) {
            if (fits(placement)) {
                validPlacements.add(placement);
            }
            placement = placement.rotate();
        }
        return validPlacements;
    }

    /**
     * The tile fits if the primary path fits.
     * @param placement the tile to check for a valid fit.
     * @return true of the tile fits
     */
    boolean fits(TilePlacement placement) {

        for (byte i = 0; i < HEX_SIDES; i++) {
            TilePlacement nbr = initialBoard.getNeighbor(placement, i);

            if (nbr != null) {
                PathColor pathColor = placement.getPathColor(i);

                if (pathColor == primaryColor && pathColor == nbr.getPathColor((byte)(i+3))) {
                    return true;
                }
            }
        }

        return false;
    }
}
