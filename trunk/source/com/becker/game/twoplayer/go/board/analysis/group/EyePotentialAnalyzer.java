package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.common.Box;
import com.becker.common.Location;
import com.becker.game.twoplayer.go.board.GoBoard;
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
     * @return eye potential - a measure of how easily this group can make 2 eyes
     *    (0 - 2; 2 meaning it already has 2 guaranteed eyes or can easily get them).
     */
    public float calculateEyePotential() {
        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();

        boundingBox_.expandGloballyBy(1, numRows, numCols);
        boundingBox_.expandBordersToEdge(1, numRows, numCols);
        return findTotalEyePotential();
    }

    /**
     * Make sure that every internal enemy stone is really an enemy and not just dead.
     * compare it with one of the group strings.
     * @return eyePotential - a measure of how easily this group can make 2 eyes (0 - 2; 2 meaning has 2 eyes).
     */
    private float findTotalEyePotential() {

        GoString groupString = group_.getMembers().iterator().next();

        int rMin = boundingBox_.getMinRow();
        int rMax = boundingBox_.getMaxRow();
        int cMin = boundingBox_.getMinCol();
        int cMax = boundingBox_.getMaxCol();

        float totalPotential = 0;
        totalPotential += getTotalRowPotentials(groupString, rMin, rMax, cMin, cMax);
        totalPotential += getTotalColumnPotentials(groupString, rMin, rMax, cMin, cMax);

        return (float)Math.min(1.9, Math.sqrt(totalPotential)/1.3);
    }

    /**
     * @return  total of all the row run potentials.
     */
    private float getTotalRowPotentials(GoString groupString, int rMin, int rMax, int cMin, int cMax) {
        float totalPotential = 0;
        for ( int r = rMin; r <= rMax; r++ ) {
            totalPotential += getRowColPotential(new Location(r, cMin), 0, 1, rMax, cMax, groupString);
        }
        return totalPotential;
    }

    /**
     * @return total of all the column run potentials.
     */
    private float getTotalColumnPotentials(GoString groupString, int rMin, int rMax, int cMin, int cMax) {
        float totalPotential = 0;
        for ( int c = cMin; c <= cMax; c++ ) {
            totalPotential += getRowColPotential(new Location(rMin, c), 1, 0, rMax, cMax, groupString);
        }
        return totalPotential;
    }

    /**
     * Find the potential for one of the bbox's rows or columns.
     * @return eye potential for row and column at pos
     */
    private float getRowColPotential(Location pos, int rowInc, int colInc, int maxRow, int maxCol,
                                     GoString groupString) {
        float runPotential = 0;
        int breadth = (rowInc == 1) ? (maxRow - pos.getRow()) : (maxCol - pos.getCol());
        GoBoardPosition startSpace = (GoBoardPosition) board_.getPosition( pos );
        boolean ownedByPlayer1 = group_.isOwnedByPlayer1();
        do {
            GoBoardPosition nextSpace = (GoBoardPosition) board_.getPosition( pos );
            GoBoardPosition firstSpace = nextSpace;
            boolean containsEnemy = false;
            int runLength = 0;

            while (inRun(pos, maxRow, maxCol, nextSpace, ownedByPlayer1)) {
                if (containsEnemy(groupString, ownedByPlayer1, nextSpace)) {
                    containsEnemy = true;
                }
                runLength++;
                pos.increment(rowInc, colInc);
                nextSpace = (GoBoardPosition) board_.getPosition( pos );
            }
            boolean bounded = isBounded(startSpace, nextSpace, firstSpace);
            runPotential += accrueRunPotential(rowInc, pos, breadth, firstSpace, containsEnemy, runLength, bounded);

            pos.increment(rowInc, colInc);
        } while (pos.getCol() <= maxCol && pos.getRow() <= maxRow);
        return runPotential;
    }

    private boolean containsEnemy(GoString groupString, boolean ownedByPlayer1, GoBoardPosition space) {
        return space.isOccupied() && space.getPiece().isOwnedByPlayer1() != ownedByPlayer1
                && groupString.isEnemy(space);
    }

    private boolean inRun(Location pos, int maxRow, int maxCol, GoBoardPosition space, boolean ownedByPlayer1) {
        return (pos.getCol() <= maxCol && pos.getRow() <= maxRow
                && (space.isUnoccupied() ||
                   (space.isOccupied() && space.getPiece().isOwnedByPlayer1() != ownedByPlayer1)));
    }

    private boolean isBounded(GoBoardPosition startSpace, GoBoardPosition space, GoBoardPosition firstSpace) {
        return !(firstSpace.equals(startSpace)) && space!=null && space.isOccupied();
    }

    /**
     * Accumulate the potential for this run.
     * @return  accrued run potential.
     */
    private float accrueRunPotential(int rowInc, Location pos, int breadth,
                                     GoBoardPosition firstSpace, boolean containsEnemy,
                                     int runLength, boolean bounded) {
        float runPotential = 0;
        if (!containsEnemy && runLength < breadth && runLength > 0) {
            int firstPos, max, currentPos;
            if (rowInc == 1) {
                firstPos = firstSpace.getRow();
                max = board_.getNumRows();
                currentPos = pos.getRow();
            } else {
                firstPos = firstSpace.getCol();
                max = board_.getNumCols();
                currentPos = pos.getCol();
            }

            runPotential += getRunPotential(runLength, firstPos, currentPos, max, bounded);
        }
        return runPotential;
    }


    /**
     * @return potential score for the runlength.
     */
    private float getRunPotential(int runLength, int firstPos, int endPosP1, int max,
                                                    boolean boundedByFriendStones) {
        float potential;
        assert(runLength > 0);

        if ((firstPos == 1 || endPosP1 == max + 1 || boundedByFriendStones)) {
            potential = getRunPotentialInternal(runLength);
        }
        else {
            potential = getRunPotentialToBoundary(runLength);
        }
        return potential;
    }

    /**
     * This case is where the run is next to an edge or bounded by friend stones.
     * Weight the potential more heavily.
     * @return potential score for the runlength.
     */
    private float getRunPotentialInternal(int runLength) {
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
     * A run to boundary. Less weight attributed.
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
            case 8: potential = 0.06f; break;
            default : potential = 0.05f;
        }
        return potential;
    }
}