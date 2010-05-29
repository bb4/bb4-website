package com.becker.game.twoplayer.go.board;

import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import com.becker.game.twoplayer.go.board.analysis.GroupHealthAnalyzer;
import com.becker.game.twoplayer.go.*;
import com.becker.common.*;
import com.becker.common.util.Util;
import com.becker.game.common.*;

import java.util.*;

/**
 *  A GoGroup is composed of a loosely connected set of one or more same color strings.
 *  A GoString by comparison, is composed of a strongly connected set of one or more same color stones.
 *  Groups may be connected by diagonals or one space jumps, or uncut knights moves, but not nikken tobi.
 *
 *  @see GoString
 *  @see GoBoard
 *  @author Barry Becker
 */
public final class GoGroup extends GoSet
{

    /** a set of the strings that are in the group. */
    private Set<GoString> members_;

    /** Responsible for determining how alive or dead the group is. */
    private GroupHealthAnalyzer healthAnalyzer_;

    /**
     * constructor. Create a new group containing the specified string.
     * @param string make the group from this string.
     */
    public GoGroup( GoString string )
    {
        commonInit();
        ownedByPlayer1_ = string.isOwnedByPlayer1();
         
        getMembers().add( string );
        string.setGroup( this );        
    }

    /**
     * Constructor. 
     * Create a new group containing the specified list of stones
     * Every stone in the list passed in must say that it is owned by this new group,
     * and every string must be wholy owned by this new group.
     * @param stones list of stones to create a group from.
     */
    public GoGroup( List stones )
    {
        commonInit( );
        ownedByPlayer1_ = ((GoBoardPosition) stones.get( 0 )).getPiece().isOwnedByPlayer1();
        Iterator it = stones.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition stone = (GoBoardPosition) it.next();
            assert stone.getPiece().isOwnedByPlayer1() == ownedByPlayer1_ : 
                "Stones in group must all be owned by the same player. stones="+ stones;
            //actually this is ok - sometimes happens legitimately
            //assert isFalse(stone.isVisited(), stone+" is marked visited in "+stones+" when it should not be.");
            GoString string = stone.getString();
            assert ( string != null ): "There is no owning string for "+stone;
            if ( !getMembers().contains( string ) ) {
                assert ( ownedByPlayer1_ == string.isOwnedByPlayer1()): string +"ownership not the same as " + this;
                //string.confirmOwnedByOnlyOnePlayer();
                getMembers().add( string );
            }
            string.setGroup( this );
        }       
    }

    private void commonInit()
    {
        healthAnalyzer_ = new GroupHealthAnalyzer(this);       
    }
    
    @Override
    protected void initializeMembers() {
        members_ = new HashSet<GoString>();
    }
    
    /**
     * @return  the hashSet containing the members
     */
    @Override
    public Set<GoString> getMembers() {
        return members_;
    }
    
    /**
     * add a string to the group.
     * @param string the string to add
     * @param board the owning board
     */
    public void addMember( GoString string, GoBoard board )
    {
        assert ( string.isOwnedByPlayer1() == ownedByPlayer1_):
                "strings added to a group must have like ownership. String="+string
                +". Group we are trying to add it to: "+this;
        if (getMembers().contains( string ) ) {
            assert ( string.getGroup() == this) :
                    "The " + this + " already contains the string, but the " + string
                    + " says its owning group is " + string.getGroup();
             return;
        }
        // remove it from the old group
        GoGroup oldGroup = string.getGroup();
        if ( oldGroup != null && oldGroup != this ) {
            oldGroup.remove( string );
        }
        string.setGroup( this );
        getMembers().add( string );
        healthAnalyzer_.breakEyeCache();       
    }


    /**
     * merge another group into this one.
     * @param group the group to merge into this one
     * @param board owning board
     */
    public void merge( GoGroup group, GoBoard board )
    {
        if ( this == group ) {
            // its a self join
            GameContext.log( 1, "Warning: attempting a self join." );
            return;
        }
        for (Object s : group.getMembers()) {
            GoString string = (GoString) s;
            string.setGroup(this);
            addMember(string, board);
        }
        group.removeAll();
        healthAnalyzer_.breakEyeCache();  
    }

    /**
     * subtract the contents of a specified set of stones from this one.
     * It is an error if the specified set of stones is not a prpper subset.
     * Really we just remove the strings that own these stones.
     * @param stones the list of stones to subtract from this one
     */
    public void remove( List stones )
    {
        // use a HashSet to avoid duplicate strings
        // otherwise we might try to remove the same string twice.
        Set<GoString> hsStrings = new HashSet<GoString>();

        Iterator it = stones.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            hsStrings.add( s.getString() );
        }
        it = hsStrings.iterator();
        while ( it.hasNext() ) {
            GoString str = (GoString) it.next();
            // remove the string associated with the stone
            remove( str );
        }
        healthAnalyzer_.breakEyeCache();     
    }

    /**
     * remove a string from this group
     * @param string the string to remove from the group
     */
    public void remove( GoString string )
    {
        if (string == null) {
            GameContext.log(2, "attempting to remove "+string+" string from group. "+this);
            return;
        }
        if (getMembers().isEmpty()) {
            GameContext.log(2, "attempting to remove "+string+" from already empty group.");
            return;
        }
        getMembers().remove( string );
       healthAnalyzer_.breakEyeCache();     
    }


    /**
     * Get the number of liberties that the group has.
     * @return the number of liberties that the group has
     */
    @Override
    public Set<GoBoardPosition> getLiberties(GoBoard board)
    {
        return healthAnalyzer_.getLiberties(board);
    }

    /**
     * Calculate the number of stones in the group.
     * @return number of stones in the group.
     */
    public int getNumStones()
    {
        return healthAnalyzer_.getNumStones();
    }
    
    public float calculateAbsoluteHealth( GoBoard board, GameProfiler profiler )
    {
        return healthAnalyzer_.calculateAbsoluteHealth(board, profiler);
    }
    
    public float calculateRelativeHealth( GoBoard board, GoProfiler profiler )
    {
        return healthAnalyzer_.calculateRelativeHealth(board,  profiler);
    }
    
    public float getAbsoluteHealth() {
        return healthAnalyzer_.getAbsoluteHealth();
    }
    public float getRelativeHealth() {
        return healthAnalyzer_.getRelativeHealth();
    }

    /**
     * @return a list of the stones in this group.
     */
    public Set<GoBoardPosition> getStones()
    {
        Set<GoBoardPosition> stones = new HashSet<GoBoardPosition>(10);
        Iterator<GoString> it = getMembers().iterator();
        while ( it.hasNext() ) {
            GoString string = it.next();
            stones.addAll( string.getMembers() );
        }
        // verify that none of these member stones are null.
        for (GoBoardPosition s: stones)
        {
            assert (s != null): "unexpected null stone in "+ stones;
        }
        return stones;
    }
    
    
    /**
     * @return  set of eyes currently identified for this group.
     */
    public Set<GoEye> getEyes(GoBoard board)
    {
        return healthAnalyzer_.getEyes(board);
    }


    /**
     * set the health of strings in this group
     * @param health the health of the group
     */
    public void updateTerritory( float health )
    {
        Iterator it = getMembers().iterator();
        while ( it.hasNext() ) {
            GoString string = (GoString) it.next();
            if (string.isUnconditionallyAlive()) {
                string.updateTerritory( ownedByPlayer1_? 1.0f : -1.0f );
            } else {
                string.updateTerritory( health );
            }
        }
    }


    /**
     * @return a deep copy of this GoGroup
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();
        ((GoGroup)clone).healthAnalyzer_ = new GroupHealthAnalyzer((GoGroup) clone);
        return clone;     
    }

    public boolean hasChanged() {
        return healthAnalyzer_.hasChanged();
    }

    /**
     * returns true if this group contains the specified stone
     * @param stone the stone to check for containment of
     * @return true if the stone is in this group
     */
    public  boolean containsStone(GoBoardPosition stone )
    {
        Iterator it = getMembers().iterator();
        while ( it.hasNext() ) {
            GoString string = (GoString) it.next();
            if ( string.getMembers().contains( stone ) )
                return true;
        }
        return false;
    }
    
    
    /**
     * @param stones list of stones to check if same as those in this group
     * @return true if this group exacly contains the list of stones and no others
     */
    public boolean exactlyContains(List<GoBoardPosition> stones)
    {
        if ( !contains(stones ) )
            return false;
        // make sure that every stone in the group is also in the list.
        // that way we are assured that they are the same.
        Iterator<GoBoardPosition> sIt = getStones().iterator();
        while ( sIt.hasNext() ) {
            GoBoardPosition s = sIt.next();
            if ( !stones.contains( s ) )
                return false;
        }
        return true;
    }
    
    /**
     * see if the group contains all the stones that are in the specified list (it may contain others as well)
     * @param stones list of stones to check if same as those in this group
     * @return true if all the strings are in this group
     */
    private boolean contains( List<GoBoardPosition> stones )
    {
        Iterator<GoBoardPosition> it = stones.iterator();
        while ( it.hasNext() ) {
            GoString s = (it.next()).getString();
            if ( !getMembers().contains( s ) )
                return false;
        }
        return true;
    }

    /**
     *  @return true if the piece is an enemy of the set owner.
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
    @Override
    protected boolean isEnemy( GoBoardPosition pos)
    {
        assert (pos.isOccupied());
        GoStone stone = (GoStone)pos.getPiece();
        boolean muchWeaker = GoBoardUtil.isStoneMuchWeaker(this, stone);

        return ( stone.isOwnedByPlayer1() != ownedByPlayer1_  && !muchWeaker);
    }
    
    

    /**
     * get the textual representation of the group.
     * @return string form
     */
    @Override
    public String toString()
    {
        return toString( "\n" );
    }

    /**
     * get the html representation of the group.
     * @return html form
     */
    public String toHtml()
    {
        return toString( "<br>" );
    }

    /**
     * @param newline string to use for the newline - eg "\n" or "<br>".
     */
    private String toString( String newline )
    {
        StringBuffer sb = new StringBuffer( " GROUP {" + newline );
        Iterator it = getMembers().iterator();
        // print the member strings
        if ( it.hasNext() ) {
            GoString p = (GoString) it.next();
            sb.append( "    " + p.toString() );
        }
        while ( it.hasNext() ) {
            GoString p = (GoString) it.next();
            sb.append( ',' + newline + "    "+ p.toString() );
        }
        sb.append( newline+ '}' );
        Set<GoEye> eyes = getEyes(null);
        if (eyes!=null && !eyes.isEmpty())
            sb.append(eyes.toString() +newline);
        // make sure that the health and eyes are up to date
        //calculateHealth();
        sb.append( "abs health=" + Util.formatNumber(healthAnalyzer_.getAbsoluteHealth()) );
        sb.append( " rel health=" + Util.formatNumber(healthAnalyzer_.getRelativeHealth()));
        sb.append( " group Liberties=" + healthAnalyzer_.getNumLiberties() + '\n' );
        return sb.toString();
    }

}



