// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model.fitting;

import com.becker.puzzle.tantrix.model.PathColor;
import com.becker.puzzle.tantrix.model.Tantrix;
import com.becker.puzzle.tantrix.model.TilePlacement;

import static com.becker.puzzle.tantrix.model.HexTile.NUM_SIDES;

/**
 * Determines valid primary path fits for a specified tile relative to an existing set at a specific location.
 * This is less strict than tile fitter - which checks all paths.
 *
 * @author Barry Becker
 */
public class PrimaryPathFitter extends AbstractFitter {

    private Tantrix tantrix;

    /**
     * Constructor
     */
    public PrimaryPathFitter(Tantrix tantrix, PathColor primaryColor) {
        super(primaryColor);
        this.tantrix = tantrix;
    }

    /**
     * The tile fits if the primary path fits.
     * Check all the neighbors (that exist) and verify that if that direction is a primary path output, then it matches.
     * @param placement the tile to check for a valid fit.
     * @return true of the tile fits
     */
    @Override
    public boolean isFit(TilePlacement placement) {

        for (byte i = 0; i < NUM_SIDES; i++) {
            TilePlacement nbr = tantrix.getNeighbor(placement, i);

            if (nbr != null) {
                PathColor pathColor = placement.getPathColor(i);
                //System.out.println("nbr"+i+" of "+placement+" ="+ nbr + ". Out color in dir " + i + "="+ pathColor +". Does it match " + nbr.getPathColor((byte)(i+3)) + "?");

                if (pathColor == primaryColor && pathColor == nbr.getPathColor((byte)(i+3))) {
                    return true;
                }
            }
        }

        return false;
    }
}
