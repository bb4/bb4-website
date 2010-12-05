package com.becker.game.twoplayer.common.search;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.optimization.parameter.ParameterArray;


/**
 * Stub implementation of Searchable to help test the search strategy classes without needing
 * a specific game implementation.
 *
 * @author Barry Becker
 */
public class SearchableStub implements Searchable {

    SearchOptions searchOptions_;
    MoveList moves_;

    public SearchableStub(SearchOptions options) {
        searchOptions_ = options;
        moves_ = new MoveList();
    }

    public SearchableStub(SearchableStub stub) {
        this(stub.getSearchOptions());
        moves_ = new MoveList(stub.getMoveList());
    }

    public Searchable copy() {
        return new SearchableStub(this);
    }
    
    /** 
     * {@inheritDoc}
     */
    public SearchOptions getSearchOptions() {
        return searchOptions_;
    }

    /**
     * {@inheritDoc}
     */
    public void makeInternalMove( TwoPlayerMove m )  {
        moves_.add(m);
    }

    /**
     * {@inheritDoc}
     */
    public void undoInternalMove( TwoPlayerMove m ) {
        moves_.removeLast();
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

    public MoveList getMoveList() {
        return moves_;
    }

    public int getNumMoves() {
        return moves_.size();
    }


    /**
     * {@inheritDoc}
     * @@ we should remove the final argument because it always seems to be true
     */
    public MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective ) {
        return new MoveList(((TwoPlayerMoveStub) lastMove).getChildren());
    }

    /**
     * {@inheritDoc}
     * @@ we should remove the final argument because it always seems to be true
     */
    public MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective ) {
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
    public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective ) {
        return ((TwoPlayerMoveStub)lastMove).causedUrgency();
    }

    /**
     *{@inheritDoc}
     */
    public Long getHashKey() {
        long key = 0;
        for (Move m : moves_) {
            key += m.hashCode();
        }
        return key;
    }
}
