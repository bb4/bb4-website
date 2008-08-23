package com.becker.game.twoplayer.go;

import com.becker.common.*;
import com.becker.common.util.Util;
import com.becker.game.common.*;

import java.util.*;

/**
 *  A GoGroup is composed of a loosely connected set of one or more same color strings.
 *  A GoString by comparison, is composed of a strongly connected set of one or more same color stones.
 *  A GoArmy is a loosely coupled set of Groups
 *  Groups may be connected by diagonals or one space jumps, or uncut knights moves, but not nikken tobi.
 *
 *  @see GoString
 *  @see GoArmy
 *  @see GoBoard
 *  @author Barry Becker
 */
public final class GoGroup extends GoSet
{

    // the army to which this group belongs
    private GoArmy army_;

    /**
     * need 2 true eyes to be unconditionally alive.
     * this is a set of GoEyes which give the spaces in the eye
     * it includes eyes of all types including false eyes
     * false-eye: any string of spaces or dead enemy stones for which one is a false eye.
     */
    private Set<GoEye> eyes_;
    /**
     *measure of how easily the group can make 2 eyes.
     */
    private float eyePotential_;
    
    /**
     * This is a number between -1 and 1 that indicates how likely the group is to live
     * independent of the health of the stones around it.
     * all kinds of factors can contribute to the health of a group.
     * Local search should be used to make this as accurate as possible.
     * If the health is 1.0 then the group has at least 2 eyes and is unconditionally alive.
     * If the health is -1.0 then there is no way to save the group even if you could
     * play 2 times in a row.
     * Unconditional life means the group cannot be killed no matter how many times the opponent plays.
     * A score of near 0 indicates it is very uncertain whether the group will live or die.
     */
    private float absoluteHealth_ = 0;
    /**
     * This measure of health is also between -1 and 1 but it should be more
     * accurate because it takes into account the health of neighboring enemy groups as well.
     * it uses the absolute health as a base and exaggerates it base on the relative strength of the
     * weakest enemy nbr group.
      */
    private float relativeHealth_;

    /**
     * Set this to true when the eyes need to be recalculated.
     * It must be set to true if the group has changed in any way.
      */
    private boolean changed_ = true;

    /**
     * This is the cached number of liberties.
     * It updates whenever something has changed.
     */
    private Set cachedLiberties_;

    private int cachedNumStonesInGroup_;


