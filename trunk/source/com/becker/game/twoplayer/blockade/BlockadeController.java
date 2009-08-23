package com.becker.game.twoplayer.blockade;

import com.becker.game.common.*;
import com.becker.game.twoplayer.blockade.persistence.BlockadeGameExporter;
import com.becker.game.twoplayer.blockade.persistence.BlockadeGameImporter;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.sound.MusicMaker;

import java.util.*;

/**
 * Defines for the computer how it should play blockade.
 *
 * Todo items
 *   - Restrict to N vertical and N horizontal walls, or allow not to place a wall.
 *     (perhaps only allow wall placements up to (xdim*ydim)/4 walls for each player)
 *   - computer moves only one space instead of two. Computer not winning at end whne one space more required.
 *   - The winner should win as soon as he lands on an opponent base and not have to wait to place the wall.
 *
 * @author Barry Becker
 */
public class BlockadeController extends TwoPlayerController
{

    public static final int DEFAULT_LOOKAHEAD = 1;  // shoudl be 2 or 3

    /** For any given ply never consider more that BEST_PERCENTAGE of the top moves. */
    private static final int BEST_PERCENTAGE = 100;

    /** the default Blockade board is 14 by 11 */
    private static final int NUM_ROWS = 14;
    private static final int NUM_COLS = 11;


    /**
     *  Construct the Blockade game controller.
     */
    public BlockadeController()
    {
        initializeData();
        board_ = new BlockadeBoard(NUM_ROWS, NUM_COLS);
    }

    /**
     * this gets the Blockade specific weights.
     */
    protected void initializeData()
    {
        weights_ = new BlockadeWeights();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions(DEFAULT_LOOKAHEAD, BEST_PERCENTAGE, MusicMaker.APPLAUSE);
    }

    /**
     * The computer makes the first move in the game
     */
    public void computerMovesFirst() {
        // determine the possible moves and choose one at random.
        List moveList = getSearchable().generateMoves( null, weights_.getPlayer1Weights(), true );

        makeMove( getRandomMove(moveList) );
    }

    /**
     * Measure is determined by the score (amount of territory)
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin()
    {
        if (!getPlayer1().hasWon() && !getPlayer2().hasWon()) {
             return 0;
        }
        return worth(board_.getLastMove(), weights_.getDefaultWeights());
    }


    /**
     * save the current state of the blockade game to a file in SGF (4) format (standard game format).
     *This should some day be xml (xgf)
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile( String fileName, AssertionError ae )
    {
        BlockadeGameExporter exporter = new BlockadeGameExporter(this);
        exporter.saveToFile(fileName, ae);
    }


    @Override
    public void restoreFromFile( String fileName ) {
        BlockadeGameImporter importer = new BlockadeGameImporter(this);
        importer.restoreFromFile(fileName);
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
    protected int worth( Move lastMove, ParameterArray weights )
    {
        board_.getProfiler().startCalcWorth();
        BlockadeBoard board = (BlockadeBoard)board_;
        BlockadeMove m = (BlockadeMove)lastMove;
        // if its a winning move then return the winning value
        boolean player1Moved = m.isPlayer1();

        if (checkForWin(player1Moved,
                                 player1Moved? board.getPlayer2Homes() : board.getPlayer1Homes())) {
            GameContext.log(1, "FOUND WIN!!!");
            return player1Moved ? WINNING_VALUE : -WINNING_VALUE;
        }

        PlayerPathLengths pathLengths = board.findPlayerPathLengths(m);
        int worth = pathLengths.determineWorth(WINNING_VALUE, weights);
        board_.getProfiler().stopCalcWorth();
        return worth;
    }


    /**
      * If a players pawn lands on an opponent home, the game is over.
      * @param player1 the player to check to see fi won.
      * @param homes the array of home bases.
      * @return true if player has reached an opponent home. (for player1 or player2 depending on boolean player1 value)
      */
    protected static boolean checkForWin(boolean player1, BoardPosition[] homes) {
        for (int i=0; i< homes.length; i++) {
            GamePiece p = homes[i].getPiece();
            if (p != null && p.isOwnedByPlayer1() == player1)
                return true;
        }
        return false;
    }

    /**
     * @param position
     * @return a possible list of moves based on position passed in.
     */
    public List<BlockadeMove> getPossibleMoveList(BoardPosition position)
    {
        return ((BlockadeBoard)board_).getPossibleMoveList(position, !position.getPiece().isOwnedByPlayer1());
    }


    public Searchable getSearchable() {
        return new BlockadeSearchable();
    }


    public class BlockadeSearchable extends TwoPlayerSearchable {

        /**
         * Generate all possible legal and reasonable next moves.
         * In blockade, there are a huge amount of possible next moves because of all the possible
         * wall placements. So restrict wall placements to those that hinder the enemy while not hindering you.
         * lastMove may be null if there was no last move.
         */
        public List<? extends TwoPlayerMove> generateMoves( TwoPlayerMove lastMove, ParameterArray weights,
                                                                                                  boolean player1sPerspective )
        {
            board_.getProfiler().startGenerateMoves();

            MoveGenerator generator = new MoveGenerator(weights, (BlockadeBoard)board_);
            List<BlockadeMove> moveList  = generator.generateMoves(lastMove);

            boolean player1 = (lastMove != null)?  !lastMove.isPlayer1() : true;
            List<? extends TwoPlayerMove> bestMoves = 
                    getBestMoves( player1, moveList, player1sPerspective );

            board_.getProfiler().stopGenerateMoves();
            return bestMoves;
        }

        /**
         * given a move, determine whether the game is over.
         * If recordWin is true, then the variables for player1/2HasWon can get set.
         * Sometimes, like when we are looking ahead we do not want to set these.
         * @param m the move to check. If null then return true.
         * @param recordWin if true then the controller state will record wins
         */
        @Override
        public boolean done( TwoPlayerMove lastMove, boolean recordWin )
        {
            if (getNumMoves() > 0 && lastMove == null) {
                GameContext.log(0, "Game is over because there are no more moves.");
                return true;
            }
            BlockadeBoard board = (BlockadeBoard)board_;
            
            boolean p1Won = checkForWin(true, board.getPlayer2Homes());
            boolean p2Won = checkForWin(false, board.getPlayer1Homes());
            if (p1Won)
                getPlayer1().setWon(true);
            else if (p2Won)
                getPlayer2().setWon(true);
            return (p1Won || p2Won);
        }


        /**
         * @@ quiescent search not yet implemented for Blockade
         * Probably we could return moves that result in a drastic change in value.
         *
         * @param lastMove
         * @param weights
         * @param player1sPerspective
         * @return list of urgent moves
         */
        public List<BlockadeMove> generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            return null;
        }

        /**
         * returns true if the specified move caused one or more opponent pieces to become jeopardized
         */
        public boolean inJeopardy( TwoPlayerMove m )
        {
            return false;
        }
    }
}
