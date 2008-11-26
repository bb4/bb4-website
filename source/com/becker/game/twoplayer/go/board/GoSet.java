package com.becker.game.twoplayer.go.board;

import com.becker.game.common.GameContext;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

    // true if this set of stones is owned by player one (black)
    protected boolean ownedByPlayer1_;

    /**
     * constructor.
     */
    protected GoSet()
    {
        initializeMembers();
    }

    /**
     * Get the number of liberties (open surrounding spaces)
     * @param board
     */
    public abstract Set getLiberties(GoBoard board);

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
        return getMembers().size();
    }

    /**
     * @return  the hashSet containing the members
     */
    public abstract Set<? extends GoMember> getMembers();
    
    protected abstract void initializeMembers();
    
    /**
     * remove all the elelments of this set.
     */
    final void removeAll()
    {
        getMembers().clear();
    }

    /**
     * @return a deep copy of this GoSet
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();

        if (getMembers()!=null)  {
            ((GoSet)clone).initializeMembers();
            //((GoSet)clone).members_ = new HashSet();
            Set m = ((GoSet)clone).getMembers();

            Iterator it = getMembers().iterator();
            while (it.hasNext()) {
                Object c = null;
                try {
                    c = it.next();
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
    protected abstract boolean isEnemy(GoBoardPosition p);

}



