package com.becker.game.twoplayer.common.search.transposition;

import com.becker.common.geometry.Location;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.position.GoStone;
import com.becker.game.twoplayer.go.board.move.GoMove;
import junit.framework.TestCase;


/**
 * Various uniqueness tests for hash keys.
 * @author Barry Becker
 */
public class HashGoUniquenessTest extends TestCase {

    private ZobristHash hash;
    private GoBoard board;


    @Override
    public void setUp() {
        board = new GoBoard(9, 9, 0);
        hash = createZobristHash();
    }

    public void testBoard9x9Uniqueness()  {
          TwoPlayerMove[] moves1 = {
            new GoMove(new Location(9, 6), 0, black()),
            new GoMove(new Location(4, 3), 0, white()),
            new GoMove(new Location(7, 3), 0, black()),
            new GoMove(new Location(6, 6), 0, white()),
            new GoMove(new Location(7, 5), 0, black())
        };

        TwoPlayerMove[] moves2 = {
            new GoMove(new Location(5, 1), 0, black()),
            new GoMove(new Location(3, 7), 0, white()),
            new GoMove(new Location(4, 3), 0, black()),
            new GoMove(new Location(6, 4), 0, white()),
            new GoMove(new Location(7, 3), 0, black()),
            new GoMove(new Location(7, 6), 0, white()),
            new GoMove(new Location(6, 3), 0, black())
        };

        testGoBoardUniqueness(moves1, moves2);
    }

    /** The order in which the moves are placed should not matter */
    public void testBoard9x9UniquenessReordered()  {
          TwoPlayerMove[] moves1 = {
            new GoMove(new Location(7, 5), 0, black()),
            new GoMove(new Location(6, 6), 0, white()),
            new GoMove(new Location(9, 6), 0, black()),
            new GoMove(new Location(4, 3), 0, white()),
            new GoMove(new Location(7, 3), 0, black()),
        };

        TwoPlayerMove[] moves2 = {
            new GoMove(new Location(7, 3), 0, black()),
            new GoMove(new Location(7, 6), 0, white()),
            new GoMove(new Location(4, 3), 0, black()),
            new GoMove(new Location(6, 4), 0, white()),
            new GoMove(new Location(5, 1), 0, black()),
            new GoMove(new Location(3, 7), 0, white()),
            new GoMove(new Location(6, 3), 0, black())
        };

        testGoBoardUniqueness(moves1, moves2);
    }

    /**
     * In actual play I found two boards that generated the same hash key. Verify that this does not happen again.
     */
    public void testGoBoardUniqueness(TwoPlayerMove[] moves1, TwoPlayerMove[] moves2) {

        applyMoves(moves1);
        HashKey key1 = hash.getKey();

        board.reset();
        applyMoves(moves2);
        HashKey key2 = hash.getKey();

        assertEquals("Keys not equal", key1,  key2);
    }

    private GoStone black() {
        return new GoStone(true);
    }
    private GoStone white() {
        return new GoStone(false);
    }

    private void applyMoves(TwoPlayerMove[] moves) {
        for (TwoPlayerMove move : moves) {
            board.makeMove(move);
            applyMoveToHash(move.getToLocation());
        }
    }


    private void applyMoveToHash(Location move) {
        int stateIndex = board.getStateIndex(board.getPosition(move));
        hash.applyMove(move, stateIndex);
    }

    private ZobristHash createZobristHash() {
        return new ZobristHash(board, 0, true);
    }
}
