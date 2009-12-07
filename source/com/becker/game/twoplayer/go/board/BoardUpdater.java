package com.becker.game.twoplayer.go.board;

import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import com.becker.game.twoplayer.go.board.analysis.NeighborAnalyzer;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoProfiler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Responsible for updating a go board after making or undoing a move.
 * 
 * @author Barry Becker
 */
public class BoardUpdater {

    /** The board to be updated. */
    GoBoard board_;
    
    private int numWhiteStonesCaptured_ = 0;
    private int numBlackStonesCaptured_ = 0;
    
    
    BoardUpdater(GoBoard board) {
        board_ = board;
    }
    
     public int getNumCaptures(boolean player1StonesCaptured) {
        return player1StonesCaptured ? numBlackStonesCaptured_ : numWhiteStonesCaptured_ ;
    }
    
    /**
     * Update the board after move has been played.
     * @param move the move that was just made
     */
    public void updateAfterMove(GoMove move) {
 
        GoBoardPosition stone = (GoBoardPosition) (board_.getPosition(move.getToRow(), move.getToCol()));
    
        adjustLiberties(stone);

        // hitting this all the time when showing game tree.
        //assert (stone.getString() == null) : stone +" already belongs to "+stone.getString();

        CaptureList captures = determineCaptures(stone);
        move.setCaptures(captures);
        
        updateStringsAfterMoving(stone);
        removeCaptures(move.getToRow(), move.getToCol(), captures);
        assert (stone.getString().getNumLiberties(board_) > 0): "The placed stone "+stone+" has no liberties "+stone.getGroup() +"\n"+ board_.toString();
        updateGroupsAfterMoving(stone);
        updateCaptures(move, true);
    }
    
    public void updateAfterRemove(GoMove move) {

         GoBoardPosition stone =  (GoBoardPosition) (board_.getPosition(move.getToRow(), move.getToCol()));

         GoString stringThatItBelongedTo = stone.getString();
         stone.clear(board_);   // clearing a stone may cause a string to split into smaller strings
         adjustLiberties(stone);

         updateStringsAfterRemoving( stone, stringThatItBelongedTo);
         restoreCaptures(move.getCaptures());
         updateGroupsAfterRemoving( stone, stringThatItBelongedTo);
         
         updateCaptures(move, false);
     }


    /**
     * @param move the move just made or removed.
     * @param increment if true then add to number of captures, else subtract.
     */
    private void updateCaptures(GoMove move, boolean increment) {

        int numCaptures = move.getNumCaptures();
        int num = increment ? move.getNumCaptures() : -move.getNumCaptures();

        if (numCaptures > 0) {
            if (move.isPlayer1()) {
                numWhiteStonesCaptured_ += num;
            } else {
                numBlackStonesCaptured_ += num;
            }
        }
    }
    
    /**
     * Examine the neighbors of this added stone and determine how the strings have changed.
     * For strings: examine the strongly connected neighbors. If more than one string borders, then
     * we merge the strings. If only one borders, then we add this stone to that string. If no strings
     * touch the added stone, then we create a new string containing only this stone.
     * @param stone the stone that was just placed on the board.
     */
    private void updateStringsAfterMoving( GoBoardPosition stone )
    {
        GoProfiler profiler = (GoProfiler)board_.getProfiler();
        profiler.startUpdateStringsAfterMove();

        Set<GoBoardPosition> nbrs = board_.getNobiNeighbors( stone, NeighborType.FRIEND );

        GoString str;
        if ( nbrs.size() == 0 ) {
            // there are no strongly connected nbrs, create a new string
            new GoString( stone, board_);  // stone points to the new string
        }
        else {
            // there is at least one nbr, so we join to it/them
            Iterator nbrIt = nbrs.iterator();
            GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
            str = nbrStone.getString();
            str.addMember( stone, board_ );
            BoardDebugUtil.debugPrintGroups( 3, "groups before merging:", true, true, board_.getGroups());

            if ( nbrs.size() > 1 ) {
                // then we probably need to merge the strings.
                // We would not, for example, if we are completing a clump of four
                while ( nbrIt.hasNext() ) {
                    // if its the same string then there is nothing to merge
                    nbrStone = (GoBoardPosition) nbrIt.next();
                    GoString nbrString = nbrStone.getString();
                    if ( str != nbrString )   {
                        str.merge( nbrString, board_ );
                    }
                }
            }
            // now that we have merged the stone into a new string, we need to verify that that string is not in atari.
            // if it is, then we need to split that ataried string off from its group and form a new group.
            if (stone.isInAtari(board_)) {
                GoGroup oldGroup = str.getGroup();
                GameContext.log(3, "Before splitting off ataried string (due to " + stone + ") containing (" +
                                   str + ") we have: " + oldGroup);

                oldGroup.remove(str);
                GoGroup newGroup = new GoGroup(str);
                //GameContext.log(3, "after splitting we have: "+newGroup);
                assert (!newGroup.getMembers().isEmpty()) : "The group we are trying to add is empty";
                board_.getGroups().add(newGroup);
            }
        }
        cleanupGroups();
        profiler.stopUpdateStringsAfterMove();
    }


