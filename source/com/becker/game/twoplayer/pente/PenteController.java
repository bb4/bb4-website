package com.becker.game.twoplayer.pente;

import com.becker.common.Location;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.pente.analysis.MoveEvaluator;
import com.becker.optimization.parameter.ParameterArray;
import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

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

    private Random RANDOM = new Random(0);

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
    public Searchable createSearchable() {
         return new PenteSearchable();
    }


    protected class PenteSearchable extends TwoPlayerSearchable {

        MoveGenerator generator = new MoveGenerator(PenteController.this);

        /**
         * generate all possible next moves.
         */
        public MoveList generateMoves(TwoPlayerMove lastMove,
                                      ParameterArray weights, boolean player1sPerspective ) {
            return generator.generateMoves(lastMove, weights, player1sPerspective);
        }

        /**
         * Consider both our moves and opponent moves that result in wins.
         * Opponent moves that result in a win should be blocked.
         * @return Set of moves the moves that result in a certain win or a certain loss.
         */
        public MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective)
        {
            return generator.generateUrgentMoves(lastMove, weights, player1sPerspective);
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