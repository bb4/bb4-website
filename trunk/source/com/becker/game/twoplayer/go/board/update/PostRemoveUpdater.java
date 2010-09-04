package com.becker.game.twoplayer.go.board.update;

import com.becker.game.common.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.*;
import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.becker.game.twoplayer.go.board.elements.*;

import java.util.*;

/**
 * Responsible for updating a go board after undoing a move.
 *
 * @author Barry Becker
 */
public class PostRemoveUpdater extends PostChangeUpdater {

    PostRemoveUpdater(GoBoard board, Captures captures) {
        super(board, captures);
    }

    /**
     * Update strings and groups after a move was undone.
     * @param move move that was just removed.
     */
    @Override
    public void update(GoMove move) {

        GoBoardPosition stone =  (GoBoardPosition) (getBoard().getPosition(move.getToRow(), move.getToCol()));

        GoString stringThatItBelongedTo = stone.getString();
        // clearing a stone may cause a string to split into smaller strings
        stone.clear(getBoard());
        adjustLiberties(stone);

        updateStringsAfterRemove( stone, stringThatItBelongedTo);
        restoreCaptures(move.getCaptures());
        updateGroupsAfterRemove( stone, stringThatItBelongedTo);

        captures_.updateCaptures(move, false);
    }

    /**
     * update the strings after a stone has been removed.
     * Some friendly strings may have been split by the removal.
     * @param stone that was removed.
     * @param string that the stone belonged to.
     */
    private void updateStringsAfterRemove( GoBoardPosition stone, GoString string )
    {
        GoProfiler profiler = GoProfiler.getInstance();
        profiler.startUpdateStringsAfterRemove();

        // avoid error when calling from treeDlg
        if (string == null) return;

        GoGroup group = string.getGroup();
        GoBoardPositionSet nbrs =
                nbrAnalyzer_.getNobiNeighbors( stone, group.isOwnedByPlayer1(), NeighborType.FRIEND );
        //assert (string.size() > 0) : " String has 0 members! " + string;

        splitStringsIfNeeded(group, nbrs);

        if ( GameContext.getDebugMode() > 1 ) {
            getAllGroups().confirmNoEmptyStrings();
            validator_.confirmStonesInValidGroups();
            getAllGroups().confirmStonesInOneGroup(group);
        }
        profiler.stopUpdateStringsAfterRemove();
    }

    /**
     * Make new string(s) if removing the stone has caused a larger string to be split.
     * @param group group that may have been split by the removal of the stone.
     * @param nbrs stones that are neighbors of the stone that was removed.
     */
    private void splitStringsIfNeeded(GoGroup group, GoBoardPositionSet nbrs) {

        if ( nbrs.size() > 1 ) {
            Iterator nbrIt = nbrs.iterator();
            List<GoBoardPositionList> lists = new ArrayList<GoBoardPositionList>(8);
            GoBoardPosition firstNbr = (GoBoardPosition) nbrIt.next();
            GoBoardPositionList stones = nbrAnalyzer_.findStringFromInitialPosition( firstNbr, false );
            lists.add( stones );
            while ( nbrIt.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
                if ( !nbrStone.isVisited() ) {
                    GoBoardPositionList stones1 = nbrAnalyzer_.findStringFromInitialPosition( nbrStone, false );
                    GoString newString = new GoString( stones1, getBoard() );
                    group.addMember( newString);
                    lists.add( stones1 );
                }
            }
            GoBoardUtil.unvisitPositionsInLists( lists );
        }
    }

    /**
     * If there is not already a group for stones, then create one.
     * If we remove a stone from a string that is in atari, that string may rejoin a group.
     * @param stones stones to create the new group from.
     */
    private void createNewGroupIfNeeded(GoBoardPositionList stones) {
        if ( !groupAlreadyExists( stones) ) {
            //
            GoGroup newGroup = new GoGroup( stones );
            getAllGroups().add( newGroup );
            //group.remove( stones );

            if ( GameContext.getDebugMode() > 1 )
                getAllGroups().confirmStonesInOneGroup( newGroup );
        }
    }