    /**
     * Determine a list of enemy stones that are captured when this stone is played on the board.
     * In other words determine all opponent strings (at most 4) whose last liberty is at the new stone location.
     */
    private CaptureList determineCaptures(GoBoardPosition stone)
    {
        board_.getProfiler().start(GoProfiler.FIND_CAPTURES);
        assert ( stone!=null );
        Set nbrs = board_.getNobiNeighbors( stone, NeighborType.ENEMY );
        CaptureList captureList = null;
        Iterator it = nbrs.iterator();
        // keep track of the strings captured so we don't capture the same one twice
        Set<GoString> capturedStrings = new HashSet<GoString>();

        while ( it.hasNext() ) {
            GoBoardPosition enbr = (GoBoardPosition) it.next();
            assert (enbr.isOccupied()): "enbr="+enbr;

            GoString str = enbr.getString();
            assert ( str.isOwnedByPlayer1() != stone.getPiece().isOwnedByPlayer1()): "The "+str+" is not an enemy of "+stone;
            if ( str.getNumLiberties(board_) == 0 && str.size() > 0 && !capturedStrings.contains(str) ) {
                capturedStrings.add( str );
                // we need to add copies so that when the original stones on the board are
                // changed we don't change the captures
                if ( captureList == null )
                    captureList = new CaptureList();
                captureList.addAllCopied( str.getMembers() );
            }
        }
        board_.getProfiler().stop(GoProfiler.FIND_CAPTURES);
        return  captureList;
    }


    /**
     * Remove all thecaptures on the board.
     */
    private void removeCaptures(int toRow, int toCol, CaptureList captures) {
        if ( captures != null ) {
            removeCapturesOnBoard( captures );
            updateAfterRemovingCaptures( toRow, toCol );
            //GameContext.log( 2, "GoBoard: makeMove: " + this + "  groups after removing captures" );
            //GoBoardUtil.debugPrintGroups( 2, "Groups after removing captures", true, true, board.getGroups());
        }
    }


    /**
     * Make the positions on the board represented by the captureList show up empty.
     * Afterwards these empty spaces should not belong to any strings.
     */
    private void removeCapturesOnBoard(CaptureList captureList)
    {

        // remove the captured strings from the owning group (there could be up to 4)
        GoString capString = ((GoBoardPosition) captureList.get( 0 )).getString();
        GoGroup group = capString.getGroup();
        Set<GoString> capStrings = new HashSet<GoString>();

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
            GoBoardPosition stoneOnBoard = (GoBoardPosition)board_.getPosition( capStone.getRow(), capStone.getCol() );
            stoneOnBoard.clear(board_);
            // ?? restore disconnected groups?
        }

        // if there are no more stones in the group, remove it.
        if ( group.getNumStones() == 0 ) {
            board_.getGroups().remove( group );
        }

