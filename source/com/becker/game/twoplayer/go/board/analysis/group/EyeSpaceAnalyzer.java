package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.common.Box;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.becker.game.twoplayer.go.board.elements.*;

import java.util.*;

/**
 * Methods related to understanding the eye spaces within a group.
 *
 * @author Barry Becker
 */
class EyeSpaceAnalyzer {

    /** The group of go stones that we are analyzing eyespace for. */
    private GoGroup group_;

    private GoBoard board_;

    /** bounding box around our group that we are analyzing. */
    private Box boundingBox_;

    private NeighborAnalyzer nbrAnalyzer_;

    /**
     * Constructor.
     */
    public EyeSpaceAnalyzer(GoGroup group) {
        group_ = group;
    }

    public void setBoard(GoBoard board) {
        board_ = board;
        nbrAnalyzer_ = new NeighborAnalyzer(board);
        boundingBox_ = group_.findBoundingBox();
    }

    /**
     * Determine the set of eyes within a group
     * @return the set of eyes that are in this group.
     */
    public Set<GoEye> determineEyes() {

        assert (board_ != null) : "The board must be set before determining eyes.";
        List<GoBoardPositionList> candidateEyeLists = createEyeSpaceLists();
        return findEyeFromCandidates(candidateEyeLists);
    }

    /**
     * Now do a paint fill on each of the empty unvisited spaces left.
     * Most of these remaining empty spaces are connected to an eye of some type.
     * There will be some that fill spaces between black and white stones.
     * Don't count these as eyes unless the stones of the opposite color are much weaker -
     * in which case they are assumed dead and hence part of the eye.
     * @param candidateEyeLists eye space lists
     * @return set of eyes in this group/
     */
    private Set<GoEye> findEyeFromCandidates(List<GoBoardPositionList> candidateEyeLists) {
        Set<GoEye> eyes = new LinkedHashSet<GoEye>();
        boolean ownedByPlayer1 = group_.isOwnedByPlayer1();

        for ( int r = boundingBox_.getMinRow(); r <= boundingBox_.getMaxRow(); r++ ) {
            for ( int c = boundingBox_.getMinCol(); c <= boundingBox_.getMaxCol(); c++ ) {
                // if the empty space is already marked as being an eye, skip
                GoBoardPosition space = (GoBoardPosition) board_.getPosition( r, c );
                assert space != null : "pos r="+r +" c="+c;
                if ( !space.isVisited() && space.isUnoccupied() && !space.isInEye() ) {
                    GoBoardPositionList eyeSpaces =
                            nbrAnalyzer_.findStringFromInitialPosition( space, ownedByPlayer1,
                                                                 false, NeighborType.NOT_FRIEND,
                                                                 boundingBox_  );
                    candidateEyeLists.add( eyeSpaces );
                    // make sure this is a real eye.
                    // this method checks that opponent stones don't border it.
                    if ( confirmEye( eyeSpaces) ) {
                        GoEye eye =  new GoEye( eyeSpaces, board_, group_ );
                        eyes.add( eye );
                    }
                    else {
                        GameContext.log(3, eyeSpaces.toString("This list of stones was rejected as being an eye: "));
                    }
                }
            }
        }
        GoBoardUtil.unvisitPositionsInLists( candidateEyeLists );
        return eyes;
    }

    /**
     * Eliminate all the stones and spaces that are in the bounding rect,
     * but not in the group. We do this by marching around the perimeter cutting out
     * the strings of empty or opponent spaces that do not belong.
     * Note : we do not go all the way to the edge. If the border of a group includes an edge of the board,
     * then empty spaces there are most likely eyes (but not necessarily).
     * @return list of lists of eye space spaces find real eye from (and to unvisit at the end)
     */
    private List<GoBoardPositionList> createEyeSpaceLists() {

        List<GoBoardPositionList> lists = new ArrayList<GoBoardPositionList>();
        boolean ownedByPlayer1 = group_.isOwnedByPlayer1();

        if (boundingBox_.getArea() == 0) return lists;
        int rMin = boundingBox_.getMinRow();
        int rMax = boundingBox_.getMaxRow();
        int cMin = boundingBox_.getMinCol();
        int cMax = boundingBox_.getMaxCol();

        if ( boundingBox_.getMinCol() > 1 ) {
            for ( int r = rMin; r <= rMax; r++ )  {
                excludeSeed( (GoBoardPosition) board_.getPosition( r, cMin ),
                        ownedByPlayer1, lists, boundingBox_ );
            }
        }
        if ( boundingBox_.getMaxCol() < board_.getNumCols() ) {
            for ( int r = rMin; r <= rMax; r++ ) {
                excludeSeed( (GoBoardPosition) board_.getPosition( r, cMax ),
                        ownedByPlayer1, lists, boundingBox_ );
            }
        }
        if ( rMin > 1 ) {
            for ( int c = cMin; c <= cMax; c++ )  {
                excludeSeed( (GoBoardPosition) board_.getPosition( rMin, c ),
                        ownedByPlayer1, lists, boundingBox_ );
            }
        }
        if ( rMax < board_.getNumRows() ) {
            for ( int c = cMin; c <= cMax; c++ )  {
                excludeSeed( (GoBoardPosition) board_.getPosition( rMax, c ),
                        ownedByPlayer1, lists, boundingBox_ );
            }
        }

        clearEyes(rMin, rMax, cMin, cMax);
        return lists;
    }

    /**
     * Make sure all the positions do not cache their eye
     */
    private void clearEyes(int rMin, int rMax, int cMin, int cMax) {
        for ( int r = rMin; r <= rMax; r++ ) {
            for ( int c = cMin; c <= cMax; c++ ) {
                ((GoBoardPosition) board_.getPosition( r, c )).setEye(null);
            }
        }
    }


    /**
     * Mark as visited all the non-friend (empty or enemy) spaces connected to this one.
     *
     * @param space seed
     * @param lists list of stones connected to the seed stone
     */
    private void excludeSeed( GoBoardPosition space, boolean groupOwnership,
                              List<GoBoardPositionList> lists, Box box) {
        if ( !space.isVisited()
             && (space.isUnoccupied() || space.getPiece().isOwnedByPlayer1() != group_.isOwnedByPlayer1())) {
            // this will leave stones outside the group visited
            GoBoardPositionList list =
                    nbrAnalyzer_.findStringFromInitialPosition(space, groupOwnership, false,
                                                                NeighborType.NOT_FRIEND, box);

            // make sure that every occupied stone in the list is a real enemy and not just a dead opponent stone.
            // compare it with one of the group strings
            GoString groupString = group_.getMembers().iterator().next();

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
     * If there are less than 7 stones in the surrounded enemy string, then it does not have an eye
     * and is assumed to be weaker than the surrounding group of the opposite color.
     *
     * If more than 6 stones, we need to compare the health of the position relative to the surrounding group
     * to see if it is dead enough to still consider an eye.
     *
     * @param eyeList the candidate string of stones to misc for eye status
     * @return true if the list of stones is an eye
     */
    private boolean confirmEye( GoBoardPositionList eyeList) {
        if ( eyeList == null )
            return false;

        GoString groupString = group_.getMembers().iterator().next();

        for (GoBoardPosition position : eyeList) {
            GoString string = position.getString();

            if (position.isOccupied()) {
                if (string.size() > 6 && groupString.isEnemy(position)) {
                    return false;  // then not eye
                }
            }
        }
        // if we make it here, its a bonafied eye.
        return true;
    }
}
