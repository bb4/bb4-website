package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.analysis.StringLibertyAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;

import java.util.*;

/**
 *  A GoString is composed of a strongly connected set of one or more same color stones.
 *  By strongly connected I mean nobi connections only.
 *  A GoGroup by comparison, is composed of a set of one or more same color strings.
 *  Groups may be connected by diagonals, or ikken tobi, or kogeima (knight's move).
 *
 *  @see GoGroup
 *  @see com.becker.game.twoplayer.go.board.GoBoard
 *  @author Barry Becker
 */
public class GoString extends GoSet implements IGoString
{
    /** a set of the stones that are in the string */
    private GoBoardPositionSet members_;
    
    /** The group to which this string belongs. */
    protected GoGroup group_;
    
    /** If true, then we are an eye in an unconditionally alive group (according to Benson's algorithm). */
    private boolean unconditionallyAlive_;

    /** Keep track of number of liberties instead of computing each time (for performance). */
    private StringLibertyAnalyzer libertyAnalyzer_;

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
        libertyAnalyzer_ = new StringLibertyAnalyzer(board, getMembers());
    }

    /**
     * Constructor.
     * Create a new string containing the specified list of stones
     */
    public GoString( GoBoardPositionList stones, GoBoard board )
    {
        assert (stones != null && stones.size() > 0): "Tried to create list from empty list";
        GoStone stone =  (GoStone)((BoardPosition) stones.get(0)).getPiece();
        // GoEye constructor calls this method. For eyes the stone is null.
        if (stone != null)
            ownedByPlayer1_ = stone.isOwnedByPlayer1();
        for (GoBoardPosition pos : stones) {
            addMemberInternal(pos, board);
        }
        libertyAnalyzer_ = new StringLibertyAnalyzer(board, getMembers());
    }
    
    /**
     * @return  the set of member positions
     */
    @Override
    public GoBoardPositionSet getMembers() {
        return members_;
    }
    
    @Override
    protected void initializeMembers() {
        members_ = new GoBoardPositionSet();
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
        libertyAnalyzer_ = new StringLibertyAnalyzer(board, getMembers());
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

        GoBoardPositionSet stringMembers = new GoBoardPositionSet();
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
        libertyAnalyzer_ = new StringLibertyAnalyzer(board, getMembers());
    }

    /**
     * remove a stone from this string.
     * What happens if the string gets split as a result?
     * The caller should handle this case since we cannot create new strings here.
     */
    public final void remove( GoBoardPosition stone, GoBoard board )
    {
        removeInternal(stone);
        libertyAnalyzer_ = new StringLibertyAnalyzer(board, getMembers());
    }

    /**
     * remove a set (List) of stones from this string.
     * Its an error if the argument is not a proper substring.
     * @param stones stones to remove (error if not a proper substring)
     */
    public final void remove( Collection<GoBoardPosition> stones, GoBoard board )
    {
        for (GoBoardPosition stone : stones) {
            removeInternal(stone);
        }
        libertyAnalyzer_ = new StringLibertyAnalyzer(board, getMembers());
        assert ( size() > 0 );
    }

    protected void removeInternal(GoBoardPosition stone ) {
       boolean removed = getMembers().remove( stone );
       assert (removed) : "failed to remove "+stone+" from"+ this;
       stone.setString(null);
       if ( getMembers().isEmpty()) {
           group_.remove( this );
       }
    }

    public int getNumLiberties(GoBoard board) {
        return getLiberties(board).size();
    }

    /**
     * return the set of liberty positions that the string has
     * @param board
     */
    @Override
    public final GoBoardPositionSet getLiberties(GoBoard board)
    {
        return libertyAnalyzer_.getLiberties();
    }

    /**
     * If the libertyPos is occupied, then we subract this liberty, else add it.
     * @param libertyPos  position to check for liberty
     */
    public void changedLiberty(GoBoardPosition libertyPos) {
        libertyAnalyzer_.changedLiberty(libertyPos);
    }

    /**
     * Set the health of members equal to the specified value
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
    public boolean isEnemy(GoBoardPosition pos)
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
        sb.append(" UA=").append(isUnconditionallyAlive()).append(" ");
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


    /**
     * @return true if unconditionally alive.
     */
    public boolean isUnconditionallyAlive() {
        return unconditionallyAlive_;
    }

    public void setUnconditionallyAlive(boolean unconditionallyAlive) {
        this.unconditionallyAlive_ = unconditionallyAlive;
    }
    
    ////////////////// debugging/consistency check methods //////////////////////////////
    /**
     * @return true if any of the stones in the string are blank (should never happen)
     */
    public final boolean areAnyBlank()
    {
        for (GoBoardPosition stone : getMembers()) {
            if (stone.isUnoccupied())
                return true;
        }
        return false;
    }

    /**
     * Confirm that all the stones in the string have the same ownership as the string
     * we throw and error if this is not true
     */
    public final void confirmValid( GoBoard b )
    {
        Iterator<GoBoardPosition> it = getMembers().iterator();
        NeighborAnalyzer na = new NeighborAnalyzer(b);
        if ( it.hasNext() ) {
            List list = na.findStringFromInitialPosition(it.next(), true);
            // confirm that all member_ stones are in the string
            while ( it.hasNext() ) {
                GoBoardPosition s = it.next();
                assert ( list.contains( s )): list + " does not contain " + s + ". getMembers() =" + getMembers() ;
            }
        }
    }
}



