package com.becker.game.twoplayer.common.search;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
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
        moves_.pop();
    }

    /**
     * {@inheritDoc}
     */
    public boolean done( TwoPlayerMove m, boolean recordWin ) {
        return m.getInheritedValue() >= SearchStrategy.WINNING_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    public MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective ) {
        return new MoveList(((TwoPlayerMoveStub) lastMove).getChildren());
    }

    /**
     * {@inheritDoc}
     * sould return only the children that are urgent moves
     */
    public MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective ) {
        return new MoveList(((TwoPlayerMoveStub) lastMove).getChildren());
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
