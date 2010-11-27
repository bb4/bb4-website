package com.becker.game.twoplayer.go;

import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerSearchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.PositionalScore;
import com.becker.game.twoplayer.go.board.analysis.PositionalScoreAnalyzer;
import com.becker.game.twoplayer.go.board.update.DeadStoneUpdater;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Iterator;
import java.util.List;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;
import static com.becker.game.twoplayer.go.GoControllerConstants.WIN_THRESHOLD;

/**
 * For searching go games search space.
 *
 * @author Barry Becker
 */

public class GoSearchable extends TwoPlayerSearchable {

    private static final int CRITICAL_GROUP_SIZE = 4;

    /** a lookup table of scores to attribute to the board positions when calculating the worth */
    private PositionalScoreAnalyzer positionalScorer_;

    /** keeps track of dead stones.  */
    private DeadStoneUpdater deadStoneUpdater_;


    public GoSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        super(board, players, options);
        init();
    }

    public GoSearchable(GoSearchable searchable) {
        super(searchable);
        init();
    }

    public GoSearchable copy() {
        return new GoSearchable(this);
    }

    private void init() {
        deadStoneUpdater_ = new DeadStoneUpdater((GoBoard)board_);
        positionalScorer_ = new PositionalScoreAnalyzer((GoBoard)board_);
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
            //we should not call this twice
            if (getNumDeadStonesOnBoard(true)  > 0 || getNumDeadStonesOnBoard(false) > 0) {
                GameContext.log(0, " Error: should not update life and death twice.");
            }
            // now that we are finally at the end of the game,
            // update the life and death of all the stones still on the board
            GameContext.log(1,  "about to update life and death." );
            deadStoneUpdater_.determineDeadStones();
        }

        return gameOver;
    }


    /**
     *  Statically evaluate the board position.
     *
     *  @return statically evaluated value of the board.
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    @Override
    public int worth( Move lastMove, ParameterArray weights ) {
        double worth = calculateWorth(lastMove, weights);

        GameContext.log(3,"GoController.worth: worth="+worth);
        if ( worth < -WIN_THRESHOLD ) {
            // then the margin is too great the losing player should resign
            return -WINNING_VALUE;
        }
        else if ( worth > WIN_THRESHOLD ) {
            // then the margin is too great the losing player should resign
            return WINNING_VALUE;
        }
        return (int)worth;
    }


    /**
     * get the number of black (player1=true) or white (player1=false) stones that were captured and removed.
     * @param player1sStones if true, get the captures for player1, else for player2.
     * @return num captures
     */
    public int getNumCaptures( boolean player1sStones )  {
        return ((GoBoard) board_).getNumCaptures(player1sStones);
    }


    /**
     *  Statically evaluate the board position.
     *  The most naive thing we could do here is to simply return the sum of the captures
     *  for player1 - sum of the captures for player2.
     *  However for go, since search is not likely to be that useful given
     *  the huge branch factor, we need to heavily rely on a sophisticated evaluation.
     *    So what we do is have every space on the board have a score representing
     *  how strongly it is controlled by player1 (black).  If the score is 1.00, then that
     *  position is inside or part of an unconditionally alive group owned by player1 (black)
     *  or it is inside a dead white group.
     *  If the score is -1.00 then its player 2's(white) unconditionally alive group
     *  or black's dead group. A blank dame might have a score
     *  of 0. A white stone might have a positive score if its part of a white group
     *  which is considered mostly dead.
     *
     *  @return statically evaluated value of the board.
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    private double calculateWorth(Move lastMove, ParameterArray weights) {

        double worth;
        GoBoard board = (GoBoard)board_;
        // adjust for board size - so worth will be comparable regardless of board size.
        double scaleFactor = 361.0 / Math.pow(board.getNumRows(), 2);
        double gameStageBoost = getGameStageBoost();

        PositionalScore totalScore = new PositionalScore();
        for (int row = 1; row <= board.getNumRows(); row++ ) {
            for (int col = 1; col <= board.getNumCols(); col++ ) {

                totalScore.incrementBy(positionalScorer_.determineScoreForPosition(row, col, gameStageBoost, weights));
            }
        }

        double territoryDelta = board.getTerritoryDelta();
        double captureScore = getCaptureScore(weights);
        worth = scaleFactor * (totalScore.getPositionScore() + captureScore + territoryDelta);

        if (GameContext.getDebugMode() > 0)  {
            String desc = totalScore.getDescription(worth, captureScore, territoryDelta, scaleFactor);
            ((TwoPlayerMove) lastMove).setScoreDescription(desc);
        }
        return worth;
    }

    /**
     * Opening = 1.99 - 1.5;   middle = 1.5 - 1.01;    end = 1.0
     * @return a weight for the positional score based on how far along into the game we are.
     */
    private double getGameStageBoost() {
        float n = 2.0f * board_.getNumRows();
        return 0.5 + 2.0 * Math.max((n - (float)moveList_.getNumMoves())/n, 0.0);
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
     * @return score attributed to captured stones.
     */
    private double getCaptureScore(ParameterArray weights) {
        double captureWt = weights.get(GoWeights.CAPTURE_WEIGHT_INDEX).getValue();
        return captureWt * (getNumCaptures( false ) - getNumCaptures( true ));
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

        return ((GoBoard)board_).getTerritoryEstimate(forPlayer1, false);
    }


    /**
     * @param player1 if true, then the score for player one is returned else player2's score is returned
     * @return the score (larger is better regardless of player)
     */
    public double getFinalScore(boolean player1) {
        int numDead = getNumDeadStonesOnBoard(player1);
        int totalCaptures = numDead + getNumCaptures(player1);
        int p1Territory = getTerritory(player1);

        String side = (player1? "black":"white");
        GameContext.log(1, "----");
        GameContext.log(1, "final score for "+ side);
        GameContext.log(2, "getNumCaptures(" + side + ")=" + getNumCaptures(player1));
        GameContext.log(2, "num dead " + side + " stones on board: "+ numDead);
        GameContext.log(2, "getTerritory(" + side + ")="+p1Territory);
        GameContext.log(0, "terr - totalCaptures="+ (p1Territory - totalCaptures));
        return p1Territory - totalCaptures;
    }

    public int getNumDeadStonesOnBoard(boolean forPlayer1)  {
        return deadStoneUpdater_.getNumDeadStonesOnBoard(forPlayer1);
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
     * @return any moves that take captures or get out of atari.
     */
    public final MoveList generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights,
                                               boolean player1sPerspective ) {
        MoveList moves = generateMoves(lastMove, weights, player1sPerspective );
        GoBoard gb = (GoBoard) board_;
        GoMove lastMovePlayed = (GoMove) lastMove;

        // just keep the moves that take captures
        Iterator<Move> it = moves.iterator();
        while ( it.hasNext() ) {
            GoMove move = (GoMove) it.next();
            if ( move.getNumCaptures() == 0 || lastMovePlayed.causesAtari(gb) > 0 ) {
                it.remove();
            }
            else {
                move.setUrgent(true);
            }
        }
        return moves;
    }

    /**
     * returns true if the specified move caused one or more opponent pieces to become jeopardized
     * For go, if the specified move caused a group to become in atari, then we return true.
     *
     * @return true if the last move created a big change in the score
     */
    @Override
    public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective ) {
        GoBoard gb = (GoBoard) board_;
        return (( (GoMove)lastMove ).causesAtari(gb) > CRITICAL_GROUP_SIZE);
    }


    /**
     * generate all possible next moves
     */
    public final MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights,
                                        boolean player1sPerspective ) {
        GoMoveGenerator generator = new GoMoveGenerator(this);
        return generator.generateMoves(lastMove, weights, player1sPerspective);
    }
}