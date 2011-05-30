package com.becker.game.twoplayer.go;

import com.becker.game.common.GameContext;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.cache.ScoreCache;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoProfiler;
import com.becker.game.twoplayer.go.board.GoSearchable;
import com.becker.game.twoplayer.go.board.move.GoMove;
import com.becker.game.twoplayer.go.options.GoOptions;
import com.becker.game.twoplayer.go.options.GoWeights;
import com.becker.game.twoplayer.go.persistence.GoGameExporter;
import com.becker.game.twoplayer.go.persistence.GoGameImporter;

import java.util.List;

/**
 * Defines everything the computer needs to know to play Go.
 *
 * @see package.html for more info.
 * @see GoBoard
 * @author Barry Becker
 */
public final class GoController extends TwoPlayerController {

    public static final String VERSION = "0.99";

    /** if true use an additional heuristic to get more accurate scoring of group health in a second pass. */
    public static final boolean USE_RELATIVE_GROUP_SCORING = true;

    /** default num row and columns for a default square go board.   */
    static final int DEFAULT_NUM_ROWS = 5;

    /** if difference greater than this, then consider a win. */
    public static final int WIN_THRESHOLD = 2000;

    private ScoreCache scoreCache_;

    private BoardOpts boardOpts;

    /**
     * Construct the Go game controller.
     */
    public GoController() {
        boardOpts = new BoardOpts( DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS, 0);
    }

    /**
     * Construct the Go game controller given dimensions and number of handicap stones.
     * @param numHandicapStones  0 - 9 handicap stones to show initially.
     */
    public GoController( int nrows, int ncols, int numHandicapStones ) {
        boardOpts = new BoardOpts(nrows, ncols, numHandicapStones);
        initializeData();
    }

    @Override
    protected GoBoard createBoard() {
        return new GoBoard( boardOpts.numRows, boardOpts.numCols, boardOpts.numHandicaps);
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        TwoPlayerOptions options = new GoOptions();
        options.setPlayerName(true, GameContext.getLabel("BLACK"));
        options.setPlayerName(false, GameContext.getLabel("WHITE"));
        return options;
    }

    /**
     * this gets the Go specific patterns and weights.
     */
    @Override
    protected void initializeData() {
        weights_ = new GoWeights();
    }

    @Override
    protected GoProfiler getProfiler() {
        return GoProfiler.getInstance();
    }

    /**
     * specify the number of handicap stones.
     * @param handicap number of handicap stones to place on the board at star points.
     */
    public void setHandicap( int handicap ) {
        ((GoBoard) getBoard()).setHandicap( handicap );
        player1sTurn_ = false;
    }

    /**
     * @return true if the computer is to make the first move.
     */
    @Override
    public boolean doesComputerMoveFirst() {
        int handicap = ((GoBoard) getBoard()).getHandicap();
        Player player1 = getPlayers().getPlayer1();
        return ((!player1.isHuman() && (handicap == 0)) ||
                (player1.isHuman() && (handicap > 0)));
    }

    /**
     * Measure is determined by the score (amount of territory + captures)
     * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin() {
        return (int)Math.abs(getFinalScore(true) - getFinalScore(false));
    }

    /**
     * @param player1 if true, then the score for player one is returned else player2's score is returned
     * @return the score (larger is better regardless of player)
     */
    public double getFinalScore(boolean player1) {
        if (isProcessing()) {
            GameContext.log(0,  "Error: tried to get Score() while processing!");
            return 0;
        }
        GameContext.log(0, "cache results " + scoreCache_);
        return ((GoSearchable)getSearchable()).getFinalScore(player1);
    }

    /**
     * Call this at the end of the game when we need to try to get an accurate score.
     * @param forPlayer1  true if for player one (black)
     * @return the actual amount of territory for the specified player (each empty space counts as one)
     */
    public int getTerritory( boolean forPlayer1 ) {
        return((GoBoard) getBoard()).getTerritoryEstimate(forPlayer1, true);
    }

    /**
     * return the game board back to its initial opening state.
     */
    @Override
    public void reset() {
        super.reset();
        if ( ((GoBoard) getBoard()).getHandicap() > 0 )   {
            player1sTurn_ = false;
        }
        scoreCache_ = new ScoreCache();
    }

    public void computerMovesFirst()  {
        List moveList = getSearchable().generateMoves( null, weights_.getPlayer1Weights());
        // select the best (first move, since they are sorted) move to use
        GoMove m = (GoMove) moveList.get( 0 );

        makeMove( m );
    }

    /**
     * save the current state of the go game to a file in SGF (4) format (standard game format).
     *This should some day be xml (xgf)
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile( String fileName, AssertionError ae ) {
        GoGameExporter exporter = new GoGameExporter(this);
        exporter.saveToFile(fileName, ae);
    }

    @Override
    public void restoreFromFile( String fileName ) {
        GoGameImporter importer = new GoGameImporter(this);
        importer.restoreFromFile(fileName);
    }

    @Override
    protected Searchable createSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        return new GoSearchable(board, players, options, scoreCache_);
    }

    private class BoardOpts {
        int numRows, numCols;
        int numHandicaps;

        BoardOpts(int numRows, int numCols, int numHandicaps) {
            this.numRows = numRows;
            this.numCols = numCols;
            this.numHandicaps = numHandicaps;
        }
    }
}
