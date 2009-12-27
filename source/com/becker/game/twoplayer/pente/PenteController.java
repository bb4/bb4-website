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

        /*
         * generate all possible next moves.
         */
        public List<? extends TwoPlayerMove> generateMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            List<TwoPlayerMove> moveList = new LinkedList<TwoPlayerMove>();

            PenteBoard pb = (PenteBoard) board_;
            pb.determineCandidateMoves();

            boolean player1 = (lastMove == null) || !(lastMove.isPlayer1());

            int ncols = board_.getNumCols();
            int nrows = board_.getNumRows();

            for (int i = 1; i <= ncols; i++ )
                for (int j = 1; j <= nrows; j++ )
                    if ( pb.isCandidateMove( j, i ) ) {
                        assert lastMove != null;
                        TwoPlayerMove m = TwoPlayerMove.createMove( j, i, lastMove.getValue(), new GamePiece(player1));
                        pb.makeMove( m );
                        m.setValue(worth( m, weights, player1sPerspective ));
                        // now revert the board
                        pb.undoMove();
                        moveList.add( m );
                    }

            return getBestMoves( player1, moveList, player1sPerspective );
        }

        /**
         * @return the moves that result in a certain win.
         */
        public List<? extends TwoPlayerMove> generateUrgentMoves(
                TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            List<? extends TwoPlayerMove> moves = generateMoves( lastMove, weights, player1sPerspective );

            // now keep only those that result in a win.
            Iterator<? extends TwoPlayerMove> it = moves.iterator();
            while ( it.hasNext() ) {
                TwoPlayerMove move = it.next();
                if ( Math.abs( move.getInheritedValue() ) < WINNING_VALUE )
                    it.remove();
                else
                    move.setUrgent(true);
            }
            if ( moves.size() > 0 )
                GameContext.log( 3, "pente controller: the urgent moves are :" + moves );
            return moves;
        }

        /**
         * @return true if the last move created a big change in the score
         */
        @Override
        public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective  )
        {
            double newValue = worth( lastMove, weights, player1sPerspective );
            double diff = newValue - lastMove.getValue();

            // consider the delta big if >= w. Where w is the value of a near win.
            return (diff > getJeopardyWeight());
        }

        int getJeopardyWeight()  {
            return PenteWeights.JEOPARDY_WEIGHT;
        }
    }

}
