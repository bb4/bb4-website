package com.becker.game.twoplayer.blockade;

import com.becker.game.common.*;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.board.GamePiece;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.optimization.parameter.ParameterArray;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * For searching the blockade tree.
 * Could extract moveGenerator and moveEvalutator.
 *
 *
 * @author Barry Becker
 */
public class BlockadeSearchable extends TwoPlayerSearchable {

    /**
     *  Constructor.
     */
    public BlockadeSearchable(TwoPlayerBoard board,  PlayerList players, SearchOptions options) {
        super(board, players, options);
    }

    public BlockadeSearchable(BlockadeSearchable searchable) {
        super(searchable);
    }

    public BlockadeSearchable copy() {
        return new BlockadeSearchable(this);
    }

    @Override
    public BlockadeBoard getBoard() {
        return (BlockadeBoard)board_;
    }
 
    /**
     * The primary way of computing the score for Blockade is to
     * weight the difference of the 2 shortest minimum paths plus the
     * weighted difference of the 2 furthest minimum paths.
     * An alternative method might be to weight the sum of the our shortest paths
     * and difference it with the weighted sum of the opponent shortest paths.
     * The minimum path for a piece is the distance to its closest enemy home position.
     *
     * @return the value of the current board position
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    @Override
    public int worth( Move lastMove, ParameterArray weights ) {
        getProfiler().startCalcWorth();
        BlockadeMove m = (BlockadeMove)lastMove;
        // if its a winning move then return the winning value
        boolean player1Moved = m.isPlayer1();

        if (checkForWin(player1Moved,
                                 player1Moved? getBoard().getPlayer2Homes() : getBoard().getPlayer1Homes())) {
            GameContext.log(1, "FOUND WIN!!!");
            return player1Moved ? WINNING_VALUE : -WINNING_VALUE;
        }

        PlayerPathLengths pathLengths = getBoard().findPlayerPathLengths(m);
        int worth = pathLengths.determineWorth(WINNING_VALUE, weights);
        getProfiler().stopCalcWorth();
        return worth;
    }


    /**
      * If a players pawn lands on an opponent home, the game is over.
      * @param player1 the player to check to see fi won.
      * @param homes the array of home bases.
      * @return true if player has reached an opponent home. (for player1 or player2 depending on boolean player1 value)
      */
    private static boolean checkForWin(boolean player1, BoardPosition[] homes) {
        for (BoardPosition home : homes) {
            GamePiece p = home.getPiece();
            if (p != null && p.isOwnedByPlayer1() == player1)
                return true;
        }
        return false;
    }

    /**
     * Generate all possible legal and reasonable next moves.
     * In com.becker.game.twoplayer.blockade, there are a huge amount of possible next moves because of all the possible
     * wall placements. So restrict wall placements to those that hinder the enemy while not hindering you.
     * lastMove may be null if there was no last move.
     */
    public MoveList generateMoves( TwoPlayerMove lastMove, ParameterArray weights)  {
        getProfiler().startGenerateMoves();

        MoveGenerator generator = new MoveGenerator(weights, getBoard());
        MoveList moveList  = generator.generateMoves(lastMove);

        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();

        MoveList bestMoves =
            bestMoveFinder_.getBestMoves( player1, moveList);

        getProfiler().stopGenerateMoves();
        return bestMoves;
    }

    /**
     * given a move, determine whether the game is over.
     * If recordWin is true, then the variables for player1/2HasWon can get set.
     * Sometimes, like when we are looking ahead we do not want to set these.
     * @param lastMove the move to check. If null then return true.
     * @param recordWin if true then the controller state will record wins
     */
    @Override
    public boolean done( TwoPlayerMove lastMove, boolean recordWin ) {

        if (getNumMoves() > 0 && lastMove == null) {
            GameContext.log(0, "Game is over because there are no more moves.");
            return true;
        }
        BlockadeBoard board = (BlockadeBoard)board_;

        boolean p1Won = checkForWin(true, board.getPlayer2Homes());
        boolean p2Won = checkForWin(false, board.getPlayer1Homes());
        if (p1Won)
            players_.getPlayer1().setWon(true);
        else if (p2Won)
            players_.getPlayer2().setWon(true);
        return (p1Won || p2Won);
    }


    /**
     * @@ quiescent search not yet implemented for Blockade
     * Probably we could return moves that result in a drastic change in value.
     *
     * @return list of urgent moves
     */
    public MoveList generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights) {
        return new MoveList();
    }
}
