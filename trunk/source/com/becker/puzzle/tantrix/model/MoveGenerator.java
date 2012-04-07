// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;

import java.util.*;
import static com.becker.puzzle.tantrix.model.TantrixBoard.HEX_SIDES;

/**
 * Tantrix puzzle move generator. Generates valid next moves given the current state.
 *
 * @author Barry Becker
 */
public class MoveGenerator {

    private TantrixBoard board;

    /** a set of all the places that a next tile might be placed next. */
    private Set<Location> borderSpaces;


    /**
     * Constructor
     */
    public MoveGenerator(TantrixBoard board) {
        this.board = board;
        this.borderSpaces =
            new BorderFinder(board.tantrix, board.getPrimaryColor()).findBorderPositions();
    }

    /**
     * For each unplaced tile, find all valid placements given current configuration.
     * Valid placements must extend the primary path.
     * @return List of all valid tile placements for the current tantrix state.
     */
    public List<TilePlacement> generateMoves() {
        List<TilePlacement> moves = new ArrayList<TilePlacement>();
        HexTileList unplacedTiles = board.getUnplacedTiles();

        for (HexTile tile : unplacedTiles) {
            moves.addAll(findPlacementsForTile(tile));
        }
        return moves;
    }

    /**
     * @return list of all the legal placements for the specified tile.
     */
    private List<TilePlacement> findPlacementsForTile(HexTile tile) {
        List<TilePlacement> placements = new ArrayList<TilePlacement>();

        for (Location loc : borderSpaces)  {
            TilePlacement placement = getPlacementIfFits(tile, loc);
            if (placement != null) {
               placements.add(placement);
            }
        }

        return placements;
    }

    /**
     * @param tile the tile to place.
     * @param loc the location to try and place it at.
     * @return the placement if one could be found, else null.
     */
    private TilePlacement getPlacementIfFits(HexTile tile, Location loc) {
        TilePlacement placement = new TilePlacement(tile, loc, Rotation.ANGLE_0);
        int i = 0;
        while (!fits(placement) && i<HEX_SIDES) {
            placement = placement.rotate();
            i++;
        }
        return (i < HEX_SIDES) ? placement : null;
    }

    /**
     * The tile fits if the primary path and all the other paths match for edges that have neighbors.
     * @param placement the tile to check for a valid fit.
     * @return true of the tile fits
     */
    private boolean fits(TilePlacement placement) {
        boolean primaryPathMatched = false;

        for (byte i=0; i<HEX_SIDES; i++) {
            TilePlacement nbr = board.getNeighbor(placement, i);

            if (nbr != null) {
                PathColor pathColor = placement.getPathColor(i);

                if (pathColor == nbr.getPathColor((byte)(i+3))) {
                    if (pathColor == board.getPrimaryColor()) {
                        primaryPathMatched = true;
                    }
                }  else {
                    return false;
                }
            }
        }

        return primaryPathMatched;
    }
}
