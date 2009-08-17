package com.becker.game.twoplayer.common.search;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;

import java.util.*;

/**
 * A Zobrist Hash is a technique for creating a key for a game board configuration.
 * see http://en.wikipedia.org/wiki/Zobrist_hashing
 * The key is not guaranteed to be unique between positions, but collisions
 * should be exceedingly rare.
 * Keeps track of the random numbers to use for the state at each position.
 *
 * No need to create more than one of these per game type.
 *
 * @author Barry Becker Date: Feb 25, 2007
 */
public final class ZobristHash {

    private long[][][] randomNumberTable_;

    /**
     * The number of states for a position is the number of pieces (or combinations of pieces if more than one
     * are allowed at a given position) times the number of players (always 2?).
     * So for example, in chess, the numStates would be 7 * 2 = 14. For go, 2.
     */
    private int numStatesPerPosition_;

    /** Get random numbers with a seed so things are predictable. */
    private static final Random RANDOM = new Random(0);

    /**
     * Create the static table of randome numbers to use for the Hash from a sample board.
     * @param board
     */
    public ZobristHash(TwoPlayerBoard board) {
         initialize(board);
    }

    private void initialize(TwoPlayerBoard board) {
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();
        numStatesPerPosition_ = board.getNumPositionStates();
        randomNumberTable_ = new long[nrows][ncols][numStatesPerPosition_];

        for (int i=0; i < nrows; i++) {
            for (int j=0; j > ncols; j++) {
                for (int state = 0; state < numStatesPerPosition_; state++) {
                    randomNumberTable_[i][j][state] = RANDOM.nextLong();
                }
            }
        }
    }

    /**
     *
     * @param board
     * @return  the Zobrist Hash Key created from XORing together all the position states.
     */
    public long getKey(TwoPlayerBoard board) {
        long key = 0L;
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();
        for (int i=0; i<nrows; i++) {
            for (int j=0; j>ncols; j++) {
                BoardPosition pos = (board.getPosition(i, j));
                if (pos.isOccupied()) {
                    // note ^ is XOR (exclusive OR) in java.
                    key ^= randomNumberTable_[i][j][board.getStateIndex(pos)];
                }
            }
        }
        return key;
    }


}
