package com.becker.game.twoplayer.common;

import com.becker.game.common.*;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.persistence.TwoPlayerGameExporter;
import com.becker.game.twoplayer.common.persistence.TwoPlayerGameImporter;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.Optimizee;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * This is an abstract base class for a two player Game Controller.
 * It contains the key logic for 2 player zero sum games with perfect information.
 * Some examples include chess, checkers, go, othello, pente, blockade,
 * mancala, nine-mens morris, etc.
 * It implements Optimizee because the games derived from this class
 * can be optimized to improve their playing ability.
 *
 * Instance of this class process requests from the GameViewer.
 * Specifically, the GameViewer tells the TwoPlayerController what move the human made,
 * and the TwoPlayerController returns information such as the computer's move.
 *
 *  @author Barry Becker
 */
public abstract class TwoPlayerController extends GameController {

    protected boolean player1sTurn_ = true;

    /**
     * These weights determine how the computer values each move as parameters to a game dependent evaluation function.
     */
    protected GameWeights weights_;

    /** the method the computer will use for searching for the next move.  */
    private SearchStrategy strategy_;

    /** if this becomes non-null, we will fill in the game tree for display in a UI. */
    private IGameTreeViewable gameTreeListener_;

    /** Worker represents a separate thread for computing the next move. */
    private TwoPlayerSearchWorker worker_;

    /** Capable of searching for the best next move */
    private Searchable searchable_;


    /**
     * Construct the game controller.
     */
    public TwoPlayerController() {
        createPlayers();
        worker_ = new TwoPlayerSearchWorker(this);
    }

    @Override
    public GameOptions getOptions() {
        if (gameOptions_ == null) {
            gameOptions_ = createOptions();
        }
        return gameOptions_;
    }

    /**
     * @return custom set of search and game options.
     */
    protected abstract TwoPlayerOptions createOptions();

    /**
     * These options define the search algorithm and other settings.
     * @return game options
     */
    public TwoPlayerOptions getTwoPlayerOptions() {

        return (TwoPlayerOptions) getOptions();
    }

    public TwoPlayerViewable get2PlayerViewer() {
       return (TwoPlayerViewable)viewer_;
    }

    /**
     * Return the game board back to its initial opening state
     */
    @Override
    public void reset() {
        worker_.interrupt();
        super.reset();
        searchable_ = null;
        createPlayers();
        player1sTurn_ = true;
    }

    /**
     * save the current state of the game to a file in SGF (4) format (standard game format).
     *This should some day be xml (xgf)
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile( String fileName, AssertionError ae ) {
        TwoPlayerGameExporter exporter = new TwoPlayerGameExporter(this);
        exporter.saveToFile(fileName, ae);
    }

    @Override
    public void restoreFromFile( String fileName ) {
        TwoPlayerGameImporter importer = new TwoPlayerGameImporter(this);
        importer.restoreFromFile(fileName);
        TwoPlayerMove m = (TwoPlayerMove)(getLastMove());
        if (m != null) {
            m.setValue( getSearchable().worth( m, weights_.getDefaultWeights()));
        }
    }

    /**
     * @return true if the computer is supposed to make the first move.
     */
    public boolean doesComputerMoveFirst() {
        return !getPlayers().getPlayer1().isHuman();
    }

    /**
     * create the 2 players.
     */
    private void createPlayers() {
        if (getPlayers() == null) {
            PlayerList players = new PlayerList();
            players.add(new Player(getTwoPlayerOptions().getPlayerName(true), null, true));
            players.add(new Player(getTwoPlayerOptions().getPlayerName(false), null, false));
            setPlayers(players);
        }
        else {
            getPlayers().reset();
        }
    }

    /**
     * @return the search strategy to use to find the next move.
     */
    public final SearchStrategy getSearchStrategy() {
       return strategy_;
    }

    /**
     * @return true if it is currently player1's turn.
     */
    public final boolean isPlayer1sTurn() {
        return player1sTurn_;
    }

    /**
     * @return true if player2 is a computer player
     */
    public final Player getCurrentPlayer() {
        return player1sTurn_? getPlayers().getPlayer1() : getPlayers().getPlayer2();
    }

    /**
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin() {
        if (!getPlayers().anyPlayerWon())
            return 0;
        return board_.getTypicalNumMoves() / getNumMoves();
    }

    /**
     * this returns a reference to the weights class for editing
     * @return  contains the weights used for computer player1 and 2.
     */
    public final GameWeights getComputerWeights() {
        return weights_;
    }

    /**
     * retract the most recently played move
     * @return  the move which was undone (null returned if no prior move)
     */
    @Override
    public Move undoLastMove() {
        TwoPlayerMove m = (TwoPlayerMove) board_.undoMove();
        if (m != null) {
            player1sTurn_ = m.isPlayer1();
        }
        return m;
    }

    /**
     * Currently online play not available for 2 player games - coming soon!
     * @return false
     */
    @Override
    public boolean isOnlinePlayAvailable() {
        return false;
    }

