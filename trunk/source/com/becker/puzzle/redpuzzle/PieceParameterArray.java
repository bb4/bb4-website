package com.becker.puzzle.redpuzzle;

import com.becker.optimization.*;

import java.util.*;

/**
 * The parameter array to use when searching to find a red puzzle solution.
 * It has some unique properties.
 * For example, when finding a random neighbor, we consider rotations of
 * non-fitting pieces rather than just ofsetting the number by some random amount.
 *
 * Each paraemter encodes a piece (it may be rotated).
 * The encoding is 12 digit binary number from 0 (0000000000) to 8192 (111111111111).
 * There are 4 sides, each side is 3 bits.
 * The first bit is inny/outty, the 2nd and 3rd bits tells the suit.
 * An example representation for a piece with all outty hearts is:
 *   111 111 111 111
 * 00 = spade   01 = club
 * 10 = diamond 11 = heart
 *
 * @author Barry Becker Date: Aug 6, 2006
 */
public class PieceParameterArray extends ParameterArray {

    private PieceList pieces_ ;

    public static final double MAX_PARAM_VAL = 8192; // all outty hearts;


    private static final Map codeToNubMap = new HashMap();
    static {                                   // 0 to 7 decimal
        codeToNubMap.put(0, Nub.INNY_SPADE);   // 000
        codeToNubMap.put(1, Nub.INNY_CLUB);    // 001
        codeToNubMap.put(2, Nub.INNY_DIAMOND); // 010
        codeToNubMap.put(3, Nub.INNY_HEART);   // 011
        codeToNubMap.put(4, Nub.OUTY_SPADE);   // 100
        codeToNubMap.put(5, Nub.OUTY_CLUB);    // 101
        codeToNubMap.put(6, Nub.OUTY_DIAMOND); // 110
        codeToNubMap.put(7, Nub.OUTY_HEART);   // 111
    }

    public PieceParameterArray(PieceList pieces) {
        pieces_ = pieces;
        //int len = pieces.size();
        //params_ = new Parameter[len];

        //for (int i=0; i<len; i++) {
        //    params_[i] = getParamFromPiece(pieces.get(i), i);
        //}
    }

    public PieceParameterArray copy() {
        return new PieceParameterArray(pieces_);
    }

    /**
     * We want to find a potential solution close to the one that we have, disturbing the
     * pieces that are already fitted correctly as little as possible.
     *
     * @param rad proportional to the number of pieces that you want to vary.
     *  num = rad * 2
     *   e.g. num = 1 - rotate one peice (one that does not already fit)
     *   e.g. num = 4 - move or rotate 4 peices
     *   e.g. num = 9 or greater - change around all the pieces.
     * @return the random nbr (potential solution).
     */
    public ParameterArray getRandomNeighbor(double rad)
    {
        PieceList pieces = new PieceList(pieces_);

        int numSwaps = (int) (rad * 1.7);
        for (int i=0; i<numSwaps; i++) {
            doPieceSwap(pieces);
        }

        assert (pieces.size() == 9);
        // make a pass over all the pieces. If rotating a piece leads to more fits, then do it.
        for ( int k = 0; k < pieces.size(); k++ ) {

            int currentNumFits = pieces.getNumFits(k);
            pieces.get(k).rotate();
            int numFits = pieces.getNumFits(k);

            if (numFits <= currentNumFits) {
                // not improved. return to original position
                // rotating forwards 3 is the same as rotating backwards 1
                pieces.get(k).rotate(3);
            }
        }

        return new PieceParameterArray(pieces);
    }

    /**
     * exchange 2 pieces, even if it means the fintess gets worse.
     */
    private static void doPieceSwap(PieceList pieces) {
        int p1 = RANDOM.nextInt(9);
        int p2;
        do p2 = RANDOM.nextInt(9);
        while (p2 == p1);

        Piece piece2 = pieces.get(p2);
        Piece piece1 = pieces.remove(p1);
        pieces.add(p1, piece2);
        pieces.remove(piece2);
        pieces.add(p2, piece1);
    }


    /**
     * @return get a completely random solution in the parameter space.
     */
    public ParameterArray getRandomSolution()
    {
       PieceList pl = new PieceList(pieces_);
       pl.shuffle();
       return new PieceParameterArray(pl);
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
    public int size()
    {
        return pieces_.size();
    }

    /*
    private static Nub getNubFromCode(Piece.Direction dir, int code) {
        int nubCode = 0;
        switch (dir) {
            case TOP : nubCode = code >> 9; break;
            case RIGHT : nubCode = code >> 6 << 3; break;
            case BOTTOM : nubCode = code >> 3 << 6; break;
            case LEFT : nubCode = code << 9; break;
        }
        System.out.println("nubCode="+nubCode);
        return (Nub) codeToNubMap.get(nubCode);
    }

    public static Piece getPieceFromParam(Parameter param, int i) {
        int v = (int) param.getValue();
        Nub top = getNubFromCode(Piece.Direction.TOP, v);
        Nub right = getNubFromCode(Piece.Direction.RIGHT, v);
        Nub bottom = getNubFromCode(Piece.Direction.BOTTOM, v);
        Nub left = getNubFromCode(Piece.Direction.LEFT, v);
        System.out.println("param="+v+ " "+new Piece(top, right, bottom, left, i));
        return new Piece(top, right, bottom, left, i);
    }

    private static int getCodeFromNub(Nub nub) {

        int isOuty = nub.isOuty() ? 4 : 0;
        return isOuty + nub.getSuit().ordinal();
    }

    public static Parameter getParamFromPiece(Piece piece, int i) {
        double num = 0;
        int topCode = getCodeFromNub(piece.getTopNub());
        int rightCode = getCodeFromNub(piece.getRightNub());
        int bottomCode = getCodeFromNub(piece.getBottomNub());
        int leftCode = getCodeFromNub(piece.getLeftNub());
        int code = topCode << 9 + rightCode << 6 + bottomCode << 3 + leftCode;
        return new Parameter(num, 0, code, "piece_"+(i+1));
    }  */

    public String toString()
    {
        return pieces_.toString();
    }

    /**
     * @return  the parameters in a string of Comma Separated Values.
     */
    public String toCSVString()
    {
        return toString();
    }

}