    /**
     * @param enemyNbrs enemy neighbor stones.
     * @return The mergedGroup(s) from stones in the enemy nbr list.
     */
    private List<GoBoardPositionList> findMergedGroupLists(Set enemyNbrs) {
        Iterator enemyIt = enemyNbrs.iterator();
        List<GoBoardPositionList> mergedGroupLists = new ArrayList<GoBoardPositionList>();
        while (enemyIt.hasNext()) {
            GoBoardPosition seed = (GoBoardPosition)enemyIt.next();
            checkEnemyPoistionForMergeGroup(seed, mergedGroupLists);
        }
        if (mergedGroupLists.size() > 1) {
            GameContext.log(2, "More than one merged group:"+mergedGroupLists.size());
        }
        return mergedGroupLists;
    }

    /**
     * add the mergedStones to the list only if the seed is not already a member of one of the lists.
     */
    private void checkEnemyPoistionForMergeGroup(GoBoardPosition seed, List<GoBoardPositionList> mergedGroupLists) {
        // the restored merged group
        GoBoardPositionList mergedStones = nbrAnalyzer_.findGroupFromInitialPosition( seed );

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

    /**
     * restore this moves captures stones on the board
     * @param captures list of captures to remove.
     */
    private void restoreCaptures(CaptureList captures) {
        if ( captures != null ) {
            restoreCapturesOnBoard(captures);
            updateAfterRestoringCaptures(captures);
            if (GameContext.getDebugMode() > 1) {
                validator_.confirmStonesInValidGroups();
                getAllGroups().confirmAllStonesInUniqueGroups();
                GameContext.log( 3, "GoBoard: undoInternalMove: " + getBoard() + "  groups after restoring captures:" );
            }
        }
    }

    /**
     * put the captures back on the board.
     */
    private void restoreCapturesOnBoard( CaptureList captureList )
    {
        captureList.restoreOnBoard( getBoard() );

        GameContext.log( 3, "GoMove: restoring these captures: " + captureList );

        List<GoBoardPositionList> strings = getRestoredStringList(captureList);
        adjustStringLiberties(captureList);
        GoGroup group = getRestoredGroup(strings);

        assert ( group!=null): "no group was formed when restoring "
                + captureList + " the list of strings was "+strings;
        getAllGroups().add( group );
    }

    /**
     * There may have been more than one string in the captureList
     * @return list of strings that were restored ont he board.
     */
    private List<GoBoardPositionList> getRestoredStringList(CaptureList captureList) {

        GoBoardPositionList restoredList = getRestoredList(captureList);
        List<GoBoardPositionList> strings = new LinkedList<GoBoardPositionList>();

        for (GoBoardPosition s : restoredList) {
            if (!s.isVisited()) {
                GoBoardPositionList string1 = nbrAnalyzer_.findStringFromInitialPosition(s, false);
                strings.add(string1);
            }
        }
        return strings;
    }

    /**
     * @return list of captured stones that were restored on the board.
     */
    private GoBoardPositionList getRestoredList(CaptureList captureList) {
        Iterator it = captureList.iterator();
        GoBoardPositionList restoredList = new GoBoardPositionList();
        while ( it.hasNext() ) {
            GoBoardPosition capStone = (GoBoardPosition) it.next();
            GoBoardPosition stoneOnBoard =
                    (GoBoardPosition) getBoard().getPosition( capStone.getRow(), capStone.getCol() );
            stoneOnBoard.setVisited(false);    // make sure in virgin unvisited state

            //adjustLiberties(stoneOnBoard, board);
            restoredList.add( stoneOnBoard );
        }
        return restoredList;
    }

    /**
     * @return the group that was restored when the captured stones were replaced on the board.
     */
    private GoGroup getRestoredGroup(List<GoBoardPositionList> strings) {
        // ?? form new group, or check group nbrs to see if we can add to an existing one.
        boolean firstString = true;
        GoGroup group = null;
        for  (GoBoardPositionList stringList : strings) {
            GoString string = new GoString( stringList, getBoard() );
            if ( firstString ) {
                group = new GoGroup( string );
                firstString = false;
            }
            else {
                group.addMember( string);
                //GameContext.log( 2, "GoMove: restoring ----------------" + string );
            }
            string.unvisit();
        }
        return group;
    }

    /**
     * After restoring the captures, the stones surrounding the captures will probably
     * form disparate groups rather than 1 cohesive one.
     */
    private void updateAfterRestoringCaptures( CaptureList captures)
    {
        if ( GameContext.getDebugMode() > 1 ) {
             validator_.confirmStonesInValidGroups();
        }

        GoBoardPositionList enemyNobiNbrs = getEnemyNeighbors(captures);
        // in some bizarre cases there might actually be no enemy nobi neighbors
        // (such as when one stone killed all the stones on the board?)
        if (enemyNobiNbrs.size() == 0) {
            GameContext.log(0, "The restored captures ("+captures+") have no enemy neighbors (very strange!)" );
            return;
        }

        updateGroupsAfterRestoringCaptures(enemyNobiNbrs);

        if ( GameContext.getDebugMode() > 1 ) {
             validator_.confirmStonesInValidGroups();
        }
    }

    private void updateGroupsAfterRestoringCaptures(GoBoardPositionList enemyNobiNbrs) {

        List<GoBoardPositionList> listsToUnvisit = replaceBigEnemyGroupWithSmallerOnes(enemyNobiNbrs);

        for (GoBoardPositionList list : listsToUnvisit) {
            GoBoardUtil.unvisitPositions(list);
            GoGroup group = new GoGroup(list);
            if (GameContext.getDebugMode() > 1) {
                getAllGroups().confirmStonesInOneGroup(group);
                GameContext.log(2, "updateAfterRestoringCaptures(): adding sub group :" + group);
            }
            getAllGroups().add(group);
        }
    }

    /**
     * Replace the bigEnemyGroup (and secondaryEnemyGroup if it exists)
     * by the potentially disparate smaller ones.
     * @return lists of stones to unvisit.
     */
    private List<GoBoardPositionList> replaceBigEnemyGroupWithSmallerOnes(GoBoardPositionList enemyNobiNbrs) {
        GoBoardPosition firstEnemyStone = enemyNobiNbrs.get(0);
        GoGroup bigEnemyGroup = firstEnemyStone.getGroup();
        GoGroup secondaryEnemyGroup = findSecondaryEnemyGroup(enemyNobiNbrs, bigEnemyGroup);
        Iterator ennIt;

        //
        List<GoBoardPositionList> listsToUnvisit = new ArrayList<GoBoardPositionList>();
        GoBoardPositionSet gStones = bigEnemyGroup.getStones();

        // create a copy because we need to make modifications.
        GoGroupSet groupsCopy = new GoGroupSet(getAllGroups());
        groupsCopy.remove( bigEnemyGroup );
        if (secondaryEnemyGroup != null) {
            GameContext.log(1, "There was a secondary enemy group before restoring (*RARE*). The 2 groups were :" +
                               bigEnemyGroup+" and "+secondaryEnemyGroup);
            groupsCopy.remove(secondaryEnemyGroup);
        }
        board_.setGroups(groupsCopy);

        // Combine all the enemy nobi nbrs with the stones from the bigEnemyGroup when trying to find the new groups.
        GoBoardPositionList enemyNbrs = new GoBoardPositionList(enemyNobiNbrs);
        enemyNbrs.addAll(gStones);
        ennIt = enemyNbrs.iterator();
        while ( ennIt.hasNext() ) {
            GoBoardPosition enn = (GoBoardPosition) ennIt.next();
            if ( !enn.isVisited() ) {
                GoBoardPositionList list = nbrAnalyzer_.findGroupFromInitialPosition( enn, false );
                listsToUnvisit.add( list );
            }
        }
        return listsToUnvisit;
    }

    /**
     * @return  all the enemy neighbors of the stones in the captured group being restored.
     */
    private GoBoardPositionList getEnemyNeighbors(CaptureList captures) {
        GoBoardPositionList enemyNobiNbrs = new GoBoardPositionList();
        for (Object capture1 : captures) {
            GoBoardPosition capture = (GoBoardPosition) capture1;
            GoBoardPositionSet enns = nbrAnalyzer_.getNobiNeighbors(capture, NeighborType.ENEMY);
            enemyNobiNbrs.addAll(enns);
        }
        return enemyNobiNbrs;
    }

    /**
     * The bigEnemyGroup may not actually contain all the enemy nobi neighbors.
     * Although rare, one example where this is the case is when the restored group has a string
     * that is in atari. By our definition of group, ataried strings are not part of larger groups.
     * This is also known as a snapback situation.
     * It is true, I believe, that there could not be more than 2 adjacent enemy nbr groups just before
     * replacing the capture. Because if there were, one of them would have to have had no liberties.
     * I think its impossible that restoring a captured group this way will cause a capture since
     * we restore only by first removing a piece in undo move.
     * @param enemyNobiNbrs  adjacent enemies.
     * @param bigEnemyGroup big group that the enemies blong to.
     * @return secondary enemy group if there is one.
     */
    private GoGroup findSecondaryEnemyGroup(GoBoardPositionList enemyNobiNbrs, GoGroup bigEnemyGroup) {

        Iterator ennIt = enemyNobiNbrs.iterator();

        GoGroup secondaryEnemyGroup = null;
        while ( ennIt.hasNext() ) {
            GoBoardPosition enn = (GoBoardPosition) ennIt.next();
            if ( !bigEnemyGroup.containsStone( enn ))  {
                secondaryEnemyGroup = enn.getGroup();
                break;
            }
        }
        return secondaryEnemyGroup;
    }

    /**
     * Update the groups after a stone has been removed (and captures replaced perhaps).
     * Some friendly groups may have been split by the removal, while
     * some enemy groups may need to be rejoined.
     *
     * @param stone that was removed (actually, its just the position, the stone has been removed).
     * @param string the string that the stone was removed from.
     */
    private void updateGroupsAfterRemove( GoBoardPosition stone, GoString string )
    {
        GoProfiler profiler = GoProfiler.getInstance();
        profiler.startUpdateGroupsAfterRemove();

        if ( string == null ) {
            if ( GameContext.getDebugMode() > 1 )
                validator_.confirmStonesInValidGroups();
            return;
        }

        createAliveStatus(string);

        GoGroup group = string.getGroup();
        Set nbrs = nbrAnalyzer_.findGroupNeighbors( stone, group.isOwnedByPlayer1(), false );

        // create a set of friendly group nbrs and a separate set of enemy ones.
        GoBoardPositionSet friendlyNbrs = new GoBoardPositionSet();
        GoBoardPositionSet enemyNbrs = new GoBoardPositionSet();
        for (Object nbr : nbrs) {
            GoBoardPosition nbrStone = (GoBoardPosition) nbr;
            if (nbrStone.getPiece().isOwnedByPlayer1() == group.isOwnedByPlayer1())
                friendlyNbrs.add(nbrStone);
            else
                enemyNbrs.add(nbrStone);
        }

        // check for friendly groups that have been split by the removal
        updateFriendlyGroupsAfterRemoval(friendlyNbrs);

        // now check for enemy groups that have been rejoined by the removal.
        // in the most extreme case there could be 4 groups that we need to add back.
        //  eg: 4 ataried strings that are restored by the removal of this stone.
        updateEnemyGroupsAfterRemoval(enemyNbrs);

        if ( GameContext.getDebugMode() > 1 )  {
            getAllGroups().confirmNoEmptyStrings();
        }

        cleanupGroups();
        profiler.stopUpdateGroupsAfterRemove();
    }

    /**
     * Update friendly groups that may have been split (or joined) by the removal of stone.
     * @param friendlyNbrs nbrs that are on the same side as stone (just removed)
     */
    private void updateFriendlyGroupsAfterRemoval(Set friendlyNbrs) {

        if ( GameContext.getDebugMode() > 1 )  { // in a state were not necessarily in valid groups?
             validator_.confirmStonesInValidGroups();
        }
        if ( friendlyNbrs.size() > 0) {
            updateFriendlyGroups(friendlyNbrs);
        }
    }

    /**
     * Need to search even if just 1 nbr since the removal of the stone may cause a string to no longer be
     * in atari and rejoin a group.
     */
    private void updateFriendlyGroups(Set friendlyNbrs) {

        Iterator friendIt = friendlyNbrs.iterator();
        List<GoBoardPositionList> lists = new ArrayList<GoBoardPositionList>();

        while ( friendIt.hasNext() ) {
            GoBoardPosition nbrStone = (GoBoardPosition) friendIt.next();

            if ( !nbrStone.isVisited() ) {
                GoBoardPositionList stones1 = nbrAnalyzer_.findGroupFromInitialPosition( nbrStone, false );
                removeGroupsForListOfStones(stones1);
                createNewGroupIfNeeded(stones1);
                lists.add( stones1 );
            }
        }
        GoBoardUtil.unvisitPositionsInLists( lists );
        if ( GameContext.getDebugMode() > 1 ) {
            validator_.confirmStonesInValidGroups();
        }
    }


    /**
     * @param enemyNbrs enemy nbrs of the stone that was removed.
     */
    private void updateEnemyGroupsAfterRemoval(Set enemyNbrs)
    {
        if ( enemyNbrs.size() > 0 ) {

            List<GoBoardPositionList> mergedGroupLists = findMergedGroupLists(enemyNbrs);
            createRestoredGroups(mergedGroupLists);
        }
    }

    private void createRestoredGroups(List<GoBoardPositionList> mergedGroupLists) {

        for (GoBoardPositionList mergedStones : mergedGroupLists) {

            // remove all the old groups and replace them with the big ones
            removeGroupsForListOfStones(mergedStones);
            GoGroup restoredGroup = new GoGroup(mergedStones);

            getBoard().getGroups().add(restoredGroup);
        }
        if ( GameContext.getDebugMode() > 1 ) {
            validator_.confirmStonesInValidGroups();
            validator_.confirmAllStonesInGroupsClaimed(getBoard().getGroups());
        }
    }


    /**
     * if the string that the stone is being removed from was considered unconditionally alive,
     * then we need to clear out all the unconditionally alive information for the group since it is now invalid.
     * not to sure about this...
     * @param string string to determine if unconditionally alive or not and set status on.
     */
    private void createAliveStatus(GoString string) {

        if (string.isUnconditionallyAlive()) {
            GameContext.log(1, "Clearing alive status for group. String=" + string);
            GoGroup group = string.getGroup();
            for (Object s : group.getMembers())  {
                GoString str = (GoString) s;
                str.setUnconditionallyAlive(false);
            }
            Set<GoEye> eyes = group.getEyes(getBoard());
            for (GoEye eye : eyes)  {
                eye.setUnconditionallyAlive(false);
            }
        }
    }

    /**
     * return true if the stones in this list exactly match those in an existing group
     * @return true of group already exists on the board.
     */
    protected boolean groupAlreadyExists( GoBoardPositionList stones )
    {
        // first find the group that contains the stones
        for (GoGroup goGroup : getBoard().getGroups()) {
            if (goGroup.exactlyContains(stones))
                return true;
        }
        return false;
    }
}