    /**
     * constructor. Create a new group containing the specified string.
     * @param string make the group from this string.
     */
    public GoGroup( GoString string )
    {
        ownedByPlayer1_ = string.isOwnedByPlayer1();
        members_.add( string );
        string.setGroup( this );
        commonInit();
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
        ownedByPlayer1_ = ((GoBoardPosition) stones.get( 0 )).getPiece().isOwnedByPlayer1();
        Iterator it = stones.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition stone = (GoBoardPosition) it.next();
            //actually this is ok - sometimes happens legitimately
            //assert isFalse(stone.isVisited(), stone+" is marked visited in "+stones+" when it should not be.");
            GoString string = stone.getString();
            assert ( string != null ): "There is no owning string for "+stone;
            if ( !members_.contains( string ) ) {
                assert ( ownedByPlayer1_ == string.isOwnedByPlayer1()): string +"ownership not the same as " + this;
                //string.confirmOwnedByOnlyOnePlayer();
                members_.add( string );
            }
            string.setGroup( this );
        }
        commonInit( );
    }

    /**
     * common initialization
     */
    private void commonInit()
    {
        eyes_ = new LinkedHashSet<GoEye>();
        changed_ = true;
    }

    /**
     * set/get the army
     * @param army the owning army
     */
    public void setArmy( GoArmy army )
    {
        army_ = army;
    }

    /**
     * get the owning army for this group
     * @return the owning army
     */
    public GoArmy getArmy()
    {
        return army_;
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
        if ( members_.contains( string ) ) {
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
        members_.add( string );
        changed_ = true;
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
        clearEyes();
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
        Set hsStrings = new HashSet();

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
        clearEyes();
        changed_ = true;
    }

    /**
     * remove a string from this group
     * @param string the string to remove from the group
     */
    public void remove( GoString string )
    {
        clearEyes();
        if (string == null) {
            GameContext.log(2, "attempting to remove "+string+" string from group. "+this);
            return;
        }
        if (members_.isEmpty()) {
            GameContext.log(2, "attempting to remove "+string+" from already empty group.");
            return;
        }
        members_.remove( string );
        changed_ = true;
    }


    /**
     * Get the number of liberties that the group has.
     * @return the number of liberties that the group has
     */
    public Set getLiberties(GoBoard board)
    {
        if (!changed_) {
             return cachedLiberties_;
        }
        Set liberties = new HashSet();
        for (Object str : members_) {
            GoString string = (GoString) str;
            liberties.addAll(string.getLiberties(board));
        }
        cachedLiberties_ = liberties;
        return liberties;
    }

    /**
     * Calculate the number of stones in the group.
     * @return number of stones in the group.
     */
    public int getNumStones()
    {

        if (!changed_) {
            return cachedNumStonesInGroup_;
        }
        int numStones = 0;
        Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            GoString str = (GoString) it.next();
            numStones += str.size();
        }
        cachedNumStonesInGroup_ = numStones;
        return numStones;
    }

    /**
     * @return a list of the stones in this group.
     */
    public Set getStones()
    {
        Set stones = new HashSet(10);
        Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            GoString string = (GoString) it.next();
            stones.addAll( string.getMembers() );
        }
        // verify that none of these member stones are null.
        for (Object s: stones)
        {
            assert (s != null): "unexpected null stone in "+ stones;
        }
        return stones;
    }

    //------------------------------- methods related to updating eyes and group health --------------

    /**
     * compute how many eyes (connected internal blank areas) this group has.
     * the eyes are either false eyes or true (or big or territorial) eyes.
     * Also update eyePotential (a measure of how good the groups ability to make 2 eyes(.
     * This method is expensive. That is why the 2 things it computes (eyes and eyePotential) are cached.
     * @param board the owning board
     * @return 
     */
    private void updateEyes( GoBoard board )
    {
        if (!changed_)
            return;

        // list of lists of spaces to unvisit at the end
        List lists = new ArrayList();

        Box box = GoBoardUtil.findBoundingBox(members_);

        // forget what we had computed before about the eyes.
        clearEyes();

        // next eliminate all the stones and spaces that are in the bounding rect,
        // but not in the group. We do this by marching around the perimeter cutting out
        // the strings of empty or opponent spaces that do not belong.
        // Note : we do not go all the way to the edge. If the border of a group includes an edge of the board,
        // then empty spaces there are most likely eyes (but not necessarily).
        int rMin = box.getMinRow();
        int rMax = box.getMaxRow();
        int cMin = box.getMinCol();
        int cMax = box.getMaxCol();
        if ( box.getMinCol() > 1 ) {
            for ( int r = rMin; r <= rMax; r++ )
                excludeSeed( (GoBoardPosition) board.getPosition( r, cMin ),
                        ownedByPlayer1_, board, lists, box );
        }
        if ( box.getMaxCol() < board.getNumCols() ) {
            for ( int r = rMin; r <= rMax; r++ )
                excludeSeed( (GoBoardPosition) board.getPosition( r, cMax ),
                        ownedByPlayer1_, board, lists, box );
        }
        if ( rMin > 1 ) {
            for ( int c = cMin; c <= cMax; c++ )
                excludeSeed( (GoBoardPosition) board.getPosition( rMin, c ),
                        ownedByPlayer1_, board, lists, box );
        }
        if ( rMax < board.getNumRows() ) {
            for ( int c = cMin; c <= cMax; c++ )
                excludeSeed( (GoBoardPosition) board.getPosition( rMax, c ),
                        ownedByPlayer1_, board, lists, box );
        }

        // Now do a paint fill on each of the empty unvisited spaces left.
        // Most of these remaining empty spaces are connected to an eye of some type.
        // There will be some that fill spaces between black and white stones.
        // Don't count these as eyes unless the stones of the opposite color are much weaker -
        // in which case they are assumed dead and hence part of the eye.
        for ( int r = rMin; r <= rMax; r++ ) {
            for ( int c = cMin; c <= cMax; c++ ) {
                // if the empty space is already marked as being an eye, skip
                GoBoardPosition space = (GoBoardPosition) board.getPosition( r, c );
                if ( !space.isVisited() && space.isUnoccupied() && !space.isInEye() ) {
                    List eyeSpaces =
                            board.findStringFromInitialPosition( space, ownedByPlayer1_,
                                                                 false, NeighborType.NOT_FRIEND,
                                                                 box );
                    lists.add( eyeSpaces );
                    // make sure this is a real eye.
                    // this method checks that opponent stones don't border it.
                    if ( confirmEye( eyeSpaces) ) {
                        GoEye eye =  new GoEye( eyeSpaces, board, this );
                        eyes_.add( eye );
                    }
                    else {
                        GoBoardUtil.debugPrintList(3, "This list of stones was rejected as being an eye: ", eyeSpaces);
                    }
                }
            }
        }
        GoBoardUtil.unvisitPositionsInLists( lists );
        eyePotential_ = calculateEyePotential(box, board);
    }
    
    /**
     *@return eyePtoential - a measure of how easily this group can make 2 eyes (0 - 2; 2 meaning has 2 eyes).
     */
    private float calculateEyePotential(Box bbox, GoBoard board) {
        int numRows = board.getNumRows();
        int numCols = board.getNumCols();
        // Expand the bbox by one in all directions.
        // if the bbox is within one space of the edge, extend it all the way to the edge.
        // loop through the rows and columns calculating distances from group stones 
        // to the edge and to other stones.
        // if there is a (mostly living) enemy stone in the run, don't count the run.
        
        bbox.expandGloballyBy(1, numRows, numCols);
        bbox.expandBordersToEdge(1, numRows, numCols);
        float totalPotential = 0;
        String color = this.isOwnedByPlayer1()? "black" : "white";
        //System.out.println(color + ":==== In calc potential box="+ bbox);
         
        // make sure that every internal enemy stone is really an enemy and not just dead.
        // compare it with one of the group strings.
        GoString gs = ((GoString)this.getMembers().iterator().next());
            
        // first look at the row runs
        for ( int r = bbox.getMinRow(); r <= bbox.getMaxRow(); r++ ) {     
            //System.out.println("row run = "+ r);
            totalPotential += 
                    getRowColPotential(r, bbox.getMinCol(), 0, 1, bbox.getMaxRow(), bbox.getMaxCol(), board, gs);
        }
        // now acrue column run potentials
        for ( int c = bbox.getMinCol(); c <= bbox.getMaxCol(); c++ ) {        
            //System.out.println("col run = "+ c);
            totalPotential += 
                    getRowColPotential(bbox.getMinRow(), c, 1, 0, bbox.getMaxRow(), bbox.getMaxCol(), board, gs);
        }
        
        return (float)Math.min(1.9, Math.sqrt(totalPotential)/1.3);
    }
    
    /**
     * Find the potential for one of the bbox's rows or columns.
     */
    private float getRowColPotential(int r, int c, int rowInc, int colInc, int maxRow, int maxCol, 
                                                          GoBoard board, GoString groupString) {
        float rowPotential = 0;           
        int breadth = (rowInc ==1)? (maxRow - r) : (maxCol - c);        
        GoBoardPosition startSpace = (GoBoardPosition) board.getPosition( r, c );      
        String rc = rowInc == 1 ? "col": "row";
        //System.out.println(rc+":");
        do {         
            GoBoardPosition space = (GoBoardPosition) board.getPosition( r, c );          
            GoBoardPosition firstSpace = space;  
            boolean containsEnemy = false;
            int runLength = 0;
            while (c <= maxCol && r <= maxRow && (space.isUnoccupied() ||  
                      (space.isOccupied() && space.getPiece().isOwnedByPlayer1() != isOwnedByPlayer1()))) {
                if (space.isOccupied() &&  space.getPiece().isOwnedByPlayer1() != isOwnedByPlayer1() 
                    && groupString.isEnemy(space)) {
                    containsEnemy =  true;
                }
                runLength++;
                r += rowInc; c += colInc;
                space = (GoBoardPosition) board.getPosition( r, c );                
            }
            boolean bounded = !(firstSpace.equals(startSpace)) && space!=null && space.isOccupied();             
            // now acrue the potential      
            //System.out.println("check containsEnemy="+containsEnemy+" runLength="+runLength + " ("+r+","+c+") useIt="+(!containsEnemy && runLength < breadth && runLength > 0));
            if (!containsEnemy && runLength < breadth && runLength > 0) {               
                 int firstPos, max, currentPos;
                 if (rowInc ==1) {
                     firstPos = firstSpace.getRow();
                     max = board.getNumRows();   
                     currentPos = r;
                 } else {
                     firstPos = firstSpace.getCol();
                     max = board.getNumCols();   
                     currentPos = c;
                 }                 
                 rowPotential += getRunPotential(runLength, firstPos, currentPos, max, bounded);                                           
            }               
            r += rowInc; c += colInc;
        } while (c <= maxCol && r <= maxRow);
        // System.out.println("rcPotential = " + rowPotential);  
        return rowPotential;
    }
    
  
    private float getRunPotential(int runLength, int firstPos, int endPosP1, int max, 
                                                    boolean boundedByStones) {
        float potential = 0;
        assert(runLength > 0);
        // this case is where the run is next to an edge or bounded by friend stones. 
        // Weight the potential more heavily.
        if ((firstPos == 1 || endPosP1 == max || boundedByStones)) {
            switch (runLength) {   
                case 1: potential = 0.25f; break;
                case 2: potential = 0.35f; break;
                case 3: potential = 0.4f; break;
                case 4: potential = 0.3f; break;
                case 5: potential = 0.2f; break;
                case 6: potential = 0.15f; break;
                case 7: potential = 0.1f; break;
                default : potential = 0.05f;
            }
        }
        else {
            // a run to boundary. Less weight attributed.
            switch (runLength) {        
                case 1: potential = 0.05f; break;
                case 2: potential = 0.15f; break;
                case 3: potential = 0.2f; break;
                case 4: potential = 0.25f; break;
                case 5: potential = 0.2f; break;
                case 6: potential = 0.15f; break;
                case 7: potential = 0.1f; break;
                case 8: potential = 0.6f; break;
                default : potential = 0.05f;
            }
        }
        String color = this.isOwnedByPlayer1()?"black":"white";
       // System.out.println(color + " potential for first="+ firstPos + " end="+endPosP1 
        //        + " max="+max+ " runLen="+runLength+" bounded="+boundedByStones  +" IS:"+potential);
        return potential;
    }
    
    /**
     * mark as visited all the non-friend (empty or enemy) spaces connected to this one.
     *
     * @param space seed
     * @param board owner
     * @param lists list of stones connected to the seed stone
     */
    private void excludeSeed( GoBoardPosition space, boolean groupOwnership, GoBoard board, List lists, Box box)
    {
        if ( !space.isVisited()
             && (space.isUnoccupied() || space.getPiece().isOwnedByPlayer1() != isOwnedByPlayer1())) {
            // this will leave stones outside the group visited
            List list = board.findStringFromInitialPosition( space, groupOwnership, false,
                                                             NeighborType.NOT_FRIEND, box );

            // make sure that every occupied stone in the list is a real enemy and not just a dead opponent stone.
            // compare it with one of the group strings
            GoString groupString = ((GoString)this.getMembers().iterator().next());

            Iterator it = list.iterator();
            while (it.hasNext()) {
                GoBoardPosition p = (GoBoardPosition)it.next();
                if (p.isOccupied()) {
                    // if its a very weak opponent (ie dead) then don't exclude it from the list
                    if (!groupString.isEnemy(p))  {
                        p.setVisited(false);
                        it.remove();  // remove it from the list
                    }
                }
            }

            if ( list.size() > 0 ) {
                lists.add( list );
            }
        }
    }


    /**
     * Check this list of stones to confirm that enemy stones don't border it.
     * If they do, then it is not an eye - return false.
     *
     * @param eyeList the candidate string of stones to misc for eye status
     * @return true if the list of stones is an eye
     */
    private boolean confirmEye( List eyeList)
    {
        if ( eyeList == null )
            return false;

        // each occupied stone of the eye must be very weak (ie not an enemy, but dead opponent)
        // compare it with one of the group strings
        GoString groupString = (GoString)(members_.iterator().next());

        Iterator it = eyeList.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition position = (GoBoardPosition) it.next();
            GoString string = position.getString();

            if (position.isOccupied()) {
                GoStone stone  = (GoStone) position.getPiece();
                if (string.size() == 1 && Math.abs(stone.getHealth()) <= 0.11) {
                    // since its a lone stone inside an enemy eye, we assume it is more dead than alive
                    stone.setHealth(stone.isOwnedByPlayer1() ? -0.6f : 0.6f);
                }
                if (groupString.isEnemy(position)) {
                    return false;  // not eye
                }
            }
        }
        // if we make it here, its a bonafied eye.
        return true;
    }

    /**
     * @return  set of eyes currently identified for this group.
     */
    public Set<GoEye> getEyes()
    {
        return eyes_;
    }

    /**
     * clear the current eyes for the group (in preparation for recomputing them).
     */
    private void clearEyes()
    {
        if (eyes_.isEmpty())
            return;
        Iterator<GoEye> it = eyes_.iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
        eyes_.clear();
        changed_ = true;
    }

    public float getAbsoluteHealth()
    {
        return absoluteHealth_;
    }
    
    /**
     * only used in tester. otherwise would be private.
     */
    public float getEyePotential()
    {
        return eyePotential_;
    }

    /**
     * Calculate the absolute health of a group.
     * All the stones in the group have the same health rating because the
     * group lives or dies as a unit
     * (not entirely true - strings live or die as unit, but there is a relationship).
     * Good health of a black group is positive; white, negative.
     * The health is a function of the number of eyes, false-eyes, liberties, and
     * the health of surrounding groups. If the health of an opponent bordering group
     * is in worse shape than our own then we get a boost since we can probably
     * kill that group first. See calculateRelativeHealth below.
     * A perfect 1 (or -1) indicates unconditional life (or death).
     * This means that the group cannot be killed (or given life) no matter
     * how many times the opponent plays (see Dave Benson 1977).
     *http://senseis.xmp.net/?BensonsAlgorithm
     *
     * @@ need expert advice to make this work well.
     * @@ make the constants parameters and optimize them.
     * @@ we currently don't give any bonus for false eyes. should we?
     *
     * @return the overall health of the group independent of nbr groups.
     */
    float calculateAbsoluteHealth( GoBoard board, GameProfiler profiler )
    {

        if ( !changed_ )
            return absoluteHealth_;

        // if nothing has changed about the group, then we can return the cached value
        int numLiberties = getLiberties(board).size();

        // we multiply by a +/- sign depending on the side
        float side = ownedByPlayer1_? 1.0f : -1.0f;

        // we need to come up with some approximation for the health so update eyes can be done more accurately.
        float numEyes = calcNumEyes();
        absoluteHealth_ = determineHealth(side, numEyes, numLiberties, board);

        profiler.start(GoProfiler.UPDATE_EYES);
        updateEyes( board );  // expensive
        profiler.stop(GoProfiler.UPDATE_EYES);

        numEyes = Math.max(eyePotential_, calcNumEyes());

        // health based on eye shape - the most significant factor
        float health = determineHealth(side, numEyes, numLiberties, board);

        // Should there be any bonus at all for false eyes??  no
        // health += (side * .015 * numFalseEyes);

        absoluteHealth_ = health;
        if (Math.abs(absoluteHealth_) > 1.0) {
            GameContext.log(0,  "Warning: health exceeded 1.0: " +" health="+health+" numEyes="+numEyes);
            absoluteHealth_ = side;
        }

        changed_ = false;  // cached until something changes

        //if (numLiberties<=1)
        //    GameContext.log(2, "health for "+this+" = health="+health+"  + health="+health);
        return absoluteHealth_;
    }


    /**
     *Determine approximately how many eyes the group has. 
     *This is purposely a little vague, but if more than 2.0, then must be unconditionally alive.
     *The value that we count for each type of eye could be optimized.
     */
    private float calcNumEyes() {
        // figure out how many of each eye type we have
        Iterator<GoEye> it = eyes_.iterator();
        float numEyes = 0;
        while ( it.hasNext() ) {
            GoEye eye = it.next();
            switch (eye.getEyeType()) {
                case FALSE_EYE:
                    numEyes+= 0.19f;
                    break;
                case TRUE_EYE:
                    numEyes++;
                    break;
                case BIG_EYE:
                    numEyes += 1.1;
                    break; 
                case TERRITORIAL_EYE:
                    numEyes += 1.6f;
                    break; // counts as 2 true eyes
            }            
        }
        
        return numEyes;
    }

    /**
     * determine the health of the group based on the number of eyes and the number of liberties.
     */
    public float determineHealth(float side, float numEyes, int numLiberties, GoBoard board)  {
        float health;

        if ( numEyes >= 2.0 )  {
           health = calcTwoEyedHealth(side, board);
        }
        else if (numEyes >= 1.5) {
            health = calcAlmostTwoEyedHealth(side, numLiberties);
        }
        else if (numEyes >= 1.0) {
            health = calcOneEyedHealth(side, numLiberties);
        }
        else {
            health = calcNoEyeHealth(side, numLiberties);
        }
        return health;
    }

    private static final float BEST_TWO_EYED_HEALTH = 1.0f;
    private static final float BEST_ALMOST_TWO_EYED_HEALTH = 0.94f;
    private static final float BEST_ONE_EYED_HEALTH = 0.89f;

    /**
     *Calculate the health of a group that has 2 eyes.
     */
    private float calcTwoEyedHealth(float side, GoBoard board) {
        float health;
        if (isUnconditionallyAlive(board)) {
            // in addition to this, the individual strings will get a score of side (ie +/- 1).
            health = BEST_TWO_EYED_HEALTH * side;
        }
        else {
            // its probably alive
            // may not be alive if the opponent has a lot of kos and gets to play lots of times in a row
            health = BEST_ALMOST_TWO_EYED_HEALTH * side;
        }
        return health;
    }

    /**
     * Calculate the health of a group that has only one eye.
     */
    private float calcAlmostTwoEyedHealth(float side, int numLiberties) {
        float health = 0;
        if (numLiberties > 6)  {
            health = side * Math.min(BEST_ALMOST_TWO_EYED_HEALTH, (1.15f - 20.0f/(numLiberties + 23.0f)));
        }
        else  {  // numLiberties<=5. Very unlikely to occur           
            switch (numLiberties) {
                case 0:                
                case 1:                                                   
                    assert false: "can't have almost 2 eyes and only 1 or fewer liberties! " + this.toString();        
                    break;
                case 2:
                    health = side * 0.02f;
                     GameContext.log(0, "We have almost 2 eyes but only 2 Liberties. How can that be? " + this.toString());
                    break;  
                case 3:
                    health = side * 0.05f;
                    break;
                case 4:
                    health = side * 0.1f;
                    break;
                case 5:
                    health = side * 0.19f;
                    break;
                case 6:
                    health = side * 0.29f;
                    break;
                default: assert false;
            }
        }
        return health;
    }

    /**
     * Calculate the health of a group that has only one eye.
     */
    private static float calcOneEyedHealth(float side, int numLiberties) {
        float health = 0;
        if (numLiberties > 6)  {
            health = side * Math.min(BEST_ONE_EYED_HEALTH, (1.03f - 20.0f/(numLiberties + 20.0f)));
        }
        else  {  // numLiberties<=5
            switch (numLiberties) {
                case 0:
                    // this can't happen because the stone should already be captured.
                    assert false: "can't have 1 eye and no liberties!";
                    break;
                case 1:
                    // @@ we need to consider a seki here.
                    // what if the neighboring enemy group also has one or zero eyes?
                    // one eye beats no eyes.
                    health = -side * 0.8f;
                    break;
                case 2:
                    health = -side * 0.3f;
                    break;
                case 3:
                    health = -side * 0.2f;
                    break;
                case 4:
                    health = -side * 0.05f;
                    break;
                case 5:
                    health = side * 0.01f;
                    break;
                case 6:
                    health = side * 0.19f;
                    break;
                default: assert false;
            }
        }
        return health;
    }

    /**
     *   Calculate the health of a group that has no eyes.
     */
    private float calcNoEyeHealth(float side, int numLiberties) {
        float health = 0;
        int numStones = getNumStones();

        if ( numLiberties > 5 )  {// numEyes == 0
            health = side * Math.min(0.8f, (1.2f - 46.0f/(numLiberties+40.0f)));
        }
        else if (numStones == 1) {
            switch (numLiberties) { // numEyes == 0
                case 0:
                    // this can't happen because the stone should already be captured.
                    assert false : "can't have no liberties and still be on the board! "+ this;
                    health = -side;
                    break;
                case 1:
                    health = -side * 0.6f;
                    break;
                case 2:
                    // @@ consider seki situations where the adjacent enemy group also has no eyes.
                    //      XXXXXXX     example of seki here.
                    //    XXooooooX
                    //    Xo.XXX.oX
                    //    XooooooXX
                    //    XXXXXXX
                    health = side * 0.02f;
                    break;
                case 3:
                    health = side * 0.1f;
                    break;
                case 4:
                    health = side * 0.1f;
                    break;
                default: assert false : "there were too many liberties for a single stone: "+numLiberties;
            }
        } else {
            switch (numLiberties) { // numEyes == 0
                case 0:
                    // this can't happen because the stone should already be captured.
                    //assert false : "can't have no liberties and still be on the board! "+ this;
                    health = -side;
                    break;
                case 1:
                    health = -side * 0.6f;
                    break;
                case 2:
                    // @@ consider seki situations where the adjacent enemy group also has no eyes.
                    //      XXXXXXX     example of seki here.
                    //    XXooooooX
                    //    Xo.XXX.oX
                    //    XooooooXX
                    //    XXXXXXX
                    health = -side * 0.3f;
                    break;
                case 3:
                    health = side * 0.02f;
                    break;
                case 4:
                    health = side * 0.05f;
                    break;
                case 5:
                    health = side * 0.1f;
                    break;
                default: assert false;
            }
        }
        return health;
    }

    /**
     * Calculate the relative health of a group.
     * This method must be called only after calculateAbsoluteHealth has be done for all groups.
     * Good health is positive for a black group.
     * This measure of the group's health should be much more accurate than the absolute health
     * because it takes into account the relative health of neighboring groups.
     * If the health of an opponent bordering group is in worse shape
     * than our own then we get a boost since we can probably kill that group first.
     *
     * @return the overall health of the group.
     */
    float calculateRelativeHealth( GoBoard board, GoBoardPosition lastMove, Profiler profiler )
    {
        // we multiply by a +/- sign depending on the side
        float side = ownedByPlayer1_? 1.0f : -1.0f;

        // the default if there is no weakest group.
        relativeHealth_ = absoluteHealth_;
        Set groupStones = getStones();

        profiler.start(GoProfiler.GET_ENEMY_GROUPS_NBRS);
        Set cachedEnemyNbrGroups = getEnemyGroupNeighbors(board, groupStones);
        profiler.stop(GoProfiler.GET_ENEMY_GROUPS_NBRS);

        // of these enemy groups which is the weakest?
        double weakestHealth = -side;
        GoGroup weakestGroup = null;
        for (Object egroup : cachedEnemyNbrGroups) {
            GoGroup enemyGroup = (GoGroup)egroup;
            double h = enemyGroup.getAbsoluteHealth();
            if ((side * h) > (side * weakestHealth)) {
                weakestHealth = h;
                weakestGroup = enemyGroup;
            }
        }

        // if there is a weakest group then boost ourselves relative to it.
        // it may be a positive or negative boost to our health depending on its relative strength.
        if (weakestGroup != null)  {
            // what proportion of the groups stones are close to enemy groups?
            // this gives us an indication of how surrounded we are.
            // If we are very surrounded then we give a big boost for being stronger or weaker than a nbr.
            // If we are not very surrounded then we don't give much of a boost because there are other
            // ways to make life (i.e. run out/away).
            int numWithEnemyNbrs = 0;
            for (Object p : groupStones) {
                GoBoardPosition stone = (GoBoardPosition)p;
                if (stone.isVisited()) {
                    numWithEnemyNbrs++;
                    stone.setVisited(false); // clear the visited state.
                }
            }
            double proportionWithEnemyNbrs = (double)numWithEnemyNbrs / ((double)groupStones.size() + 2);

            double diff = absoluteHealth_ + weakestGroup.getAbsoluteHealth();
            // @@ should use a weight to help determine how much to give a boost.

            // must be bounded by -1 and 1
            relativeHealth_ =
                    (float) (Math.min(1.0, Math.max(-1.0, absoluteHealth_ + diff * proportionWithEnemyNbrs)));
        }

        GoBoardUtil.unvisitPositions(groupStones);

        return relativeHealth_;
    }

    public double getRelativeHealth()
    {
        return relativeHealth_;
    }

    /**
     * @@ may need to make this n^2 method more efficient.
     * note: has intentional side effect of marking stones with enemy group nbrs as visited.
     * @param board
     * @param groupStones the set of stones in the group to find enemies of.
     * @return a HashSet of the groups that are enemies of this group
     */
    private Set getEnemyGroupNeighbors(GoBoard board, Set groupStones)
    {
        Set enemyNbrs = new HashSet();

        // for every stone in the group.
        for (Object s : groupStones) {
            GoBoardPosition stone = (GoBoardPosition)s;
            Set nbrs = board.getGroupNeighbors(stone, false);

            // if the stone has any enemy nbrs then mark it visited.
            // later we will count how many got visited.
            // this is a bit of a hack to determine how surrounded the group is by enemy groups
            for (Object peNbr : nbrs) {
                GoBoardPosition possibleEnemy = (GoBoardPosition)peNbr;
                if (possibleEnemy.getPiece().isOwnedByPlayer1() != this.isOwnedByPlayer1()
                        && !possibleEnemy.isInEye()) {
                    // setting visited to true to indicate there is an enemy nbr within group distance.
                    stone.setVisited(true);
                    // if the group is already there, it does not get added again.
                    assert (possibleEnemy.getGroup()!=null);
                    enemyNbrs.add(possibleEnemy.getGroup());
                }
            }
        }
        return enemyNbrs;
    }


    private boolean isUnconditionallyAlive(GoBoard board)  {
        return GoGroupUtil.isUnconditionallyAlive(this, board);
    }

    /**
     * set the health of strings in this group
     * @param health the health of the group
     */
    public void updateTerritory( float health )
    {
        Iterator it = members_.iterator();
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
     * @return a deep copy of this GoSet
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();

        if (this.eyes_!=null)  {
            ((GoGroup)clone).eyes_ = new HashSet<GoEye>();
            Set m = ((GoGroup)clone).eyes_;

            Iterator<GoEye> it = this.eyes_.iterator();
            while (it.hasNext()) {
                GoMember c = it.next();
                m.add(c.clone());
            }
        }

        return clone;
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
     *  @return true if the piece is an enemy of the set owner.
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
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
        Iterator it = members_.iterator();
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
        if (eyes_!=null && !eyes_.isEmpty())
            sb.append(eyes_.toString() +newline);
        // make sure that the health and eyes are up to date
        //calculateHealth();
        sb.append( "abs health=" + Util.formatNumber(absoluteHealth_) );
        sb.append( " rel health=" + Util.formatNumber(relativeHealth_));
        sb.append( " group Liberties=" + (cachedLiberties_==null? 0:cachedLiberties_.size()) + '\n' );
        return sb.toString();
    }

    /**
     *
     * @return true if the group has changed (structurally) in any way.
     */
    public boolean hasChanged()
    {
        return changed_;
    }

}



