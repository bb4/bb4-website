package com.becker.game.twoplayer.go.board;

import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
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

    /** a set of the stones that are in the string */
    private Set<GoBoardPosition> members_;
    
    /** The group to which this string belongs. */
    protected GoGroup group_;

    /** Used by Benson's algorithm to help determine unconditional life. */
    private Set<GoString> neighbors_;
    
    /** If true, then we are an eye in an unconditionally alive group (according to Benson's algorithm). */
    private boolean unconditionallyAlive_;
    
    /** Keep track of number of liberties instead of computing each time (for performance). */
    private Set<GoBoardPosition> liberties_;

    /**
     * Constructor. Create a new string containing the specified stone.
     */
    public GoString( GoBoardPosition stone, GoBoard board )
    {
        assert ( stone.isOccupied() );
        ownedByPlayer1_ = stone.getPiece().isOwnedByPlayer1();
        getMembers().add( stone );
        stone.setString( this );
        group_ = null;
        initializeLiberties(board);
    }

    /**
     * constructor. Create a new string containing the specified list of stones
     */
    public GoString( List stones, GoBoard board )
    {
        assert (stones != null && stones.size() > 0): "Tried to create list from empty list";
        GoStone stone =  (GoStone)((BoardPosition) stones.get( 0 )).getPiece();
        // GoEye constructor calls this method. For eyes the stone is null.
        if (stone != null)
            ownedByPlayer1_ = stone.isOwnedByPlayer1();
        for (Object stone1 : stones) {
            GoBoardPosition pos = (GoBoardPosition) stone1;
            addMemberInternal(pos, board);
        }
        initializeLiberties(board);
    }
    
    /**
     * @return  the hashSet containing the members
     */
    @Override
    public Set<GoBoardPosition> getMembers() {
        return members_;
    }
    
    @Override
    protected void initializeMembers() {
        members_ = new HashSet<GoBoardPosition>();
    }

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
    public void addMember( GoBoardPosition stone, GoBoard board)
    {
        addMemberInternal(stone, board);
        initializeLiberties(board);
    }

    /**
     * add a stone to the string
     */
    protected void addMemberInternal(GoBoardPosition stone, GoBoard board)
    {
        assert ( stone.isOccupied()): "trying to add empty space to string. stone=" + stone ;
        assert ( stone.getPiece().isOwnedByPlayer1() == ownedByPlayer1_):
                "stones added to a string must have like ownership";
        if ( getMembers().contains( stone ) ) {
            // this case can happen sometimes.
            // For example if the new stone completes a loop and self-joins the string to itself
            //GameContext.log( 2, "Warning: the string, " + this + ", already contains the stone " + stone );
            assert  (stone.getString() == null) || (this == stone.getString()):
                    "bad stone "+stone+" or bad owning string "+ stone.getString();
        }
        // if the stone is already owned by another string, we need to verify that that other string has given it up.
        if (stone.getString() != null) {
            stone.getString().remove(stone, board);
            //: stone +" is already owned by another string: "+ stone.getString();
        }

        stone.setString( this );
        getMembers().add( stone );
    }

    /**
     * merge a string into this one
     */
    public final void merge( GoString string, GoBoard board )
    {
        if ( this == string ) {
            GameContext.log( 1, "Warning: merging " + string + " into itself" );
            // its a self join
            return;
        }

        Set<GoBoardPosition> stringMembers = new HashSet<GoBoardPosition>();
        stringMembers.addAll(string.getMembers());
        // must remove these after iterating otherwise we get a ConcurrentModificationException
        string.getGroup().remove(string);
        string.removeAll();

        Iterator it = stringMembers.iterator();
        GoBoardPosition stone;
        while ( it.hasNext() ) {
            stone = (GoBoardPosition) it.next();
            GoString myString = stone.getString();
            if (myString != null && myString != string) {
                myString.remove(stone, board);
            }
            stone.setString(null);
            addMemberInternal( stone, board);
        }
        stringMembers.clear();
        initializeLiberties(board);
    }

    /**
     * remove a stone from this string.
     * What happens if the string gets split as a result?
     * The caller should handle this case since we cannot create new strings here.
     */
    public final void remove( GoBoardPosition stone, GoBoard board )
    {
        boolean removed = getMembers().remove( stone );
        assert (removed) : "failed to remove "+stone+" from"+ this;
        stone.setString(null);
        if ( getMembers().isEmpty()) {
            group_.remove( this );
        }
        initializeLiberties(board);
    }

    /**
     * remove a set (List) of stones from this string.
     * Its an error if the argument is not a proper substring.
     * @param stones stones to remove (error if not a proper substring)
     */
    public final void remove( Collection stones, GoBoard board )
    {
        for (Object stone1 : stones) {
            GoBoardPosition stone = (GoBoardPosition) stone1;
            remove(stone, board);
        }
        initializeLiberties(board);
        assert ( size() > 0 );
    }


    public int getNumLiberties(GoBoard board) {
        return getLiberties(board).size();
    }

    /**
     * return the set of liberty positions that the string has
     * @param board
     */
    @Override
    public final Set<GoBoardPosition> getLiberties(GoBoard board)
    {
        return liberties_;
    }

    private Set initializeLiberties(GoBoard board) {
        liberties_ = new HashSet<GoBoardPosition>();

        for (GoBoardPosition stone : getMembers()) {
            addLiberties(stone, liberties_, board);
        }
        return liberties_;
    }

    /**
     * If the libertyPos is occupied, then we subract this liberty, else add it.
     * @param libertyPos  position to check for liberty
     */
    public void changedLiberty(GoBoardPosition libertyPos) {
         if (libertyPos.isOccupied()) {
             liberties_.remove(libertyPos);
             // hitting if showing game tree perhaps because already removed.
             //assert removed : "could not remove " + libertyPos +" from "+liberties_;  
         } else {
             assert (!liberties_.contains(libertyPos)) : this + " already had " + libertyPos +" as a liberty and we were not expecting that. Liberties_=" + liberties_;
             liberties_.add(libertyPos);
             if (getMembers().size() == 1)
                 assert(liberties_.size() <= 4) :this +" has too many liberties for one stone :"+ liberties_ +  " just added :"+libertyPos;
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

    private static void addLiberty( BoardPosition libertySpace, Set<GoBoardPosition> liberties )
    {
        // this assumes a HashSet will not allow you to add the same object twice (no dupes)
        if ( libertySpace.isUnoccupied() )
            liberties.add( (GoBoardPosition)libertySpace );
    }

    /** set the health of members equal to the specified value
     * @param health range = [0-1]
     */
    public final void updateTerritory( float health )
    {
        for (GoBoardPosition pos : getMembers()) {
            GoStone stone = (GoStone) pos.getPiece();
            stone.setHealth(health);
        }
    }


    /**
     *  @return true if the piece at the specified position is an enemy of the string owner
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
    @Override
    public boolean isEnemy( GoBoardPosition pos)
    {
        assert (group_ != null): "group for "+this+" is null";
        assert (pos.isOccupied()): "pos not occupied: ="+pos;
        GoStone stone = (GoStone)pos.getPiece();
        boolean stoneMuchWeaker = GoBoardUtil.isStoneMuchWeaker(getGroup(), stone);

        assert (getGroup().isOwnedByPlayer1() == this.isOwnedByPlayer1()): getGroup()+" string=" + this;
        return ((stone.isOwnedByPlayer1() != this.isOwnedByPlayer1() && !stoneMuchWeaker));
    }

    /**
     * make sure all the stones in the string are unvisited
     */
    public final void unvisit()
    {
        for (GoBoardPosition stone : getMembers()) {
            stone.setVisited(false);
        }
    }

    String getPrintPrefix()
    {
        return " STRING(";
    }

    /**
     * @return  a string representation for the string.
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer( getPrintPrefix() );
        Iterator it = getMembers().iterator();
        if ( it.hasNext() ) {
            GoBoardPosition p = (GoBoardPosition) it.next();
            sb.append( p.toString() );
        }
        while ( it.hasNext() ) {
            GoBoardPosition p = (GoBoardPosition) it.next();
            sb.append( ", " );
            sb.append( p.toString() );
        }
        sb.append( ')' );
        return sb.toString();
    }

    ////////////////// debugging methods //////////////////////////////
    /**
     * return true if any of the stones in the string are blank (should never happen)
     */
    public final boolean areAnyBlank()
    {
        for (GoBoardPosition stone : getMembers()) {
            if (stone.isUnoccupied())
                return true;
        }
        return false;
    }

    /** confirm that all the stones in the string have the same ownership as the string
     *  we throw and error if this is not true
     */
    public final void confirmValid( GoBoard b )
    {
        Iterator it = getMembers().iterator();
        if ( it.hasNext() ) {
            List list = b.findStringFromInitialPosition( (GoBoardPosition) it.next(), true );
            // confirm that all member_ stones are in the string
            while ( it.hasNext() ) {
                GoBoardPosition s = (GoBoardPosition) it.next();
                assert ( list.contains( s )): list + " does not contain " + s + ". getMembers() =" + getMembers() ;
            }
        }
    }

    /** confirm that all the stones in the string have the same ownershp as the string.
     *  we throw and error if this is not true
     */
    public final void confirmOwnedByOnlyOnePlayer()
    {
        Iterator it = getMembers().iterator();

        if ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            GoStone stone = (GoStone)s.getPiece();
            assert ( stone.isOwnedByPlayer1() == this.isOwnedByPlayer1()) :
                    stone + " does not have the same owner as " + this;
        }
    }


    /**
     * @return neighbor eyes if string, or neighboring strings if we are an eye.
     */
    public Set<GoString> getNeighbors() {
        return neighbors_;
    }

    /**
     * Set our neigbors (eyes if string, or neighboring strings if we are an eye).
     */
    public void setNbrs(Set<GoString> nbrEyes) {
        neighbors_ = nbrEyes;
    }

    /**
     * @return true if unconditionally alive.
     */
    public boolean isUnconditionallyAlive() {
        return unconditionallyAlive_;
    }

    public void setUnconditionallyAlive(boolean unconditionallyAlive) {
        this.unconditionallyAlive_ = unconditionallyAlive;
    }
}



