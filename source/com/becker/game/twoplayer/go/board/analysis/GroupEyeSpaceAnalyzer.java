package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.board.*;
import com.becker.common.Box;

import java.util.*;

/**
 * Methods related to understanding the eyes within a group.
 *
 * @author Barry Becker
 */
class GroupEyeSpaceAnalyzer {

    /** The group of go stones that we are analyzing. */
    private GoGroup group_;

    private GoBoard board_;

    /** bounding box around our group that we are analyzing. */
    private Box boundingBox_;

    /**
     * Constructor.
     */
    public GroupEyeSpaceAnalyzer(GoGroup group, GoBoard board) {
        group_ = group;
        board_ = board;
        assert board_ != null;
        boundingBox_ = findBoundingBox(group_.getMembers());
    }

    /**
     * @return the set of eyes that are in this group.
     */
    public Set<GoEye> determineEyes() {

        Set<GoEye> eyes = new LinkedHashSet<GoEye>();
         // list of lists of spaces to unvisit at the end
        List<List> lists = new ArrayList<List>();

        // next eliminate all the stones and spaces that are in the bounding rect,
        // but not in the group. We do this by marching around the perimeter cutting out
        // the strings of empty or opponent spaces that do not belong.
        // Note : we do not go all the way to the edge. If the border of a group includes an edge of the board,
        // then empty spaces there are most likely eyes (but not necessarily).
        int rMin = boundingBox_.getMinRow();
        int rMax = boundingBox_.getMaxRow();
        int cMin = boundingBox_.getMinCol();
        int cMax = boundingBox_.getMaxCol();
        boolean ownedByPlayer1 = group_.isOwnedByPlayer1();

        if ( boundingBox_.getMinCol() > 1 ) {
            for ( int r = rMin; r <= rMax; r++ )
                excludeSeed( (GoBoardPosition) board_.getPosition( r, cMin ),
                        ownedByPlayer1, board_, lists, boundingBox_  );
        }
        if ( boundingBox_.getMaxCol() < board_.getNumCols() ) {
            for ( int r = rMin; r <= rMax; r++ )
                excludeSeed( (GoBoardPosition) board_.getPosition( r, cMax ),
                        ownedByPlayer1, board_, lists, boundingBox_  );
        }
        if ( rMin > 1 ) {
            for ( int c = cMin; c <= cMax; c++ )
                excludeSeed( (GoBoardPosition) board_.getPosition( rMin, c ),
                        ownedByPlayer1, board_, lists, boundingBox_  );
        }
        if ( rMax < board_.getNumRows() ) {
            for ( int c = cMin; c <= cMax; c++ )
                excludeSeed( (GoBoardPosition) board_.getPosition( rMax, c ),
                        ownedByPlayer1, board_, lists, boundingBox_  );
        }

        // did not used to need this. do we still?
        for ( int r = rMin; r <= rMax; r++ ) {
            for ( int c = cMin; c <= cMax; c++ ) {
                GoBoardPosition space = (GoBoardPosition) board_.getPosition( r, c );
                space.setEye(null);
            }
        }

        // Now do a paint fill on each of the empty unvisited spaces left.
        // Most of these remaining empty spaces are connected to an eye of some type.
        // There will be some that fill spaces between black and white stones.
        // Don't count these as eyes unless the stones of the opposite color are much weaker -
        // in which case they are assumed dead and hence part of the eye.
        for ( int r = rMin; r <= rMax; r++ ) {
            for ( int c = cMin; c <= cMax; c++ ) {
                // if the empty space is already marked as being an eye, skip
                GoBoardPosition space = (GoBoardPosition) board_.getPosition( r, c );
                if ( !space.isVisited() && space.isUnoccupied() && !space.isInEye() ) {
                    List<GoBoardPosition> eyeSpaces =
                            board_.findStringFromInitialPosition( space, ownedByPlayer1,
                                                                 false, NeighborType.NOT_FRIEND,
                                                                 boundingBox_  );
                    lists.add( eyeSpaces );
                    // make sure this is a real eye.
                    // this method checks that opponent stones don't border it.
                    if ( confirmEye( eyeSpaces) ) {
                        GoEye eye =  new GoEye( eyeSpaces, board_, group_ );
                        eyes.add( eye );
                    }
                    else {
                        BoardDebugUtil.debugPrintList(3, "This list of stones was rejected as being an eye: ", eyeSpaces);
                    }
                }
            }
        }
        GoBoardUtil.unvisitPositionsInLists( lists );
        return eyes;
    }


