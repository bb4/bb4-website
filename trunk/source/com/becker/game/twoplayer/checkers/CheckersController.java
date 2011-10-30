package com.becker.game.twoplayer.checkers;

import com.becker.game.common.MoveList;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Defines how the computer should play checkers.
 *
 * @author Barry Becker
 */
public class CheckersController extends TwoPlayerController {

    /**
     *  Constructor.
     */
    public CheckersController() {
        initializeData();
    }

    @Override
    protected CheckersBoard createBoard() {
        return new CheckersBoard();
    }

    /**
     * this gets the checkers specific weights.
     */
    @Override
    protected void initializeData() {
        weights_ = new CheckersWeights();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new CheckersOptions();
    }

    /**
     * The computer makes the first move in the game.
     */
    public void computerMovesFirst() {
        // determine the possible moves and choose one at random.
        MoveList moveList = getSearchable().generateMoves( null, weights_.getPlayer1Weights());

        assert (!moveList.isEmpty());
        makeMove( moveList.getRandomMove() );

        player1sTurn_ = false;
    }

    /**
     * Measure is determined by the score (amount of territory)
     * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin()  {
        if (!getPlayers().anyPlayerWon())  {
            return 0;
        }
        return getSearchable().worth(getLastMove(), weights_.getDefaultWeights());
    }

    @Override
    protected Searchable createSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        return new CheckersSearchable(board, players, options);
    }
}
