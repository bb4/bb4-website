package com.becker.game.twoplayer.go;

import com.becker.game.common.GameContext;
import com.becker.game.common.Player;
import com.becker.game.common.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.persistence.GoGameExporter;
import com.becker.game.twoplayer.go.persistence.GoGameImporter;

import java.util.List;

import static com.becker.game.twoplayer.go.GoControllerConstants.DEFAULT_NUM_ROWS;

/**
 * Defines everything the computer needs to know to play Go.
 *
 * @see package.html for more info.
 * @see GoBoard
 * @author Barry Becker
 */
public final class GoController extends TwoPlayerController {

    public static final String VERSION = "0.99";


    /**
     * Construct the Go game controller.
     */
    public GoController()
    {
        this( DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS, 0);
    }

    /**
     * Construct the Go game controller given dimensions and number of handicap stones.
     * @param numHandicapStones  0 - 9 handicap stones to show initially.
     */
    public GoController( int nrows, int ncols, int numHandicapStones )
    {
        board_ = new GoBoard( nrows, ncols, numHandicapStones );
        initializeData();
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
        ((GoBoard) board_).setHandicap( handicap );
        player1sTurn_ = false;
    }

    /**
     * @return true if the computer is to make the first move.
     */
    @Override
    public boolean doesComputerMoveFirst() {
        int handicap = ((GoBoard) board_).getHandicap();
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
        return ((GoSearchable)getSearchable()).getFinalScore(player1);
    }

    /**
     * get a territory estimate for player1 or player2
     * When the game is over, this should return a precise value for the amount of territory
     * (not yet filled with captures).
     * So the estimate will be very rough at the beginning of the game, but should get better as more pieces are played.
     *
     * Since this can be called while we are processing, we return cached values in
     * those cases to avoid a ConcurrentModificationException.
     *
     * @param forPlayer1 if true, get the captures for player1, else for player2
     * @return estimate of the amount of territory the player has
     */
    public int getTerritoryEstimate( boolean forPlayer1 ) {
        return ((GoSearchable)getSearchable()).getTerritoryEstimate(forPlayer1);

    }

    /**
     * Call this at the end of the game when we need to try to get an accurate score.
     * @param forPlayer1  true if player one (black)
     * @return the actual score (each empty space counts as one)
     */
    public int getTerritory( boolean forPlayer1 ) {
        return((GoBoard) board_).getTerritoryEstimate(forPlayer1, true);
    }

    /**
     * return the game board back to its initial opening state.
     */
    @Override
    public void reset() {
        super.reset();
        if ( ((GoBoard) board_).getHandicap() > 0 )   {
            player1sTurn_ = false;
        }
    }

    public void computerMovesFirst()  {
        List moveList = getSearchable().generateMoves( null, weights_.getPlayer1Weights(), true );
        // select the best(first since sorted) move to use
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
        return new GoSearchable(board, players, options);
    }
}
