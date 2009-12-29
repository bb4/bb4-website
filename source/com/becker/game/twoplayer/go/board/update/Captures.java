package com.becker.game.twoplayer.go.board.update;

import com.becker.game.twoplayer.go.GoMove;

/**
 *  Maintains the count of captured stones for each side.
 * @author Barry Becker
 */
public class Captures {


    protected int numWhiteStonesCaptured_ = 0;
    protected int numBlackStonesCaptured_ = 0;

    /**
     * Constructor
     */
    public Captures() {}


    public int getNumCaptures(boolean player1StonesCaptured) {
        return player1StonesCaptured ? numBlackStonesCaptured_ : numWhiteStonesCaptured_ ;
    }


    /**
     * @param move the move just made or removed.
     * @param increment if true then add to number of captures, else subtract.
     */
    public void updateCaptures(GoMove move, boolean increment) {

        int numCaptures = move.getNumCaptures();
        int num = increment ? move.getNumCaptures() : -move.getNumCaptures();

        if (numCaptures > 0) {
            if (move.isPlayer1()) {
                numWhiteStonesCaptured_ += num;
            } else {
                numBlackStonesCaptured_ += num;
            }
        }
    }

}
