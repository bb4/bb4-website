package com.becker.game.twoplayer.common.search.test.strategy;

import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.test.Progress;

/**
 * The expected moves for a given game for each combination of game progress and player.
 * @author Barry Becker
 */
public class ExpectedMoveMatrix {

    private TwoPlayerMove beginningP1;
    private TwoPlayerMove beginningP2;
    private TwoPlayerMove middleP1;
    private TwoPlayerMove middleP2;
    private TwoPlayerMove endP1;
    private TwoPlayerMove endP2;

    public ExpectedMoveMatrix(TwoPlayerMove beginningPlayer1, TwoPlayerMove beginningPlayer2,
                              TwoPlayerMove middlePlayer1, TwoPlayerMove middlePlayer2,
                              TwoPlayerMove endPlayer1, TwoPlayerMove endPlayer2) {
        beginningP1 = beginningPlayer1;
        beginningP2 = beginningPlayer2;
        middleP1 = middlePlayer1;
        middleP2 = middlePlayer2;
        endP1 = endPlayer1;
        endP2 = endPlayer2;
    }


    public TwoPlayerMove getExpectedMove(Progress progress, boolean player1) {
         TwoPlayerMove expectedMove = null;
         switch (progress) {
            case BEGINNING :
                expectedMove = player1 ?  beginningP1 : beginningP2;
                break;
            case MIDDLE :
                expectedMove = player1 ?  middleP1 : middleP2;
                break;
            case END :
                expectedMove = player1 ?  endP1 : endP2;
                break;
        }
        return expectedMove;
     }
}
