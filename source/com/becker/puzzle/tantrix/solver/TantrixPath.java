// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;

import com.becker.common.math.MathUtil;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.PermutedParameterArray;
import com.becker.puzzle.redpuzzle.model.PieceList;
import com.becker.puzzle.tantrix.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of tiles representing a primary color path that is used when searching to find a tantrix solution.
 * It has some unique properties.
 * For example, when finding a random neighbor, we select a tile at random and then consider all the
 * 7 other permutations of attaching the current path segments on either side. If any of those give a path
 * with a higher score that is what we use for the permuted path.
 *
 * @author Barry Becker
 */
public class TantrixPath extends PermutedParameterArray {

    private TilePlacementList tiles_;


    /**
     * The list of tiles that are passed in must be a continuous primary path,
     *  but it is not required that it be a loop, or that any of the secondary colors match.
     * @param tiles ordered path tiles.
     */
    public TantrixPath(TilePlacementList tiles) {
        tiles_ = tiles;
    }

    @Override
    public TantrixPath copy() {
        TantrixPath copy = new TantrixPath(tiles_);

        copy.setFitness(this.getFitness());
        return copy;
    }

    /**
     * We want to find a potential solution close to the one that we have, 
     * with minimal disturbance of the pieces that are already fit, but yet improved from what we had.
     * The main criteria for quality of the path is
     *  1) How close the ends of the path are to each other. Perfection achieved when we have a closed loop.
     *  2) Better if more matching secondary path colors
     *  3) Fewer inner spaces and a bbox with less area.
     *
     * @param radius proportional to the amount of variation. This might be a little difficult for tantrix.
     *   If the radius is small, or there is a closed loop, consider swapping pieces who's
     *   primary path have the same shape. If the radius is large, we could perhaps do random permutation from
     *   more than one spot.
     * @return the random nbr (potential solution).
     */
    @Override
    public PermutedParameterArray getRandomNeighbor(double radius) {

        TilePlacementList tiles = new TilePlacementList(tiles_);

        TilePlacement pivotTile = tiles_.get(MathUtil.RANDOM.nextInt(tiles_.size()));

/*
        for (int i = 0; i < numSwaps; i++) {
            doPieceSwap(tiles);
        }
        //assert !this.equals(new PieceParameterArray(pieces)) :
        //    "The piecelists should not be equal new=" + pieces + " orig=" + tiles_;

        assert (tiles.size() == NUM_PIECES);
        // make a pass over all the pieces.
        // If rotating a piece leads to more fits, then do it.
        for ( int k = 0; k < tiles.size(); k++) {

            int numFits = tiles.getNumFits(k);
            int bestNumFits = numFits;
            int bestRot = 1;
            for (int i = 0; i < 3; i++) {

                tiles.rotate(k, 1);  // fix
                numFits = tiles.getNumFits(k);
                if (numFits > bestNumFits) {
                    bestNumFits = numFits;
                    bestRot = 2 + i;
                }
            }
            // rotate the piece to position of best fit.
            tiles.rotate(k, bestRot); // fix
        }         */

        return new TantrixPath(tiles);
    }



    /**
     * @return get a completely random solution in the parameter space.
     */
    @Override
    public ParameterArray getRandomSample() {

        TantrixBoard board = new TantrixBoard(new HexTileList(tiles_));
        RandomPathGenerator gen = new RandomPathGenerator(board);
        return gen.generateRandomPath();
    }

    /**
     * @return the tiles corresponding to the encoded parameter array.
     */
    public TilePlacementList getPieceList() {
        return tiles_;
    }

    /**
     * @return the number of parameters in the array.
     */
    @Override
    public int size() {
        return tiles_.size();
    }

    @Override
    public String toString() {
        return tiles_.toString();
    }

    /**
     * @return  the parameters in a string of Comma Separated Values.
     */
    @Override
    public String toCSVString() {
        return toString();
    }
}
