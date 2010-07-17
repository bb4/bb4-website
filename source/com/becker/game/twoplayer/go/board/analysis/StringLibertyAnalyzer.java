package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.common.BoardPosition;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoBoardPosition;

import java.util.HashSet;
import java.util.Set;

/**
 * Determines number of liberties on a string.
 *
 * @author Barry Becker
 */
public class StringLibertyAnalyzer {

    /** Keep track of number of liberties instead of computing each time (for performance). */
    private Set<GoBoardPosition> liberties_;


    public StringLibertyAnalyzer(GoBoard board, Set<GoBoardPosition> members) {
        initializeLiberties(board, members);
    }


    private void initializeLiberties(GoBoard board, Set<GoBoardPosition> members) {
        liberties_ = new HashSet<GoBoardPosition>();

        for (GoBoardPosition stone : members) {
            addLiberties(stone, liberties_, board);
        }
    }


    /**
     * @return number of liberties that the string has
     */
    public final Set<GoBoardPosition> getLiberties()
    {
        return liberties_;
    }


    /**
     * If the libertyPos is occupied, then we remove this liberty, else add it.
     * @param libertyPos  position to check for liberty
     */
    public void changedLiberty(GoBoardPosition libertyPos) {
         if (libertyPos.isOccupied()) {
             liberties_.remove(libertyPos);
             // hitting if showing game tree perhaps because already removed.
             //assert removed : "could not remove " + libertyPos +" from "+liberties_;
         } else {
             assert (!liberties_.contains(libertyPos)) : this + " already had " + libertyPos
                     + " as a liberty and we were not expecting that. Liberties_=" + liberties_;
             liberties_.add(libertyPos);
         }
    }


    /**
     * only add liberties for this stone if they are not already in the set
     */
    private static void addLiberties( GoBoardPosition stone, Set<GoBoardPosition> liberties, GoBoard board )
    {
        int r = stone.getRow();
        int c = stone.getCol();
        if ( r > 1 )
            addLiberty( board.getPosition( r - 1, c ), liberties );
        if ( r < board.getNumRows() )
            addLiberty( board.getPosition( r + 1, c ), liberties );
        if ( c > 1 )
            addLiberty( board.getPosition( r, c - 1 ), liberties );
        if ( c < board.getNumCols() )
            addLiberty( board.getPosition( r, c + 1 ), liberties );
    }

    /**
     * 
     * @param libertySpace
     * @param liberties
     */
    private static void addLiberty( BoardPosition libertySpace, Set<GoBoardPosition> liberties )
    {
        // this assumes a HashSet will not allow you to add the same object twice (no dupes)
        if ( libertySpace.isUnoccupied() )
            liberties.add( (GoBoardPosition)libertySpace );
    }

}