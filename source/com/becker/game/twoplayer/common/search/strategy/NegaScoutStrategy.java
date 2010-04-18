package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

import java.util.List;

/**
 *  This strategy class defines the NegaScout search algorithm.
 * (also known as principal variation search /PVS)
 *  Negascout is very much like negamax except that it uses a 0 sized search window
 *  and iterative deepening.
 *  See http://en.wikipedia.org/wiki/Negascout
 *
 *  psudo code:<pre>
 *  int negascout(node, depth, α, β)   {}
 *     if node is a terminal node or depth = 0 {
 *         return the heuristic value of node
 *     }
 *     b = β                                 // initial window is (-β, -α)
 *     foreach child of node {
 *        a = -negascout (child, depth-1, -b, -α)
 *        if a>α
 *             α = a
 *        if α≥β
 *             return α                      // Beta cut-off
 *        if α≥b                           // check if null-window failed high
 *            α = -negascout(child, depth-1, -β, -α)  // full re-search
 *            if α≥β
 *                return α                   // Beta cut-off
 *        b = α+1                           // set new null window
 *   }
 *   return α
 *}
 *
 * int NegaScout ( p, α, β );   {
 *    determine successors p_1,...,p_w of p
 *    if ( w = 0 )
 *       return  Evaluate(p)                // leaf
 *    b = β;
 *    for ( i = 1; i <= w; i++ ) {
 *       t = -NegaScout ( p_i, -b, -α );
 *       if (t > α) && (t < β) && (i > 1) && (d < maxdepth-1)
 *           α = -NegaScout ( p_i, -β, -t )  // re-search
 *       α = max( α, t )
 *       if ( α >= β )
 *          return  α                   // cut-off
 *       b = α + 1                      // set new null window
 *    }
 *    return α
 *}
 *
 * negascout(node, alpha, beta)
    if node is a leaf
        return an evaluated score for the node
    maxscore = alpha
    b = beta
    for each child of node
        v = -negascout(child, -b, -alpha)
        if alpha < v < beta and not the first child and depth > 1
              v = -negascout(child, -beta, -v)  // re-search
        alpha = max(alpha, v)
        if alpha >= beta
            return alpha  // cut-off
        b = alpha + 1  // set new null window
    return alpha

 *  </pre>

 *  @author Barry Becker
 */
public class NegaScoutStrategy extends NegaMaxStrategy
{
    /**
     * Construct NegaScout strategy given a controller interface.
     * @inheritDoc
     */
    public NegaScoutStrategy( Searchable controller, ParameterArray weights )
    {
        super( controller, weights );
    }

    /**
     * @inheritDoc
     */
    @Override
    protected TwoPlayerMove findBestMove(TwoPlayerMove lastMove,
                                       int depth,  List<? extends TwoPlayerMove> list,
                                       int alpha, int beta, SearchTreeNode parent) {
        int i = 0;
        int newBeta = beta;
        TwoPlayerMove selectedMove;
        TwoPlayerMove bestMove = list.get(0);

        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = list.remove(0);
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
                    searchable_.makeInternalMove( theMove );
                    selectedMove = searchInternal( theMove, depth-1 , -beta, -alpha, child );
                    searchable_.undoInternalMove( theMove );

                    selectedValue = -selectedMove.getInheritedValue();
                    theMove.setInheritedValue(selectedValue);
                    //theMove.setInheritedValue(alpha); // which is right?
                    bestMove = theMove;

                    if (alpha >= beta) {
                        break;
                    }
                }
                i++;
                newBeta = alpha + 1;
            }
        }

        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }
}