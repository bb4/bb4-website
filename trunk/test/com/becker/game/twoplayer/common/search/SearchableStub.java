/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common.search;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.AbstractSearchable;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.transposition.HashKey;
import com.becker.optimization.parameter.ParameterArray;


/**
 * Stub implementation of Searchable to help test the search strategy classes without needing
 * a specific game implementation.
 *
 * @author Barry Becker
 */
public class SearchableStub extends AbstractSearchable {

    protected SearchStrategy strategy_;

    public SearchableStub(SearchOptions options) {
        super(new MoveList(), options);
    }

    public SearchableStub(SearchableStub stub) {
        this(stub.getSearchOptions());
        moveList_ = new MoveList(stub.getMoveList());
    }

    public Searchable copy() {
        return new SearchableStub(this);
    }
    

    /**
     * {@inheritDoc}
     */
    public void makeInternalMove( TwoPlayerMove m )  {
        moveList_.add(m);
    }

    /**
     * {@inheritDoc}
     */
    public void undoInternalMove( TwoPlayerMove m ) {
        moveList_.removeLast();
    }

    /**
     * {@inheritDoc}
     */
    public boolean done( TwoPlayerMove m, boolean recordWin ) {
        return m.getInheritedValue() >= SearchStrategy.WINNING_VALUE;
    }

    public int worth(Move lastMove, ParameterArray weights) {
        return lastMove.getValue();
    }

    public int worth(Move lastMove, ParameterArray weights, boolean player1sPerspective) {
        return lastMove.getValue();
    }

    public TwoPlayerBoard getBoard() {
        return null;
    }


    /**
     * {@inheritDoc}
     * @@ we should remove the final argument because it always seems to be true
     */
    public MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        return new MoveList(((TwoPlayerMoveStub) lastMove).getChildren());
    }

    /**
     * {@inheritDoc}
     * @@ we should remove the final argument because it always seems to be true
     */
    public MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        MoveList urgentMoves = new MoveList();
        for (Move m : ((TwoPlayerMoveStub) lastMove).getChildren()) {
            TwoPlayerMove move = (TwoPlayerMove)m;
            if (move.isUrgent())  {
                urgentMoves.add(move);
            }
        }
        return urgentMoves;
    }

    /**
     * {@inheritDoc}
     */
    public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights) {
        return ((TwoPlayerMoveStub)lastMove).causedUrgency();
    }

    /**
     *{@inheritDoc}
     */
    public HashKey getHashKey() {
        HashKey key = new HashKey();
        for (Move m : moveList_) {
            //key += m.hashCode();
            key.applyMove(((TwoPlayerMove)m).getToLocation(), m.hashCode());
        }
        return key;
    }
}
