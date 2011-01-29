package com.becker.game.twoplayer.pente;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.pente.analysis.MoveEvaluator;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Defines everything the computer needs to know to play Pente.
 *
 * @author Barry Becker
*/
public class PenteSearchable extends TwoPlayerSearchable {

    protected MoveEvaluator moveEvaluator_;

    PenteMoveGenerator generator;


    public PenteSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        super(board, players, options);
        init();
    }

    public PenteSearchable(PenteSearchable searchable) {
        super(searchable);
        init();
    }

    public PenteSearchable copy() {
        return new PenteSearchable(this);
    }

    @Override
    public PenteBoard getBoard() {
        return (PenteBoard) board_;
    }

    private void init() {
        generator = new PenteMoveGenerator(this);
        moveEvaluator_ = new MoveEvaluator(board_, createPatterns());
    }

    protected Patterns createPatterns() {
        return new PentePatterns();
    }
    
    /**
     * Statically evaluate the board position.
     * @return the lastMoves value modified by the value add of the new move.
     *  a large positive value means that the move is good from the specified players viewpoint
     */
    @Override
    public int worth( Move lastMove, ParameterArray weights ) {
        return moveEvaluator_.worth(lastMove, weights);
    }

    /**
     * generate all possible next moves.
     */
    public MoveList generateMoves(TwoPlayerMove lastMove,
                                  ParameterArray weights) {
        return generator.generateMoves(lastMove, weights);
    }

    /**
     * Consider both our moves and opponent moves that result in wins.
     * Opponent moves that result in a win should be blocked.
     * @return Set of moves the moves that result in a certain win or a certain loss.
     */
    public MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        return generator.generateUrgentMoves(lastMove, weights);
    }

    /**
     * Consider the delta big if >= w. Where w is the value of a near win.
     * @return true if the last move created a big change in the score
     */
    @Override
    public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights ) {
        if (lastMove == null)
            return false;
        double newValue = worth( lastMove, weights);
        double diff = newValue - lastMove.getValue();
        return (diff > getJeopardyWeight());
    }

    protected int getJeopardyWeight()  {
        return PenteWeights.JEOPARDY_WEIGHT;
    }
}