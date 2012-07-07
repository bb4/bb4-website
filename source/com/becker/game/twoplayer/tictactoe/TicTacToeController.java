/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.tictactoe;

import com.becker.game.common.player.PlayerList;
import com.becker.game.common.player.PlayerOptions;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.pente.PenteController;

import java.awt.*;

/**
 * Defines everything the computer needs to know to play TicTacToe.
 *
 * Without taking symmetries into account, the number of possible games is 255,168 (Henry Bottomley, 2001)
 * Accounting for board symmetries, the number of games in these conditions is 26,830 (Schaeffer 2002)
 * See http://en.wikipedia.org/wiki/Tic-tac-toe
 *
 * @author Barry Becker
*/
public class TicTacToeController extends PenteController {

    /**
     *  Constructor
     */
    public TicTacToeController() {
        initializeData();
    }

    @Override
    protected TicTacToeBoard createBoard() {
        return new TicTacToeBoard();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions();
    }

    @Override
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new TicTacToePlayerOptions(playerName, color);
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
    protected Searchable createSearchable(TwoPlayerBoard board, PlayerList players) {
        return new TicTacToeSearchable(board, players);
    }
}
