package com.becker.game.twoplayer.tictactoe;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.pente.PenteController;

/**
 * Defines everything the computer needs to know to play Pente.
 *
 * @author Barry Becker
*/
public class TicTacToeController extends PenteController
{

    /** for any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int BEST_PERCENTAGE = 100;

 
    /**
     *  Construct the Pente game controller
     */
    public TicTacToeController()
    {
        initializeData();
        board_ = new TicTacToeBoard();
    }

    @Override
    protected int getDefaultBestPercentage() {
        return BEST_PERCENTAGE;
    }

    /**
     *  this gets the pente specific patterns and weights
     */
    @Override
    protected void initializeData()
    {
        weights_ = new TicTacToeWeights();
        patterns_ = new TicTacToePatterns();
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