    /**
     *@return eyePtoential - a measure of how easily this group can make 2 eyes (0 - 2; 2 meaning has 2 eyes).
     */
    public float calculateEyePotential() {
        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        // Expand the bbox by one in all directions.
        // if the bbox is within one space of the edge, extend it all the way to the edge.
        // loop through the rows and columns calculating distances from group stones
        // to the edge and to other stones.
        // if there is a (mostly living) enemy stone in the run, don't count the run.

        boundingBox_.expandGloballyBy(1, numRows, numCols);
        boundingBox_.expandBordersToEdge(1, numRows, numCols);
        float totalPotential = 0;

        // make sure that every internal enemy stone is really an enemy and not just dead.
        // compare it with one of the group strings.
        GoString gs = group_.getMembers().iterator().next();

        // first look at the row runs
        for ( int r = boundingBox_.getMinRow(); r <= boundingBox_.getMaxRow(); r++ ) {
            //System.out.println("row run = "+ r);
            totalPotential +=
                    getRowColPotential(r, boundingBox_.getMinCol(), 0, 1, boundingBox_.getMaxRow(), boundingBox_.getMaxCol(), board_, gs);
        }
        // now acrue column run potentials
        for ( int c = boundingBox_.getMinCol(); c <= boundingBox_.getMaxCol(); c++ ) {
            //System.out.println("col run = "+ c);
            totalPotential +=
                    getRowColPotential(boundingBox_.getMinRow(), c, 1, 0, boundingBox_.getMaxRow(), boundingBox_.getMaxCol(), board_, gs);
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
        do {
            GoBoardPosition space = (GoBoardPosition) board.getPosition( r, c );
            GoBoardPosition firstSpace = space;
            boolean containsEnemy = false;
            int runLength = 0;
            boolean ownedByPlayer1 = group_.isOwnedByPlayer1();

            while (c <= maxCol && r <= maxRow && (space.isUnoccupied() ||
                      (space.isOccupied() && space.getPiece().isOwnedByPlayer1() != ownedByPlayer1))) {
                if (space.isOccupied() &&  space.getPiece().isOwnedByPlayer1() != ownedByPlayer1
                    && groupString.isEnemy(space)) {
                    containsEnemy =  true;
                }
                runLength++;
                r += rowInc; c += colInc;
                space = (GoBoardPosition) board.getPosition( r, c );
            }
            boolean bounded = !(firstSpace.equals(startSpace)) && space!=null && space.isOccupied();
            // now acrue the potential
            //System.out.println("check containsEnemy="+containsEnemy+" runLength="+runLength + " ("+r+","+c+") useIt="
            //  +(!containsEnemy && runLength < breadth && runLength > 0));
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


    /**
     *
     * @return
     */
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
        return potential;
    }

    /**
     * Mark as visited all the non-friend (empty or enemy) spaces connected to this one.
     *
     * @param space seed
     * @param board owner
     * @param lists list of stones connected to the seed stone
     */
    private void excludeSeed( GoBoardPosition space, boolean groupOwnership, GoBoard board, List<List> lists, Box box)
    {
        if ( !space.isVisited()
             && (space.isUnoccupied() || space.getPiece().isOwnedByPlayer1() != group_.isOwnedByPlayer1())) {
            // this will leave stones outside the group visited
            List list = board.findStringFromInitialPosition( space, groupOwnership, false,
                                                             NeighborType.NOT_FRIEND, box );

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
     * @param eyeList the candidate string of stones to misc for eye status
     * @return true if the list of stones is an eye
     */
    private boolean confirmEye( List<GoBoardPosition> eyeList)
    {
        if ( eyeList == null )
            return false;

        // each occupied stone of the eye must be very weak (ie not an enemy, but dead opponent)
        // compare it with one of the group strings
        GoString groupString = group_.getMembers().iterator().next();

        for (GoBoardPosition position : eyeList) {
            GoString string = position.getString();

            if (position.isOccupied()) {
                GoStone stone = (GoStone) position.getPiece();
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
     * @param positions to find bounding box of
     * @return bounding box of set of stones/positions passed in
     */
    private static Box findBoundingBox(Set positions)  {
        int rMin = 100000; // something huge ( more than max rows)
        int rMax = 0;
        int cMin = 100000; // something huge ( more than max cols)
        int cMax = 0;

        // first determine a bounding rectangle for the group.
        Iterator it = positions.iterator();

        while ( it.hasNext() ) {
            GoString string = (GoString) it.next();

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

}
