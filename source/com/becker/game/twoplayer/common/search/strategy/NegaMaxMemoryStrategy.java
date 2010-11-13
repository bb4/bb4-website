package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.SearchWindow;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.transposition.Entry;
import com.becker.game.twoplayer.common.search.transposition.TranspositionTable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

/**
 *  This strategy class defines the NegaMax with memory search algorithm.
 *  This version stores the values of moves that have already been searched.
 *  Based on psuedo code from Artificial Intelligence for Games by Millington and Funge.
 *
 *  @author Barry Becker
 */
public final class NegaMaxMemoryStrategy extends NegaMaxStrategy
                                         implements MemorySearchStrategy {
    /** Stores positions that have already been evaluated, so we do not need to repeat work. */
    private TranspositionTable lookupTable;

    /**
     * Constructor.
     */
    public NegaMaxMemoryStrategy( Searchable controller, ParameterArray weights ) {
        super( controller, weights );
        lookupTable = new TranspositionTable();
    }

    public TranspositionTable getTranspositionTable() {
        return lookupTable;
    }

    /**
     * @inheritDoc
     */
    @Override
    public TwoPlayerMove search( TwoPlayerMove lastMove, SearchTreeNode parent ) {
        // need to negate alpha and beta on initial call.
        SearchWindow window = getOptions().getInitialSearchWindow();
        int g = window.getMidPoint();
        return searchInternal( lastMove, lookAhead_, new SearchWindow(g, g), parent );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TwoPlayerMove searchInternal( TwoPlayerMove lastMove,
                                           int depth,
                                           SearchWindow window, SearchTreeNode parent ) {
        Long key = searchable_.getHashKey();
        Entry entry = lookupTable.get(key);
        if (lookupTable.entryExists(entry, lastMove, depth, window)) {
            if (entry.lowerValue > window.alpha) {
                entry.bestMove.setInheritedValue(entry.lowerValue);
                return entry.bestMove;
            }
            else if (entry.upperValue < window.beta) {
                entry.bestMove.setInheritedValue(entry.upperValue);
                return entry.bestMove;
            }
        }

        entry = new Entry(lastMove, depth, new SearchWindow(-SearchStrategy.INFINITY, SearchStrategy.INFINITY));

        boolean done = searchable_.done( lastMove, false);
        if ( depth <= 0 || done ) {
            if (doQuiescentSearch(depth, done, lastMove))  {
                TwoPlayerMove qMove = quiescentSearch(lastMove, depth, window, parent);
                if (qMove != null)  {
                    entry = new Entry(qMove, qMove.getInheritedValue());
                    lookupTable.put(key, entry);
                    return qMove;
                }
            }
            int sign = fromPlayer1sPerspective(lastMove) ? 1 : -1;
            int value = sign * lastMove.getValue();
            lastMove.setInheritedValue(value);
            entry.lowerValue = value;
            entry.upperValue = value;
            lookupTable.put(key, entry);
            return lastMove;
        }

        MoveList list = searchable_.generateMoves(lastMove, weights_, true);

        if (depth == lookAhead_)
            numTopLevelMoves_ = list.size();

        if ( emptyMoveList( list, lastMove) )   return null;

        return findBestMove(lastMove, depth, list, window, parent);
    }


    /**
     * @inheritDoc
     */
    @Override
    protected TwoPlayerMove findBestMove(TwoPlayerMove lastMove, int depth, MoveList list,
                                         SearchWindow window, SearchTreeNode parent) {
        int i = 0;
        int bestInheritedValue = -SearchStrategy.INFINITY;
        TwoPlayerMove selectedMove;

        TwoPlayerMove bestMove = (TwoPlayerMove)list.get(0);
        Entry entry = new Entry(bestMove, depth, window);

        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = getNextMove(list);
            if (pauseInterrupted())
                return lastMove;
            updatePercentDone(depth, list);

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, window, i++);

            selectedMove = searchInternal( theMove, depth-1, window.negateAndSwap(), child );

            searchable_.undoInternalMove( theMove );

            if (selectedMove != null) {

                int selectedValue = -selectedMove.getInheritedValue();
                theMove.setInheritedValue( selectedValue );

                if ( selectedValue > bestInheritedValue ) {
                    bestMove = theMove;
                    entry.bestMove = theMove;
                    bestInheritedValue = selectedValue;
                    if ( alphaBeta_ && bestInheritedValue >= window.beta) {
                        showPrunedNodesInTree(list, parent, i, selectedValue, window);
                        break;
                    }
                }  
            }
        }
        storeBestMove(window.alpha, entry, bestMove.getInheritedValue());
        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * Store off the best move so we do not need to analyze it again.
     */
    private void storeBestMove(int alpha, Entry entry, int bestValue) {
        if (bestValue <= alpha) {
            entry.upperValue = bestValue;
        }
        else  {
            entry.lowerValue = bestValue;
        }
        lookupTable.put(searchable_.getHashKey(), entry);
    }
}