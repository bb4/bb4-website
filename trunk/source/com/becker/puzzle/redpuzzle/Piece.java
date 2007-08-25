package com.becker.puzzle.redpuzzle;

/**
 * One of the 9 board pieces in the Red Puzzle.
 * Immutable.
 * Rotation returns a copy.
 *
 * @author Barry Becker
 */
public final class Piece
{

    // the suits of the edges.
    private Nub[] nubs_ = null;

    // Indicates which way the piece is oriented/rotated.
    private Direction orientation_;

    // the number of the piece (1-9).
    private byte pieceNumber_ = 0;

    public static enum Direction {TOP, RIGHT, BOTTOM, LEFT}

    /**
     * Constructor.
     * Assumes default orientation
     */
    public Piece( Nub topNub, Nub rightNub, Nub bottomNub, Nub leftNub,
                  int pieceNumber ) {
        this(topNub, rightNub, bottomNub, leftNub, pieceNumber, Direction.TOP);
    }
    
    /**
     * Use this constructor if you need to specify the orientation.
     */
    private Piece( Nub topNub, Nub rightNub, Nub bottomNub, Nub leftNub,
                  int pieceNumber, Direction orientation) {
        nubs_ = new Nub[4];

        nubs_[Direction.TOP.ordinal()] = topNub;
        nubs_[Direction.RIGHT.ordinal()] = rightNub;
        nubs_[Direction.BOTTOM.ordinal()] = bottomNub;
        nubs_[Direction.LEFT.ordinal()] = leftNub;

        assert ( pieceNumber >= 1 && pieceNumber <= 9 ) : "the piece number is not valid : " + pieceNumber;
        pieceNumber_ = (byte) pieceNumber;
        orientation_ = orientation;
    }

    /**
     * Copy constructor.
     * @param p
     */
    public Piece(Piece p) {
        this(p.nubs_[0], p.nubs_[1], p.nubs_[2], p.nubs_[3], p.getNumber());
        this.orientation_ = p.getOrientation();
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
    public Piece rotate() {
        return rotate(1);       
    }

    /**
     *  This rotates the piece the specified number of 90 degree inrements.
     */
    public Piece rotate(int num) {
        Direction[] values = Direction.values();
        Direction newOrientation = values[(orientation_.ordinal() + num) % values.length];
        return new Piece(nubs_[0], nubs_[1], nubs_[2], nubs_[3], pieceNumber_, newOrientation);
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

    /**
     *Sum of (orientation index + requested direction ) modulo the number of Directions (4).
     */
    private int getDirectionIndex(Direction dir)  {
        return (orientation_.ordinal() + dir.ordinal()) % Direction.values().length;
    }

    /**
     *Two pieces are equal if they have the same nubs, even if they are rotated differently.
     * @param piece to compare to
     * @return true if logically equal (independent of rotation).
     */
    public boolean equals(Object piece) {
        Piece p = (Piece) piece;
        
        return (               
               p.nubs_[0] == this.nubs_[0] &&
               p.nubs_[1] == this.nubs_[1] &&
               p.nubs_[2] == this.nubs_[2] &&
               p.nubs_[3] == this.nubs_[3] &&
               p.getNumber() == this.getNumber()
        );
    }

    /**
     * @return a nice readable string representation for debugging.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("Piece "+ getNumber() + " (orientation="+orientation_+"): ");
        for (Direction d : Direction.values()) {
            Nub n = getNub(d);
            buf.append(d.toString() + ':' + n.toString() + ";  ");
        }
        return buf.toString();
    }
    
    public String toRawString() {
        StringBuffer buf = new StringBuffer("Piece "+ getNumber() + ":");
        for (int i=0; i<4; i++) {           
            buf.append(nubs_[i].toString() + " ");
        }
        return buf.toString();
    }
}
