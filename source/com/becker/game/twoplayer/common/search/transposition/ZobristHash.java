package com.becker.game.twoplayer.common.search.transposition;

import com.becker.common.Location;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import java.util.Random;

/**
 * A Zobrist Hash is a technique for creating a key for a game board configuration.
 * see http://en.wikipedia.org/wiki/Zobrist_hashing
 * The key is not guaranteed to be unique between positions, but collisions
 * should be exceedingly rare.
 * Keeps track of the random numbers to use for the state at each position.
 *
 * No need to create more than one of these per game type.
 * (perhaps we could have instances per type and move numStates here.)
 *
 * @author Barry Becker
 */
public final class ZobristHash {

    private long[][][] randomNumberTable_;

    /** Get random numbers with a seed so things are predictable. */
    private final Random RANDOM;

    private TwoPlayerBoard board;

    private Long currentKey;

    /**
     * Create the static table of randome numbers to use for the Hash from a sample board.
     * @param board game board
     */
    public ZobristHash(TwoPlayerBoard board) {
        RANDOM = new Random(0);
        GameContext.log(0, "***  created new ZOBRIST HASH  ***");
        this.board = board;
        initialize();
    }

    /**
     * The number of states for a position is the number of pieces (or combinations of pieces - if more than one
     * are allowed at a given position) times the number of players (always 2?).
     * So for example, in chess, the numStates would be 7 * 2 = 14. For go, its 2.
     */
    private void initialize() {
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();
        int numStatesPerPosition = board.getNumPositionStates();
        randomNumberTable_ = new long[nrows][ncols][numStatesPerPosition];

        for (int i=0; i < nrows; i++) {
            for (int j=0; j < ncols; j++) {
                for (int state = 0; state < numStatesPerPosition; state++) {
                    randomNumberTable_[i][j][state] = RANDOM.nextLong();
                }
            }
        }
        currentKey = getInitialKey(board);
    }

    /**
     * @param board   board state to initailize hash from.
     * @return the Zobrist Hash Key created from XORing together all the position states.
     */
    private Long getInitialKey(TwoPlayerBoard board) {
        currentKey = 0L;
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();

        for (int i=1; i<=nrows; i++) {
            for (int j=1; j<=ncols; j++) {
                BoardPosition pos = board.getPosition(i, j);
                if (pos.isOccupied()) {
                    // note ^ is XOR (exclusive OR) in java.
                    applyPositionToKey(new Location(i, j), board.getStateIndex(pos));
                }
            }
        }
        return currentKey;
    }

    /**
     * @return  the current zobrish hash key for the board state.
     */
    public Long getKey() {
        return currentKey;
    }

    public void applyMove(TwoPlayerMove move, int stateIndex) {
        if (!move.isPassingMove())
            applyPositionToKey(move.getToLocation(), stateIndex);
    }

    /**
     * note ^ is XOR (exclusive OR) in java.
     * @return key after the move has been made
     */
    private Long applyPositionToKey(Location location, int stateIndex) {
        currentKey ^= randomNumberTable_[location.getRow()-1][location.getCol()-1][stateIndex];
        return currentKey;
    }

}
