package com.becker.game.twoplayer.pente;

import com.becker.game.common.GameContext;
import com.becker.game.common.board.GamePiece;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;

import java.awt.*;
import java.util.Random;

/**
 * Defines everything the computer needs to know to play Pente.
 *
 * @author Barry Becker
*/
public class PenteController extends TwoPlayerController {

    private static final int DEFAULT_NUM_ROWS = 20;

    private Dimension size;

    /**
     *  Constructor
     */
    public PenteController() {
        size = new Dimension(DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS);
        initializeData();
    }

    /**
     *  Construct the Pente game controller given an initial board size
     */
    public PenteController(int nrows, int ncols ) {
        size = new Dimension( nrows, ncols );
        initializeData();
    }

    @Override
    protected PenteBoard createBoard() {
        return new PenteBoard(size.width, size.height);
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new PenteOptions();
    }

    /**
     *  this gets the pente specific patterns and weights
     */
    @Override
    protected void initializeData() {
        weights_ = new PenteWeights();
    }

    /**
     * the first move of the game (made by the computer)
     */
    public void computerMovesFirst()
    {
        int delta = getWinRunLength() - 1;
        int c = (int) (GameContext.random().nextFloat() * (getBoard().getNumCols() - 2 * delta) + delta + 1);
        int r = (int) (GameContext.random().nextFloat() * (getBoard().getNumRows() - 2 * delta) + delta + 1);
        TwoPlayerMove m = TwoPlayerMove.createMove( r, c, 0, new GamePiece(true) );
        makeMove( m );
    }

    protected int getWinRunLength() {
        return PentePatterns.WIN_RUN_LENGTH;
    }


    @Override
    protected Searchable createSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        return new PenteSearchable(board, players, options);
    }
}