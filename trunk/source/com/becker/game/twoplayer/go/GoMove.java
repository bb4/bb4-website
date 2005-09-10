package com.becker.game.twoplayer.go;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import java.util.*;

/**
 *  describes a change in state from one board
 *  position to the next in a Go game.
 *
 *  @see GoBoard
 *  @author Barry Becker
 */
public final class GoMove extends TwoPlayerMove
{
    // a linked list of the pieces that were captured with this move
    // null if there were no captures.
    private CaptureList captureList_ = null;

    /**
     * Constructor. This should never be called directly
     * instead call the factory method so we recycle objects.
     * use createMove to get moves, and dispose to recycle them
     */
    public GoMove( int destinationRow, int destinationCol, double val, GoStone stone )
    {
        super( (byte)destinationRow, (byte)destinationCol, val, stone );
    }

    /**
     * factory method for getting new moves.
     *  it uses recycled objects if possible.
     */
    public static GoMove createGoMove(
            int destinationRow, int destinationCol,
            double val, GoStone stone )
    {
        GoMove m = new GoMove( (byte)destinationRow, (byte)destinationCol, val, stone );
        return m;
    }

    /**
     * factory method for creating a passing move
     */
    public static GoMove createPassMove( double val,  boolean player1)
    {
        GoMove m = createGoMove( 0, 0, val, null );
        m.isPass_ = true;
        m.setPlayer1(player1);
        return m;
    }


    /**
     * check if the current move is suicidal.
     * suicidal moves (ones that kill your own pieces) are illegal.
     * Usually a move is suicidal if you play on your last liberty.
     * However, if you kill an enemy string by playing on your last liberty,
     * then it is legal.
     */
    public boolean isSuicidal( GoBoard board )
    {
        GoBoardPosition stone = (GoBoardPosition) board.getPosition( getToRow(), getToCol() );

        //Set nobiNbrs = board.getNobiNeighbors(stone, false, NeighborType.OCCUPIED);
        Set nobiNbrs = board.getNobiNeighbors(stone, false, NeighborType.ANY);
        Set occupiedNbrs = new HashSet();
        for (Object n : nobiNbrs) {
            GoBoardPosition pos = (GoBoardPosition) n;
            if (pos.isOccupied()) {
                occupiedNbrs.add(pos);
            }
        }

        if (occupiedNbrs.size() < nobiNbrs.size()) {
            // can't be suicidal if we have a liberty
            return false;
        }

        for (Object n : occupiedNbrs)  {
            GoBoardPosition nbr = (GoBoardPosition) n;
            if (nbr.getPiece().isOwnedByPlayer1() == this.isPlayer1()) {
                // friendly string
                if (nbr.getString().getNumLiberties(board) > 1) {
                    // can't be suicidal if a neighboring friendly string has > 1 liberty
                    return false;
                }
            }
            else {
               if (nbr.getString().getNumLiberties(board) == 1) {
                   // can't be suicidal if by playing we capture an opponent string.
                   return false;
                }
            }
        }
        return true;

        /*
        GoString string = stone.getString();
        if ( string == null )   {
            GameContext.log( 0, "Warning: GoMove.isSuicidal: the string is null" );
        }

        //CaptureList captures = getCaptures();
        // instead check to see if any of our neighbors have only 1 liberty. if so then return false

        if (captures != null && captures.size() > 0) {
            // if we have captured enemy stones, then this is not a suicide, even if we have no liberties ourselves.
            return false;
        }
        if ( string != null && string.getNumLiberties(board) == 0) {
            assert (string.size() > 0);
            GameContext.log( 2, "GoMove.isSuicidal: your are playing on the last liberty for this string=" + string.toString() + " captures=" + captures );
            // if we do not have captures, then it is a suicide move and should not be allowed
            return true;
        }
        return false;
        */
    }


