package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.common.board.BoardPosition;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoBoardPositionSet;

/**
 * Determines number of liberties on a string.
 *
 * @author Barry Becker
 */
public class StringLibertyAnalyzer {

    /** Keep track of number of liberties instead of computing each time (for performance). */
    private GoBoardPositionSet liberties_;


    /**
     * Constructor.
     * @param board game board
     * @param members the initial set of liberties.
     */
    public StringLibertyAnalyzer(GoBoard board, GoBoardPositionSet members) {
        initializeLiberties(board, members);
    }


    private void initializeLiberties(GoBoard board, GoBoardPositionSet members) {
        liberties_ = new GoBoardPositionSet();

        for (GoBoardPosition stone : members) {
            addLiberties(stone, liberties_, board);
        }
    }


    /**
     * @return number of liberties that the string has
     */
    public final GoBoardPositionSet getLiberties()
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
    private static void addLiberties( GoBoardPosition stone, GoBoardPositionSet liberties, GoBoard board )
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
    private static void addLiberty( BoardPosition libertySpace, GoBoardPositionSet liberties )
    {
        // this assumes a HashSet will not allow you to add the same object twice (no dupes)
        if ( libertySpace.isUnoccupied() )
            liberties.add( (GoBoardPosition)libertySpace );
    }

}