        adjustStringLiberties(captureList);
    }

    
    /**
     * update the strings after a stone has been removed.
     * Some friendly strings may have been split by the removal.
     * @param stone that was removed.
     * @param string that the stone belonged to.
     */
    private void updateStringsAfterRemoving( GoBoardPosition stone, GoString string )
    {
        GoProfiler profiler = (GoProfiler)board_.getProfiler();
        profiler.startUpdateStringsAfterRemove();

        // avoid error when calling from treeDlg
        if (string == null) return;
        //assert notNull(string, "null string after removing stone.");

        GoGroup group = string.getGroup();
        Set<GoBoardPosition> nbrs = 
                board_.getNobiNeighbors( stone, group.isOwnedByPlayer1(), NeighborType.FRIEND );
        if ( string.size() == 0 ) {
            //GameContext.log( 2, "ERROR: string size = 0" );  // assert?
            return;
        }
        // make new string(s) if removing the stone has caused a larger string to be split.
        if ( nbrs.size() > 1 ) {
            Iterator nbrIt = nbrs.iterator();
            List<List> lists = new ArrayList<List>(8);
            GoBoardPosition firstNbr = (GoBoardPosition) nbrIt.next();
            List stones = board_.findStringFromInitialPosition( firstNbr, false );
            lists.add( stones );
            while ( nbrIt.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
                if ( !nbrStone.isVisited() ) {
                    List<GoBoardPosition> stones1 = board_.findStringFromInitialPosition( nbrStone, false );
                    GoString newString = new GoString( stones1, board_ );
                    group.addMember( newString, board_ );
                    // string.remove( stones1, board_ );  // already done in the process of creating the new string.
                    lists.add( stones1 );
                }
            }
            GoBoardUtil.unvisitPositionsInLists( lists );
        }
        if ( GameContext.getDebugMode() > 1 ) {
            BoardValidationUtil.confirmNoEmptyStrings(board_.getGroups());
            BoardValidationUtil.confirmStonesInValidGroups(board_);
            BoardValidationUtil.confirmStonesInOneGroup( group, board_.getGroups() );
        }
        profiler.stopUpdateStringsAfterRemove();
    }
    
    /**
     * Update friendly groups that may have been split (or joined) by the removal of stone.
     * @param friendlyNbrs nbrs that are on the same side as stone (just removed)
     */
    private void updateFriendlyGroupsAfterRemoval(Set friendlyNbrs) {

        if ( GameContext.getDebugMode() > 1 )  { // in a state were not necessarily in valid groups?
             BoardValidationUtil.confirmStonesInValidGroups( board_);
        }
        if ( friendlyNbrs.size() > 0) {
            // need to search even if just 1 nbr since the removal of the stone may cause a string to no longer be
            // in atari and rejoin a group.

            Iterator friendIt = friendlyNbrs.iterator();
            List<List> lists = new ArrayList<List>();

            while ( friendIt.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) friendIt.next();

                if ( !nbrStone.isVisited() ) {
                    List<GoBoardPosition> stones1 = board_.findGroupFromInitialPosition( nbrStone, false );
                    removeGroupsForListOfStones(stones1);

                    if ( !groupAlreadyExists( stones1) ) {
                        // this is not necessarily the case.
                        // if we remove a stone from a string that is in atari, that string may rejoin a group.
                        //assert (stones1.size() < group.getNumStones()) : "**Error after removing "+stone +
                        //        "\n"+stones1+" ("+stones1.size()+") is not a subset of "+group;

                        GoGroup newGroup = new GoGroup( stones1 );
                        board_.getGroups().add( newGroup );
                        //group.remove( stones1 );

                        if ( GameContext.getDebugMode() > 1 )
                            BoardValidationUtil.confirmStonesInOneGroup( newGroup, board_.getGroups() );
                    }
                    lists.add( stones1 );
                }
            }

            GoBoardUtil.unvisitPositionsInLists( lists );
            if ( GameContext.getDebugMode() > 1 ) {
               BoardValidationUtil.confirmStonesInValidGroups( board_ );
            }
        }
    }

    /**
     * @param enemyNbrs enemy nbrs of the stone that was removed.
     */
    private void updateEnemyGroupsAfterRemoval(Set enemyNbrs)
    {

        if ( enemyNbrs.size() > 0 ) {

            Iterator enemyIt = enemyNbrs.iterator();
            // we need to find the mergedGroup(s) from stones in the enemy nbr list.
            List<List> mergedGroupLists = new ArrayList<List>();
            while (enemyIt.hasNext()) {
                GoBoardPosition seed = (GoBoardPosition)enemyIt.next();
                List<GoBoardPosition> mergedStones = board_.findGroupFromInitialPosition( seed ); // the restored merged group
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

            GoGroup restoredGroup;
            if (mergedGroupLists.size() > 0)  {
                Iterator mgIt = mergedGroupLists.iterator();
                while (mgIt.hasNext()) {
                    List mergedStones = (List)mgIt.next();

                    // remove all the old groups and replace them with the big ones
                    removeGroupsForListOfStones(mergedStones);

                    restoredGroup = new GoGroup( mergedStones );

                    board_.getGroups().add( restoredGroup );
                }
                if ( GameContext.getDebugMode() > 1 ) {
                    BoardValidationUtil.confirmStonesInValidGroups(board_);
                    BoardValidationUtil.confirmAllStonesInGroupsClaimed(board_.getGroups(), board_);
                }
            }
        }
    }

    private void adjustStringLiberties(CaptureList captureList) {
        // update the liberties of the surrounding strings
        Iterator it = captureList.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition captured = (GoBoardPosition) it.next();
            GoBoardPosition newLiberty = (GoBoardPosition) board_.getPosition( captured.getRow(), captured.getCol() );
            adjustLiberties(newLiberty);
        }
    }


    /**
     * Remove all the groups on the board.
     * Then for each stone, find its group and add that new group to the board's group list.
     * Continue until all stone accounted for.
     */
    private void updateGroupsAfterMoving( GoBoardPosition pos)
    {
        GoProfiler profiler = (GoProfiler)board_.getProfiler();
        profiler.startUpdateGroupsAfterMove();

        if (GameContext.getDebugMode() > 1) {
            BoardValidationUtil.confirmAllStonesInUniqueGroups(board_.getGroups());
        }

        // remove all the current groups (we will then add them back)
        board_.getGroups().clear();

        for ( int i = 1; i <= board_.getNumRows(); i++ )  {
           for ( int j = 1; j <= board_.getNumCols(); j++ ) {
               GoBoardPosition seed = (GoBoardPosition)board_.getPosition(i, j);
               if (seed.isOccupied() && !seed.isVisited()) {
                   List newGroup = board_.findGroupFromInitialPosition(seed, false);
                   GoGroup g = new GoGroup(newGroup);
                   board_.getGroups().add(g);
               }
           }
        }
        unvisitAll();

        // verify that the string to which we added the stone has at least one liberty
        assert (pos.getString().getNumLiberties(board_) > 0): "The placed stone "+pos+" has no liberties "+pos.getGroup();

        // this gets used when calculating the worth of the board
        board_.updateTerritory(false);

        if ( GameContext.getDebugMode() > 1 ) {
            BoardValidationUtil.confirmNoEmptyStrings(board_.getGroups());
            BoardValidationUtil.confirmStonesInValidGroups(board_);
            BoardValidationUtil.confirmAllStonesInUniqueGroups(board_.getGroups());
            try {
                BoardValidationUtil.confirmAllStonesInGroupsClaimed(board_.getGroups(), board_);
            } catch (AssertionError e) {
                GameContext.log(1, "The move was :"+pos);
                throw e;
            }
        }

        profiler.stopUpdateGroupsAfterMove();
    }
    
    

    /**
     * After removing the captures, the stones surrounding the captures will form 1 (or sometimes 2)
     * cohesive group(s) rather than disparate ones.
     * There can be two if, for example, the capturing stone joins a string that is
     * still in atari after the captured stones have been removed.
     */
    private void updateAfterRemovingCaptures(int toRow, int toCol)
    {
        GoBoardPosition finalStone = (GoBoardPosition) board_.getPosition(toRow, toCol);

        assert (finalStone != null);
        if ( finalStone == null )
            return;

        GoBoardPosition seedStone;
        // Its a bit of a special case if the finalStone's string is in atari after the capture.
        // The string that the finalStone belongs to is still cut from the larger joined group,
        // but we need to determine the big group from some other stone not from finalStone's string.
        if (finalStone.isInAtari(board_)) {
            seedStone = findAlternativeSeed(finalStone);
        }
        else {
            seedStone = finalStone;
        }
        assert seedStone.isOccupied();
        List bigGroup = board_.findGroupFromInitialPosition( seedStone );
        assert ( bigGroup.size() > 0 );

        removeGroupsForListOfStones(bigGroup);

        GoGroup newBigGroup = new GoGroup( bigGroup );
        board_.getGroups().add( newBigGroup );
    }


    /**
     * We need to identify this pattern:
     *   #0
     *   0'
     *   #0
     * (or      #O
     *           O'
     *       if on the edge or corner)
     * Where the middle 0 is the stone passed in.
     * There are 4 cases to check
     * @param stone
     * @return one of the other 2 0's in the picture.
     */
    private GoBoardPosition findAlternativeSeed(GoBoardPosition stone)
    {
        // List nbrs = this.getNobiNeighbors(stone, NeighborType.ANY)
        // After we find where the blank is we can pretty much just assert the other positions.
        int r = stone.getRow();
        int c = stone.getCol();

        GoBoardPosition alternative;
        alternative = getConfirmedAlternative(stone, r, c, 0, -1);
        if (alternative != null)
           return alternative;
        alternative = getConfirmedAlternative(stone, r, c, 0, 1);
        if (alternative != null)
           return alternative;
        alternative = getConfirmedAlternative(stone, r, c, -1, 0);
        if (alternative != null)
           return alternative;
        alternative = getConfirmedAlternative(stone, r, c, 1, 0);
        if (alternative != null)
           return alternative;
        assert (false) : "There was no alternative seed for "+stone +" board:\n"+this;
        return stone;
    }


    /**
     * @param stone to find alternative for.
     * @return null if no alternative found
     */
    private GoBoardPosition getConfirmedAlternative(BoardPosition stone,
                                                    int r, int c, int rowOffset, int colOffset)
    {
        BoardPosition blankPos = board_.getPosition(r + rowOffset, c + colOffset);
        if (blankPos != null && blankPos.isUnoccupied()) {
            BoardPosition enemy1;
            BoardPosition enemy2;
            if (rowOffset == 0) {
                enemy1 = board_.getPosition(r + 1, c + colOffset);
                enemy2 = board_.getPosition(r - 1, c + colOffset);
            }
            else {
                assert (colOffset == 0);
                enemy1 = board_.getPosition(r + rowOffset, c + 1);
                enemy2 = board_.getPosition(r + rowOffset, c - 1);
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
    private void restoreCaptures(CaptureList captures) {
        if ( captures != null ) {
            restoreCapturesOnBoard(captures);
            updateAfterRestoringCaptures(captures);
            if (GameContext.getDebugMode() > 1) {
                BoardValidationUtil.confirmStonesInValidGroups( board_);
                BoardValidationUtil.confirmAllStonesInUniqueGroups(board_.getGroups());
                GameContext.log( 3, "GoBoard: undoInternalMove: " + board_ + "  groups after restoring captures:" );
            }
        }
    }


    /**
     * put the captures back on the board.
     */
    private void restoreCapturesOnBoard( CaptureList captureList )
    {
       
        captureList.restoreOnBoard( board_ );

        //GameContext.log( 2, "GoMove: restoring these captures: " + captureList );
        Iterator it = captureList.iterator();
        List<GoBoardPosition> restoredList = new LinkedList<GoBoardPosition>();
        while ( it.hasNext() ) {
            GoBoardPosition capStone = (GoBoardPosition) it.next();
            GoBoardPosition stoneOnBoard = 
                    (GoBoardPosition) board_.getPosition( capStone.getRow(), capStone.getCol() );
            stoneOnBoard.setVisited(false);    // make sure in virgin unvisited state

            //adjustLiberties(stoneOnBoard, board);
            restoredList.add( stoneOnBoard );
        }

        // there may have been more than one string in the capturelist
        List<List> strings = new LinkedList<List>();
        it = restoredList.iterator();

        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            if ( !s.isVisited() ) {
                List<GoBoardPosition> string1 = board_.findStringFromInitialPosition( s, false );
                strings.add( string1 );
            }
        }
        adjustStringLiberties(captureList);

        // ?? form new group, or check group nbrs to see if we can add to an existing one.
        boolean firstString = true;
        Iterator<List> sit = strings.iterator();
        GoGroup group = null;
        for  (List stringList : strings) {
            GoString string = new GoString( stringList, board_ );
            if ( firstString ) {
                group = new GoGroup( string );
                firstString = false;
            }
            else {
                group.addMember( string, board_ );
                //GameContext.log( 2, "GoMove: restoring ----------------" + string );
            }
            string.unvisit();
        }

        assert ( group!=null): "no group was formed when restoring "+restoredList+" the list of strings was "+strings;
        board_.getGroups().add( group );
    }


    /**
     * After restoring the captures, the stones surrounding the captures will probably
     * form disparate groups rather than 1 cohesive one.
     */
    private void updateAfterRestoringCaptures( CaptureList captures)
    {

        if ( GameContext.getDebugMode() > 1 ) {
             BoardValidationUtil.confirmStonesInValidGroups( board_);
        }

        List<GoBoardPosition> enemyNobiNbrs = new LinkedList<GoBoardPosition>();
        Iterator captureIt = captures.iterator();
        // find all the enemy neighbors of the stones in the captured group being restored.
        while ( captureIt.hasNext() ) {
            GoBoardPosition capture = (GoBoardPosition) captureIt.next();
            Set<GoBoardPosition> enns = board_.getNobiNeighbors( capture, NeighborType.ENEMY );
            enemyNobiNbrs.addAll( enns );
        }
        // in some bizarre cases there might actually be no enemy nobi nbrs
        // (such as when one stone killed all the stones on the board?)
        if (enemyNobiNbrs.size() == 0) {
            GameContext.log(0, "The restored captures ("+captures+") have no enemy neighbors (very strange!)" );
            return;
        }
        GoBoardPosition firstEnemyStone = enemyNobiNbrs.get( 0 );
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
        List<List> listsToUnvisit = new ArrayList<List>();
        Set<GoBoardPosition> gStones = bigEnemyGroup.getStones();

        board_.getGroups().remove( bigEnemyGroup );
        if (secondaryEnemyGroup != null) {
            GameContext.log(1, "There was a secondary enemy group before restoring (*RARE*). The 2 groups were :" +
                               bigEnemyGroup+" and "+secondaryEnemyGroup);
            board_.getGroups().remove(secondaryEnemyGroup);
        }

        // Combine all the eneme nobi nbrs with the stones from the bigEnemyGroup when trying to find the new groups.
        List<GoBoardPosition> enemyNbrs = new ArrayList<GoBoardPosition>(enemyNobiNbrs);
        enemyNbrs.addAll(gStones);
        ennIt = enemyNbrs.iterator();
        while ( ennIt.hasNext() ) {
            GoBoardPosition enn = (GoBoardPosition) ennIt.next();
            if ( !enn.isVisited() ) {
                List list = board_.findGroupFromInitialPosition( enn, false );
                listsToUnvisit.add( list );
            }
        }

        Iterator it = listsToUnvisit.iterator();
        while ( it.hasNext() ) {
            List list = (List) it.next();
            GoBoardUtil.unvisitPositions( list );
            GoGroup group = new GoGroup( list );
            if (GameContext.getDebugMode() > 1) {
                BoardValidationUtil.confirmStonesInOneGroup(group, board_.getGroups());
                GameContext.log( 2, "updateAfterRestoringCaptures("+captures+"): adding sub group :" + group );
            }
            board_.getGroups().add( group );
        }
        if ( GameContext.getDebugMode() > 1 ) {
             BoardValidationUtil.confirmStonesInValidGroups( board_);
        }
    }

    
    /**
     * Update the groups after a stone has been removed (and captures replaced perhaps).
     * Some friendly groups may have been split by the removal, while
     * some enemy groups may need to be rejoined.
     *
     * @param stone that was removed (actually, its just the position, the stone has been removed).
     * @param string the string that the stone was removed from.
     */
    private void updateGroupsAfterRemoving( GoBoardPosition stone, GoString string )
    {
        GoProfiler profiler = (GoProfiler)board_.getProfiler();
        profiler.startUpdateGroupsAfterRemove();

        if ( string == null ) {
            if ( GameContext.getDebugMode() > 1 )
                BoardValidationUtil.confirmStonesInValidGroups( board_);
            return;
        }

        GoGroup group = string.getGroup();

        // if the string that the stone is being removed from was considered unconditionally alive,
        // then we need to clear out all the unconditionally alive information for board_ group since it is now invalid.
        if (string.isUnconditionallyAlive()) {
            for (Object s : group.getMembers())  {
                GoString str = (GoString) s;
                str.setUnconditionallyAlive(false);
                str.setNbrs(null);
            }
            Set<GoEye> eyes = group.getEyes(board_);
            for (GoEye eye : eyes)  {      
                eye.setUnconditionallyAlive(false);
                eye.setNbrs(null);
            }
        }

        Set nbrs = board_.getGroupNeighbors( stone, group.isOwnedByPlayer1(), false );

        // create a set of friendly group nbrs and a separate set of enemy ones.
        Set<GoBoardPosition> friendlyNbrs = new HashSet<GoBoardPosition>(10);
        Set<GoBoardPosition> enemyNbrs = new HashSet<GoBoardPosition>(10);
        Iterator nbrIt = nbrs.iterator();
        while ( nbrIt.hasNext() ) {
            GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
            if ( nbrStone.getPiece().isOwnedByPlayer1() == group.isOwnedByPlayer1() )
                friendlyNbrs.add( nbrStone );
            else
                enemyNbrs.add( nbrStone );
        }

        // check for friendly groups that have been split by the removal
        updateFriendlyGroupsAfterRemoval(friendlyNbrs);

        // now check for enemy groups that have been rejoined by the removal.
        // in the most extreme case there could be 4 groups that we need to add back.
        //  eg: 4 ataried strings that are restored by the removal of this stone.
        updateEnemyGroupsAfterRemoval(enemyNbrs);

        if ( GameContext.getDebugMode() > 1 )  {
            BoardValidationUtil.confirmNoEmptyStrings(board_.getGroups());
        }

        cleanupGroups();

        profiler.stopUpdateGroupsAfterRemove();
    }

     
    /**
     * adjust the liberties on the strings (both black and white) that we touch.
     * @param liberty  - either occupied or not depending on if we are placing the stone or removing it.
     */
    private void adjustLiberties(GoBoardPosition liberty) {

         NeighborAnalyzer na = new NeighborAnalyzer(board_);
         Set<GoString> stringNbrs = na.findStringNeighbors( liberty );
         for (GoString sn : stringNbrs) {
             sn.changedLiberty(liberty);
         }
    }


    /**
     * remove groups that have no stones in them.
     */
    private void cleanupGroups()
    {
        Iterator it = board_.getGroups().iterator();
        while ( it.hasNext() ) {
            GoGroup group = (GoGroup) it.next();
            //group.confirmNoNullMembers();
            if ( group.getNumStones() == 0 )  {
                //assert (group.getEyes().isEmpty()): group+ " has eyes! It was assumed not to.\n"+board_;
                it.remove();
            }
        }
    }


    /**
     * Make sure that all the positions on the board are reset to the unvisited state.
     */
    private void unvisitAll()
    {
        for ( int i = 1; i <= board_.getNumRows(); i++ ) {
            for ( int j = 1; j <= board_.getNumCols(); j++ ) {
                GoBoardPosition pos = (GoBoardPosition) board_.getPosition( i, j );
                pos.setVisited(false);
            }
        }
    } 
    
    
    /**
     * Remove all the groups in groups_ corresponding to the specified list of stones.
     * @param stones
     */
    private void removeGroupsForListOfStones(List stones) {
        Iterator mIt = stones.iterator();
        while ( mIt.hasNext() ) {
            GoBoardPosition nbrStone = (GoBoardPosition) mIt.next();
            // In the case where the removed stone was causing an atari in a string in an enemy group,
            // there is a group that does not contain a nbrstone that also needs to be removed here.
            board_.getGroups().remove( nbrStone.getGroup() );
        }
    }

    
    /**
     * return true if the stones in this list exactly match those in an existing group
     */
    private boolean groupAlreadyExists( List<GoBoardPosition> stones )
    {
        Iterator<GoGroup> gIt = board_.getGroups().iterator();
        // first find the group that contains the stones
        while ( gIt.hasNext() ) {
            GoGroup g = gIt.next();
            if ( g.exactlyContains(stones) )
                return true;
        }
        return false;
    }
    
}
