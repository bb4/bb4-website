package com.becker.game.twoplayer.tictactoe;

import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.pente.PenteController;

/**
 * Defines everything the computer needs to know to play TicTacToe.
 *
 * @author Barry Becker
*/
public class TicTacToeController extends PenteController {
    
    /**
     *  Constructor
     */
    public TicTacToeController() {
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
    protected void initializeData() {
        weights_ = new TicTacToeWeights();
    }

    @Override
    protected int getWinRunLength() {
        return TicTacToePatterns.WIN_RUN_LENGTH;
    }

    @Override
    protected Searchable createSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        return new TicTacToeSearchable(board, players, options);
    }

}
