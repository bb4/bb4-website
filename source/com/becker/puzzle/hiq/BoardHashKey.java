package com.becker.puzzle.hiq;

/**
 * @author Barry Becker Date: Jan 1, 2006
 */
public class BoardHashKey {
    //private final boolean bits_[];
    private int bits_;      // the first 32 positions
    private boolean finalBit_; // the final, 33rd position
    private boolean nextToFinalBit_; // the final, 32rd position

    private static final int NUM_PEGS = 33;
    private static final int RIGHT_SHIFT = 16;

    private static final byte[] ROTATE1 =
            {2, 1, 0, 5, 4, 3, 12, 11, 10, 9, 8, 7, 6, 19, 18, 17, 16, 15, 14, 13, 26, 25, 24, 23, 22, 21, 20, 29, 28, 27, 32, 31, 30};

    private static final byte[] ROTATE2 =
            {12, 19, 26, 11, 18, 25, 2, 5, 10, 17, 24, 29, 32, 1, 4, 9, 16, 23, 28, 31, 0, 3, 8, 15, 22, 27, 30, 7, 14, 21, 6, 13, 20};
    private static final byte[] ROTATE3 =
            {26, 19, 12, 25, 18, 11, 32, 29, 24, 17, 10, 5, 2, 31, 28, 23, 16, 9, 4, 1, 30, 27, 22, 15, 8, 3, 0, 21, 14, 7, 20, 13, 6};

    private static final byte[] ROTATE4 =
            {32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
    private static final byte[] ROTATE5 =
            {30, 31, 32, 27, 28, 29, 20, 21, 22, 23, 24, 25, 26, 13, 14, 15, 16, 17, 18, 19, 6, 7, 8, 9, 10, 11, 12, 3, 4, 5, 0, 1, 2};

    private static final byte[] ROTATE6 =
            {20, 13, 6, 21, 14, 7, 30, 27, 22, 15, 8, 3, 0, 31, 28, 23, 16, 9, 4, 1, 32, 29, 24, 17, 10, 5, 2, 25, 18, 11, 26, 19, 12};
    private static final byte[] ROTATE7 =
            {6, 13, 20, 7, 14, 21, 0, 3, 8, 15, 22, 27, 30, 1, 4, 9, 16, 23, 28, 31, 2, 5, 10, 17, 24, 29, 32, 11, 18, 25, 12, 19, 26};


    public BoardHashKey() {
    }


    public BoardHashKey(PegBoard board) {

        long place = 1;
        for (int i = 0; i < PegBoard.SIZE; i++) {
            for (int j = 0; j < PegBoard.SIZE; j++) {
                if (board.isValidPosition(i, j)) {
                    boolean v = board.getPosition(i, j);
                    if (i == PegBoard.SIZE - 1) {
                        if (j == PegBoard.SIZE - 3) {
                            finalBit_ = v;
                        }
                        else if ( j == PegBoard.SIZE - 4) {
                            nextToFinalBit_ = v;
                        } else {
                           bits_ += v ? place : 0;
                           place <<= 1;
                        }
                    } else {
                        bits_ += v ? place : 0;
                        place <<= 1;
                    }
                }
            }
        }
    }

    public void set(int i, boolean val) {
        if (i == NUM_PEGS - 1) {
            finalBit_ = val;
        } else if (i == NUM_PEGS - 2)  {
            nextToFinalBit_ = val;
        }
        else {
            long place = 1 << i;
            bits_ -= get(i) ? place : 0;
            bits_ += val ? place : 0;
        }
    }

    public boolean get(int i)  {
        if (i == NUM_PEGS - 1) {
            return finalBit_;
        } else if (i == NUM_PEGS - 2) {
            return nextToFinalBit_;
        }
        long place = 1 << i;
        return ((bits_ & place) != 0);
    }

    /**
     * rotate in increments of 90 degrees
     * if rotateIndex = 0 then no rotation
     * if rotatnIndex = 1 then 90 degrees, etc
     */
    public BoardHashKey symmetry(int symmIndex) {
        switch (symmIndex) {
            case 0: return this;
            case 1: return rotate(ROTATE1);
            case 2: return rotate(ROTATE2);
            case 3: return rotate(ROTATE3);
            case 4: return rotate(ROTATE4);
            case 5: return rotate(ROTATE5);
            case 6: return rotate(ROTATE6);
            case 7: return rotate(ROTATE7);
            default: assert false;
        }
        return null;
    }

    public boolean equals(Object k) {
        BoardHashKey key = (BoardHashKey) k;
        return (bits_ == key.bits_ && finalBit_ == key.finalBit_ && nextToFinalBit_ == key.nextToFinalBit_);
    }

    public int hashCode() {
        //System.out.println("hashCode for "+toString()+" ="+ Math.abs((int) bits_));
        //return Math.abs((int) bits_);
        return nextToFinalBit_ ? -bits_ : bits_;

    }

    private BoardHashKey rotate(byte[] rotateIndices) {
        BoardHashKey rotatedKey = new BoardHashKey();
        for (int i = 0; i < NUM_PEGS; i++)  {
            rotatedKey.set(i, get(rotateIndices[i]));
        }
        return rotatedKey;
    }

    public String toString() {
        StringBuffer buf= new StringBuffer(finalBit_?"1":'0' + (nextToFinalBit_?"1":"0"));
        buf.append(Integer.toBinaryString(bits_));
        return buf.toString();
    }

}