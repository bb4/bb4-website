package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.GoBoard;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *  A GoSet is an abstract class representing a set of go entities
 *  (stones, strings, groups, or armies)
 *
 *  @see GoString
 *  @see GoGroup
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
     * @return the liberties/positions for the set.
     */
    public abstract GoBoardPositionSet getLiberties(GoBoard board);

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
     * @return the hashSet containing the members
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
     * @@ need synchronized?
     * @return a deep copy of this GoSet
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();

        if (getMembers()!=null)  {
            ((GoSet)clone).initializeMembers();

            Set m = ((GoSet)clone).getMembers();

            Set<GoMember> members = new CopyOnWriteArraySet<GoMember>(getMembers());
            for (GoMember goMember : members) {
                try {
                    m.add((goMember).clone());
                } catch (ClassCastException e) {
                    GameContext.log(0, "class " + goMember.getClass() + " is not a GoMember");
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



