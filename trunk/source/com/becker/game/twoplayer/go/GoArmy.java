package com.becker.game.twoplayer.go;

import java.util.Iterator;
import java.util.Set;

/**
 *  A GoArmy is composed of a set of one or more same color groups.
 *  A GoGroup by comparison, is composed of one or more same color strings.
 *  Armies may be connected by knight's moves or 2 or 3 space jumps.
 *
 *  @see GoGroup
 *  @see GoBoard
 *  @author Barry Becker
 */
public final class GoArmy extends GoSet
{

    // unlike the territory of a group, moyo territory indicates the amount of space
    // mapped out by the army. It will potentially become real territory if all goes well.
    int moyoTerritory;
    // @@ compute dynamically instead
    private int numLiberties_;

    /**
     * constructor. Create a new army containing the specified group
     */
    public GoArmy( GoGroup group )
    {
        group.setArmy( this );
        ownedByPlayer1_ = group.isOwnedByPlayer1();
        members_.add( group );
    }

    /**
     * add a group to the army
     */
    private void addMember( GoGroup group, GoBoard board )
    {
        assert (group.isOwnedByPlayer1()==ownedByPlayer1_): "groups added to an army must have like ownership";
        assert (!members_.contains( group )): "the army already contains this group";
        group.setArmy( this );
        members_.add( group );
        numLiberties_ = numLiberties_ - 1 + group.getLiberties( board ).size();
    }

    /**
     * calculate the number of liberties
     */
    public final Set getLiberties( GoBoard board )
    {
        return null; //@@
    }

    /**
     * merge another group into this one.
     * @param army the group to merge into this one
     * @param strongConnection true if not a diagonal connection - ie directly adjacent
     * @param board
     */
    public final void merge( GoArmy army, boolean strongConnection, GoBoard board )
    {
        if ( this == army ) {
            // its a self join
            if ( strongConnection )
                numLiberties_--;
            return;
        }
        Iterator it = members_.iterator();
        while ( it.hasNext() )
            addMember( (GoGroup) it.next(), board );
        numLiberties_ = numLiberties_ + army.getLiberties( board ).size();
        if ( strongConnection )
            numLiberties_--;
    }


    // remove a string from this group (only if it is empty)
    public void remove( GoGroup group )
    {
        assert (group.size() == 0): "can't remove a non-empty group";
        members_.remove( group );
    }

    public String toString()
    {
        String s = " These are the groups in this army :\n";
        Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            GoGroup p = (GoGroup) it.next();
            s += "(" + p.toString() + "),";
        }
        return s;
    }
}



