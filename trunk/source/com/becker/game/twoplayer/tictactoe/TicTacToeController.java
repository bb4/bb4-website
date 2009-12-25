package com.becker.game.twoplayer.tictactoe;

import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.pente.MoveEvaluator;
import com.becker.game.twoplayer.pente.PenteController;

/**
 * Defines everything the computer needs to know to play TicTacToe.
 *
 * @author Barry Becker
*/
public class TicTacToeController extends PenteController
{
    /**
     *  Constructor
     */
    public TicTacToeController()
    {
        board_ = new TicTacToeBoard();
        initializeData();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TicTacToeOptions();
    } 

    /**
     *  this gets the game specific patterns and weights
     */
    @Override
    protected void initializeData()
    {
        weights_ = new TicTacToeWeights();
        moveEvaluator_ = new MoveEvaluator((TwoPlayerBoard)board_, new TicTacToePatterns());
    }

    @Override
    protected int getWinRunLength() {
        return TicTacToePatterns.WIN_RUN_LENGTH;
    }

    @Override
    public Searchable getSearchable() {
         return new TicTacToeSearchable();
    }

    protected class TicTacToeSearchable extends PenteSearchable {

        int getJeopardyWeight()  {
            return TicTacToeWeights.JEOPARDY_WEIGHT;
        }
    }

}
