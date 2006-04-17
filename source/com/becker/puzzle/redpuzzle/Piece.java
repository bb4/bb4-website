package com.becker.puzzle.redpuzzle;

/**
 * One of the 9 board pieces in the Red Puzzle.
 * @author Barry Becker
 */
public final class Piece
{

    // the suits of the edges.
    private Nub[] nubs_;

    // Indicates which way the piece is oriented/rotated.
    private Direction orientation_ = Direction.TOP;

    // the number of the piece (1-9).
    private int pieceNumber_;

    /**
     * Constructor.
     */
    public Piece( Nub topNub, Nub rightNub, Nub bottomNub, Nub leftNub,
                  int pieceNumber ) {
        nubs_ = new Nub[4];

        nubs_[Direction.TOP.ordinal()] = topNub;
        nubs_[Direction.RIGHT.ordinal()] = rightNub;
        nubs_[Direction.BOTTOM.ordinal()] = bottomNub;
        nubs_[Direction.LEFT.ordinal()] = leftNub;

        assert ( pieceNumber >= 1 && pieceNumber <= 9 ) : "the piece number is not valid : " + pieceNumber;
        pieceNumber_ = pieceNumber;
        orientation_ = Direction.TOP;
    }

    /**
     * @param dir
     * @return the suit of the nub fot the specified direction.
     */
    public Nub getNub(Direction dir) {
        return nubs_[getDirectionIndex(dir)];
    }

    /**
     *  This rotates the piece 90 degrees clockwise.
     */
    public void rotate() {
        Direction[] values = Direction.values();
        orientation_ = values[(orientation_.ordinal() + 1) % values.length];
    }

    /**
     * @return the way in which this piece is oriented.
     */
    public Direction getRotation() {
        return orientation_;
    }

    public boolean isFullyRotated() {
        return (orientation_ == Direction.LEFT);
    }

    /**
     * initial unrotated state.
     */
    public void resetOrientation() {
        orientation_ = Direction.TOP;
    }
    /**
     * @return the unique number assigned to this piece.
     */
    public int getNumber() {
        return pieceNumber_;
    }


    private int getDirectionIndex(Direction dir)  {
       return (orientation_.ordinal() + dir.ordinal()) % Direction.values().length;
    }
}

