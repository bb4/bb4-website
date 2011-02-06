package com.becker.game.twoplayer.common.search.transposition;

import com.becker.common.Location;
import com.becker.game.common.MoveList;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * As an experiment to see the sequence of moves that led to a certain hash key,
 * include the move history in the key itself. Should never do this in practice.
 *
 * @author Barry Becker
 */
public final class HashKey {

    private Long key;
    //public LinkedList<Location> moveHistory;

    /**
     * Create the static table of random numbers to use for the Hash from a sample board.
     */
    public HashKey() {

        key = 0L;
        //moveHistory = new LinkedList<Location>();

    }

    /**
     * Constructor used for tests where we want to create a HashKey with a specific value.
     * @param key key value.
     */
    public HashKey(Long key) {
        this.key = key;
    }

    public void applyMove(Location move, long specialNumber) {
        key ^= specialNumber;
        /*
        if (!moveHistory.isEmpty() && moveHistory.getLast().equals(move)) {
            moveHistory.removeLast();
        } else {
            moveHistory.add(move);
        }*/
    }

    public boolean matches(Long key) {
        return this.key.equals(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashKey)) return false;

        HashKey hashKey = (HashKey) o;

        return !(key != null ? !key.equals(hashKey.key) : hashKey.key != null);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }


    public String toString() {
        return "key="+ key; // + " history=" + moveHistory.toString();
    }
}
