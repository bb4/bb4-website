package com.becker.game.twoplayer.go.board;

import com.becker.common.Box;
import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import com.becker.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
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
public final class GoGroup extends GoSet implements IGoGroup
{
    /** a set of same color strings that are in the group. */
    private Set<GoString> members_;

    /** Responsible for determining how alive or dead the group is. */
    private GroupAnalyzer groupAnalyzer_;

    /**
     * constructor. Create a new group containing the specified string.
     * @param string make the group from this string.
     */
    public GoGroup(GoString string)
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
    public GoGroup( List<GoBoardPosition> stones )
    {
        commonInit();
        ownedByPlayer1_ = (stones.get(0)).getPiece().isOwnedByPlayer1();
        for (GoBoardPosition stone : stones) {
            assert stone.getPiece().isOwnedByPlayer1() == ownedByPlayer1_ :
                    "Stones in group must all be owned by the same player. stones=" + stones;
            //actually this is ok - sometimes happens legitimately
            //assert isFalse(stone.isVisited(), stone+" is marked visited in "+stones+" when it should not be.");
            GoString string = stone.getString();
            assert (string != null) : "There is no owning string for " + stone;
            if (!getMembers().contains(string)) {
                assert (ownedByPlayer1_ == string.isOwnedByPlayer1()) : string + "ownership not the same as " + this;
                //string.confirmOwnedByOnlyOnePlayer();
                getMembers().add(string);
            }
            string.setGroup(this);
        }       
    }

    private void commonInit()
    {
        groupAnalyzer_ = new GroupAnalyzer(this);
    }
    
    @Override
    protected void initializeMembers() {
        members_ = new LinkedHashSet<GoString>();
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
     */
    public void addMember(GoString string)
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
        groupAnalyzer_.invalidate();
    }

    /**
     * subtract the contents of a specified set of stones from this one.
     * It is an error if the specified set of stones is not a prpper subset.
     * Really we just remove the strings that own these stones.
     * @param stones the list of stones to subtract from this one
     */
    public void remove( List stones ) {
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
    }

    /**
     * remove a string from this group
     * @param string the string to remove from the group
     */
    public void remove( GoString string ) {
        if (string == null) {
            GameContext.log(2, "attempting to remove "+string+" string from group. "+this);
            return;
        }
        if (getMembers().isEmpty()) {
            GameContext.log(2, "attempting to remove "+string+" from already empty group.");
            return;
        }
        getMembers().remove( string );
       groupAnalyzer_.invalidate();
    }

    /**
     * Get the number of liberties that the group has.
     * @return the number of liberties that the group has
     */
    @Override
    public Set<GoBoardPosition> getLiberties(GoBoard board)
    {
        return groupAnalyzer_.getLiberties(board);
    }

    /**
     * Calculate the number of stones in the group.
     * @return number of stones in the group.
     */
    public int getNumStones()
    {
        return groupAnalyzer_.getNumStones();
    }
    
    public float calculateAbsoluteHealth( GoBoard board)
    {
        return groupAnalyzer_.calculateAbsoluteHealth(board);
    }
    
    public float calculateRelativeHealth( GoBoard board)
    {
        return groupAnalyzer_.calculateRelativeHealth(board);
    }
    
    public float getAbsoluteHealth() {
        return groupAnalyzer_.getAbsoluteHealth();
    }
    
    public float getRelativeHealth() {
        return groupAnalyzer_.getRelativeHealth();
    }

    /**
     * @return a list of the stones in this group.
     */
    public Set<GoBoardPosition> getStones()
    {
        Set<GoBoardPosition> stones = new HashSet<GoBoardPosition>(10);
        for (GoString string : getMembers()) {
            stones.addAll(string.getMembers());
        }
        return stones;
    }
    
    
    /**
     * @return  set of eyes currently identified for this group.
     */
    public Set<GoEye> getEyes(GoBoard board)
    {
        return groupAnalyzer_.getEyes(board);
    }

    /**
     * set the health of strings in this group
     * @param health the health of the group
     */
    public void updateTerritory( float health )
    {
        for (GoString string : getMembers()) {
            if (string.isUnconditionallyAlive()) {
                string.updateTerritory(ownedByPlayer1_ ? 1.0f : -1.0f);
            } else {
                string.updateTerritory(health);
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
        ((GoGroup)clone).groupAnalyzer_ = new GroupAnalyzer((GoGroup) clone);
        return clone;     
    }

    public boolean isValid() {
        return groupAnalyzer_.isValid();
    }

    /**
     * returns true if this group contains the specified stone
     * @param stone the stone to check for containment of
     * @return true if the stone is in this group
     */
    public  boolean containsStone(GoBoardPosition stone )
    {
        for (GoString string : getMembers()) {
            if (string.getMembers().contains(stone))
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
        for (GoBoardPosition stone : getStones()) {

            if (!stones.contains(stone))
                return false;
        }
        return true;
    }
    
    /**
     * See if the group contains all the stones that are in the specified list (it may contain others as well)
     * @param stones list of stones to check if same as those in this group
     * @return true if all the strings are in this group
     */
    private boolean contains(List<GoBoardPosition> stones)
    {
        for (GoBoardPosition stone : stones) {
            boolean found = false;
            for (GoString str : getMembers()) {
                if (str.getMembers().contains(stone)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    /**
     * @return true if the piece is an enemy of the set owner.
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
     * @return bounding box of set of stones/positions passed in
     */
    public Box findBoundingBox()  {
        int rMin = 100000; // something huge ( more than max rows)
        int rMax = 0;
        int cMin = 100000; // something huge ( more than max cols)
        int cMax = 0;

        // first determine a bounding rectangle for the group.

        for (GoString string : this.getMembers()) {

            for (GoBoardPosition stone : string.getMembers()) {
                int row = stone.getRow();
                int col = stone.getCol();
                if (row < rMin) rMin = row;
                if (row > rMax) rMax = row;
                if (col < cMin) cMin = col;
                if (col > cMax) cMax = col;
            }
        }

        return new Box(rMin, cMin, rMax, cMax);
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
     * @return string form.
     */
    private String toString( String newline )
    {
        StringBuffer sb = new StringBuffer( " GROUP {" + newline );
        Iterator it = getMembers().iterator();
        // print the member strings
        if ( it.hasNext() ) {
            GoString p = (GoString) it.next();
            sb.append("    ").append(p.toString());
        }
        while ( it.hasNext() ) {
            GoString p = (GoString) it.next();
            sb.append(',').append(newline).append("    ").append(p.toString());
        }
        sb.append(newline).append('}');
        Set<GoEye> eyes = getEyes(null);
        if (eyes!=null && !eyes.isEmpty())
            sb.append(eyes.toString()).append(newline);
        // make sure that the health and eyes are up to date
        //calculateHealth();
        sb.append("abs health=").append(Util.formatNumber(groupAnalyzer_.getAbsoluteHealth()));
        sb.append(" rel health=").append(Util.formatNumber(groupAnalyzer_.getRelativeHealth()));
        sb.append(" group Liberties=").append(groupAnalyzer_.getNumLiberties(null)).append('\n');
        return sb.toString();
    }
}
