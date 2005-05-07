package com.becker.puzzle.hiq;


/**
 *  One of the 9 board pieces in the Red Puzzle
 */
public final class Peg
{

    private static final int NUM_SIDES = 4;

    // This number (0-3) indicates which way the piece is rotated.
    // It is at 0 initially and after a reset. 1,2,3 represent increments of
    // 90 degree rotations clockwise.
    private int rotation_ = 0;

    // the suits of the edges
    private char[] suit_ = null;

    // whether or not the suit on the edge is outward facing
    private boolean[] out_ = null;

    // the number of the piece (1-9)
    private int pieceNumber_;

    // Constructor. This should never be called directly
    // instead call the factory method so we recycle objects.
    // use createMove to get moves, and dispose to recycle them
    public Peg( char s1, char s2, char s3, char s4,
                  boolean out1, boolean out2, boolean out3, boolean out4,
                  int pieceNumber )
    {
        suit_ = new char[4];
        out_ = new boolean[4];

        suit_[0] = s1;
        suit_[1] = s2;
        suit_[2] = s3;
        suit_[3] = s4;

        out_[0] = out1;
        out_[1] = out2;
        out_[2] = out3;
        out_[3] = out4;

        if ( pieceNumber < 1 || pieceNumber > 9 )
            System.out.println( "the piece number is not valid : " + pieceNumber );
        pieceNumber_ = pieceNumber;

        rotation_ = 0;
    }

    public void reset()
    {
        rotation_ = 0;
    }

    // this rotates the piece 90 degrees clockwise
    public void rotate()
    {
        rotation_ = (rotation_ + 1);
        if ( rotation_ > 4 )
            System.out.println( "Error: rotation >4" );
    }

    public int getRotation()
    {
        return rotation_;
    }

    public int getNumber()
    {
        return pieceNumber_;
    }

    public char topSuit()
    {
        return suit_[rotation_ % NUM_SIDES];
    }

    public boolean topOut()
    {
        return out_[rotation_ % NUM_SIDES];
    }

    public char rightSuit()
    {
        return suit_[(rotation_ + 1) % NUM_SIDES];
    }

    public boolean rightOut()
    {
        return out_[(rotation_ + 1) % NUM_SIDES];
    }

    public char bottomSuit()
    {
        return suit_[(rotation_ + 2) % NUM_SIDES];
    }

    public boolean bottomOut()
    {
        return out_[(rotation_ + 2) % NUM_SIDES];
    }

    public char leftSuit()
    {
        return suit_[(rotation_ + 3) % NUM_SIDES];
    }

    public boolean leftOut()
    {
        return out_[(rotation_ + 3) % NUM_SIDES];
    }

}

