package com.becker.game.twoplayer.go.board.update;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoStone;
import com.becker.game.twoplayer.go.board.move.GoMove;


/**
 * Stub implementation of TwoPlayerMove to help test the search strategy classes without needing
 * a specific game implementation.
 *
 * @author Barry Becker
 */
public class GoMoveStub extends GoMove {

    private int numCaptures = 0;

    /**
     * Constructor. This should never be called directly
     * instead call the factory method so we recycle objects.
     * use createMove to get moves, and dispose to recycle them
     */
    public GoMoveStub( GoStone stone ) {
        super( new Location(1, 1), 1, stone );
    }

    @Override
    public boolean isSuicidal( GoBoard board ) {
        return false;
    }


    public void setNumCaptures(int numCaptures) {
        this.numCaptures = numCaptures;
    }

    @Override
    public int getNumCaptures() {
        return numCaptures;
    }
}
