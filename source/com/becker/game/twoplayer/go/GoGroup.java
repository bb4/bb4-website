package com.becker.game.twoplayer.go;

import com.becker.game.common.GameContext;
import com.becker.game.common.Box;
import com.becker.common.*;

import java.util.*;

/**
 *  A GoGroup is composed of a loosely connected set of one or more same color strings.
 *  A GoString by comparison, is composed of a strongly connected set of one or more same color stones.
 *  A GoArmy is a loosely coupled set of Groups
 *  Groups may be connected by diagonals or one space jumps, or uncut knights moves, but not nikken tobi
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
    private Set eyes_;
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

    // Set this to true when the eyes need to be recalculated.
    // It must be set to true if the group has changed in any way.
    private boolean changed_ = true;

    // this is the cached number of liberties
    // updates whenever something has changed
    private int cachedNumLiberties_ = 0;
    private Set cachedEnemyNbrGroups_ = new HashSet();


    /** constructor. Create a new group containing the specified string
     * @param string make the group from this string
     */
    public GoGroup( GoString string )
    {
        ownedByPlayer1_ = string.isOwnedByPlayer1();
        members_.add( string );
        string.setGroup( this );
        commonInit();
    }

    /**
     * constructor. Create a new group containing the specified list of stones
     * Every stone in the list passed in must say that it is owned by this new group,
     * and every string must be wholy owned by this new group.
     * @param stones list of stones to create a group from
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
                assert ( ownedByPlayer1_ == string.isOwnedByPlayer1()): string +"ownership not the same as "+this;
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
        eyes_ = new HashSet();
        changed_ = true;
    }

    /**
     * set/get the army
     * @param army the owning army
     */
    public final void setArmy( GoArmy army )
    {
        army_ = army;
    }

    /**
     * get the owning army for this group
     * @return the owning army
     */
    public final GoArmy getArmy()
    {
        return army_;
    }

    /**
     * add a string to the group.
     * @param string the string to add
     * @param board the owning board
     */
    public final void addMember( GoString string, GoBoard board )
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
     * get the number of liberties that the group has.
     * @return the number of liberties that the group has
     * @param board owner
     */
    public final Set getLiberties( GoBoard board )
    {
        Set liberties = new HashSet();
        for (Object str : members_) {
            GoString string = (GoString) str;
            liberties.addAll(string.getLiberties(board));
        }
        return liberties;
    }

    /**
     * calculate the number of stones in the group
     * @return number of stones in the group
     */
    public final int getNumStones()
    {
        int numStones = 0;
        Iterator it = members_.iterator();
        if ( GameContext.getDebugMode() <= 2 ) {
            while ( it.hasNext() ) {
                GoString str = (GoString) it.next();
                numStones += str.size();
            }
            return numStones;
        }

        // The evaluation of this and str in the second argument of the Assert
        // is extremely expensive (factor of 10!)
        // That's why we only do this assert when running in debug mode.
        // now that we use java native assertions there is no more overhead when they are turned off, but
        // when they are on I still only want to incur the overhead when the debug level is high.
        while ( it.hasNext() ) {
            GoString str = (GoString) it.next();
            // debug (make sure none of the stones are blank)
            assert ( !str.areAnyBlank()): "Error: " + this + " contains a " + str + " with blanks in it.";
            numStones += str.size();
        }
        return numStones;
    }

    /**
     * merge another group into this one.
     * @param group the group to merge into this one
     * @param board owning board
     */
    public final void merge( GoGroup group, GoBoard board )
    {
        if ( this == group ) {
            // its a self join
            GameContext.log( 2, "Warning: attempting a self join" );
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
     * It is an error if the specified set of stones is not a proper subset.
     * Really we just remove the strings that own these stones.
     * @param stones the list of stones to subtract from this one
     */
    public final void remove( List stones )
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
    public final void remove( GoString string )
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
     * remove a stone from this group.
     * Note that removing a stone may cause a single string to break into several smaller ones
     * @param stone the stone to remove from the group
     * @param board on which the stone was played
     */
    public final void remove( GoBoardPosition stone, GoBoard board )
    {
        assert (this.containsStone(stone)): this + " \n does not contain "+stone;
        GoString string = stone.getString();
        if ( string != null ) {
            // if this is the only stone in the string, then remove the whole string.
            if ( string.size() == 1 )
                remove( string );
            else {
                string.remove( stone );
                // now need to consider the creation of several smaller strings here
                int origSize = string.size();
                Set nbrs = board.getNobiNeighbors( stone, string.isOwnedByPlayer1(), NeighborType.FRIEND );
                if (nbrs.size()==0) {
                    board.printNobiNeighborsOf(stone);
                }
                assert ( nbrs.size() > 0): stone +" had no friendly nbrs "+nbrs+" string="+string
                              +". These 2 must be the same: size="+string.size()+" origSize="+origSize;
                Iterator it = nbrs.iterator();
                List splitStrings = new LinkedList();
                boolean split = false;
                while ( it.hasNext() ) {
                    GoBoardPosition nbrStone = (GoBoardPosition) it.next();
                    if ( !nbrStone.isVisited() ) {
                        List splitString = board.findStringFromInitialPosition( nbrStone, false );
                        if ( splitString.size() < origSize ) {
                            GoString newString = new GoString( splitString );
                            newString.setGroup( this );
                            members_.add( newString );
                            splitStrings.add( newString );
                            split = true;
                        }
                    }
                }
                string.unvisit();
                if ( split ) {
                    members_.remove( string );
                    string.removeAll();
                    it = splitStrings.iterator();
                    while ( it.hasNext() ) {
                        GoString s = (GoString) it.next();
                        s.unvisit();
                    }
                }
            }
        }
        clearEyes();
        changed_ = true;
    }


    /**
     * @return a list of the stones in this group
     */
    public final List getStones()
    {
        List stones = new ArrayList();
        Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            GoString string = (GoString) it.next();
            stones.addAll( string.getMembers() );
        }
        return stones;
    }

    //------------------------------- methods related to updating eyes and group health --------------

    /**
     * compute how many eyes (connected internal blank areas) this group has.
     * the eyes are either false eyes or true (or big or territorial) eyes.
     * This method is expensive.
     * @param board the owning board
     */
    private void updateEyes( GoBoard board )
    {
        if (!changed_)
            return;

        // list of lists of spaces to unvisit at the end
        List lists = new ArrayList();

        Box box = GoBoardUtil.findBoundingBox(members_);

        // forget what we had computed before about the eyes
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

        // now do a paint fill on each of the empty unvisited spaces left.
        // most of these remaining empty spaces are connected to an eye of some type.
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
            GoString groupString = ((GoBoardPosition)this.getStones().get(0)).getString();
            assert (groupString!=null): "stones = "+this.getStones();    // hitting this 7/14/03

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

    private int getNumTrueEyes()
    {
        int numTrueEyes = 0;
        Iterator it = this.getEyes().iterator();
        while (it.hasNext())  {
            GoEye eye = (GoEye)it.next();
            if (eye.getEyeType() != EyeType.FALSE_EYE)  {
                numTrueEyes++;
            }
        }
        return numTrueEyes;
    }


    /**
     * check this list of stones to confirm that enemy stones don't border it.
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
                GoStone stone  = ((GoStone)position.getPiece());
                if (string.size() == 1 && stone.getHealth() == 0) {
                    // since its a lone stone inside an enemy eye, we assume it is more dead than alive

                    //System.out.print("was "+stone.getHealth());
                    stone.setHealth(stone.isOwnedByPlayer1() ? -0.5f : 0.5f);
                    //System.out.println("setting to " + stone.getHealth());
                }
                if (groupString.isEnemy(position)) {
                    //GameContext.log(3, (position +" was determined to be an enemy of "+this);
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
    public Set getEyes()
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
        Iterator it = eyes_.iterator();
        while (it.hasNext()) {
            GoEye eye = (GoEye)it.next();
            eye.clear();
        }
        eyes_.clear();
        changed_ = true;
    }

    public float getAbsoluteHealth()
    {
        return absoluteHealth_;
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
     *
     * @@ need expert advice to make this work well.
     * @@ make the constants parameters and optimize them.
     * @@ we currently don't give any bonus for false eyes. should we?
     *
     * @return the overall health of the group independent of nbr groups.
     */
    final float calculateAbsoluteHealth( GoBoard board, GoProfiler profiler )
    {
        // if nothing has changed about the group, then we can return the cached value
        int numLiberties = getLiberties( board ).size();
        if ( !changed_ && cachedNumLiberties_ == numLiberties)
            return absoluteHealth_;

        cachedNumLiberties_ = numLiberties;

        // health based on eye shape - the most significant factor
        float health = 0;

        // we multiply by a +/- sign depending on the side
        float side = ownedByPlayer1_? 1.0f : -1.0f;

        // we need to come up with some approximation for the health so update eyes can be done more accurately.
        int numEyes = calcNumEyes();
        absoluteHealth_ = determineHealth(side, numEyes, numLiberties, board);

        profiler.start(GoProfiler.UPDATE_EYES);
        updateEyes( board );  // expensive
        profiler.stop(GoProfiler.UPDATE_EYES);

        changed_ = false;  // cached until something changes

        numEyes = calcNumEyes();
        health = determineHealth(side, numEyes, numLiberties, board);

        // Should there be any bonus at all for false eyes??  no
        // health += (side * .015 * numFalseEyes);

        absoluteHealth_ = health;
        if (Math.abs(absoluteHealth_)>1.0) {
            GameContext.log(0,  "Warning: health exceeded 1.0: " +" health="+health+" numEyes="+numEyes);
            absoluteHealth_ = side;
        }

        //if (numLiberties<=1)
        //    GameContext.log(2, "health for "+this+" = health="+health+"  + health="+health);
        return absoluteHealth_;
    }

    private int calcNumEyes() {
        // figure out how many of each eye type we have
        Iterator it = eyes_.iterator();
        int numEyes = 0;
        int numFalseEyes = 0;
        while ( it.hasNext() ) {
            GoEye eye = (GoEye) it.next();
            switch (eye.getEyeType()) {
                case FALSE_EYE:
                    numFalseEyes++;
                    break;
                case TRUE_EYE:
                    numEyes++;
                    break;
                case BIG_EYE:
                    numEyes++;
                    break; // @@ count as 1 eye for now. maybe 1.5 would be better
                case TERRITORIAL_EYE:
                    numEyes += 1.8;
                    break; // counts as 2 true eyes
            }
        }
        return numEyes;
    }

    /**
     * determine the health of the group based on the number of eyes and the number of liberties.
     */
    private float determineHealth(float side, int numEyes, int numLiberties, GoBoard board)  {
        float health = 0;
        int numStones = getNumStones();

        if ( numEyes >= 2 )  {
            if (isUnconditionallyAlive(board)) {
                // in addition to this, the individual strings will get a score of side (ie +/- 1).
                health = .98f * side;
            }
            else {
                // its probably alive
                // may not be alive if the opponent has a lot of kos and gets to play lots of times in a row
                health = .95f * side;
            }
        }
        else if ( (numEyes == 1) && (numLiberties > 6) )
            health = side * Math.min(.9f, (1.0f - 20.0f/(numLiberties + 23.0f)));
        else if ( numEyes == 1 ) {  // numLiberties<=5
            switch (numLiberties) {
                case 0:
                    // this can't happen because the stone should already be captured.
                    assert false: "can't have 1 eye and no liberties!";
                    break;
                case 1:
                    // @@ we need to consider a seki here.
                    // what if the neighboring enemy group also has one or zero eyes?
                    // one eye beats no eyes.
                    health = -side * .8f;
                    break;
                case 2:
                    health = -side * .6f;
                    break;
                case 3:
                    health = -side * .3f;
                    break;
                case 4:
                    health = -side * .05f;
                    break;
                case 5:
                    health = side * .01f;
                    break;
                case 6:
                    health = side * .19f;
                    break;
                default: assert false;
            }
        }
        else if ( numLiberties > 5 )  // numEyes == 0
            health = side * Math.min(.8f, (1.2f - 46.f/(numLiberties+40.f)));
        else {
            if (numStones == 1) {
                switch (numLiberties) { // numEyes == 0
                    case 0:
                        // this can't happen because the stone should already be captured.  @@Hitting this
                        assert false : "can't have no liberties and still be on the board! "+ this;
                        health = -side;
                        break;
                    case 1:
                        health = -side * .3f;
                        break;
                    case 2:
                        // @@ consider seki situations where the adjacent enemy group also has no eyes.
                        //      XXXXXXX     example of seki here.
                        //    XXooooooX
                        //    Xo.XXX.oX
                        //    XooooooXX
                        //    XXXXXXX
                        health = side * .02f;
                        break;
                    case 3:
                        health = side * .1f;
                        break;
                    case 4:
                        health = side * 0.3f;
                        break;
                    default: assert false;
                }
            } else {
                switch (numLiberties) { // numEyes == 0
                    case 0:
                        // this can't happen because the stone should already be captured.  @@Hitting this
                        //assert false : "can't have no liberties and still be on the board! "+ this;
                        health = -side;
                        break;
                    case 1:
                        health = -side * .4f;
                        break;
                    case 2:
                        // @@ consider seki situations where the adjacent enemy group also has no eyes.
                        //      XXXXXXX     example of seki here.
                        //    XXooooooX
                        //    Xo.XXX.oX
                        //    XooooooXX
                        //    XXXXXXX
                        health = -side * .1f;
                        break;
                    case 3:
                        health = side * .02f;
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
     * than our own then we get a boost since we can probably
     * kill that group first.
     *
     * @return the overall health of the group.
     */
    final float calculateRelativeHealth( GoBoard board, GoBoardPosition lastMove, Profiler profiler )
    {
        // we multiply by a +/- sign depending on the side
        float side = ownedByPlayer1_? 1.0f : -1.0f;

        // the default if there is no weakest group.
        relativeHealth_ = absoluteHealth_;

        profiler.start(GoProfiler.GET_ENEMY_GROUPS_NBRS);
        cachedEnemyNbrGroups_ = getEnemyGroupNeighbors(board);
        profiler.stop(GoProfiler.GET_ENEMY_GROUPS_NBRS);

        // of these enemy groups which is the weakest?
        double weakestHealth = -side;
        GoGroup weakestGroup = null;
        for (Object egroup : cachedEnemyNbrGroups_) {
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
            List groupStones = getStones();
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
            //System.out.println( "before abs = "+absoluteHealth_+" after (rel)="+ relativeHealth_ );
        }

        GoBoardUtil.unvisitPositionsInList(getStones());

        return relativeHealth_;
    }

    public double getRelativeHealth()
    {
        return relativeHealth_;
    }


    /**
     * Use Benson's algorithm (1977) to determine if a set of strings and eyes within a group
     * is unconditionally alive
     * @return
     */
    public boolean isUnconditionallyAlive(GoBoard board) {

        // first find the neighbor string sets for each eye in the group
        for (Object e : eyes_) {
            GoEye eye = (GoEye)  e;
            if (eye.getNbrs() == null) {
                eye.setNbrs(new HashSet());
            }
            for (Object point : eye.getMembers()) {
                GoBoardPosition pos = (GoBoardPosition) point;
                if (pos.isUnoccupied()) {
                    Set nbrs = board.getNobiNeighbors(pos, eye.isOwnedByPlayer1(), NeighborType.FRIEND);
                    for (Object n : nbrs) {
                        GoBoardPosition nbr = (GoBoardPosition) n;
                        if (nbr.getString().getGroup() != this) {
                            // this eye is not UA.
                            eye.setNbrs(null);
                            break;
                        }
                        else {
                            if (eye.getNbrs() != null) {
                                eye.getNbrs().add(nbr.getString());
                            }
                        }
                    }
                }
            }
        }

        // now create the neighbor eye sets for each qualified string
        Set uaStrings = new HashSet();
        for (Object e : eyes_) {
            GoEye eye = (GoEye)  e;
            if (eye.getNbrs() != null)
            for (Object s : eye.getNbrs()) {
                GoString str = (GoString) s;
                if (str.getNbrs() == null) {
                    str.setNbrs(new HashSet());
                }
                str.getNbrs().add(eye);
                uaStrings.add(str);
            }
        }

        // remove qualified strings that do not have at least 2 qualified eyes associated
        // and also remove those disqualified eyes from the stirng lists
        // need to iterate until all qualified strings hav at least 2 qualified eyes.
        boolean done = false;
        while (!done) {
            done = true;
            for (Object s : uaStrings) {
                GoString uaString = (GoString) s;
                if (uaString.getNbrs().size() == 1) {
                   GoEye eye = (GoEye) uaString.getNbrs().iterator().next() ;
                   for (Object ss : eye.getNbrs()) {
                       GoString string = (GoString) ss;
                       string.getNbrs().remove(eye);
                   }
                   eye.setNbrs(null);
                }
            }
        }

        // finally we have all the information we need to determine if any poriton of this group is
        //unconditionally alive. We also mark the corresponding strings and eyes as such as appropriate.
        boolean unconditionallyAlive = false;
        for (Object s : uaStrings) {
            GoString uaString = (GoString) s;
            if (uaString.getNbrs().size() >= 2) {
                uaString.setUnconditionallyAlive(true);
                // if any one of the strings in the group is UA then the group is considered UA
                // even thought that is not really technically the case.
                unconditionallyAlive = true;
                for (Object e : uaString.getNbrs()) {
                    GoEye eye = (GoEye) e;
                    eye.setUnconditionallyAlive(true);
                }
            }
        }
        return unconditionallyAlive;
    }


    /**
     * @@ may need to make this n^2 method more efficient.
     * note: has intentional side effect of marking stones with enemy group nbrs as visited.
     * @param board
     * @return a HashSet of the groups that are enemies of this group
     */
    private Set getEnemyGroupNeighbors(GoBoard board)
    {
        Set enemyNbrs = new HashSet();

        // for every stone in the group.
        for (Object s : getStones()) {
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

    /**
     * set the health of strings in this group
     * @param health the health of the group
     */
    public final void updateTerritory( float health )
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

    /*
    private static boolean isGroupNeighbor(GoBoardPosition pos, GoBoard board)
    {
        Set groupNbrs = board.getGroupNeighbors(pos, false);
        Iterator it = groupNbrs.iterator();
        while (it.hasNext()) {
            GoBoardPosition p = (GoBoardPosition)it.next();
            if (p == pos)
                return true;
        }
        return false;
    }*/

     /**
     * @return a deep copy of this GoSet
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();

        if (this.eyes_!=null)  {
            ((GoGroup)clone).eyes_ = new HashSet();
            Set m = ((GoGroup)clone).eyes_;

            Iterator it = this.eyes_.iterator();
            while (it.hasNext()) {
                GoMember c = (GoMember)it.next();
                m.add(c.clone());
            }
        }

        return clone;
    }

    /**
     *  @return true if the piece is an enemy of the set owner.
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
    protected boolean isEnemy( GoBoardPosition pos, GoBoard board )
    {
        assert (pos.isOccupied());
        GoStone stone = (GoStone)pos.getPiece();
        boolean withinDifferenceThreshold = !GoBoardUtil.isStoneMuchWeaker(this, stone);

        return ( stone.isOwnedByPlayer1() != ownedByPlayer1_  && withinDifferenceThreshold);
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
    public final String toHtml()
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
        sb.append( " group Liberties=" + cachedNumLiberties_ + "\n" );
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

    /////////// utility/debugging methods  /////////////////////////

    /**
     * returns true if this group contains the specified stone
     * @param stone the stone to check for containment of
     * @return true if the stone is in this group
     */
    public final boolean containsStone( GoBoardPosition stone )
    {
        Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            GoString string = (GoString) it.next();
            if ( string.getMembers().contains( stone ) )
                return true;
        }
        return false;
    }

    /** see if the group contains all the stones that are in the specified list (it may contain others as well)
     * @param stones list of stones to check if same as those in this group
     * @return true if all the strings are in this group
     */
    private boolean contains( List stones )
    {
        Iterator it = stones.iterator();
        while ( it.hasNext() ) {
            GoString s = ((GoBoardPosition) it.next()).getString();
            if ( !members_.contains( s ) )
                return false;
        }
        return true;
    }

    /**
     * @param stones list of stones to check if same as those in this group
     * @return true if this group exacly contains the list of stones and no others
     */
    public final boolean exactlyContains( List stones )
    {
        if ( !contains( stones ) )
            return false;
        // make sure that every stone in the group is also in the list.
        // that way we are assured that they are the same.
        Iterator sIt = getStones().iterator();
        while ( sIt.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) sIt.next();
            if ( !stones.contains( s ) )
                return false;
        }
        return true;
    }

    /**
     * go through the groups strings and verify that they are valid (have all nobi connections)
     */
    public final void confirmValidStrings( GoBoard b )
    {
        Iterator it = members_.iterator();
        while ( it.hasNext() ) {
            GoString string = (GoString) it.next();
            string.confirmValid( b );
        }
    }

    public final void confirmNoNullMembers()
    {
        Iterator it = getStones().iterator();
        boolean failed = false;
        while (it.hasNext()) {
            GoBoardPosition s = (GoBoardPosition)it.next();
            if (s.getPiece()==null) failed = true;
        }
        if (failed) {
            Assert.exception("Group contains an empty position: "+this.toString());
        }
    }

}



