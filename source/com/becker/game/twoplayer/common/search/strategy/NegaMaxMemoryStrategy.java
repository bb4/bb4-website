package com.becker.game.twoplayer.common.search.strategy;

import com.becker.common.math.Range;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.transposition.Entry;
import com.becker.game.twoplayer.common.search.transposition.TranspositionTable;
import com.becker.game.twoplayer.common.search.tree.PruneType;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

/**
 *  This strategy class defines the NegaMax with memory search algorithm.
 *  This version stores the values of moves that have already been searched.
 *  Based on psuedo code from Artificial Intelligence for Games by Millington and Funge.
 *
 *  @@ Need to fix.
 *  @author Barry Becker
 */
public final class NegaMaxMemoryStrategy extends NegaMaxStrategy
{
    /** Stores positions that have already been evaluated, so we do not need to repeat work. */
    private TranspositionTable lookupTable;

    private int cacheHits = 0;
    private int cacheNearHits = 0;
    private int cacheMisses = 0;


    /**
     * Constructor.
     */
    public NegaMaxMemoryStrategy( Searchable controller, ParameterArray weights )
    {
        super( controller, weights );
        lookupTable = new TranspositionTable();
    }

    /**
     * @inheritDoc
     */
    @Override
    public TwoPlayerMove search( TwoPlayerMove lastMove, SearchTreeNode parent ) {
        // need to negate alpha and beta on initial call.
        Range window = getOptions().getInitialSearchWindow();
        int g = (int)((window.getMax() +  window.getMin())/2);
        return searchInternal( lastMove, lookAhead_, g, g, parent );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TwoPlayerMove searchInternal( TwoPlayerMove lastMove,
                                          int depth,
                                          int alpha, int beta, SearchTreeNode parent ) {
        Long key = searchable_.getHashKey();
        Entry entry = lookupTable.get(key);
        if (entryExists(lastMove, depth, alpha, beta, entry)) {
            if (entry.lowerValue > alpha) {
                entry.bestMove.setInheritedValue(entry.lowerValue);
                return entry.bestMove;
            }
            else if (entry.upperValue < beta) {
                entry.bestMove.setInheritedValue(entry.upperValue);
                return entry.bestMove;
            }
        }

        entry = new Entry(lastMove, depth, -SearchStrategy.INFINITY, SearchStrategy.INFINITY);

        boolean done = searchable_.done( lastMove, false);
        if ( depth == 0 || done ) {

            if ( quiescence_ && depth == 0 && !done)  {
                TwoPlayerMove qMove = quiescentSearch(lastMove, depth, alpha, beta, parent);
                entry = new Entry(qMove, qMove.getInheritedValue());
                lookupTable.put(key, entry);
                return qMove;
            }
            int sign = fromPlayer1sPerspective(lastMove) ? 1 : -1;
            int value = sign * lastMove.getValue();
            lastMove.setInheritedValue(value);
            entry.lowerValue = value;
            entry.upperValue = value;
            lookupTable.put(key, entry);
            return lastMove;
        }

        // generate a list of all (or bestPercent) candidate next moves, and pick the best one
        MoveList list =
                searchable_.generateMoves(lastMove, weights_, true);

        movesConsidered_ += list.size();
        if (depth == lookAhead_)
            numTopLevelMoves_ = list.size();

        if ( emptyMoveList( list, lastMove) ) {
            // if there are no possible next moves, return null (we hit the end of the game).
            return null;
        }

        TwoPlayerMove bestMove = findBestMove(lastMove, depth, list, alpha, beta, parent);

        //System.out.println("Cache hits=" + cacheHits + " nearHits=" + cacheNearHits +" misses="  + cacheMisses);

        return bestMove;
    }

    /**
     * if we can just look up the best move in the transposition table, then just do that.
     * @return saved best move in entry
     */
    private boolean entryExists(TwoPlayerMove lastMove, int depth, int alpha, int beta, Entry entry) {
        if (entry != null && entry.depth >= depth) {
            cacheHits++;
            System.out.println("Cache hit. \nentry.depth=" + entry.depth + " depth=" + depth  + "\n" + entry);

            if (entry.upperValue <= alpha || entry.upperValue == entry.lowerValue)  {
                entry.bestMove.setInheritedValue(entry.upperValue);
                lastMove.setInheritedValue(-entry.upperValue);
                return true;
            }
            if (entry.lowerValue >= beta) {
                entry.bestMove.setInheritedValue(entry.lowerValue);
                lastMove.setInheritedValue(-entry.lowerValue);
                return true;
            }
        }
        else {
            if (entry != null) cacheNearHits++;
            else cacheMisses++;
        }
        return false;
    }


    /**
     * @inheritDoc
     */
    @Override
    protected TwoPlayerMove findBestMove(TwoPlayerMove lastMove, int depth, MoveList list,
                                         int alpha, int beta, SearchTreeNode parent) {
        int i = 0;
        int bestInheritedValue = -SearchStrategy.INFINITY;
        TwoPlayerMove selectedMove;

        TwoPlayerMove bestMove = (TwoPlayerMove)list.get(0);
        Entry entry = new Entry(bestMove, depth, alpha, beta);

        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = (TwoPlayerMove)list.remove(0);
            if (pauseInterrupted())
                return lastMove;
            updatePercentDone(depth, list);

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i++);

            selectedMove = searchInternal( theMove, depth-1, -beta, -alpha, child );

            searchable_.undoInternalMove( theMove );

            if (selectedMove != null) {

                int selectedValue = -selectedMove.getInheritedValue();
                theMove.setInheritedValue( selectedValue );

                if ( selectedValue > bestInheritedValue ) {
                    bestMove = theMove;
                    entry.bestMove = theMove;
                    bestInheritedValue = selectedValue;
                }
                if ( alphaBeta_ ) {
                    if ( bestInheritedValue > alpha ) {
                        alpha = bestInheritedValue;
                        bestMove = theMove;
                        entry.bestMove = theMove;
                    }
                    if ( alpha >= beta ) {
                        showPrunedNodesInTree( list, parent, i, selectedValue, beta, PruneType.BETA);
                        break;
                    }
                }
            }
        }
        storeBestMove(alpha,entry, bestMove.getInheritedValue());
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