// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import static com.becker.puzzle.tantrix.model.TantrixBoard.HEX_SIDES;

/**
 * Used to determine whether or not a given tantrix state is a valid solution.
 *
 *  @author Barry Becker
 */
public class SolutionVerifier {

    TantrixBoard board;
    /**
     * Constructor.
     * @param board the tantrix state to test for solution.
     */
    public SolutionVerifier(TantrixBoard board) {
        this.board = board;
    }

    /**
     * The puzzle is solved if there is a loop of the primary color
     * and all secondary colors match. Since a tile can only be placed in
     * a valid position, we only need to check if there is a complete loop.
     * @return true if solved.
     */
    public boolean isSolved() {
        if (!board.getUnplacedTiles().isEmpty()) {
            return false;
        }

        int numVisited = 0;
        TilePlacement lastTilePlaced = board.getLastTile();
        TilePlacement currentTile = lastTilePlaced;
        TilePlacement previousTile = null;
        TilePlacement nextTile;

        do {
            nextTile = findNeighborTile(currentTile, previousTile);
            previousTile = currentTile;
            currentTile = nextTile;
            numVisited++;
        } while (currentTile != null && !currentTile.equals(lastTilePlaced));

        // check that there are no holes.

        boolean isLoop = (numVisited == board.getNumTiles() && lastTilePlaced.equals(currentTile));
        return isLoop && noInnerSpaces();
    }

    /**
     * Loop through the edges until we find the primary color.
     * If it does not direct us back to where we came from then go that way.
     * @param currentPlacement where we are now
     * @param previousTile where we were
     * @return the next tile in the path if there is one. Otherwise null.
     */
    private TilePlacement findNeighborTile(TilePlacement currentPlacement, TilePlacement previousTile) {

        for (byte i = 0; i < HEX_SIDES; i++) {
            PathColor color = currentPlacement.getPathColor(i);
            if (color == board.getPrimaryColor()) {
                TilePlacement nbr = board.getNeighbor(currentPlacement, i);
                if (nbr != null && !nbr.equals(previousTile)) {
                    return nbr;
                }
            }
        }
        return null;
    }

    private boolean noInnerSpaces() {
        return true;
    }
}
