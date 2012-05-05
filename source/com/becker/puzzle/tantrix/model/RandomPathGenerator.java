// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;
import com.becker.common.math.MathUtil;
import com.becker.puzzle.tantrix.model.fitting.PrimaryPathFitter;
import com.becker.puzzle.tantrix.solver.PathEvaluator;
import com.becker.puzzle.tantrix.solver.TantrixPath;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

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
            // if placement == null we need toString() backtrack.
            currentBoard = currentBoard.placeTile(placement);
        }
        return new TantrixPath(moves, primaryColor, new PathEvaluator(currentBoard));
    }

    /**
     * For each unplaced tile, find all valid placements given current configuration.
     * Valid placements must extend the primary path.
     * @return List of all valid tile placements for the current tantrix state.
     */
    public TilePlacement generateRandomPathMove(TantrixBoard board) {

        HexTileList unplacedTiles = (HexTileList) board.getUnplacedTiles().clone();
        Collections.shuffle(unplacedTiles, MathUtil.RANDOM);

        TilePlacement nextMove = null;
        int i=0;
        while (nextMove == null)   {
            HexTile tile = unplacedTiles.get(i++);
            boolean isLast = unplacedTiles.isEmpty();
            nextMove = findPrimaryPathPlacementForTile(board, tile, isLast);
        }
        return nextMove;
    }

    /**
     * @return the first placement for the specified tile which matches the primary path
     *     but not necessarily the secondary paths. The opposite end of the primary path can only
     *     retouch the tantrix if it is the last tile to be placed in the random path.
     */
    private TilePlacement findPrimaryPathPlacementForTile(TantrixBoard board, HexTile tile, boolean isLast) {

        TilePlacement lastPlaced = board.getLastTile();
        PrimaryPathFitter fitter = new PrimaryPathFitter(board.getTantrix(), board.getPrimaryColor());

        Map<Integer, Location> outgoing = lastPlaced.getOutgoingPathLocations(primaryColor);
        Location nextLocation = null;
        for (int i : outgoing.keySet()) {
            if (board.getTilePlacement(outgoing.get(i)) == null) {
                nextLocation = outgoing.get(i);
            }
        }
        assert nextLocation != null;

        TilePlacementList validFits =
                fitter.getFittingPlacements(tile, nextLocation);

        assert validFits.size() == 2 :
                "Tantrix="+ board.getTantrix() + "\nlastPlaced="+lastPlaced.getLocation()
                +"\nThere must be two ways for the primary path to fit, but we had "
                + validFits + " for  placing " + tile + " at " + lastPlaced.getLocation();

        if (!isLast) {
            cullPlacementsThatRetouch(board, lastPlaced, validFits);
        }

        if (validFits.isEmpty())  {
            return null;
        }

        return validFits.get(MathUtil.RANDOM.nextInt(validFits.size()));
    }

    /**
     * Avoid having the random extension to the path loop back and retouch the tantrix.
     * We remove those placements from consideration (unless it is the last one).
     */
    private void cullPlacementsThatRetouch(TantrixBoard board, TilePlacement lastPlaced, TilePlacementList validFits) {
        Iterator<TilePlacement> iter = validFits.iterator();
        while (iter.hasNext()) {
            TilePlacement fit = iter.next();
            Map<Integer, Location> outgoing = fit.getOutgoingPathLocations(primaryColor);
            boolean retouchesTantrix = false;

            for (int i : outgoing.keySet()) {
                TilePlacement p = board.getTilePlacement(outgoing.get(i));
                if (p != null && !p.equals(lastPlaced)) {
                    retouchesTantrix = true;
                }
            }
            if (retouchesTantrix) {
                iter.remove();
            }
        }
    }
}
