package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.transposition.Entry;
import com.becker.game.twoplayer.common.search.transposition.TranspositionTable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

/**
 *  This strategy class defines the NegaScout with memory search algorithm.
 *  This version stores the values of moves that have already been searched.
 *  See http://people.csail.mit.edu/plaat/mtdf.html
 *  and http://en.wikipedia.org/wiki/Negascout
 *
 * Transposition table (TT) enhanced Alpha-Beta
 * (from http://www.top-5000.nl/ps/An%20Algorithm%20faster%20than%20negascout%20and%20SSS%20in%20pratice.pdf)
 * <pre>
 *   function AlphaBetaWithMemory(n, a, b) {
 *     // Check if position is in table and has been searched to sufficient depth.
 *     if (retrieve(n)) {
 *        if (n.max <= a or n.max == n.min)  return n.max;
 *        if (n.min >= b) return n.min ;
 *    }
 *    // Reached the maximum search depth
 *    if (n = leaf) {
 *      n.min = n.max = g = eval(n);
 *    }
 *    else  {
 *      g = -inf;
 *      c = firstchild(n);
 *      // Search until a cutoff occurs or all children have been considered
 *      while g < b and c != null {
 *        g = max(g, -AlphaBetaWithMemory(c, -b, -a));
 *        a = max(a, g);
 *        c = nextbrother(c);
 *      }
 *      // Save in transposition table
 *      if g <= a then n.max = g;
 *      if a < g < b then n.max = n.min = g;
 *      if g >= b then n.min = g;
 *     }
 *    store(n);
 *    return g;
 * }
 * </pre>
 *  @author Barry Becker 
 */
public final class NegaScoutMemoryStrategy extends NegaScoutStrategy
{
    /** Stores positions that have already been evaluated, so we do not need to repeat work. */
    private TranspositionTable lookupTable;

    private int cacheHits = 0;
    private int cacheNearHits = 0;
    private int cacheMisses = 0;
    /**
     * Constructor.
     */
    public NegaScoutMemoryStrategy( Searchable controller, ParameterArray weights )
    {
        super( controller, weights );
        lookupTable = new TranspositionTable();
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
        if (entryExists(lastMove, depth, alpha, beta, entry))
            return entry.bestMove;

        boolean done = searchable_.done( lastMove, false);
        if ( depth == 0 || done ) {

            if ( quiescence_ && depth == 0 && !done)  {
                TwoPlayerMove qMove = quiescentSearch(lastMove, depth, alpha, beta, parent);
                entry = new Entry(qMove, qMove.getInheritedValue());
                lookupTable.put(key, entry);
                return qMove;
            }
            int sign = fromPlayer1sPerspective(lastMove) ? 1 : -1;
            lastMove.setInheritedValue(sign * lastMove.getValue());
            entry = new Entry(lastMove, -lastMove.getInheritedValue());
            lookupTable.put(key, entry);
            return lastMove;
        }

        // generate a list of all (or bestPercent) candidate next moves, and pick the best one
        MoveList list =
                searchable_.generateMoves(lastMove, weights_, true);

        movesConsidered_ += list.size();
        if (depth == lookAhead_)
            numTopLevelMoves_ = list.size();

        if ( emptyMoveList(list, lastMove) ) {
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
            //System.out.println("Cache hit. \nentry.depth=" + entry.depth + " depth=" + depth  + "\n" + entry);

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
    protected TwoPlayerMove findBestMove(TwoPlayerMove lastMove,int depth,  MoveList list,
                                         int alpha, int beta, SearchTreeNode parent) {
        int i = 0;
        int newBeta = beta;
        TwoPlayerMove selectedMove;

        TwoPlayerMove bestMove = (TwoPlayerMove) list.get(0);
        Entry entry = new Entry(bestMove, depth, alpha, beta);

        //System.out.println("list.size="+ list.size() + " int depth=" + depth + "     alpha="+ alpha +" beta=" + beta);
        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = getNextMove(list);
            if (pauseInterrupted())
                return lastMove;
            updatePercentDone(depth, list);

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i );

            // search with minimal search window
            selectedMove = searchInternal( theMove, depth-1, -newBeta, -alpha, child );

            searchable_.undoInternalMove( theMove );
            if (selectedMove != null) {

                int selectedValue = -selectedMove.getInheritedValue();
                theMove.setInheritedValue( selectedValue );

                if (selectedValue > alpha) {
                    alpha = selectedValue;
                }
                if (alpha >= beta) {
                    theMove.setInheritedValue(alpha);
                    bestMove = theMove;
                    break;
                }
                if (alpha >= newBeta) {
                    // re-search with narrower window (typical alpha beta search).
                    //System.out.println("re-searching with narrower window a=" + -beta +" b="+ -alpha);
                    searchable_.makeInternalMove( theMove );
                    selectedMove = searchInternal( theMove, depth-1 , -beta, -alpha, child );
                    searchable_.undoInternalMove( theMove );

                    selectedValue = -selectedMove.getInheritedValue();
                    theMove.setInheritedValue(selectedValue);
                    bestMove = theMove;

                    if (alpha >= beta) {
                        break;
                    }
                }
                i++;
                newBeta = alpha + 1;
            }
        }
        storeBestMove(alpha, beta, entry, bestMove.getInheritedValue());
        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * Store off the best move so we do not need to analyze it again.
     */
    private void storeBestMove(int alpha, int beta, Entry entry, int bestValue) {
        if (bestValue <= alpha) {
            entry.upperValue = bestValue;
        }
        else if (alpha < bestValue && bestValue < beta) {
            entry.lowerValue = bestValue;
            entry.upperValue = bestValue;
        }
        else if (bestValue >= beta) {
            entry.lowerValue = bestValue;
        }
        lookupTable.put(searchable_.getHashKey(), entry);
    }
}