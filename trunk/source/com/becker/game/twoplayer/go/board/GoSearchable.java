/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board;

import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerSearchable;
import com.becker.game.twoplayer.common.cache.ScoreCache;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.transposition.HashKey;
import com.becker.game.twoplayer.go.board.analysis.BoardEvaluator;
import com.becker.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.move.GoMove;
import com.becker.game.twoplayer.go.board.move.GoMoveGenerator;
import com.becker.game.twoplayer.go.board.update.DeadStoneUpdater;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Iterator;
import java.util.List;

/**
 * For searching go game's search space.
 *
 * @author Barry Becker
 */
public class GoSearchable extends TwoPlayerSearchable {

    /**
     * Size of group that needs to be in atari before we consider a group urgent.
     * Perhaps this should be one.
     */
    private static final int CRITICAL_GROUP_SIZE = 3;
    private static final boolean USE_SCORE_CACHING = true;

    /** keeps track of dead stones.  */
    private DeadStoneUpdater deadStoneUpdater_;

    private ScoreCache scoreCache_;
    private BoardEvaluator boardEvaluator_;


    /**
     * Constructor.
     */
    public GoSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options, ScoreCache cache) {
        super(board, players, options);
        scoreCache_ = cache;
        init();
    }

    public GoSearchable(GoSearchable searchable) {
        super(searchable);
        scoreCache_ = searchable.scoreCache_;
        init();
    }

    public GoSearchable copy() {
        return new GoSearchable(this);
    }

    @Override
    public GoBoard getBoard() {
        return (GoBoard) board_;
    }

    /** don't really want to expose this, but renderer needs it */
    public GroupAnalyzer getGroupAnalyzer(IGoGroup group) {
        return boardEvaluator_.getGroupAnalyzer(group);
    }

    private void init() {
        deadStoneUpdater_ = new DeadStoneUpdater(getBoard());
        boardEvaluator_ = new BoardEvaluator(getBoard());
    }

    @Override
    protected GoProfiler getProfiler() {
        return GoProfiler.getInstance();
    }

    /**
     * Given a move determine whether the game is over.
     * If recordWin is true then the variables for player1/2HasWon can get set.
     * Sometimes, like when we are looking ahead, we do not want to set these.
     * The game is over if we have a resignation move, or the last two moves were passing moves.
     *
     * @param m the move to check
     * @param recordWin if true then the controller state will record wins
     * @return true if the game is over
     */
    @Override
    public final boolean done( TwoPlayerMove m, boolean recordWin ) {

        boolean gameOver = false;

        if (m == null ) {
            gameOver = true;
        }
        else if (m.isResignationMove())  {
            if (recordWin) {
                setWinner(!m.isPlayer1());
            }
            gameOver = true;
        }
        else if (twoPasses(m)) {
            if (recordWin) {
                setWinner(getFinalScore(true) > getFinalScore(false));
            }
            gameOver = true;
        }
        if (!gameOver) {
            // try normal handling
            gameOver = super.done( m, recordWin );
        }

        if (gameOver && recordWin) {
            doFinalBookKeeping();
        }

        return gameOver;
    }

    /**
     * Update final territory and number of dead stones.
     * Include this in calcWorth because we call updateTerritory which is under calcWorth for timing.
     */
    private void doFinalBookKeeping() {

        getProfiler().startCalcWorth();
        boardEvaluator_.updateTerritoryAtEndOfGame();

        //we should not call this twice
        if (getNumDeadStonesOnBoard(true)  > 0 || getNumDeadStonesOnBoard(false) > 0) {
            GameContext.log(0, " Error: should not update habitat and death twice.");
        }
        // now that we are finally at the end of the game,
        // update the habitat and death of all the stones still on the board
        GameContext.log(1,  "about to update habitat and death." );
        deadStoneUpdater_.determineDeadStones();
        getProfiler().stopCalcWorth();
    }

    /**
     *  Statically evaluate the board position.
     *  @return statically evaluated value for the board.
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    @Override
    public int worth( Move lastMove, ParameterArray weights ) {

        if (USE_SCORE_CACHING) {
            return cachedWorth(lastMove, weights);
        } else {
            return boardEvaluator_.worth(lastMove, weights);
        }
    }

    /**
     *  If we have a cached worth value for this board position, then use that instead of recomputing it.
     *  @return statically evaluated value for the board.
     */
    public int cachedWorth( Move lastMove, ParameterArray weights ) {

        // Try turning off all forms of go caching.
        // Why doesn't playing with caching give same results as without?
        HashKey key = getHashKey();
        // uncomment this to do caching.
        //ScoreEntry cachedScore = scoreCache_.get(key);
        ///////// comment this to do debugging
        //if (cachedScore != null) {
        //    return cachedScore.getScore();
        //}

        int worth = boardEvaluator_.worth(lastMove, weights);

        /* if (cachedScore == null) {
            scoreCache_.put(key, new ScoreEntry(key, worth, getBoard().toString(), boardEvaluator_.getWorthInfo()));
        }
        else {
            if (cachedScore.getScore() != worth) {
                StringBuilder bldr = new StringBuilder();
                bldr.append("\ncachedScore ").
                        append(cachedScore).
                        //append("\nfor key=").
                        //append(getHashKey()).
                        append("\ndid not match ").
                        append(worth).append(" for \n").
                        append(getBoard().toString()).
                        append("\ncurrent info: ").
                        append(boardEvaluator_.getWorthInfo()).
                        append("using current key=").
                        append(getHashKey());
                System.out.println(bldr.toString());
                System.out.flush();
            }
        } */
        return worth;
    }

    /**
     * get the number of black (player1=true) or white (player1=false) stones that were captured and removed.
     * @param player1sStones if true, get the captures for player1, else for player2.
     * @return num captures of the specified color
     */
    public int getNumCaptures( boolean player1sStones )  {
        return getBoard().getNumCaptures(player1sStones);
    }

    /**
     * @param m the move to play.
     */
    @Override
    public void makeInternalMove( TwoPlayerMove m ) {

        super.makeInternalMove(m);
        updateHashIfCaptures((GoMove) m);
    }

    /**
     * takes back the most recent move.
     * @param m  move to undo
     */
    @Override
    public void undoInternalMove( TwoPlayerMove m ) {

        super.undoInternalMove(m);
        updateHashIfCaptures((GoMove) m);
    }

    /**
     * Whether we are removing captures from the board or adding them back, the operation is the same: XOR.
     */
    private void updateHashIfCaptures(GoMove goMove)  {

        if (goMove.getNumCaptures() > 0)  {
            for (BoardPosition pos : goMove.getCaptures()) {
                hash.applyMove(pos.getLocation(), getBoard().getStateIndex(pos));
            }
            // this is needed to disambiguate ko's.
            if (goMove.isKo(getBoard())) {
                hash.applyMoveNumber(getNumMoves() + goMove.getNumCaptures());
            }
        }
    }

    /**
     * @param move last move
     * @return true if last two moves were passing moves.
     */
    private boolean twoPasses(TwoPlayerMove move) {

        List moves = moveList_;
        if ( move.isPassingMove() && moves.size() > 2 ) {
            GoMove secondToLast = (GoMove) moves.get( moves.size() - 2 );
            if ( secondToLast.isPassingMove() ) {
                GameContext.log( 0, "Done: The last 2 moves were passes :" + move + ", " + secondToLast );
                return true;
            }
        }
        return false;
    }

    /**
     *  @param player1 if true, set player1 as the winner, else player2.
     */
    private void setWinner(boolean player1) {
        if (player1) {
            players_.getPlayer1().setWon(true);
        }
        else {
            players_.getPlayer2().setWon(true);
        }
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
        Move m = moveList_.getLastMove();
        if ( m == null )
            return 0;

        return boardEvaluator_.getTerritoryEstimate(forPlayer1, false);
    }

    /**
     * Call this at the end of the game when we need to try to get an accurate score.
     * @param forPlayer1  true if player one (black)
     * @return the actual score (each empty space counts as one)
     */
    public int getFinalTerritory(boolean forPlayer1) {
        return boardEvaluator_.getTerritoryEstimate(forPlayer1, true);
    }

    /**
     * Only valid after final bookkeeping has been done at the end of the game.
     * @param forPlayer1  player to get dead stones for.
     * @return number of dead stones of specified players color.
     */
    public int getNumDeadStonesOnBoard(boolean forPlayer1)  {
        return deadStoneUpdater_.getNumDeadStonesOnBoard(forPlayer1);
    }

    /**
     * @param player1 if true, then the score for player one is returned else player2's score is returned
     * @return the score (larger is better regardless of player)
     */
    public double getFinalScore(boolean player1) {
        int numDead = getNumDeadStonesOnBoard(player1);
        int totalCaptures = numDead + getNumCaptures(!player1);
        int p1Territory = getFinalTerritory(player1);

        String side = (player1? "black":"white");
        GameContext.log(1, "----");
        GameContext.log(1, "final score for "+ side);
        GameContext.log(2, "getNumCaptures(" + side + ")=" + getNumCaptures(player1));
        GameContext.log(2, "num dead " + side + " stones on board: "+ numDead);
        GameContext.log(2, "getTerritory(" + side + ")="+p1Territory);
        GameContext.log(0, "terr + totalEnemyCaptures="+ (p1Territory + totalCaptures));
        return p1Territory + totalCaptures;
    }

    /**
     * @return any moves that take captures or get out of atari.
     */
    public final MoveList generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights) {

        MoveList moves = generateMoves(lastMove, weights);
        GoMove lastMovePlayed = (GoMove) lastMove;

        // just keep the moves that take captures
        Iterator<Move> it = moves.iterator();
        while ( it.hasNext() ) {
            GoMove move = (GoMove) it.next();

            // urgent if we capture or atari other stones.
            boolean isUrgent = move.getNumCaptures() > 0 || putsGroupInAtari(lastMovePlayed);
            if (isUrgent) {
                move.setUrgent(true);
            }
            else {
                it.remove();
            }
        }
        return moves;
    }

    /**
     * Determine if the last move caused atari on another group (without putting ourselves in atari).
     * @param lastMovePlayed last position just played.
     * @return true if the lastMovePlayed puts the lastPositions string in atari.
     */
    private boolean putsGroupInAtari(GoMove lastMovePlayed) {
        GoBoardPosition lastPos = (GoBoardPosition) getBoard().getPosition(lastMovePlayed.getToLocation());
        return (lastMovePlayed.numStonesAtaried(getBoard()) >= CRITICAL_GROUP_SIZE
                && lastPos.getString().getNumLiberties(getBoard()) > 1);
    }

    /**
     * True if the specified move caused CRITICAL_GROUP_SIZE or more opponent pieces to become jeopardized
     * For go, if the specified move caused a sufficiently large group of stones to become in atari, then we return true.
     *
     * @return true if the last move created a big change in the score
     */
    @Override
    public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights) {

        return (( (GoMove)lastMove ).numStonesAtaried(getBoard()) >= CRITICAL_GROUP_SIZE);
    }

    /**
     * generate all good next moves (statically evaluated)
     */
    public final MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        GoMoveGenerator generator = new GoMoveGenerator(this);
        return generator.generateEvaluatedMoves(lastMove, weights);
    }
}