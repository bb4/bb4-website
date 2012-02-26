/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.tictactoe;

import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.pente.pattern.Patterns;
import com.becker.game.twoplayer.pente.PenteSearchable;

/**
 * Defines everything the computer needs to know to play TicTacToe.
 *
 * @author Barry Becker
*/
public class TicTacToeSearchable extends PenteSearchable {

    /**
     *  Constructor
     */
    public TicTacToeSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        super(board, players, options);
    }

    public TicTacToeSearchable(PenteSearchable searchable) {
        super(searchable);
    }

    @Override
    protected Patterns createPatterns() {
        return new TicTacToePatterns();
    }
        
    @Override
    public TicTacToeSearchable copy() {
        return new TicTacToeSearchable(this);
    }

    @Override
    protected int getJeopardyWeight()  {
        return TicTacToeWeights.JEOPARDY_WEIGHT;
    }
}
