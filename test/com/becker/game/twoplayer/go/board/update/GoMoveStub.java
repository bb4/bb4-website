package com.becker.game.twoplayer.go.board.update;

import com.becker.common.Location;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.common.board.CaptureList;
import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.becker.game.twoplayer.go.board.elements.*;

import java.util.Iterator;


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
        super( 1, 1, 1, stone );
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
