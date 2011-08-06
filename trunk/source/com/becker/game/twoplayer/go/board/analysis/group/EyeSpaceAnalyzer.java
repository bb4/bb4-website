package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.common.geometry.Box;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.becker.game.twoplayer.go.board.elements.eye.GoEye;
import com.becker.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionList;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionLists;

import java.util.Iterator;

/**
 * Methods related to understanding the eye spaces within a group.
 *
 * @author Barry Becker
 */
class EyeSpaceAnalyzer {

    /** The group of go stones that we are analyzing eyespace for. */
    private IGoGroup group_;

    private GoBoard board_;

    /** bounding box around our group that we are analyzing. */
    private Box boundingBox_;

    private NeighborAnalyzer nbrAnalyzer_;
    private GroupAnalyzerMap analyzerMap_;

    /**
     * The minimum stones a groups needs to have for for an eye is 7 if in center, 5 on edge, and 3 in corner.
     */
    private static final int MIN_STONES_FOR_EYE  = 3;


    /**
     * Constructor.
     */
    public EyeSpaceAnalyzer(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        group_ = group;
        analyzerMap_ = analyzerMap;
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
    public GoEyeSet determineEyes() {

        assert (board_ != null) : "The board must be set before determining eyes.";
        GoBoardPositionLists excludedEyeSpaceLists = createExcludedLists();
        return findEyesFromCandidates(excludedEyeSpaceLists);
    }

    /**
     * Eliminate all the stones and spaces that are in the bounding rectangle,
     * but not in the group. We do this by marching around the perimeter cutting out
     * the strings of empty or opponent spaces that do not belong.
     * Note: we do not go all the way to the edge. If the border of a group includes an edge of the board,
     * then empty spaces there are most likely eyes (but not necessarily).
     * @return list of lists of eye space spaces find real eye from (and to unvisit at the end)
     */
    private GoBoardPositionLists createExcludedLists() {

        GoBoardPositionLists lists = new GoBoardPositionLists();
        boolean ownedByPlayer1 = group_.isOwnedByPlayer1();

        if (boundingBox_.getArea() == 0) return lists;
        int rMin = boundingBox_.getMinRow();
        int rMax = boundingBox_.getMaxRow();
        int cMin = boundingBox_.getMinCol();
        int cMax = boundingBox_.getMaxCol();

        if ( cMin > 1 ) {
            for ( int r = rMin; r <= rMax; r++ )  {
                excludeSeed( (GoBoardPosition) board_.getPosition( r, cMin ),
                        ownedByPlayer1, lists);
            }
        }
        if ( cMax < board_.getNumCols() ) {
            for ( int r = rMin; r <= rMax; r++ ) {
                excludeSeed( (GoBoardPosition) board_.getPosition( r, cMax ),
                        ownedByPlayer1, lists);
            }
        }
        if ( rMin > 1 ) {
            for ( int c = cMin; c <= cMax; c++ )  {
                excludeSeed( (GoBoardPosition) board_.getPosition( rMin, c ),
                        ownedByPlayer1, lists);
            }
        }
        if ( rMax < board_.getNumRows() ) {
            for ( int c = cMin; c <= cMax; c++ )  {
                excludeSeed( (GoBoardPosition) board_.getPosition( rMax, c ),
                        ownedByPlayer1, lists);
            }
        }

        clearEyes(rMin, rMax, cMin, cMax);
        return lists;
    }

    /**
     * Do a paint fill on each of the empty unvisited spaces.
     * Most of these remaining empty spaces are connected to an eye of some type.
     * There will be some that fill spaces between black and white stones.
     * Don't count these as eyes unless the stones of the opposite color are much weaker -
     * in which case they are assumed dead and hence part of the eye.
     * @param eyeSpaceLists eye space lists
     * @return set of eyes in this group
     */
    private GoEyeSet findEyesFromCandidates(GoBoardPositionLists eyeSpaceLists) {
        GoEyeSet eyes = new GoEyeSet();
        boolean ownedByPlayer1 = group_.isOwnedByPlayer1();
        GroupAnalyzer groupAnalyzer = analyzerMap_.getAnalyzer(group_);

        Box innerBox = createBoxExcludingBorder(boundingBox_);
        for ( int r = innerBox.getMinRow(); r < innerBox.getMaxRow(); r++ ) {
            for ( int c = innerBox.getMinCol(); c < innerBox.getMaxCol(); c++ ) {

                // if the empty space is already marked as being an eye, skip
                GoBoardPosition space = (GoBoardPosition) board_.getPosition( r, c );
                assert space != null : "pos r="+r +" c="+c;
                if ( !space.isVisited() && space.isUnoccupied() && !space.isInEye() ) {
                    GoBoardPositionList eyeSpaces =
                            nbrAnalyzer_.findStringFromInitialPosition( space, ownedByPlayer1,
                                                                 false, NeighborType.NOT_FRIEND,
                                                                 boundingBox_  );
                    eyeSpaceLists.add(eyeSpaces);
                    // make sure this is a real eye.
                    if ( confirmEye( eyeSpaces) ) {
                        GoEye eye =  new GoEye( eyeSpaces, board_, group_, groupAnalyzer);
                        eyes.add( eye );
                    }
                    else {
                        GameContext.log(3, eyeSpaces.toString("This list of stones was rejected as being an eye: "));
                    }
                }
            }
        }
        eyeSpaceLists.unvisitPositionsInLists();
        return eyes;
    }

    /**
     * @param box to reduce by the outside edge.
     * @return A new bounding box where we shave off the outer edge, unless on the edge of the board.
     */
    private Box createBoxExcludingBorder(Box box) {
        int maxRow = board_.getNumRows();
        int maxCol = board_.getNumCols();

        int innerMinRow = (box.getMinRow() > 1) ? Math.min(box.getMinRow() + 1, maxRow) : 1;
        int innerMinCol = (box.getMinCol() > 1) ? Math.min(box.getMinCol() + 1, maxCol) : 1;

        return new Box(
                innerMinRow,
                innerMinCol,
                (box.getMaxRow() < maxRow) ? Math.max(box.getMaxRow(), innerMinRow) : maxRow + 1,
                (box.getMaxCol() < maxCol) ?  Math.max(box.getMaxCol(), innerMinCol) : maxCol + 1
        );
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
     * Mark as visited all the non-friend (empty or enemy) spaces connected to the specified seed.
     *
     * @param space seed
     * @param lists list of stones connected to the seed stone
     */
    private void excludeSeed( GoBoardPosition space, boolean groupOwnership,
                              GoBoardPositionLists lists) {
        if ( !space.isVisited()
             && (space.isUnoccupied() || space.getPiece().isOwnedByPlayer1() != group_.isOwnedByPlayer1())) {
            // this will leave stones outside the group visited
            GoBoardPositionList exclusionList =
                    nbrAnalyzer_.findStringFromInitialPosition(space, groupOwnership, false,
                                                                NeighborType.NOT_FRIEND, boundingBox_);

            Iterator it = exclusionList.iterator();
            GroupAnalyzer groupAnalyzer = analyzerMap_.getAnalyzer(group_);

            while (it.hasNext()) {
                GoBoardPosition p = (GoBoardPosition)it.next();
                if (p.isOccupied()) {
                    // if its a very weak opponent (ie dead) then don't exclude it from the list
                    if (!groupAnalyzer.isTrueEnemy(p))  {
                        p.setVisited(false);
                        it.remove();  // remove it from the list
                    }
                }
            }

            if ( exclusionList.size() > 0 ) {
                lists.add( exclusionList );
            }
        }
    }

    /**
     * Check this list of stones to confirm that enemy stones don't border it.
     * If they do, then it is not an eye - return false.
     *
     * If there are less than MIN_STONES_FOR_EYE stones in the surrounding enemy string, then it does not have an eye
     * and is assumed to be weaker than the surrounding group of the opposite color.
     *
     * If there are MIN_STONES_FOR_EYE stones or more (fewer on edge),
     * we need to compare the health of the position relative to the surrounding group
     * to see if it is dead enough to still consider an eye.
     *
     * @param eyeList the candidate string of stones to test for eye status
     * @return true if the list of stones is an eye
     */
    private boolean confirmEye(GoBoardPositionList eyeList) {
        if ( eyeList == null )
            return false;

        //GroupAnalyzer groupAnalyzer = analyzerMap_.getAnalyzer(group_);

        for (GoBoardPosition position : eyeList) {
            //IGoString string = position.getString();

            if (boundingBox_.isOnEdge(position.getLocation()) && !withinBorderEdge(position)) {
                // then the potential eye breaks through to the outside of the group bounds,
                //so we really cannot consider it eyeList yet, though it likely will be.
                return false;
            }
            /*
            if (position.isOccupied()) {

                if (string.size() >= MIN_STONES_FOR_EYE && groupAnalyzer.isTrueEnemy(position)) {
                    return false;  // then not eye
                }
            }  */
        }

        // if we make it here, its a bonafied eye.
        return true;
    }

    /**
     * Positions marked E are considered on edge of edge.
     * Note that we are within the edge border if the position
     * is both on the bbox corner and the board corner.
     *
     *   E****        ******
     *       *    or  *    *
     *       E        E    E
     *
     * @param position
     * @return true if on edge of border edge
     */
    private boolean withinBorderEdge(GoBoardPosition position) {
        boolean isOnbboxCorner = boundingBox_.isOnCorner(position.getLocation());
        boolean isInCorner = board_.isInCorner(position);
        boolean edgeOfEdge = isOnbboxCorner ^ isInCorner;
        return board_.isOnEdge(position) && !edgeOfEdge;
    }
}
