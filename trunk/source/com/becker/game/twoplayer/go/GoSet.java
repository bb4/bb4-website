package com.becker.game.twoplayer.go;

import com.becker.game.common.GameContext;

import java.util.*;

/**
 *  A GoSet is an abstract class representing a set of go entities
 *  (stones, strings, groups, or armies)
 *
 *  @see GoString
 *  @see GoGroup
 *  @see GoArmy
 *  @author Barry Becker
 */
public abstract class GoSet implements GoMember
{

    // a set of the stones/strings/groups that are in the string/group/army
    HashSet members_ = null;

    // true if this set of stones is owned by player one (black)
    boolean ownedByPlayer1_;

    /**
     * constructor.
     */
    public GoSet()
    {
        members_ = new HashSet();
    }

    /**
     *  get the number of liberties (open surrounding spaces)
     */
    public abstract Set getLiberties( GoBoard board );

    /**
     * @return  true if set is owned by player one
     */
    public final boolean isOwnedByPlayer1()
    {
        return ownedByPlayer1_;
    }

    /**
     * @return  the number of stones in the set
     */
    public final int size()
    {
        return members_.size();
    }

    /**
     * @return  the hashSet containing the members
     */
    public final HashSet getMembers()
    {
        return members_;
    }

    /**
     * remove all the elelments of this set.
     */
    final void removeAll()
    {
        members_.clear();
    }

    /**
     * @return a deep copy of this GoSet
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();

        if (this.members_!=null)  {
            ((GoSet)clone).members_ = new HashSet();
            HashSet m = ((GoSet)clone).members_;

            Iterator it = this.members_.iterator();
            while (it.hasNext()) {
                Object c = null;
                try {
                    c = (Object)it.next();
                    m.add(((GoMember)c).clone());
                } catch (ClassCastException e) {
                    GameContext.log(0,  "class "+c.getClass() +" is not a GoMember" );
                    e.printStackTrace();
                }
            }
        }

        return clone;
    }

    /**
     *  @return true if the piece is an enemy of the set owner
     */
    protected boolean isEnemy( GoBoardPosition p, GoBoard board)
    {
        // default implementation. Most subclasses will override.
        return (p.isOccupied() && p.getPiece().isOwnedByPlayer1() != ownedByPlayer1_ );
    }

    /**
     * @return a String representation of this set
     */
    public abstract String toString();

}



