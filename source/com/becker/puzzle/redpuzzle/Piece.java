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

    private enum Direction {TOP, RIGHT, BOTTOM, LEFT};

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


    public Nub getTopNub() {
        return  getNub(Direction.TOP);
    }
    public Nub getRightNub() {
        return  getNub(Direction.RIGHT);
    }
    public Nub getBottomNub() {
        return  getNub(Direction.BOTTOM);
    }
    public Nub getLeftNub() {
        return  getNub(Direction.LEFT);
    }

    /**
     * @param dir
     * @return the suit of the nub fot the specified direction.
     */
    private Nub getNub(Direction dir) {
        return nubs_[getDirectionIndex(dir)];
    }

    /**
     *  This rotates the piece 90 degrees clockwise.
     */
    public void rotate() {
        Direction[] values = Direction.values();
        orientation_ = values[(orientation_.ordinal() + 1) % values.length];
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

    /**
     * @return a nice readable string representation for debugging.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("Piece "+ getNumber() + '\n');
        for (Direction d : Direction.values()) {
            Nub n = getNub(d);
            buf.append(d +" " + n.toString() + '\n');
        }
        return buf.toString();
    }
}

