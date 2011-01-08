package com.becker.game.twoplayer.blockade;

import com.becker.game.common.*;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.blockade.persistence.BlockadeGameExporter;
import com.becker.game.twoplayer.blockade.persistence.BlockadeGameImporter;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;

import java.util.List;

/**
 * Defines for the computer how it should play com.becker.game.twoplayer.blockade.
 *
 * Todo items
 *   - Restrict to N vertical and N horizontal walls, or allow not to place a wall.
 *     (perhaps only allow wall placements up to (xdim*ydim)/4 walls for each player)
 *   - computer moves only one space instead of two. Computer not winning at end whne one space more required.
 *   - The winner should win as soon as he lands on an opponent base and not have to wait to place the wall.
 *
 * @author Barry Becker
 */
public class BlockadeController extends TwoPlayerController {

    /** the default Blockade board is 14 by 11 */
    private static final int NUM_ROWS = 14;
    private static final int NUM_COLS = 11;


    /**
     *  Construct the Blockade game controller.
     */
    public BlockadeController() {
        initializeData();
        board_ = new BlockadeBoard(NUM_ROWS, NUM_COLS);
    }

    /**
     * this gets the Blockade specific weights.
     */
    @Override
    protected void initializeData() {
        weights_ = new BlockadeWeights();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new BlockadeOptions();
    }

    /**
     * The computer makes the first move in the game
     */
    public void computerMovesFirst() {
        // determine the possible moves and choose one at random.
        MoveList moveList = getSearchable().generateMoves(null, weights_.getPlayer1Weights());

        makeMove( moveList.getRandomMove() );
    }

    /**
     * Measure is determined by the score (amount of territory)
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin() {

        if (!getPlayers().anyPlayerWon()) {
             return 0;
        }
        return getSearchable().worth(getLastMove(), weights_.getDefaultWeights());
    }

    /**
     * save the current state of the com.becker.game.twoplayer.blockade game to a file in SGF (4) format (standard game format).
     *This should some day be xml (xgf)
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile( String fileName, AssertionError ae ) {

        BlockadeGameExporter exporter = new BlockadeGameExporter(this);
        exporter.saveToFile(fileName, ae);
    }


    @Override
    public void restoreFromFile( String fileName ) {
        BlockadeGameImporter importer = new BlockadeGameImporter(this);
        importer.restoreFromFile(fileName);
    }

    /**
     * @param position location
     * @return a possible list of moves based on position passed in.
     */
    public List<BlockadeMove> getPossibleMoveList(BoardPosition position) {
        return ((BlockadeBoard)board_).getPossibleMoveList(position, !position.getPiece().isOwnedByPlayer1());
    }

    @Override
    public Searchable createSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        return new BlockadeSearchable(board, players, options );
    }
}
