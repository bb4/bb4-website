// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;

import java.util.*;
import static com.becker.puzzle.tantrix.model.TantrixBoard.HEX_SIDES;

/**
 * Tantrix puzzle move generator. Generates valid next moves.
 *
 * @author Barry Becker
 */
public class MoveGenerator {

    TantrixBoard board;

    /** a set of all the places that the next tile might be placed */
    Set<Location> legalPositions;

    /**
     * Constructor
     */
    public MoveGenerator(TantrixBoard board) {
        this.board = board;
        this.legalPositions = findLegalPositions();
    }

    /**
     * For each unplaced tile, find all valid placements given current configuration.
     * @return List of all valid tile placements for the current board state.
     */
    public List<TilePlacement> generateMoves() {
        List<TilePlacement> moves = new ArrayList<TilePlacement>();

        HexTileList unplacedTiles = board.getUnplacedTiles();
        assert !unplacedTiles.isEmpty();

        for (HexTile tile : unplacedTiles) {
            moves.addAll(findPlacementsForTile(tile));
        }
        return moves;
    }

    /** @return list of all the legal placements for the specified tile. */
    private List<TilePlacement> findPlacementsForTile(HexTile tile) {
        List<TilePlacement> placements = new ArrayList<TilePlacement>();

        for (Location loc : legalPositions)  {
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
        while (!fits(placement) && i<6) {
            placement = placement.rotate();
            i++;
        }
        return (i<6)? placement : null;
    }

    /**
     * The tile fits if all the paths match for edges that have neighbors
     * @param placement
     * @return true of the tile fits
     */
    private boolean fits(TilePlacement placement) {
        for (byte i=0; i<HEX_SIDES; i++) {
            TilePlacement nbr = board.getNeighbor(placement, i);
            if (nbr != null && placement.getPathColor(i) != nbr.getPathColor((byte)(i+3))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Travel the primary path in both directions, adding all adjacent
     * empty placements.
     * @return list of legal next placements
     */
    private Set<Location> findLegalPositions() {
        Set<Location> positions = new LinkedHashSet<Location>();
        TilePlacement lastPlaced = board.getLastTile();

        Queue<TilePlacement> searchQueue = new LinkedList<TilePlacement>();
        searchQueue.add(lastPlaced);

        while (!searchQueue.isEmpty()) {
            TilePlacement placement = searchQueue.remove();
            positions.addAll(findEmptyNeighborLocations(placement));

            searchQueue.addAll(findPrimaryPathNeighbors(placement));
        }

        return positions;
    }

    private List<TilePlacement> findPrimaryPathNeighbors(TilePlacement previous) {

        List<TilePlacement> pathNbrs  = new LinkedList<TilePlacement>();
        for (byte i=0; i<HEX_SIDES; i++) {
            PathColor color = previous.getTile().getEdgeColor(i);
            if (color == board.getPrimaryColor()) {
                TilePlacement nbr = board.getNeighbor(previous, i);
                if (nbr != null) {
                    pathNbrs.add(nbr);
                }
            }
        }
        return pathNbrs;
    }

    private List<Location> findEmptyNeighborLocations(TilePlacement placement) {
        List<Location> emptyNbrLocations = new LinkedList<Location>();
        for (byte i=0; i<HEX_SIDES; i++) {

            Location nbrLoc = board.getNeighborLocation(placement, i);
            if (board.getTilePlacement(nbrLoc) == null) {
                emptyNbrLocations.add(nbrLoc);
            }
        }
        return emptyNbrLocations;
    }
}
