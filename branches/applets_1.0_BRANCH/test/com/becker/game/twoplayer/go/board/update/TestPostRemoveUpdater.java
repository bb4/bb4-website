package com.becker.game.twoplayer.go.board.update;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoStone;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author Barry Becker
 */
public class TestPostRemoveUpdater extends GoTestCase {

    /** instance under test */
    private PostRemoveUpdater removeUpdater;

    private CaptureCounts captureCounts  = new CaptureCounts();

    public void testTrivialRemove() {

        GoBoard board = new GoBoard(5, 5, 0);

        removeUpdater = new PostRemoveUpdater(board, captureCounts);

        // a black move on a virgin board.
        Location location = new Location(2, 2);
        GoMove move = new GoMove(location, 0, new GoStone(true));
        board.makeMove(move);  // the board already has its own update. Should we use that or inject?
        removeUpdater.update(move);

        GoBoardPosition pos = (GoBoardPosition) board.getPosition(location);
        assertEquals("Unexpected captures", 0, captureCounts.getNumCaptures(true));
        assertNull("Unexpected string at now empty position", pos.getString());
        assertEquals("Unexpected number of groups", 0, board.getGroups().size());
    }

}