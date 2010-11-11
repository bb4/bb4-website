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
public final class NegaScoutMemoryStrategy extends NegaScoutStrategy {
    /** Stores positions that have already been evaluated, so we do not need to repeat work. */
    private TranspositionTable lookupTable;

    private int cacheHits = 0;
    private int cacheNearHits = 0;
    private int cacheMisses = 0;

    /**
     * Constructor.
     */
    public NegaScoutMemoryStrategy(Searchable controller, ParameterArray weights) {
        super( controller, weights );
        lookupTable = new TranspositionTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TwoPlayerMove searchInternal( TwoPlayerMove lastMove, int depth,
                                          SearchWindow window, SearchTreeNode parent ) {
        Long key = searchable_.getHashKey();
        Entry entry = lookupTable.get(key);
        if (entryExists(lastMove, depth, window, entry))
            return entry.bestMove;

        boolean done = searchable_.done( lastMove, false);
        if ( depth <= 0 || done ) {

            if (doQuiescentSearch(depth, done, lastMove))  {
                TwoPlayerMove qMove = quiescentSearch(lastMove, depth, window, parent);
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

        MoveList list = searchable_.generateMoves(lastMove, weights_, true);

        movesConsidered_ += list.size();
        if (depth == lookAhead_)
            numTopLevelMoves_ = list.size();

        if ( emptyMoveList(list, lastMove) ) {
            // if there are no possible next moves, return null (we hit the end of the game).
            return null;
        }

        TwoPlayerMove bestMove = findBestMove(lastMove, depth, list, window, parent);
        //System.out.println("Cache hits=" + cacheHits + " nearHits=" + cacheNearHits +" misses="  + cacheMisses);
        return bestMove;
    }

    /**
     * if we can just look up the best move in the transposition table, then just do that.
     * @return saved best move in entry
     */
    private boolean entryExists(TwoPlayerMove lastMove, int depth, SearchWindow window, Entry entry) {
        if (entry != null && entry.depth >= depth) {
            cacheHits++;
            //System.out.println("Cache hit. \n entry.depth=" + entry.depth + " depth=" + depth  + "\n" + entry);

            if (entry.upperValue <= window.alpha || entry.upperValue == entry.lowerValue)  {
                entry.bestMove.setInheritedValue(entry.upperValue);
                lastMove.setInheritedValue(-entry.upperValue);
                return true;
            }
            if (entry.lowerValue >= window.beta) {
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
                                         SearchWindow window, SearchTreeNode parent) {
        int i = 0;
        int newBeta = window.beta;
        TwoPlayerMove selectedMove;

        TwoPlayerMove bestMove = (TwoPlayerMove) list.get(0);
        Entry entry = new Entry(bestMove, depth, window);

        //System.out.println("list.size="+ list.size() + " int depth=" + depth + "     alpha="+ alpha +" beta=" + beta);
        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = getNextMove(list);
            if (pauseInterrupted())
                return lastMove;
            updatePercentDone(depth, list);

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, window, i );

            // search with minimal search window
            selectedMove = searchInternal( theMove, depth-1, new SearchWindow(-newBeta, -window.alpha), child );

            searchable_.undoInternalMove( theMove );
            if (selectedMove != null) {

                int selectedValue = -selectedMove.getInheritedValue();
                theMove.setInheritedValue( selectedValue );

                if (selectedValue > window.alpha) {
                    window.alpha = selectedValue;
                }
                if (window.alpha >= window.beta) {
                    theMove.setInheritedValue(window.alpha);
                    bestMove = theMove;
                    break;
                }
                if (window.alpha >= newBeta) {
                    // re-search with narrower window (typical alpha beta search).
                    //System.out.println("re-searching with narrower window a=" + -beta +" b="+ -alpha);
                    searchable_.makeInternalMove( theMove );
                    selectedMove = searchInternal( theMove, depth-1, window.negateAndSwap(), child );
                    searchable_.undoInternalMove( theMove );

                    selectedValue = -selectedMove.getInheritedValue();
                    theMove.setInheritedValue(selectedValue);
                    bestMove = theMove;

                    if (window.alpha >= window.beta) {
                        break;
                    }
                }
                i++;
                newBeta = window.alpha + 1;
            }
        }
        storeBestMove(window, entry, bestMove.getInheritedValue());
        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * Store off the best move so we do not need to analyze it again.
     */
    private void storeBestMove(SearchWindow window, Entry entry, int bestValue) {
        if (bestValue <= window.alpha) {
            entry.upperValue = bestValue;
        }
        else if (window.alpha < bestValue && bestValue < window.beta) {
            entry.lowerValue = bestValue;
            entry.upperValue = bestValue;
        }
        else if (bestValue >= window.beta) {
            entry.lowerValue = bestValue;
        }
        lookupTable.put(searchable_.getHashKey(), entry);
    }
}