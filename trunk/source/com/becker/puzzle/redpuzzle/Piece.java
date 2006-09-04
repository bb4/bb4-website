package com.becker.puzzle.redpuzzle;

/**
 * One of the 9 board pieces in the Red Puzzle.
 * @author Barry Becker
 */
public final class Piece
{

    // the suits of the edges.
    private Nub[] nubs_ = null;

    // Indicates which way the piece is oriented/rotated.
    private Direction orientation_ = Direction.TOP;

    // the number of the piece (1-9).
    private int pieceNumber_ = 0;

    public static enum Direction {TOP, RIGHT, BOTTOM, LEFT};

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
     * Copy constructor.
     * @param p
     */
    public Piece(Piece p) {
        this(p.nubs_[0], p.nubs_[1], p.nubs_[2], p.nubs_[3], p.getNumber());
        this.orientation_ = p.getOrientation();
        //assert(p.equals(this)) : p + " not equal to " + this;
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

    /**
     *  This rotates the piece the specified number of 90 degree inrements.
     */
    public void rotate(int num) {
        Direction[] values = Direction.values();
        orientation_ = values[(orientation_.ordinal() + num) % values.length];
    }

    public void resetOrientation() {
        orientation_ = Direction.TOP;
    }

    public Direction getOrientation() {
        return orientation_;
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
     * @param piece to compare to
     * @return true if logically equal.
     */
    public boolean equals(Object piece) {
        Piece p = (Piece) piece;
        return (
               p.getTopNub() == this.getTopNub() &&
               p.getRightNub() == this.getRightNub() &&
               p.getBottomNub() == this.getBottomNub() &&
               p.getLeftNub() == this.getLeftNub() &&
               p.getNumber() == this.getNumber() &&
               p.getOrientation() == this.getOrientation()
        );
    }

    /**
     * @return a nice readable string representation for debugging.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("Piece "+ getNumber() + " (orientation="+orientation_+"): ");
        for (Direction d : Direction.values()) {
            Nub n = getNub(d);
            buf.append(d.toString() + ':' + n.toString() + '\t');
        }
        return buf.toString();
    }
}

