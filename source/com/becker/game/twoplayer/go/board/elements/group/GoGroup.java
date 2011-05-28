package com.becker.game.twoplayer.go.board.elements.group;

import com.becker.common.geometry.Box;
import com.becker.common.format.FormatUtil;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.becker.game.twoplayer.go.board.elements.GoSet;
import com.becker.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionList;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.elements.position.GoStone;
import com.becker.game.twoplayer.go.board.elements.string.GoStringSet;
import com.becker.game.twoplayer.go.board.elements.string.IGoString;

import java.util.Iterator;

import static com.becker.game.twoplayer.go.GoController.USE_RELATIVE_GROUP_SCORING;

/**
 *  A GoGroup is composed of a loosely connected set of one or more same color strings.
 *  A GoString by comparison, is composed of a strongly connected set of one or more same color stones.
 *  Groups may be connected by diagonals or one space jumps, or uncut knights moves, but not nikken tobi.
 *
 *  @author Barry Becker
 */

public final class GoGroup extends GoSet
                           implements IGoGroup {

    /** a set of same color strings that are in the group. */
    private GoStringSet members_;

    /** Responsible for determining how alive or dead the group is. */
    private GroupAnalyzer groupAnalyzer_;

    /**
     * Constructor. Create a new group containing the specified string.
     * @param string make the group from this string.
     */
    public GoGroup(IGoString string) {
        commonInit();
        ownedByPlayer1_ = string.isOwnedByPlayer1();
         
        getMembers().add( string );
        string.setGroup( this );        
    }

    /**
     * Constructor. 
     * Create a new group containing the specified list of stones
     * Every stone in the list passed in must say that it is owned by this new group,
     * and every string must be wholly owned by this new group.
     * @param stones list of stones to create a group from.
     */
    public GoGroup( GoBoardPositionList stones ) {
        commonInit();
        ownedByPlayer1_ = (stones.getFirst()).getPiece().isOwnedByPlayer1();
        for (GoBoardPosition stone : stones) {
            assimilateStone(stones, stone);
        }       
    }

    private void assimilateStone(GoBoardPositionList stones, GoBoardPosition stone) {
        assert stone.getPiece().isOwnedByPlayer1() == ownedByPlayer1_ :
                "Stones in group must all be owned by the same player. stones=" + stones;
        // actually this is ok - sometimes happens legitimately
        // assert isFalse(stone.isVisited(), stone+" is marked visited in "+stones+" when it should not be.");
        IGoString string = stone.getString();
        assert (string != null) : "There is no owning string for " + stone;
        if (!getMembers().contains(string)) {
            assert (ownedByPlayer1_ == string.isOwnedByPlayer1()) : string + "ownership not the same as " + this;
            //string.confirmOwnedByOnlyOnePlayer();
            getMembers().add(string);
        }
        string.setGroup(this);
    }

    private void commonInit()  {
        groupAnalyzer_ = new GroupAnalyzer(this);
    }

    /**
     * Must be ordered (i.e. LinkedHashSet
     */
    @Override
    protected void initializeMembers() {
        members_ = new GoStringSet();
    }
    
    /**
     * @return  the hashSet containing the members
     */
    @Override
    public GoStringSet getMembers() {
        return members_;
    }

    /**
     * make sure all the stones in the string are unvisited or visited, as specified
     */
    public void setVisited(boolean visited) {
        for (IGoString str : getMembers()) {
            str.setVisited(visited);
        }
    }
    
    /**
     * add a string to the group.
     * @param string the string to add
     */
    public void addMember(IGoString string) {

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
        IGoGroup oldGroup = string.getGroup();
        if ( oldGroup != null && oldGroup != this ) {
            oldGroup.remove( string );
        }
        string.setGroup( this );
        getMembers().add( string );
        groupAnalyzer_.invalidate();
    }

    /**
     * remove a string from this group
     * @param string the string to remove from the group
     */
    public void remove(IGoString string) {
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
    public GoBoardPositionSet getLiberties(GoBoard board) {
        return groupAnalyzer_.getLiberties(board);
    }

    public int getNumLiberties(GoBoard board) {
        return getLiberties(board).size();
    }

    /**
     * Calculate the number of stones in the group.
     * @return number of stones in the group.
     */
    public int getNumStones() {
        return groupAnalyzer_.getNumStones();
    }
    
    public float calculateAbsoluteHealth( GoBoard board) {
        return groupAnalyzer_.calculateAbsoluteHealth(board);
    }
    
    public float calculateRelativeHealth( GoBoard board) {
        return groupAnalyzer_.calculateRelativeHealth(board);
    }
    
    public float getAbsoluteHealth() {
        return groupAnalyzer_.getAbsoluteHealth();
    }

    /**
     * We try to use the cached relative health value if we can.
     * @param board needed to calculate new value if not cached
     * @param useCachedValue if true, just return the cached value instead of checking for validity.
     * @return relative health
     */
    public float getRelativeHealth(GoBoard board, boolean useCachedValue) {
        if (!USE_RELATIVE_GROUP_SCORING) {
            return getAbsoluteHealth();
        }
        if (groupAnalyzer_.isValid() || useCachedValue) {
            if (!groupAnalyzer_.isValid())
                GameContext.log(3, "using cached relative health when not valid");
            return groupAnalyzer_.getRelativeHealth();
        }
        GameContext.log(0, "stale abs health. recalculating relative health");
        return groupAnalyzer_.calculateRelativeHealth(board);
    }

    /**
     * @return a list of the stones in this group.
     */
    public GoBoardPositionSet getStones() {
        GoBoardPositionSet stones = new GoBoardPositionSet();
        for (IGoString string : getMembers()) {
            stones.addAll(string.getMembers());
        }
        return stones;
    }
    
    /**
     * @return  set of eyes currently identified for this group.
     */
    public GoEyeSet getEyes(GoBoard board) {
        return groupAnalyzer_.getEyes(board);
    }

    /**
     * set the health of strings in this group
     * @param health the health of the group
     */
    public void updateTerritory( float health ) {
        for (IGoString string : getMembers()) {
            if (string.isUnconditionallyAlive()) {
                string.updateTerritory(ownedByPlayer1_ ? 1.0f : -1.0f);
            } else {
                string.updateTerritory(health);
            }
        }
    }

    public boolean isValid() {
        return groupAnalyzer_.isValid();
    }

    /**
     * returns true if this group contains the specified stone
     * @param stone the stone to check for containment of
     * @return true if the stone is in this group
     */
    public boolean containsStone(GoBoardPosition stone ) {
        for (IGoString string : getMembers()) {
            if (string.getMembers().contains(stone))
                return true;
        }
        return false;
    }

    /**
     * @return true if the piece is an enemy of the set owner.
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
    @Override
    public boolean isEnemy( GoBoardPosition pos) {
        assert (pos.isOccupied());
        GoStone stone = (GoStone)pos.getPiece();
        boolean muchWeaker = isStoneMuchWeaker(stone);

        return ( stone.isOwnedByPlayer1() != ownedByPlayer1_  && !muchWeaker);
    }

    /**
     * @return bounding box of set of stones/positions passed in
     */
    public Box findBoundingBox()  {
        int rMin = 10000; // something huge ( more than max rows)
        int rMax = 0;
        int cMin = 10000; // something huge ( more than max cols)
        int cMax = 0;

        // first determine a bounding rectangle for the group.
        for (IGoString string : this.getMembers()) {

            for (GoBoardPosition stone : string.getMembers()) {
                int row = stone.getRow();
                int col = stone.getCol();
                if (row < rMin) rMin = row;
                if (row > rMax) rMax = row;
                if (col < cMin) cMin = col;
                if (col > cMax) cMax = col;
            }
        }
        return (rMin > rMax) ? new Box(0, 0, 0, 0) : new Box(rMin, cMin, rMax, cMax);
    }

    /**
     * get the textual representation of the group.
     * @return string form
     */
    @Override
    public String toString() {
        return toString( "\n" );
    }

    /**
     * get the html representation of the group.
     * @return html form
     */
    public String toHtml() {
        return toString( "<br>" );
    }

    /**
     * @param newline string to use for the newline - eg "\n" or "<br>".
     * @return string form.
     */
    private String toString( String newline ) {

        StringBuilder sb = new StringBuilder( " GROUP {" + newline );
        Iterator it = getMembers().iterator();
        // print the member strings
        if ( it.hasNext() ) {
            IGoString p = (IGoString) it.next();
            sb.append("    ").append(p.toString());
        }
        while ( it.hasNext() ) {
            IGoString p = (IGoString) it.next();
            sb.append(',').append(newline).append("    ").append(p.toString());
        }
        sb.append(newline).append('}');
        GoEyeSet eyes = getEyes(null);
        if (eyes!=null && !eyes.isEmpty())
            sb.append(eyes.toString()).append(newline);
        // make sure that the health and eyes are up to date
        //calculateHealth();
        sb.append("abs health=").append(FormatUtil.formatNumber(groupAnalyzer_.getAbsoluteHealth()));
        sb.append(" rel health=").append(FormatUtil.formatNumber(groupAnalyzer_.getRelativeHealth()));
        sb.append(" group Liberties=").append(groupAnalyzer_.getNumLiberties(null)).append('\n');
        return sb.toString();
    }

    /**
     * @return true if the stone is much weaker than the group
     */
    public boolean isStoneMuchWeaker(GoStone stone) {
        return groupAnalyzer_.isStoneMuchWeakerThanGroup(stone);
    }
}
