package com.becker.game.twoplayer.go;

import com.becker.game.twoplayer.go.board.*;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.elements.*;

import java.util.*;

/**
 *  describes a change in state from one board
 *  position to the next in a Go game.
 *
 *  @see GoBoard
 *  @author Barry Becker
 */
public final class GoMove extends TwoPlayerMove
{
    /** 
     * A linked list of the pieces that were captured with this move.
     * null if there were no captures.
     */
    private CaptureList captureList_ = null;

    /**
     * Constructor. This should never be called directly
     * instead call the factory method so we recycle objects.
     * use createMove to get moves, and dispose to recycle them
     */
    public GoMove( int destinationRow, int destinationCol, int val, GoStone stone )
    {
        super( (byte)destinationRow, (byte)destinationCol, val, stone );
    }

    /**
     * factory method for getting new moves.
     * it uses recycled objects if possible.
     * @return new go move
     */
    public static GoMove createGoMove(
            int destinationRow, int destinationCol,
            int val, GoStone stone )
    {
        return new GoMove( (byte)destinationRow, (byte)destinationCol, val, stone );
    }

    /**
     * factory method for creating a passing move
     * @return new passing move
     */
    public static GoMove createPassMove( int val,  boolean player1)
    {
        GoMove m = createGoMove( 1, 1, val, null );
        m.isPass_ = true;
        m.setPlayer1(player1);
        return m;
    }

    /**
     * factory method for creating a passing move
     * @return new passing move
     */
    public static GoMove createResignationMove(boolean player1)
    {
        GoMove m = createGoMove( 1, 1, 0, null );
        m.isResignation_ = true;
        m.setPlayer1(player1);
        return m;
    }

    /**
     * check if the current move is suicidal.
     * suicidal moves (ones that kill your own pieces) are illegal.
     * Usually a move is suicidal if you play on your last liberty.
     * However, if you kill an enemy string by playing on your last liberty,
     * then it is legal.
     * @return true if this move is suicidal.
     */
    public boolean isSuicidal( GoBoard board )
    {
        GoBoardPosition stone = (GoBoardPosition) board.getPosition( getToRow(), getToCol() );

        NeighborAnalyzer na = new NeighborAnalyzer(board);
        GoBoardPositionSet nobiNbrs = na.getNobiNeighbors(stone, false, NeighborType.ANY);
        GoBoardPositionSet occupiedNbrs = new GoBoardPositionSet();
        for (GoBoardPosition pos : nobiNbrs) {
            if (pos.isOccupied()) {
                occupiedNbrs.add(pos);
            }
        }

        return !hasLiberties(occupiedNbrs, nobiNbrs) && partOfDeadString(occupiedNbrs, board);

    }

    /**
     * Can't be suicidal if we have a liberty.
     * @return true if one or more liberties still available.
     */
    private boolean hasLiberties(GoBoardPositionSet occupiedNbrs, GoBoardPositionSet nobiNbrs) {
        return (nobiNbrs.size() > occupiedNbrs.size());
    }

    /**
     * If the newly placed stone captures an opponent string, then we reutnr false.
     * @param occupiedNbrs The 4 occupied Nbrs neighbors to check
     * @return true if the newly placed stone is part of a string that is now captured as a reuslt of playing.
     */
    private boolean partOfDeadString(GoBoardPositionSet occupiedNbrs, GoBoard board ) {
        for (GoBoardPosition nbr : occupiedNbrs)  {
            if (nbr.getPiece().isOwnedByPlayer1() == this.isPlayer1()) {
                // friendly string
                if (nbr.getString().getNumLiberties(board) > 1) {
                    // can't be suicidal if a neighboring friendly string has > 1 liberty
                    return false;
                }
            }
            else {
               if (nbr.getString().getNumLiberties(board) == 1) {
                   // can't be suicidal if by playing we capture an opponent string.
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * returns true if the specified move caused one or more opponent groups to be in atari
     *
     * @return a number > 0 if the move m caused an atari. The number gives the number of stones in atari.
     */
    public int causesAtari( GoBoard board )
    {
        if ( isPassingMove() )
            return 0; // a pass cannot cause an atari

        GoBoardPosition pos = (GoBoardPosition)board.getPosition( getToRow(), getToCol() );
        NeighborAnalyzer na = new NeighborAnalyzer(board);
        GoBoardPositionSet enemyNbrs = na.getNobiNeighbors( pos, NeighborType.ENEMY );
        Iterator it = enemyNbrs.iterator();
        int numInAtari = 0;
        GoStringSet stringSet = new GoStringSet();
        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            GoString atariedString = s.getString();
            if (!stringSet.contains(atariedString) && atariedString.getNumLiberties(board) == 1 ) {
                numInAtari += atariedString.size();
            }
            stringSet.add(atariedString); // once its in the set we won't check it again.
        }
        return numInAtari;
    }

    /**
     * I would like to avoid this setter.
     * @param captures
     */
    public void setCaptures(CaptureList captures) {
        captureList_ = captures;
    }

    public CaptureList getCaptures() {
        return captureList_;
    }

    public int getNumCaptures() {
        if (captureList_!= null) {
            return captureList_.size();
        } else {
            return 0;
        }
    }

    /**
     *  make a deep copy of the move object
     */
    @Override
    public TwoPlayerMove copy()
    {
        CaptureList newList = null;
        if ( captureList_ != null ) {
            // then make a deep copy
            GameContext.log( 2, "**** GoMove: this is the capturelist we are copying:" + captureList_.toString() );
            newList = captureList_.copy();
        }
        GoMove cp = 
                createGoMove( toLocation_.getRow(), toLocation_.getCol(),
                                       getValue(), (getPiece() == null)? null : (GoStone)getPiece().copy() );
        cp.captureList_ = newList;
        cp.setPlayer1(isPlayer1());
        cp.setSelected(this.isSelected());
        return cp;
    }   

    /**
     *
     * @return stringified form.
     */
    @Override
    public String toString()
    {
        String s = super.toString();
        if ( captureList_ != null ) {
            s += "num captured="+captureList_.size();
        }
        return s;
    }
}



