package com.becker.puzzle.redpuzzle;

import java.util.*;

/**
 * The pieces that sre in the red puzzle.
 * In no particular order.
 * model in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public class PieceList {

    private static final int NUM_PIECES = 9;

    private List<Piece> pieces_;

    private static final Piece[] INITIAL_PIECES = {
         new Piece( Nub.INNY_SPADE,  Nub.INNY_HEART,    Nub.OUTY_SPADE,   Nub.OUTY_DIAMOND, 1),
         new Piece( Nub.OUTY_HEART,  Nub.OUTY_SPADE,  Nub.INNY_SPADE, Nub.INNY_CLUB, 2),
         new Piece( Nub.OUTY_HEART,  Nub.OUTY_DIAMOND,  Nub.INNY_DIAMOND, Nub.INNY_HEART, 3),
         new Piece( Nub.OUTY_HEART,  Nub.OUTY_DIAMOND,  Nub.INNY_CLUB, Nub.INNY_CLUB, 4),
         new Piece( Nub.OUTY_CLUB,  Nub.OUTY_HEART,  Nub.INNY_SPADE, Nub.INNY_HEART, 5),
         new Piece( Nub.OUTY_CLUB,  Nub.OUTY_HEART,  Nub.INNY_DIAMOND, Nub.INNY_CLUB, 6),
         new Piece( Nub.OUTY_SPADE,  Nub.OUTY_DIAMOND,  Nub.INNY_HEART, Nub.INNY_DIAMOND, 7),
         new Piece( Nub.OUTY_DIAMOND,  Nub.OUTY_CLUB,  Nub.INNY_CLUB, Nub.INNY_DIAMOND, 8),
         new Piece( Nub.OUTY_SPADE,  Nub.OUTY_SPADE,  Nub.INNY_HEART, Nub.INNY_CLUB, 9),
     };


    /**
     * a list of puzzle pieces
     */
    public PieceList() {

        pieces_ = new ArrayList<Piece>();
    }

    /**
     * Factory method for creating the initial puzzle pieces.
     * @return the initial 9 pieces (in random order) to use when solving.
     */
    public static PieceList getInitialPuzlePieces() {
        PieceList pieces = new PieceList();

        for (Piece p : INITIAL_PIECES)  {
           pieces.add(p);
        }

        // shuffle the pieces so we get difference solutions -
        // or at least different approaches to the solution if there is only one.
        pieces.shuffle();

        return pieces;
    }

    /**
     *
     * @param i the index of the piece to get.
     * @return the i'th piece.
     */
    public Piece get(int i)  {
        assert i < NUM_PIECES : "there are only 9 pieces.";

        return pieces_.get(i);
    }

    /**
     * @return the last piece added.
     */
    public Piece getLast()  {

        return pieces_.get(pieces_.size() - 1);
    }

    /**
     * @param p piece to add to the end of the list.
     */
    public void add(Piece p) {
        pieces_.add(p);
        assert pieces_.size() <= NUM_PIECES : "there are only 9 pieces.";
    }

    /**
     * @param p the piece to remove.
     * @return true if the list contained this element.
     */
    public boolean remove(Piece p) {
        return pieces_.remove(p);
    }

    public Piece removeLast() {
        Piece p = pieces_.get(pieces_.size() - 1);
        pieces_.remove( p );
        return p;
    }

    public void shuffle() {
        Collections.shuffle(pieces_);
    }

    /**
     * @return the number of pieces in the list.
     */
    public int size() {
        return pieces_.size();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("PieceList:");
        for (Piece p : pieces_) {
            buf.append(" " + p);
        }
        return buf.toString();
    }

}
