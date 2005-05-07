package com.becker.game.twoplayer.go;

import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;

import java.util.*;

/**
 *  A GoString is composed of a strongly connected set of one or more same color stones.
 *  By strongly connected I mean nobi connections only.
 *  A GoGroup by comparison, is composed of a set of one or more same color strings.
 *  Groups may be connected by diagonals, or ikken tobi, or kogeima (knight's move).
 *
 *  @see GoGroup
 *  @see GoBoard
 *  @author Barry Becker
 */
public class GoString extends GoSet
{

    // the group to which this string belongs
    protected GoGroup group_;

    /**
     * constructor. Create a new string containing the specified stone
     */
    public GoString( GoBoardPosition stone )
    {
        assert ( stone.isOccupied() );
        ownedByPlayer1_ = stone.getPiece().isOwnedByPlayer1();
        members_.add( stone );
        stone.setString( this );
        group_ = null;
    }

    /**
     * constructor. Create a new string containing the specified list of stones
     */
    public GoString( List stones )
    {
        assert (stones != null && stones.size() > 0): "Tried to create list from empty list";
        GoStone stone =  (GoStone)((GoBoardPosition) stones.get( 0 )).getPiece();
        // GoEye constructor calls this method. For eyes the stone is null.
        if (stone != null)
            ownedByPlayer1_ = stone.isOwnedByPlayer1();
        Iterator it = stones.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition pos = (GoBoardPosition) it.next();
            addMember( pos );
        }
    }


    // set/get the group
    public final void setGroup( GoGroup group )
    {
        group_ = group;
    }

    public final GoGroup getGroup()
    {
        return group_;
    }

    /**
     * add a stone to the string
     */
    public void addMember( GoBoardPosition stone )
    {
        assert ( stone.getPiece().isOwnedByPlayer1() == ownedByPlayer1_):
                "stones added to a string must have like ownership";
        assert ( stone.isOccupied()): "trying to add empty space to string. stone=" + stone ;
        if ( members_.contains( stone ) ) {
            // this case can happen sometimes.
            // For example if the new stone completes a loop and self-joins the string to itself
            //GameContext.log( 2, "Warning: the string, " + this + ", already contains the stone " + stone );
            assert  (stone.getString() == null) || (this == stone.getString()):
                    "bad stone "+stone+" or bad owning string "+ stone.getString();
        }
        // if the stone is already owned by another string, we need to verify that that other string has given it up.
        if (stone.getString() != null) {
            stone.getString().remove(stone);
            //: stone +" is already owned by another string: "+ stone.getString();
        }

        stone.setString( this );
        members_.add( stone );
    }

    /**
     * merge a string into this one
     */
    public final void merge( GoString string )
    {
        if ( this == string ) {
            GameContext.log( 1, "Warning: merging " + string + " into itself" );
            // its a self join
            return;
        }
        GoGroup g = string.getGroup();
        //g.remove( string );

        Set stringMembers = new HashSet();
        stringMembers.addAll(string.getMembers());
        // must remove these after iterating otherwise we get a ConcurrentModificationException
        string.removeAll();

        Iterator it = stringMembers.iterator();
        GoBoardPosition stone;
        while ( it.hasNext() ) {
            stone = (GoBoardPosition) it.next();
            GoString myString = stone.getString();
            if (myString != null && myString != string) {
                myString.remove(stone);
            }
            stone.setString(null);
            addMember( stone );
        }
        stringMembers.clear();
    }

    /**
     * remove a stone from this string.
     * What happens if the string gets split as a result?
     * The caller should handle this case since we cannot create new strings here.
     */
    public final void remove( GoBoardPosition stone )
    {
        boolean removed = members_.remove( stone );
        assert (removed) : "failed to remove "+stone+" from"+ this;
        stone.setString(null);
        if ( members_.isEmpty() ) {
            group_.remove( this );
        }
    }

    /**
     * remove a set (List) of stones from this string.
     * Its an error if the argument is not a proper substring.
     * @param stones stones to remove (error if not a proper substring)
     */
    public final void remove( Collection stones, GoBoard board )
    {
        Iterator it = stones.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition stone = (GoBoardPosition) it.next();
            // hitting this from UpdateStringsAfterRemoving
            //assert ( members_.contains( stone )): "ERROR: GoString.remove: " + stone + " is not a subset of \n" + this;
            remove( stone );
        }
        assert ( size() > 0 );
    }

    /**
     * return the set of liberty positions that the string has
     */
    public final Set getLiberties( GoBoard board )
    {
        Set liberties = new HashSet();

        Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition stone = (GoBoardPosition) it.next();
            addLiberties( stone, liberties, board );
        }
        return liberties;
    }

    /**
     *
     * @param board
     * @return  true if the string is in atari
     */
    public boolean isInAtari(GoBoard board)
    {
        return (getLiberties(board).size() == 0);
    }


    /**
     * only add liberties for this stone if they are not already in the set
     */
    private static void addLiberties( GoBoardPosition stone, Set liberties, GoBoard board )
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

    private static void addLiberty( BoardPosition libertySpace, Set liberties )
    {
        // this assumes a HashSet will not allow you to add the same object twice (no dupes)
        if ( libertySpace.isUnoccupied() )
            liberties.add( libertySpace );
    }

    /** set the health of members equal to the specified value
     * @param health range = [0-1]
     */
    public final void updateTerritory( float health )
    {
        Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition pos = (GoBoardPosition) it.next();
            GoStone stone = (GoStone)pos.getPiece();
            stone.setHealth( health );
        }
    }


    /**
     *  @return true if the piece is an enemy of the string owner
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
    protected boolean isEnemy( GoBoardPosition pos, GoBoard board )
    {
        assert (group_!=null): "group for "+this+" is null";
        assert (pos.isOccupied()): "pos not occupied: ="+pos;
        GoStone stone = (GoStone)pos.getPiece();
        boolean withinDifferenceThreshold = !GoBoardUtil.isStoneMuchWeaker(getGroup(), stone);

        assert (getGroup().isOwnedByPlayer1() == this.isOwnedByPlayer1()): getGroup()+" string="+this;
        return ((stone.isOwnedByPlayer1() != this.isOwnedByPlayer1() && withinDifferenceThreshold));
    }

    /**
     * make sure all the stones in the string are unvisited
     */
    public final void unvisit()
    {
        Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition stone = (GoBoardPosition) it.next();
            stone.setVisited( false );
        }
    }

    String getPrintPrefix()
    {
        return " STRING(";
    }

    /**
     * @return  a string representation for the string.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer( getPrintPrefix() );
        Iterator it = members_.iterator();
        if ( it.hasNext() ) {
            GoBoardPosition p = (GoBoardPosition) it.next();
            sb.append( p.toString() );
        }
        while ( it.hasNext() ) {
            GoBoardPosition p = (GoBoardPosition) it.next();
            sb.append( ", " );
            sb.append( p.toString() );
        }
        sb.append( ")" );
        return sb.toString();
    }

    ////////////////// debugging methods //////////////////////////////
    /**
     * return true if any of the stones in the string are blank (should never happen)
     */
    public final boolean areAnyBlank()
    {
        final Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            final GoBoardPosition stone = (GoBoardPosition) it.next();
            if ( stone.isUnoccupied() )
                return true;
        }
        return false;
    }

    /** confirm that all the stones in the string have the same ownership as the string
     *  we throw and error if this is not true
     */
    public final void confirmValid( GoBoard b )
    {
        Iterator it = members_.iterator();
        if ( it.hasNext() ) {
            List list = b.findStringFromInitialPosition( (GoBoardPosition) it.next(), true );
            // confirm that all member_ stones are in the string
            while ( it.hasNext() ) {
                GoBoardPosition s = (GoBoardPosition) it.next();
                assert ( list.contains( s )): list + " does not contain " + s + ". members_ =" + members_ ;
            }
        }
    }

    /** confirm that all the stones in the string have the same ownershp as the string.
     *  we throw and error if this is not true
     */
    public final void confirmOwnedByOnlyOnePlayer()
    {
        Iterator it = members_.iterator();

        if ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            GoStone stone = (GoStone)s.getPiece();
            assert ( stone.isOwnedByPlayer1() == this.isOwnedByPlayer1()) :
                    stone + " does not have the same owner as " + this;
        }
    }
}



