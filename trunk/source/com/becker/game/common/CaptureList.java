package com.becker.game.common;

import java.util.*;

/**
 *  This class represents a linked list of captured pieces.
 *  It provides convenience methods for removing and restoring those
 *  pieces to a game board.
 *
 *  @see Board
 *  @author Barry Becker
 */
public class CaptureList extends LinkedList<BoardPosition>
{
    private static final long serialVersionUID = 0L;

    /**
     * remove the captured pieces from the board.
     */
    public void removeFromBoard( Board b )
    {
        modifyCaptures( b, true );
    }

    /**
     * restore the captured pieces on the board.
     */
    public void restoreOnBoard( Board b )
    {
        modifyCaptures( b, false );
    }

    /**
     * @param b the game board
     * @param remove if true then remove the pieces, else restore them
     */
    private void modifyCaptures( Board b, boolean remove )
    {
        Iterator it = this.iterator();
        while ( it.hasNext() ) {
            BoardPosition capture = (BoardPosition) it.next();
            BoardPosition pos = b.getPosition( capture.getRow(), capture.getCol() );
            if ( remove )
                pos.setPiece( null );
            else  {
                pos.setPiece( capture.getPiece().copy() );
            }
        }
    }

    /**
     *  returns true if the piece was already captured
     */
    public boolean alreadyCaptured( BoardPosition p )
    {
        for (Object o : this) {
            BoardPosition capture = (BoardPosition) o;
            if (capture.getRow() == p.getRow() &&
                    capture.getCol() == p.getCol() &&
                    capture.getPiece().getType() == p.getPiece().getType())
                return true;
        }
        return false;
    }

    /**
     * @return a deep copy of the capture list.
     */
    public CaptureList copy()
    {
        Iterator it = this.iterator();
        CaptureList newList = new CaptureList();
        while ( it.hasNext() ) {
            BoardPosition capture = (BoardPosition) it.next();
            newList.add( capture.copy() );
        }
        return newList;
    }

    /**
     * @return true if list is not null and not 0 sized.
     */
    public boolean addAllCopied( Collection list )
    {
        if ( list == null )
            return false;
        // we need to add copies so that when the original stones on the board are
        // changed we don't change the captures
        Iterator it = list.iterator();
        while ( it.hasNext() ) {
            BoardPosition capture = (BoardPosition) it.next();
            // make sure none of the captures are blanks
            assert capture.isOccupied();
            this.add( capture.copy() );
        }
        return (!list.isEmpty());
    }

    /**
     * Produces a string representation of the list of captured pieces.
     */
    @Override
    public String toString()
    {
        String s = " These piece(s) were captured by this move:\n";
        Iterator it = this.iterator();
        while ( it.hasNext() ) {
            BoardPosition p = (BoardPosition) it.next();
            s += '(' + p.toString() + "),";
        }
        return s;
    }
}