    /**
     * The computer will search for and make its next move.
     * The search for the best computer move happens on a separate thread so the UI does not lock up.
     * @param player1 if true then the computer moving is player1
     * @return the move the computer selected (may return null if no move possible)
     */
    TwoPlayerMove findComputerMove( boolean player1 ) {
        ParameterArray weights;
        player1sTurn_ = player1;

        getProfiler().startProfiling();

        assert (!getMoveList().isEmpty()) : "Error: null before search";
        TwoPlayerMove move = (TwoPlayerMove) getMoveList().getLastMove();
        TwoPlayerMove lastMove = move.copy();

        weights = player1 ? weights_.getPlayer1Weights() : weights_.getPlayer2Weights();

        if ( gameTreeListener_ != null ) {
            gameTreeListener_.resetTree(lastMove);
        }
        TwoPlayerMove selectedMove = searchForNextMove(weights, lastMove);

        if ( selectedMove != null ) {
            makeMove( selectedMove );
            GameContext.log( 2, "computer move :" + selectedMove.toString() );
        }

        getProfiler().stopProfiling(strategy_.getNumMovesConsidered());

        return selectedMove;
    }

    /**
     * **** SEARCH ******
     * @return the best move to use as the next move.
     */
    private TwoPlayerMove searchForNextMove(ParameterArray weights, TwoPlayerMove lastMove) {
        strategy_ = getTwoPlayerOptions().getSearchOptions().getSearchStrategy(getSearchable(), weights);

        SearchTreeNode root = null;
        if (gameTreeListener_ != null) {
            strategy_.setGameTreeEventListener(gameTreeListener_);
            root = gameTreeListener_.getRootNode();
        }

        return strategy_.search( lastMove, root );
    }

    /**
     * record the humans move p.
     * @param m the move the player made
     * @return the same move with some of the fields filled in
     */
    public final Move manMoves( Move m ) {
        makeMove( m );
        // we pass the default weights because we just need to know if the game is over
        m.setValue(getSearchable().worth( m, weights_.getDefaultWeights() ));
        return m;
    }

    /**
     * this makes an arbitrary move (assumed valid) and adds it to the move list.
     * Calling this does not keep track of weights or the search.
     * Its most common use is for browsing the game tree.
     *  @param m the move to play.
     */
    @Override
    public void makeMove( Move m ) {
        board_.makeMove( m );
        player1sTurn_ = !((TwoPlayerMove)m).isPlayer1();
    }

    /**
     * When this method is called the game controller will asynchronously search for the next move
     * for the computer to make. It returns immediately (unless the computer is playing itself).
     * Usually returns false, but will return true if it is a computer vs computer game, and the
     * game is over.
     * @param player1ToMove true if is player one to move.
     * @return true if the game is over.
     * @throws AssertionError thrown if something bad happened while searching.
     */
    public boolean requestComputerMove(boolean player1ToMove) throws AssertionError {
        return requestComputerMove(player1ToMove, getTwoPlayerOptions().isAutoOptimize());
    }

    /**
     * Request the next computer move. It will be the best move that the computer can find.
     * Launches a separate thread to do the search for the next move.
     * @param player1ToMove true if player one to move.
     * @param synchronous if true then the method does not return until the next move has been found.
     * @return true if the game is over
     * @throws AssertionError if something bad happened while searching.
     */
     public boolean requestComputerMove(final boolean player1ToMove, boolean synchronous) throws AssertionError {

        return worker_.requestComputerMove(player1ToMove, synchronous);
    }

    /**
     * Let the computer play against itself for a long time as it optimizes its parameters.
     * @return the resulting optimized parameters.
     */
    public ParameterArray runOptimization() {
        Optimizer optimizer = new Optimizer( this.getOptimizee(), getTwoPlayerOptions().getAutoOptimizeFile() );

        ParameterArray optimizedParams;
        optimizedParams =
                optimizer.doOptimization( OptimizationStrategyType.HILL_CLIMBING,
                                          getComputerWeights().getDefaultWeights(),
                                          WINNING_VALUE);
        return optimizedParams;
    }

    /**
     *  @return true if the viewer is currently processing (i.e. searching)
     */
    public boolean isProcessing() {
        return worker_.isProcessing();
    }

    public void pause() {
        if (getSearchStrategy() == null) {
            GameContext.log(1, "There is no search to pause" );
            return;
        }
        getSearchStrategy().pause();
        GameContext.log(1, "search strategy paused." );
    }

    public boolean isPaused()  {
        return getSearchStrategy().isPaused();
    }

    /**
     * if desired we can set a game tree listener. If non-null then this
     * will be updated as the search is conducted. The GameTreeDialog
     * is an example of something that implements this interface and can
     * be used to view the game tree as the search is progressing.
     *
     * Here's how the GameTreeDialog is able to show the game tree:
     * When the user indicates that they want to see the GameTreeDialog,
     * the game panel gives the GameTreeDialog to the Controller:
     * controller_.setGameTreeListener( treeDialog_ );
     * Then whenever a move by either party occurs, the GameTreeDialog recieves
     * a game tree event. The GameTreeDialog renders the tree that was build up during search.
     * It already has a reference to the root of the tree.
     * If this method is never called, the controller knows
     * that it should not bother to create the tree when searching.
     */
    public final void setGameTreeViewable( IGameTreeViewable gameTreeViewable ) {
        gameTreeListener_ = gameTreeViewable;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDone() {
        TwoPlayerMove lastMove = (TwoPlayerMove)getLastMove();
        return getSearchable().done(lastMove, false);
    }

    final Optimizee getOptimizee() {
        return new TwoPlayerOptimizee(this);
    }

    public Searchable getSearchable() {
        if (searchable_ == null) {
            SearchOptions options = ((TwoPlayerOptions) gameOptions_).getSearchOptions();
            searchable_ = createSearchable((TwoPlayerBoard)board_, getPlayers(),  options);
        }
        return searchable_;
    }

    protected abstract Searchable createSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options);

}