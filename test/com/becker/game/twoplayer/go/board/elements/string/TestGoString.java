package com.becker.game.twoplayer.go.board.elements.string;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoStone;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author Barry Becker
 */
public class TestGoString extends GoTestCase {


    public void testStringConstruction() {

        GoBoardPosition stone = new GoBoardPosition(4, 4, null, new GoStone(true, 0.5f));
        GoBoard board = new GoBoard(9, 0);
        GoString string = new GoString(stone, board);

        assertFalse(string.isUnconditionallyAlive());
        assertFalse(string.areAnyBlank());
        assertEquals("Unexpected number of liberties", 4, string.getNumLiberties(board));
        assertTrue(string.isOwnedByPlayer1());
    }



    public static Test suite() {
        return new TestSuite(TestGoString.class);
    }
}