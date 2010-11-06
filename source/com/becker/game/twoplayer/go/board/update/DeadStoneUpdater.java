package com.becker.game.twoplayer.go.board.update;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoStone;

/**
 * Responsible for .
 * @author Barry Becker
 */
public final class DeadStoneUpdater
{
    private GoBoard board_;

    /** keeps track of dead stones.  */
    private DeadStones deadStones_;


    /**
     * Construct the Go game controller.
     */
    public DeadStoneUpdater(GoBoard board)
    {
        board_ = board;
        deadStones_ = new DeadStones();
    }


    public int getNumDeadStonesOnBoard(boolean forPlayer1)  {
        return deadStones_.getNumberOnBoard(forPlayer1);
    }

    public void reset() {
        deadStones_.clear();
    }

    /**
     * Update the final life and death status of all the stones still on the board.
     * This method must only be called once at the end of the game or stones will get prematurely marked as dead.
     * @@ should do in 2 passes.
     * The first can update the health of groups and perhaps remove obviously dead stones.
     */
    public void determineDeadStones()
    {
       board_.updateTerritory(true);

       for ( int row = 1; row <= board_.getNumRows(); row++ ) {    //rows
            for ( int col = 1; col <= board_.getNumCols(); col++ ) {  //cols
                GoBoardPosition space = (GoBoardPosition)board_.getPosition( row, col );
                if (space.isOccupied())  {
                    GoStone stone = (GoStone)space.getPiece();
                    int side = (stone.isOwnedByPlayer1() ? 1: -1);
                    GameContext.log(1, "life & death: "+space+" health="+stone.getHealth()
                                       +" string health=" +space.getGroup().getRelativeHealth(board_, true));
                    if (side*stone.getHealth() < 0)  {
                        // then the stone is more dead than alive, so mark it so
                        GameContext.log(1, "setting "+space+" to dead");
                        stone.setDead(true);
                        deadStones_.increment(space.getPiece().isOwnedByPlayer1());
                    }
                }
            }
        }
    }
}
