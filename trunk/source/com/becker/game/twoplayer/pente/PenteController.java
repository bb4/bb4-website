package com.becker.game.twoplayer.pente;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.pente.analysis.MoveEvaluator;
import com.becker.optimization.parameter.ParameterArray;

import java.util.*;

/**
 * Defines everything the computer needs to know to play Pente.
 *
 * @author Barry Becker
*/
public class PenteController extends TwoPlayerController
{
    private static final int DEFAULT_NUM_ROWS = 20;

    protected MoveEvaluator moveEvaluator_;

    /**
     *  Constructor
     */
    public PenteController()
    { 
        board_ = new PenteBoard( DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS );
        initializeData();
    }

    /**
     *  Construct the Pente game controller given an initial board size
     */
    public PenteController(int nrows, int ncols )
    {
        board_ = new PenteBoard( nrows, ncols );
        initializeData();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new PenteOptions();
    }

    /**
     *  this gets the pente specific patterns and weights
     */
    @Override
    protected void initializeData()
    {
        weights_ = new PenteWeights();
        moveEvaluator_ = new MoveEvaluator((TwoPlayerBoard)board_, new PentePatterns());
    }

    /**
     * the first move of the game (made by the computer)
     */
    public void computerMovesFirst()
    {
        int delta = getWinRunLength() - 1;
        int c = (int) (RANDOM.nextFloat() * (board_.getNumCols() - 2 * delta) + delta + 1);
        int r = (int) (RANDOM.nextFloat() * (board_.getNumRows() - 2 * delta) + delta + 1);
        TwoPlayerMove m = TwoPlayerMove.createMove( r, c, 0, new GamePiece(true) );
        makeMove( m );
    }

    protected int getWinRunLength() {
        return PentePatterns.WIN_RUN_LENGTH;
    }


    /**
     *  Statically evaluate the board position.
     *  @return the lastMoves value modified by the value add of the new move.
     *   a large positive value means that the move is good from the specified players viewpoint
     */
    @Override
    protected int worth( Move lastMove, ParameterArray weights )
    {
        return moveEvaluator_.worth(lastMove, weights);
    }

    @Override
    public Searchable getSearchable() {
         return new PenteSearchable();
     }


    protected class PenteSearchable extends TwoPlayerSearchable {

        /**
         * generate all possible next moves.
         */
        public List<? extends TwoPlayerMove> generateMoves( TwoPlayerMove lastMove,
                                                            ParameterArray weights, boolean player1sPerspective )
        {
            List<TwoPlayerMove> moveList = new LinkedList<TwoPlayerMove>();

            PenteBoard pb = (PenteBoard) board_;
            pb.determineCandidateMoves();

            boolean player1 = (lastMove == null) || !(lastMove.isPlayer1());

            int ncols = board_.getNumCols();
            int nrows = board_.getNumRows();

            for (int i = 1; i <= ncols; i++ ) {
                for (int j = 1; j <= nrows; j++ ) {
                    if ( pb.isCandidateMove( j, i )) {
                        TwoPlayerMove m;
                        if (lastMove == null)
                           m = TwoPlayerMove.createMove( j, i, 0, new GamePiece(player1));
                        else
                           m = TwoPlayerMove.createMove( j, i, lastMove.getValue(), new GamePiece(player1));
                        pb.makeMove( m );
                        m.setValue(worth( m, weights, player1sPerspective ));
                        // now revert the board
                        pb.undoMove();
                        moveList.add( m );
                    }
                }
            }
            return getBestMoves( player1, moveList, player1sPerspective );
        }

        /**
         * Consider both our moves and opponent moves that result in wins.
         * Opponent moves that result in a win should be blocked.
         * @return Set of moves the moves that result in a certain win or a certain loss.
         */
        public List<? extends TwoPlayerMove> generateUrgentMoves(
                TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            // no urgent moves at start of game.
            if (lastMove == null)  {
                return new LinkedList<TwoPlayerMove>();
            }
            List<TwoPlayerMove> allMoves = findMovesForBothPlayers(lastMove, weights, player1sPerspective);

            // now keep only those that result in a win or loss.
            Iterator<TwoPlayerMove> it = allMoves.iterator();
            List<TwoPlayerMove>  urgentMoves = new LinkedList<TwoPlayerMove>();
            while ( it.hasNext() ) {
                TwoPlayerMove move = it.next();
                // if its not a winning move or we already have it, then skip
                if ( Math.abs(move.getValue()) >= WINNING_VALUE  && !contains(move, urgentMoves) ) {
                    move.setUrgent(true);
                    urgentMoves.add(move);
                }
            }
            return urgentMoves;
        }

        /**
         * Consider both our moves and and opponent moves.
         * @return Set of all next moves.
         */
        private List<TwoPlayerMove> findMovesForBothPlayers(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective) {
            List<TwoPlayerMove> allMoves = new ArrayList<TwoPlayerMove>();
            List<? extends TwoPlayerMove> moves = generateMoves( lastMove, weights, player1sPerspective );
            allMoves.addAll(moves);

            TwoPlayerMove oppLastMove = lastMove.copy();
            oppLastMove.setPlayer1(!lastMove.isPlayer1());
            List<? extends TwoPlayerMove> opponentMoves =
                    generateMoves( oppLastMove, weights, !player1sPerspective );
            for (TwoPlayerMove move : opponentMoves){
                move.setPlayer1(!lastMove.isPlayer1());
                move.setPiece(new GamePiece(!lastMove.isPlayer1()));
                allMoves.add(move);
            }

            return allMoves;
        }


        private boolean contains(TwoPlayerMove move, List<TwoPlayerMove> moves)
        {
            for (TwoPlayerMove m : moves) {
                if (m.getToLocation().equals(move.getToLocation())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Consider the delta big if >= w. Where w is the value of a near win.
         * @return true if the last move created a big change in the score
         */
        @Override
        public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective  )
        {
            if (lastMove == null)
                return false;
            double newValue = worth( lastMove, weights, player1sPerspective );
            double diff = newValue - lastMove.getValue();
            return (diff > getJeopardyWeight());
        }

        protected int getJeopardyWeight()  {
            return PenteWeights.JEOPARDY_WEIGHT;
        }
    }
}