    /**
     * returns true if the specified move caused one or more opponent groups to be in atari
     *
     * @return a number > 0 if the move m caused an atari. The number gives the number of stones in atari.
     */
    public int causesAtari( GoBoard board )
    {
        if ( isPassingMove() )
            return 0; // a pass cannot cause an atari

        GoBoardPosition pos = (GoBoardPosition)board.getPosition( getToRow(), getToCol() );
        Set enemyNbrs = board.getNobiNeighbors( pos, NeighborType.ENEMY );
        Iterator it = enemyNbrs.iterator();
        int numInAtari = 0;
        Set stringSet = new HashSet();
        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            GoString atariedString = s.getString();
            //System.out.println("atariedString.getLiberties( this ).size()="+atariedString.getLiberties( this ).size());
            if (!stringSet.contains(atariedString) && atariedString.getNumLiberties(board) == 1 ) {
                numInAtari += atariedString.size();
            }
            stringSet.add(atariedString); // once its in the set we own't check it again.
        }
        return numInAtari;
    }


    public CaptureList getCaptures() {
        return captureList_;
    }

    public int getNumCaptures() {
        if (captureList_!= null) {
            return captureList_.size();
        } else {
            return 0;
        }
    }

    /**
     *  make a deep copy of the move object
     */
    public TwoPlayerMove copy()
    {
        CaptureList newList = null;
        if ( captureList_ != null ) {
            // then make a deep copy
            GameContext.log( 2, "******* GoMove: this is the capturelist we are copying:" + captureList_.toString() );
            newList = captureList_.copy();
        }
        GoMove cp = createGoMove( toRow_, toCol_, getValue(), (getPiece() == null)? null : (GoStone)getPiece().copy() );
        cp.captureList_ = newList;
        cp.setPlayer1(isPlayer1());
        cp.setSelected(this.isSelected());
        cp.setTransparency(this.getTransparency());
        return cp;
    }


    public void updateBoardAfterMoving(GoBoard board) {

        GoBoardPosition stone = (GoBoardPosition) (board.getPosition(getToRow(), getToCol()));

        adjustLiberties(stone, board);

        // hitting this all the time when showing game tree.
        assert (stone.getString() == null) : stone +" already belongs to "+stone.getString();

        determineCaptures(stone, board);
        updateStringsAfterMoving(stone, board);
        removeCaptures(board);
        updateGroupsAfterMoving(stone, board );
    }


     public void updateBoardAfterRemoving(GoBoard board) {

         GoBoardPosition stone =  (GoBoardPosition) (board.getPosition(getToRow(),getToCol()));

         GoString stringThatItBelongedTo = stone.getString();
         stone.clear(board);   // clearing a stone may cause a string to split into smaller strings
         adjustLiberties(stone, board);

         updateStringsAfterRemoving( stone, stringThatItBelongedTo, board );
         restoreCaptures(board);
         updateGroupsAfterRemoving( stone, stringThatItBelongedTo, board );
     }

    /**
     * adjust the liberties on the strings (both black and white) that we touch.
     * @param liberty  - either occupied or not depending on if we are placing the stone or removing it.
     */
    private static void adjustLiberties(GoBoardPosition liberty, GoBoard board) {

         Set stringNbrs = board.findStringNeighbors( liberty );
         for (Object sn : stringNbrs) {
             GoString s = (GoString) sn;
             s.changedLiberty(liberty);
         }
    }

    /**
     * Examine the neighbors of this added stone and determine how the strings have changed.
     * For strings: examine the strongly connected neighbors. If more than one string borders, then
     * we merge the strings. If only one borders, then we add this stone to that string. If no strings
     * touch the added stone, then we create a new string containing only this stone.
     * @param stone the stone that was just placed on the board.
     */
    private void updateStringsAfterMoving( GoBoardPosition stone, GoBoard board )
    {
        GoBoard.getProfiler().startUpdateStringsAfterMove();

        Set nbrs = board.getNobiNeighbors( stone, NeighborType.FRIEND );

        GoString str;
        if ( nbrs.size() == 0 ) {
            // there are no strongly connected nbrs, create a new string
            new GoString( stone, board );  // stone points to the new string
        }
        else {
            // there is at least one nbr, so we join to it/them
            Iterator nbrIt = nbrs.iterator();
            GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
            str = nbrStone.getString();
            str.addMember( stone, board );
            GoBoardUtil.debugPrintGroups( 3, "groups before merging:", true, true, board.getGroups());

            if ( nbrs.size() > 1 ) {
                // then we probably need to merge the strings.
                // We would not, for example, if we are completing a clump of four
                while ( nbrIt.hasNext() ) {
                    // if its the same string then there is nothing to merge
                    nbrStone = (GoBoardPosition) nbrIt.next();
                    GoString nbrString = nbrStone.getString();
                    if ( str != nbrString )   {
                        str.merge( nbrString, board );
                    }
                }
            }
            // now that we have merged the stone into a new string, we need to verify that that string is not in atari.
            // if it is, then we need to split that ataried string off from its group and form a new group.
            if (stone.isInAtari(board)) {
                GoGroup oldGroup = str.getGroup();
                GameContext.log(3, "Before splitting off ataried string (due to "+stone+") containing ("+str+") we have: "+oldGroup);

                oldGroup.remove(str);
                GoGroup newGroup = new GoGroup(str);
                //GameContext.log(3, "after splitting we have: "+newGroup);
                assert (!newGroup.getMembers().isEmpty()) : "The group we are trying to add is empty";
                board.getGroups().add(newGroup);
            }
        }
        cleanupGroups(board);
        GoBoard.getProfiler().stopUpdateStringsAfterMove();
    }



    /**
     * update the strings after a stone has been removed.
     * Some friendly strings may have been split by the removal.
     * @param stone that was removed.
     * @param string that the stone belonged to.
     */
    private static void updateStringsAfterRemoving( GoBoardPosition stone, GoString string,  GoBoard board )
    {
        GoBoard.getProfiler().startUpdateStringsAfterRemove();

        // avoid error when calling from treeDlg
        if (string == null) return;
        //assert notNull(string, "null string after removing stone.");

        GoGroup group = string.getGroup();
        Set nbrs = board.getNobiNeighbors( stone, group.isOwnedByPlayer1(), NeighborType.FRIEND );
        if ( string.size() == 0 ) {
            //GameContext.log( 2, "ERROR: string size = 0" );  // assert?
            return;
        }
        // make new string(s) if removing the stone has caused a larger string to be split.
        if ( nbrs.size() > 1 ) {
            Iterator nbrIt = nbrs.iterator();
            List lists = new ArrayList(8);
            GoBoardPosition firstNbr = (GoBoardPosition) nbrIt.next();
            List stones = board.findStringFromInitialPosition( firstNbr, false );
            lists.add( stones );
            while ( nbrIt.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
                if ( !nbrStone.isVisited() ) {
                    List stones1 = board.findStringFromInitialPosition( nbrStone, false );
                    GoString newString = new GoString( stones1, board );
                    group.addMember( newString, board );
                    // string.remove( stones1, this );  // already done in the process of creating the new string.
                    lists.add( stones1 );
                }
            }
            GoBoardUtil.unvisitPositionsInLists( lists );
        }
        //cleanupGroups();
        if ( GameContext.getDebugMode() > 1 ) {
            GoBoardUtil.confirmNoEmptyStrings(board.getGroups());
            GoBoardUtil.confirmStonesInValidGroups(board.getGroups(), board);
            GoBoardUtil.confirmStonesInOneGroup( group, board.getGroups() );
        }
        GoBoard.getProfiler().stopUpdateStringsAfterRemove();
    }



    /**
     * Update the groups after a stone has been removed (and captures replaced perhaps).
     * Some friendly groups may have been split by the removal, while
     * some enemy groups may need to be rejoined.
     *
     * @param stone that was removed (actually, its just the position, the stone has been removed).
     * @param string the string that the stone was removed from.
     */
    private void updateGroupsAfterRemoving( GoBoardPosition stone, GoString string, GoBoard board )
    {
        GoBoard.getProfiler().startUpdateGroupsAfterRemove();

        if ( string == null ) {
            if ( GameContext.getDebugMode() > 1 )
                GoBoardUtil.confirmStonesInValidGroups(board.getGroups(), board);
            return;
        }

        GoGroup group = string.getGroup();

        // if the string that the stone is being removed from was considered unconditionally alive,
        // then we need to clear out all the unconditionally alive information for this group since it is now invalid.
        if (string.isUnconditionallyAlive()) {
            for (Object s : group.getMembers())  {
                GoString str = (GoString) s;
                str.setUnconditionallyAlive(false);
                str.setNbrs(null);
            }
            for (Object e : group.getEyes())  {
                GoEye eye = (GoEye) e;
                eye.setUnconditionallyAlive(false);
                eye.setNbrs(null);
            }
        }

        Set nbrs = board.getGroupNeighbors( stone, group.isOwnedByPlayer1(), false );

        // create a set of friendly group nbrs and a separate set of enemy ones.
        Set friendlyNbrs = new HashSet(10);
        Set enemyNbrs = new HashSet(10);
        Iterator nbrIt = nbrs.iterator();
        while ( nbrIt.hasNext() ) {
            GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
            if ( nbrStone.getPiece().isOwnedByPlayer1() == group.isOwnedByPlayer1() )
                friendlyNbrs.add( nbrStone );
            else
                enemyNbrs.add( nbrStone );
        }

        // check for friendly groups that have been split by the removal
        updateFriendlyGroupsAfterRemoval(friendlyNbrs, board);

        // now check for enemy groups that have been rejoined by the removal.
        // in the most extreme case there could be 4 groups that we need to add back.
        //  eg: 4 ataried strings that are restored by the removal of this stone.
        updateEnemyGroupsAfterRemoval(enemyNbrs, board);

        if ( GameContext.getDebugMode() > 1 )  {
            GoBoardUtil.confirmNoEmptyStrings(board.getGroups());
        }

        cleanupGroups(board);

        GoBoard.getProfiler().stopUpdateGroupsAfterRemove();
    }



    /**
     * Update friendly groups that may have been split (or joined) by the removal of stone.
     * @param friendlyNbrs nbrs that are on the same side as stone (just removed)
     */
    private static void updateFriendlyGroupsAfterRemoval(Set friendlyNbrs, GoBoard board) {

        if ( GameContext.getDebugMode() > 1 )  { // in a state were not necessarily in valid groups?
             GoBoardUtil.confirmStonesInValidGroups(board.getGroups(), board);
        }
        if ( friendlyNbrs.size() > 0) {
            // need to search even if just 1 nbr since the removal of the stone may cause a string to no longer be
            // in atari and rejoin a group.

            Iterator friendIt = friendlyNbrs.iterator();
            List lists = new ArrayList();

            while ( friendIt.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) friendIt.next();

                if ( !nbrStone.isVisited() ) {
                    List stones1 = board.findGroupFromInitialPosition( nbrStone, false );
                    board.removeGroupsForListOfStones(stones1);

                    if ( !GoBoardUtil.groupAlreadyExists( stones1, board) ) {
                        // this is not necessarily the case.
                        // if we remove a stone from a string that is in atari, that string may rejoin a group.
                        //assert (stones1.size() < group.getNumStones()) : "**Error after removing "+stone +
                        //        "\n"+stones1+" ("+stones1.size()+") is not a subset of "+group;

                        GoGroup newGroup = new GoGroup( stones1 );
                        board.getGroups().add( newGroup );
                        //group.remove( stones1 );

                        if ( GameContext.getDebugMode() > 1 )
                            GoBoardUtil.confirmStonesInOneGroup( newGroup, board.getGroups() );
                    }
                    lists.add( stones1 );
                }
            }

            GoBoardUtil.unvisitPositionsInLists( lists );
            if ( GameContext.getDebugMode() > 1 ) {
               GoBoardUtil.confirmStonesInValidGroups( board.getGroups(), board );
            }
        }
    }

    /**
     * @param enemyNbrs enemy nbrs of the stone that was removed.
     */
    private static void updateEnemyGroupsAfterRemoval(Set enemyNbrs, GoBoard board)
    {

        if ( enemyNbrs.size() > 0 ) {

            Iterator enemyIt = enemyNbrs.iterator();
            // we need to find the mergedGroup(s) from stones in the enemy nbr list.
            List mergedGroupLists = new ArrayList();
            while (enemyIt.hasNext()) {
                GoBoardPosition seed = (GoBoardPosition)enemyIt.next();
                List mergedStones = board.findGroupFromInitialPosition( seed ); // the restored merged group
                // add the mergedStones to the list only if the seed is not already a member of one of the lists
                boolean newList = true;
                Iterator lit = mergedGroupLists.iterator();
                while (lit.hasNext() && newList) {
                    List mgl = (List)lit.next();
                    if (mgl.contains(seed))
                        newList = false;
                }
                if (newList)
                    mergedGroupLists.add(mergedStones);
            }
            if (mergedGroupLists.size() > 1) {
                GameContext.log(2, "More than one merged group:"+mergedGroupLists.size());
            }

            //GameContext.log( 0, "The enemy group *MERGED* by removing ("+stone+") and seeded by ("+firstStone+") is " + mergedStones );

            GoGroup restoredGroup;
            if (mergedGroupLists.size() > 0)  {
                Iterator mgIt = mergedGroupLists.iterator();
                while (mgIt.hasNext()) {
                    List mergedStones = (List)mgIt.next();

                    // remove all the old groups and replace them with the big ones
                    board.removeGroupsForListOfStones(mergedStones);

                    restoredGroup = new GoGroup( mergedStones );

                    board.getGroups().add( restoredGroup );
                }
                if ( GameContext.getDebugMode() > 1 ) {
                    GoBoardUtil.confirmStonesInValidGroups(board.getGroups(), board);
                    GoBoardUtil.confirmAllStonesInGroupsClaimed(board.getGroups(), board);
                }
            }
        }
    }


    /**
     * Determine a list of enemy stones that are captured when this stone is played on the board.
     * In other words determine all opponent strings (at most 4) whose last liberty is at the new stone location.
     */
    private void determineCaptures(GoBoardPosition stone, GoBoard board)
    {
        GoBoard.getProfiler().start(GoProfiler.FIND_CAPTURES);
        assert ( stone!=null );
        Set nbrs = board.getNobiNeighbors( stone, NeighborType.ENEMY );
        CaptureList captureList = null;
        Iterator it = nbrs.iterator();
        // keep track of the strings captured so we don't capture the same one twice
        Set capturedStrings = new HashSet();

        while ( it.hasNext() ) {
            GoBoardPosition enbr = (GoBoardPosition) it.next();
            assert (enbr.isOccupied()): "enbr="+enbr;

            GoString str = enbr.getString();
            assert ( str.isOwnedByPlayer1() != stone.getPiece().isOwnedByPlayer1()): "The "+str+" is not an enemy of "+stone;
            if ( str.getNumLiberties(board) == 0 && str.size() > 0 && !capturedStrings.contains(str) ) {
                capturedStrings.add( str );
                // we need to add copies so that when the original stones on the board are
                // changed we don't change the captures
                if ( captureList == null )
                    captureList = new CaptureList();
                captureList.addAllCopied( str.getMembers() );
            }
        }
        GoBoard.getProfiler().stop(GoProfiler.FIND_CAPTURES);
        captureList_ =  captureList;
    }


    private void removeCaptures(GoBoard board) {
        if ( getCaptures() != null ) {
            removeCapturesOnBoard( board );
            updateAfterRemovingCaptures( board );
            //GameContext.log( 2, "GoBoard: makeMove: " + this + "  groups after removing captures" );
            //GoBoardUtil.debugPrintGroups( 2, "Groups after removing captures", true, true, board.getGroups());
        }
    }


    /**
     * Make the positions on the board represented by the captureList show up empty.
     * Afterwards these empty spaces should not belong to any strings.
     */
    private void removeCapturesOnBoard(GoBoard board)
    {
        CaptureList captureList = getCaptures();

        // remove the captured strings from the owning group (there could be up to 4)
        GoString capString = ((GoBoardPosition) captureList.get( 0 )).getString();
        GoGroup group = capString.getGroup();
        Set capStrings = new HashSet();

        // we can't just call captureList.removeOnBoard because we need to do additional updates for go.
        Iterator it = captureList.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition capStone = (GoBoardPosition) it.next();
            capString = capStone.getString();
            // remove the captured strings from the group
            if ( !capStrings.contains( capString ) ) {
                capStrings.add( capString );

                group.remove( capString );

            }
            GoBoardPosition stoneOnBoard = (GoBoardPosition) board.getPosition( capStone.getRow(), capStone.getCol() );
            stoneOnBoard.clear(board);
            // ?? restore disconnected groups?
        }

        // if there are no more stones in the group, remove it.
        if ( group.getNumStones() == 0 ) {
            board.getGroups().remove( group );
        }

        adjustStringLiberties(captureList, board);
    }

    private static void adjustStringLiberties(CaptureList captureList, GoBoard board) {
        // update the liberties of the surrounding strings
        Iterator it = captureList.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition captured = (GoBoardPosition) it.next();
            GoBoardPosition newLiberty = (GoBoardPosition) board.getPosition( captured.getRow(), captured.getCol() );
            adjustLiberties(newLiberty, board);
        }
    }


    /**
     * OLD WAY (had some problems
     *
     * remove all the groups on the board.
     * Then for each stone, find its group and add that new group to the board's group list.
     * Continue until all stone accounted for.
     */
    private static void updateGroupsAfterMoving( GoBoardPosition pos, GoBoard board )
    {
        GoBoard.getProfiler().startUpdateGroupsAfterMove();

        if (GameContext.getDebugMode() > 1) {
            GoBoardUtil.confirmAllStonesInUniqueGroups(board.getGroups());
        }

        // remove all the current groups (we will then add them back)
        board.getGroups().clear();

        for ( int i = 1; i <= board.getNumRows(); i++ )  {
           for ( int j = 1; j <= board.getNumCols(); j++ ) {
               GoBoardPosition seed = (GoBoardPosition)board.getPosition(i, j);
               if (seed.isOccupied() && !seed.isVisited()) {
                   List newGroup = board.findGroupFromInitialPosition(seed, false);
                   GoGroup g = new GoGroup(newGroup);
                   board.getGroups().add(g);
               }
           }
        }
        unvisitAll(board);

        // verify that the string to which we added the stone has at least one liberty
        //assert (pos.getString().getNumLiberties(board) > 0): "The placed stone "+pos+" has no liberties "+pos.getGroup();

        // this gets used when calculating the worth of the board
        board.updateTerritory(pos);

        if ( GameContext.getDebugMode() > 1 ) {
            GoBoardUtil.confirmNoEmptyStrings(board.getGroups());
            GoBoardUtil.confirmStonesInValidGroups(board.getGroups(), board);
            GoBoardUtil.confirmAllStonesInUniqueGroups(board.getGroups());
            try {
                GoBoardUtil.confirmAllStonesInGroupsClaimed(board.getGroups(), board);
            } catch (AssertionError e) {
                GameContext.log(1, "The move was :"+pos);
                throw e;
            }
        }

        GoBoard.getProfiler().stopUpdateGroupsAfterMove();
    }


    private static void unvisitAll(Board board)
    {
        for ( int i = 1; i <= board.getNumRows(); i++ ) {
            for ( int j = 1; j <= board.getNumCols(); j++ ) {
                GoBoardPosition pos = (GoBoardPosition) board.getPosition( i, j );
                pos.setVisited(false);
            }
        }
    }

    /**
     * After removing the captures, the stones surrounding the captures will form 1 (or sometimes 2)
     * cohesive group(s) rather than disparate ones.
     * There can be two if, for example, the capturing stone joins a string that is
     * still in atari after the captured stones have been removed.
     */
    private void updateAfterRemovingCaptures( GoBoard board )
    {
        GoBoardPosition finalStone = (GoBoardPosition) board.getPosition(getToRow(), getToCol());

        assert (finalStone != null);
        if ( finalStone == null )
            return;

        GoBoardPosition seedStone;
        // Its a bit of a special case if the finalStone's string is in atari after the capture.
        // The string that the finalStone belongs to is still cut from the larger joined group,
        // but we need to determine the big group from some other stone not from finalStone's string.
        if (finalStone.isInAtari(board)) {
            seedStone = findAlternativeSeed(finalStone, board);
        }
        else {
            seedStone = finalStone;
        }
        assert seedStone.isOccupied();
        List bigGroup = board.findGroupFromInitialPosition( seedStone );
        assert ( bigGroup.size() > 0 );

        board.removeGroupsForListOfStones(bigGroup);

        GoGroup newBigGroup = new GoGroup( bigGroup );
        board.getGroups().add( newBigGroup );
    }



    /**
     * We need to identify this pattern:
     *   #0
     *   0'
     *   #0
     * (or       #O
     *           O'
     *       if on the edge or corner)
     * Where the middle 0 is the stone passed in.
     * There are 4 cases to check
     * @param stone
     * @return one of the other 2 0's in the picture.
     */
    private GoBoardPosition findAlternativeSeed(GoBoardPosition stone, GoBoard board)
    {
        // List nbrs = this.getNobiNeighbors(stone, NeighborType.ANY)
        // After we find where the blank is we can pretty much just assert the other positions.
        int r = stone.getRow();
        int c = stone.getCol();

        GoBoardPosition alternative;
        alternative = getConfirmedAlternative(stone, r, c, 0, -1, board);
        if (alternative != null)
           return alternative;
        alternative = getConfirmedAlternative(stone, r, c, 0, 1, board);
        if (alternative != null)
           return alternative;
        alternative = getConfirmedAlternative(stone, r, c, -1, 0, board);
        if (alternative != null)
           return alternative;
        alternative = getConfirmedAlternative(stone, r, c, 1, 0, board);
        if (alternative != null)
           return alternative;
        assert (false) : "There was no alternative seed for "+stone +" board:\n"+this;
        return stone;
    }


    /**
     *
     * @param stone to find alternative for.
     * @return null if no alternative found
     */
    private static GoBoardPosition getConfirmedAlternative(BoardPosition stone,
                                                    int r, int c, int rowOffset, int colOffset, GoBoard board)
    {
        BoardPosition blankPos = board.getPosition(r + rowOffset, c + colOffset);
        if (blankPos != null && blankPos.isUnoccupied()) {
            BoardPosition enemy1;
            BoardPosition enemy2;
            if (rowOffset == 0) {
                enemy1 = board.getPosition(r + 1, c + colOffset);
                enemy2 = board.getPosition(r - 1, c + colOffset);
            }
            else {
                assert (colOffset == 0);
                enemy1 = board.getPosition(r + rowOffset, c + 1);
                enemy2 = board.getPosition(r + rowOffset, c - 1);
            }
            if (enemy1 != null && enemy2 != null) {
                assert( enemy1.getPiece().isOwnedByPlayer1() == enemy2.getPiece().isOwnedByPlayer1()
                    && enemy1.getPiece().isOwnedByPlayer1() == stone.getPiece().isOwnedByPlayer1()) :
                    "unexpected ownership (e1="+enemy1.getPiece().isOwnedByPlayer1()+",e2="+enemy2.getPiece().isOwnedByPlayer1()
                        +") for "+enemy1+" and "+enemy2+" based on "+stone+". Blank="+blankPos;
            }
            if (enemy1 != null && enemy1.isOccupied())
                return (GoBoardPosition)enemy1;
            else
                return (GoBoardPosition)enemy2;
        }
        else
            return null;
    }

    /**
     * restore this moves captures stones on the board
     * @param board
     */
    private  void restoreCaptures(GoBoard board) {
        if ( getCaptures() != null ) {
            restoreCapturesOnBoard(board);
            updateAfterRestoringCaptures(board);
            if (GameContext.getDebugMode() > 1) {
                GoBoardUtil.confirmStonesInValidGroups(board.getGroups(), board);
                GoBoardUtil.confirmAllStonesInUniqueGroups(board.getGroups());
                GameContext.log( 3, "GoBoard: undoInternalMove: " + this + "  groups after restoring captures:" );
            }
        }
    }


    /**
     * put the captures back on the board.
     */
    private void restoreCapturesOnBoard( GoBoard board )
    {
        CaptureList captureList = getCaptures();

        captureList.restoreOnBoard( board );

        //GameContext.log( 2, "GoMove: restoring these captures: " + captureList );
        Iterator it = captureList.iterator();
        List restoredList = new LinkedList();
        while ( it.hasNext() ) {
            GoBoardPosition capStone = (GoBoardPosition) it.next();
            GoBoardPosition stoneOnBoard = (GoBoardPosition) board.getPosition( capStone.getRow(), capStone.getCol() );
            stoneOnBoard.setVisited(false);    // make sure in virgin unvisited state

            //adjustLiberties(stoneOnBoard, board);
            restoredList.add( stoneOnBoard );
        }

        // there may have been more than one string in the capturelist
        List strings = new LinkedList();
        it = restoredList.iterator();

        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            if ( !s.isVisited() ) {
                List string1 = board.findStringFromInitialPosition( s, false );
                strings.add( string1 );
            }
        }
        adjustStringLiberties(captureList, board);


        // ?? form new group, or check group nbrs to see if we can add to an existing one
        boolean firstString = true;
        it = strings.iterator();
        GoGroup group = null;
        while ( it.hasNext() ) {
            List stringList = (List) it.next();
            GoString string = new GoString( stringList, board );
            if ( firstString ) {
                group = new GoGroup( string );
                firstString = false;
            }
            else {
                group.addMember( string, board );
                //GameContext.log( 2, "GoMove: restoring ----------------" + string );
            }
            string.unvisit();
        }

        assert ( group!=null): "no group was formed when restoring "+restoredList+" the list of strings was "+strings;
        board.getGroups().add( group );
    }


    /**
     * After restoring the captures, the stones surrounding the captures will probably
     * form disparate groups rather than 1 cohesive one.
     */
    private void updateAfterRestoringCaptures( GoBoard board)
    {
        CaptureList captures = getCaptures();

        if ( GameContext.getDebugMode() > 1 )
             GoBoardUtil.confirmStonesInValidGroups(board.getGroups(), board);

        List enemyNobiNbrs = new LinkedList();
        Iterator captureIt = captures.iterator();
        // find all the enemy neighbors of the stones in the captured group being restored.
        while ( captureIt.hasNext() ) {
            GoBoardPosition capture = (GoBoardPosition) captureIt.next();
            Set enns = board.getNobiNeighbors( capture, NeighborType.ENEMY );
            enemyNobiNbrs.addAll( enns );
        }
        // in some bizarre cases there might actually be no enemy nobi nbrs
        // (such as when one stone killed all the stones on the board?)
        if (enemyNobiNbrs.size() == 0) {
            GameContext.log(0, "The restored captures ("+captures+") have no enemy neighbors (very strange!)" );
            return;
        }
        GoBoardPosition firstEnemyStone = (GoBoardPosition) enemyNobiNbrs.get( 0 );
        GoGroup bigEnemyGroup = firstEnemyStone.getGroup();

        // The bigEnemyGroup may not actually contain all the enemy nobi neighbors.
        // Although rare, one example where this is the case is when the restored group has a string
        // that is in atari. By our definition of group, ataried strings are not part of larger groups.
        // This is also known as a snapback stuation.
        // It is true, I believe, that there could not be more than 2 adjacent enemy nbr groups just befoer
        // replacing the capture. Because if there were, one of them would have to have had no liberties.
        // I think its impossible that restoring a captured group this way will cause a capture since
        // we restore only by first removing a piece in undo move
        Iterator ennIt = enemyNobiNbrs.iterator();

        GoGroup secondaryEnemyGroup = null;
        while ( ennIt.hasNext() ) {
            GoBoardPosition enn = (GoBoardPosition) ennIt.next();
            if ( !bigEnemyGroup.containsStone( enn ))  {
                secondaryEnemyGroup = enn.getGroup();
                break;
            }
        }

        // now replace the bigEnemyGroup (and secondaryEnemyGroup if it exists)
        // by the potentially disparate smaller ones.
        List listsToUnvisit = new ArrayList();
        Set gStones = bigEnemyGroup.getStones();

        board.getGroups().remove( bigEnemyGroup );
        if (secondaryEnemyGroup != null) {
            GameContext.log(1, "There was a secondary enemy group before restoring (*RARE*). The 2 groups were :" +bigEnemyGroup+" and "+secondaryEnemyGroup);
            board.getGroups().remove(secondaryEnemyGroup);
        }

        // compine all the enmey nobi nbrs with the stones from the bigEnemyGroup when trying to find the new groups.
        List enemyNbrs = new ArrayList(enemyNobiNbrs);
        enemyNbrs.addAll(gStones);
        ennIt = enemyNbrs.iterator();
        while ( ennIt.hasNext() ) {
            GoBoardPosition enn = (GoBoardPosition) ennIt.next();
            if ( !enn.isVisited() ) {
                List list = board.findGroupFromInitialPosition( enn, false );
                listsToUnvisit.add( list );
            }
        }

        Iterator it = listsToUnvisit.iterator();
        while ( it.hasNext() ) {
            List list = (List) it.next();
            GoBoardUtil.unvisitPositions( list );
            GoGroup group = new GoGroup( list );
            if (GameContext.getDebugMode() > 1) {
                GoBoardUtil.confirmStonesInOneGroup(group, board.getGroups());
                GameContext.log( 2, "updateAfterRestoringCaptures("+captures+"): adding sub group :" + group );
            }
            board.getGroups().add( group );
        }
        if ( GameContext.getDebugMode() > 1 )
             GoBoardUtil.confirmStonesInValidGroups(board.getGroups(), board);
    }



    /**
     * remove groups that have no stones in them.
     */
    private void cleanupGroups(GoBoard board)
    {
        Iterator it = board.getGroups().iterator();
        while ( it.hasNext() ) {
            GoGroup group = (GoGroup) it.next();
            //group.confirmNoNullMembers();
            if ( group.getNumStones() == 0 )  {
                assert (group.getEyes().isEmpty()): group+ " has eyes! It was assumed not to.\n"+this;
                it.remove();
            }
        }
    }



    /**
     * return the SGF (4) representation of the move
     * SGF stands for Smart Game Format and is commonly used for Go
     */
    public String getSGFRepresentation()
    {
        // passes are not represented in SGF - so just skip it if the piece is null.
        if (getPiece() == null)
             return "[]";
        StringBuffer buf = new StringBuffer("");
        char player = 'W';
        if ( getPiece().isOwnedByPlayer1() )
            player = 'B';
        buf.append( ';' );
        buf.append( player );
        buf.append( '[' );
        buf.append( (char) ('a' + toCol_ - 1) );
        buf.append( (char) ('a' + toRow_ - 1) );
        buf.append( ']' );
        buf.append( '\n' );
        return buf.toString();
    }

    public String toString()
    {
        String s = super.toString();
        if ( captureList_ != null ) {
            s += "num captured="+captureList_.size();
        }
        return s;
    }

}



