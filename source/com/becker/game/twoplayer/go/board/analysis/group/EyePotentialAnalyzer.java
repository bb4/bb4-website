package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.common.Box;
import com.becker.common.Location;
import com.becker.game.twoplayer.go.board.*;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoGroup;
import com.becker.game.twoplayer.go.board.elements.GoString;

/**
 * Figure out how likely (the potential) that a group can form two eyes.
 *
 * @author Barry Becker
 */
class EyePotentialAnalyzer {

    /** The group of go stones that we are analyzing. */
    private GoGroup group_;

    private GoBoard board_;

    /** bounding box around our group that we are analyzing. */
    private Box boundingBox_;


    /**
     * Constructor.
     */
    public EyePotentialAnalyzer(GoGroup group, GoBoard board) {
        group_ = group;
        board_ = board;
        assert board_ != null;
        boundingBox_ = group_.findBoundingBox();
    }

    /**
     * Expand the bbox by one in all directions.
     * if the bbox is within one space of the edge, extend it all the way to the edge.
     * loop through the rows and columns calculating distances from group stones
     * to the edge and to other stones.
     * if there is a (mostly living) enemy stone in the run, don't count the run.
     * @return eyePtoential - a measure of how easily this group can make 2 eyes
     *    (0 - 2; 2 meaning has 2 guaranteed eyes).
     */
    public float calculateEyePotential() {
        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();

        boundingBox_.expandGloballyBy(1, numRows, numCols);
        boundingBox_.expandBordersToEdge(1, numRows, numCols);
        return findTotalEyePotential();
    }

    /**
     * @return eyePotential - a measure of how easily this group can make 2 eyes (0 - 2; 2 meaning has 2 eyes).
     */
    private float findTotalEyePotential() {

        // make sure that every internal enemy stone is really an enemy and not just dead.
        // compare it with one of the group strings.
        GoString groupString = group_.getMembers().iterator().next();

        int rMin = boundingBox_.getMinRow();
        int rMax = boundingBox_.getMaxRow();
        int cMin = boundingBox_.getMinCol();
        int cMax = boundingBox_.getMaxCol();
        float totalPotential = 0;

        // first look at the row runs
        for ( int r = boundingBox_.getMinRow(); r <= boundingBox_.getMaxRow(); r++ ) {
            totalPotential += getRowColPotential(r, cMin, 0, 1, rMax, cMax, board_, groupString);
        }
        // now accrue column run potentials
        for ( int c = cMin; c <= cMax; c++ ) {
            totalPotential += getRowColPotential(rMin, c, 1, 0, rMax, cMax, board_, groupString);
        }

        return (float)Math.min(1.9, Math.sqrt(totalPotential)/1.3);
    }

    /**
     * Find the potential for one of the bbox's rows or columns.
     * @return eye potential for row and column at r,c
     *
     */
    private float getRowColPotential(int row, int col, int rowInc, int colInc, int maxRow, int maxCol,
                                     GoBoard board, GoString groupString) {
        float rowPotential = 0;
        Location pos  = new Location(row, col);
        int breadth = (rowInc ==1)? (maxRow - row) : (maxCol - col);
        GoBoardPosition startSpace = (GoBoardPosition) board.getPosition( pos );
        do {
            GoBoardPosition space = (GoBoardPosition) board.getPosition( pos );
            GoBoardPosition firstSpace = space;
            boolean containsEnemy = false;
            int runLength = 0;
            boolean ownedByPlayer1 = group_.isOwnedByPlayer1();

            while (pos.getCol() <= maxCol && pos.getRow() <= maxRow && (space.isUnoccupied() ||
                      (space.isOccupied() && space.getPiece().isOwnedByPlayer1() != ownedByPlayer1))) {
                if (space.isOccupied() &&  space.getPiece().isOwnedByPlayer1() != ownedByPlayer1
                    && groupString.isEnemy(space)) {
                    containsEnemy =  true;
                }
                runLength++;
                pos.increment(rowInc, colInc);
                space = (GoBoardPosition) board.getPosition( pos );
            }
            boolean bounded = !(firstSpace.equals(startSpace)) && space!=null && space.isOccupied();
            // now accrue the potential
            if (!containsEnemy && runLength < breadth && runLength > 0) {
                 int firstPos, max, currentPos;
                 if (rowInc ==1) {
                     firstPos = firstSpace.getRow();
                     max = board.getNumRows();
                     currentPos = pos.getRow();
                 } else {
                     firstPos = firstSpace.getCol();
                     max = board.getNumCols();
                     currentPos = pos.getCol();
                 }
                 rowPotential += getRunPotential(runLength, firstPos, currentPos, max, bounded);
            }
            pos.increment(rowInc, colInc);
        } while (pos.getCol() <= maxCol && pos.getRow() <= maxRow);
        return rowPotential;
    }

    /**
     * @return potential score for the runlength.
     */
    private float getRunPotential(int runLength, int firstPos, int endPosP1, int max,
                                                    boolean boundedByFriendStones) {
        float potential;
        assert(runLength > 0);

        if ((firstPos == 1 || endPosP1 == max || boundedByFriendStones)) {
            potential = getRunPotentialOnEdge(runLength);
        }
        else {
            potential = getRunPotentialToBoundary(runLength);
        }
        return potential;
    }

    /**
     * this case is where the run is next to an edge or bounded by friend stones.
     * Weight the potential more heavily.
     * @return potential score for the runlength.
     */
    private float getRunPotentialOnEdge(int runLength) {
        float potential;
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
        return potential;
    }

    /**
     * a run to boundary. Less weight attributed.
     * @return potential score for the runlength.
     */
    private float getRunPotentialToBoundary(int runLength) {
        float potential;
        switch (runLength) {
            case 1: potential = 0.05f; break;
            case 2: potential = 0.15f; break;
            case 3: potential = 0.2f; break;
            case 4: potential = 0.25f; break;
            case 5: potential = 0.2f; break;
            case 6: potential = 0.15f; break;
            case 7: potential = 0.1f; break;
            case 8: potential = 0.06f; break;    // was 0.6
            default : potential = 0.05f;
        }
        return potential;
    }
}