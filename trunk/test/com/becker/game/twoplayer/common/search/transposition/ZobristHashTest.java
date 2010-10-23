package com.becker.game.twoplayer.common.search.transposition;

import com.becker.common.Location;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.tictactoe.TicTacToeBoard;
import junit.framework.TestCase;

/**
 * Verify expected hash key are generated based on board state.
 * @author Barry Becker
 */
public class ZobristHashTest  extends TestCase {

    private static final Long CENTER_X_HASH = 428667830982598836L;
    private static final Long CORNER_O_HASH = -6688467811848818630L;

    private ZobristHash hash;
    private TwoPlayerBoard board;

    @Override
    public void setUp() {
        board = new TicTacToeBoard();
        hash = new ZobristHash(board);
    }

    public void testEmptyBoardHash() {
        assertEquals("Unexpected hashkey for empty board", new Long(0), hash.getKey());
    }

    public void testCenterXHash() {
        applyMoveToHash(2, 2, true);
        assertEquals("Unexpected hashkey for board with center X",
                CENTER_X_HASH, hash.getKey());
    }

    public void testCenterXBoard() {
        TwoPlayerMove m = TwoPlayerMove.createMove(new Location(2, 2), 0, new GamePiece(true));
        board.makeMove(m);
        hash = new ZobristHash(board);
        assertEquals("Unexpected hashkey for board with center X",
                CENTER_X_HASH, hash.getKey());
    }

    public void testCornerOHash() {
        applyMoveToHash(1, 1, false);
        assertEquals("Unexpected hashkey for board with corner O",
                CORNER_O_HASH, hash.getKey());
    }

    public void testCornerOBoard() {
        TwoPlayerMove m = TwoPlayerMove.createMove(new Location(1, 1), 0, new GamePiece(false));
        board.makeMove(m);
        hash = new ZobristHash(board);
        assertEquals("Unexpected hashkey for board with corner O",
                CORNER_O_HASH, hash.getKey());
    }

    public void testHashAfterUndo() {

        applyMoveToHash(2, 2, true);
        applyMoveToHash(2, 2, true);
        assertEquals("Unexpected hashkey for entry board after undo",
                new Long(0), hash.getKey());
    }


    public void testHashAfterTwoMoves() {

        applyMoveToHash(2, 2, true);
        applyMoveToHash(1, 1, false);
        assertEquals("Unexpected hashkey for board after 2 moves",
                new Long(-6422371760107745138L), hash.getKey());
    }

    public void testHashAfterTwoMovesThenUndo() {

        applyMoveToHash(2, 2, true);
        applyMoveToHash(1, 1, false);

        applyMoveToHash(2, 2, true);
        assertEquals("Unexpected hashkey for board after 2 moves then an undo",
                CORNER_O_HASH, hash.getKey());        
    }


    private void applyMoveToHash(int row, int col, boolean player1)
    {
        GamePiece p = new GamePiece(player1);
        TwoPlayerMove m = TwoPlayerMove.createMove(new Location(row, col), 0, p);
        int stateIndex = board.getStateIndex(new BoardPosition(row, col, p));
        hash.applyMove(m, stateIndex);
    }

}
