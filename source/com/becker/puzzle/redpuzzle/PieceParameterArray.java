package com.becker.puzzle.redpuzzle;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.PermutedParameterArray;

/**
 * The parameter array to use when searching (using optimization) to find a red puzzle solution.
 * It has some unique properties.
 * For example, when finding a random neighbor, we consider rotations of
 * non-fitting pieces rather than just offsetting the number by some random amount.
 *
 * @author Barry Becker Date: Aug 6, 2006
 */
public class PieceParameterArray extends PermutedParameterArray {

    private PieceList pieces_ ;
    private static final int NUM_PIECES = 9;


    public PieceParameterArray(PieceList pieces) {
        pieces_ = pieces;
    }

    @Override
    public PieceParameterArray copy() {
        PieceParameterArray copy = new PieceParameterArray(pieces_);
        copy.setFitness(this.getFitness());
        return copy;
    }

    /**
     * We want to find a potential solution close to the one that we have, 
     * with minimal disturbance of the pieces that are already fit.
     *
     * @param radius proportional to the number of pieces that you want to vary.
     * @return the random nbr (potential solution).
     */
    @Override
    public PermutedParameterArray getRandomNeighbor(double radius) {
        PieceList pieces = new PieceList(pieces_);

        int numSwaps = 1;   //Math.max(1, (int) (rad * 2.0));

        for (int i=0; i<numSwaps; i++) {
            doPieceSwap(pieces);
        }

        assert (pieces.size() == NUM_PIECES);
        // make a pass over all the pieces.
        // If rotating a piece leads to more fits, then do it.
        for ( int k = 0; k < pieces.size(); k++) {

            int numFits = pieces.getNumFits(k);
            int bestNumFits = numFits;
            int bestRot = 1;
            for (int i=0; i<3; i++) {

                pieces.rotate(k, 1);  // fix
                numFits = pieces.getNumFits(k);
                if (numFits > bestNumFits) {
                    bestNumFits = numFits;
                    bestRot = 2 + i;
                }
            }
            // rotate the piece to position of best fit.
            pieces.rotate(k, bestRot); // fix
        }

        return new PieceParameterArray(pieces);
    }

    /**
     * exchange 2 pieces, even if it means the fitness gets worse.
     *
     * Skew away from selecting pieces that have fits.
     * The probability of selecting pieces that already have fits is sharply reduced.
     * The denonimator is 1 + the number of fits that the piece has.
     */
    private static void doPieceSwap(PieceList pieces) {

        double[] swapProbabilities = findSwapProbabilities(pieces);
        double totalProb = 0;
        for (int i=0; i<NUM_PIECES; i++) totalProb += swapProbabilities[i];

        int p1 = getPieceFromProb(totalProb * RANDOM.nextDouble(), swapProbabilities);
        int p2;
        do {
            p2 = getPieceFromProb(totalProb * RANDOM.nextDouble(), swapProbabilities);
        } while (p2 == p1);
     
        pieces.doSwap(p1, p2);
    }

    /**
     *
     * @param pieces
     * @return probability used to determine if we do a piece swap.
     *   Pieces that already fit have a low probability of being swapped.
     */
    private static double[] findSwapProbabilities(PieceList pieces) {

        double[] swapProbabilities = new double[NUM_PIECES];
        for (int i=0; i<NUM_PIECES; i++) {
            swapProbabilities[i] = 1.0 / (1.0 + pieces.getNumFits(i)); //Math.pow(pieces.getNumFits(i), 2));
        }
        return swapProbabilities;
    }

    /**
     * @param p some value between 0 and the totalProbability (i.e. 100%).
     * @return the piece that was selected given the probability.
     */
    private static int getPieceFromProb(double p, double[] probabilities) {
        double total = 0;
        int i = 0;
        while (total < p && i<NUM_PIECES) {
            total += probabilities[i++];
        }
        return --i;
    }


    /**
     * @return get a completely random solution in the parameter space.
     */
    @Override
    public ParameterArray getRandomSample() {
       PieceList pl = new PieceList(pieces_);
       PieceList shuffledPieces = pl.shuffle();
       return new PieceParameterArray(shuffledPieces);
    }

    /**
     *
     * @return the piece list corresponding to the encoded parameter array.
     */
    public PieceList getPieceList() {
        return pieces_;
    }

    /**
     * @return the number of parameters in the array.
     */
    @Override
    public int size() {
        return pieces_.size();
    }

    @Override
    public String toString() {
        return pieces_.toString();
    }

    /**
     * @return  the parameters in a string of Comma Separated Values.
     */
    @Override
    public String toCSVString() {
        return toString();
    }

}
