package com.becker.game.twoplayer.go.board;

import com.becker.game.twoplayer.go.board.analysis.CandidateMoveAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.NeighborAnalyzer;
import com.becker.game.twoplayer.go.*;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;
import com.becker.common.*;

import com.becker.game.twoplayer.go.board.analysis.TerritoryAnalyzer;
import java.util.*;

import static com.becker.game.twoplayer.go.GoControllerConstants.*;

/**
 * Representation of a Go Game Board
 * There are a lot of datastructures to organize the state of the pieces.
 * For example, we update strings, and groups (and eventually armies) after each move.
 * After updating we can use these structures to estimate territory for each side.
 *
 * @author Barry Becker
 */
public final class GoBoard extends TwoPlayerBoard
{

    // this is a set of active groups. Groups are composed of strings.
    private Set<GoGroup> groups_;

    private HandicapStones handicap_;

    private BoardUpdater boardUpdater_;

    private TerritoryAnalyzer territoryAnalyzer_;

    /**
     *  Constructor.
     *  @param numRows num rows
     *  @param numCols num cols
     *  @param numHandicapStones number of black handicap stones to initialize with.
     */
    public GoBoard( int numRows, int numCols, int numHandicapStones )
    {
        // need to synchronize this to avoid concurrent modification error during search.
        groups_ = Collections.synchronizedSet(new HashSet<GoGroup>(10));
        //armies_ = new HashSet(0);
        setSize( numRows, numCols );
        setHandicap(numHandicapStones);
        boardUpdater_ = new BoardUpdater(this);
        territoryAnalyzer_ = new TerritoryAnalyzer(this);
    }

    /**
     * start over from the beggining and reinitialize everything.
     */
    @Override
    public void reset()
    {
        super.reset();
        groups_.clear();
        for ( int i = 1; i <= getNumRows(); i++ )  {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new GoBoardPosition(i,j, null, null);
            }
        }
        // first time through we need to initialize the starpoint positions
        setHandicap(getHandicap());
    }

    public void setHandicap(int handicap) {
        handicap_ = new HandicapStones(handicap, getNumRows());
        makeMoves(handicap_.getHandicapMoves());
    }

    /**
      * @return a deep copy of the board.
      */
    @Override
     public Object clone() throws CloneNotSupportedException
     {
        Object clone = super.clone();

        // make copies of all the groups and armies
        if (groups_!=null) {
            ((GoBoard)clone).groups_ = new HashSet<GoGroup>(10);

            Set<GoGroup> groupsCopy = ((GoBoard)clone).groups_;

            // new way to interate
            synchronized(groups_)   {
                for (GoGroup g : groups_)  {
                    groupsCopy.add((GoGroup)g.clone());
                }
            }
        }

        return clone;
     }


    /**
     * set the dimensions of the game board (must be square).
     * must call reset() after changing the size.
     * @param numRows number of rows
     * @param numCols number of columns
     *
     * @@ Bill says just create new board instead of calling reset or resize
     */
    @Override
    public void setSize( int numRows, int numCols )
    {
        numRows_ = numRows;
        numCols_ = numRows; // intentionally same as numRows
        if ( numRows_ != numCols_ )  {
            GameContext.log(0,  "The board must be square and have an odd edge length" );
            if (numRows < numCols)
                numCols_ = numRows;
            else
                numRows_ = numCols;
        }
        if ( numRows_ % 2 == 0 ) numRows_++;
        if ( numCols_ % 2 == 0 ) numCols_++;

        rowsTimesCols_ = numRows_ * numCols_;
        // we don't use the 0 edges of the board
        positions_ = new BoardPosition[numRows_ + 1][numCols_ + 1];
        reset();
    }


    /**
     * get the number of handicap stones used in this game.
     * @return number of handicap stones
     */
    public int getHandicap()
    {
        if (handicap_ == null) {
            return 0;
        }
        return handicap_.getNumber();
    }


    /**
     *in go there is not really a theoretical limit to the number of moves,
     * but practically if we exceed this then we award the game to whoever is ahead.
     * @return the maximum number of moves ever expected for this game.
     */
    public int getMaxNumMoves()
    {
        return 3 * rowsTimesCols_;
    }

    /**
     * @return typical number of moves in a go game.
     */
    public int getTypicalNumMoves() {
        return rowsTimesCols_ - getNumRows();
    }

    /**
     * get the current set of active groups
     * @return all the valid groups on the board (for both sides)
     */
    public Set<GoGroup> getGroups()
    {
        return groups_;
    }


    @Override
    protected GameProfiler createProfiler() {
        return new GoProfiler();
    }

    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move, and updates groups,
     * removes captures, and counts territory.
     *
     * @return false if the move is somehow invalid
     */
    @Override
    protected boolean makeInternalMove( Move move )
    {
        getProfiler().startMakeMove();

        GoMove m = (GoMove)move;

        // if its a passing move, there is nothing to do
        if ( m.isPassingMove() ) {
            GameContext.log( 2, "making passing move" );
            getProfiler().stopMakeMove();
            return true;
        }

        // we hit this all the time when mousing over the game tree.
        //assert (stone.isUnoccupied()):
        //        "Position "+stone+" is already occupied. move num ="+ this.getNumMoves() +" \nBoard:\n"+this.toString();

        clearEyes();
        super.makeInternalMove( m );
        boardUpdater_.updateAfterMove(m);

        getProfiler().stopMakeMove();
        return true;
    }

    /**
     * for Go, undoing a move means changing that space back to a blank, restoring captures, and updating groups.
     * @param move  the move to undo.
     */
    protected void undoInternalMove( Move move )
    {
        getProfiler().startUndoMove();

        GoMove m = (GoMove) move;

        // there is nothing to do if it is a pass
        if ( m.isPassingMove() ) {
            getProfiler().stopUndoMove();
            return;
        }

        // first make sure that there are no references to obsolete groups.
        clearEyes();

        boardUpdater_.updateAfterRemove(m);

        getProfiler().stopUndoMove();
    }


    public int getNumCaptures(boolean player1StonesCaptured) {
        return boardUpdater_.getNumCaptures(player1StonesCaptured);
    }

    /**
     * @see TerritoryAnalyzer#getTerritoryDelta
     */
    public float getTerritoryDelta()
    {
        return territoryAnalyzer_.getTerritoryDelta();
    }

   /**
     * @see TerritoryAnalyzer#getTerritoryEstimate
     */
    public int getTerritoryEstimate( boolean forPlayer1, boolean isEndOfGame)
    {
        return territoryAnalyzer_.getTerritoryEstimate(forPlayer1, isEndOfGame);
    }

    /**
     * @see TerritoryAnalyzer#updateTerritory
     */
    public float updateTerritory(boolean isEndOfGame) {
        return territoryAnalyzer_.updateTerritory(isEndOfGame);
    }

    /**
     * determine a set of stones that are tightly connected to the specified stone.
     * This set of stones constitutes a string, but since stones cannot belong to more than
     * one string we must return a List.
     * @param stone he stone from which to begin searching for the string
     * @param returnToUnvisitedState if true then the stomes will all be marked unvisited when done searching
     */
    public List<GoBoardPosition> findStringFromInitialPosition( GoBoardPosition stone, boolean returnToUnvisitedState )
    {
        return findStringFromInitialPosition(
                stone, stone.getPiece().isOwnedByPlayer1(), returnToUnvisitedState, NeighborType.OCCUPIED,
                1, numRows_, 1, numCols_ );
    }

    public List<GoBoardPosition> findStringFromInitialPosition( GoBoardPosition stone,  boolean friendOwnedByP1,
                                                     boolean returnToUnvisitedState, NeighborType type,
                                                     Box box) {
         return findStringFromInitialPosition(
                stone, friendOwnedByP1, returnToUnvisitedState, type,
                box.getMinRow(), box.getMaxRow(), box.getMinCol(), box.getMaxCol() );
    }

    /**
     * determines a string connected from a seed stone within a specified bounding area
     * @return string from seed stone
     */
    public List<GoBoardPosition> findStringFromInitialPosition( GoBoardPosition stone,  boolean friendOwnedByP1,
                                                     boolean returnToUnvisitedState, NeighborType type,
                                                     int rMin, int rMax, int cMin, int cMax )
    {
        getProfiler().start(GoProfiler.FIND_STRINGS);
        NeighborAnalyzer na = new NeighborAnalyzer(this);
        List<GoBoardPosition> stones =
                na.findStringFromInitialPosition(stone, friendOwnedByP1, returnToUnvisitedState,
                                                                  type, rMin, rMax, cMin, cMax);
        getProfiler().stop(GoProfiler.FIND_STRINGS);

        return stones;
    }

    /**
     * get neighboring stones of the specified stone.
     * @param stone the stone (or space) whose neighbors we are to find (it must contain a piece).
     * @param neighborType (EYE, NOT_FRIEND etc)
     * @return a set of stones that are immediate (nobi) neighbors.
     */
    public Set<GoBoardPosition> getNobiNeighbors( GoBoardPosition stone, NeighborType neighborType )
    {
       return getNobiNeighbors( stone, stone.getPiece().isOwnedByPlayer1(), neighborType);
    }

    /**
     * get neighboring stones of the specified stone.
     * @param stone the stone (or space) whose neighbors we are to find.
     * @param friendOwnedByP1 need to specify this in the case that the stone is a blank space and has undefined ownership.
     * @param neighborType (EYE, NOT_FRIEND etc)
     * @return a set of stones that are immediate (nobi) neighbors.
     */
    public Set<GoBoardPosition> getNobiNeighbors( GoBoardPosition stone, boolean friendOwnedByP1, NeighborType neighborType )
    {
        NeighborAnalyzer na = new NeighborAnalyzer(this);
        return na.getNobiNeighbors(stone, friendOwnedByP1, neighborType);
    }

    /**
     * return a set of stones which are loosely connected to this stone.
     * Check the 16 purely group neighbors and 4 string neighbors
     *         ***
     *        **S**
     *        *SXS*
     *        **S**
     *         ***
     * @param stone (not necessarily occupied)
     * @param friendPlayer1 typically stone.isOwnedByPlayer1 value of stone unless it is blank.
     * @param samePlayerOnly if true then find group nbrs that are have same ownership as friendPlayer1
     */
    public Set<GoBoardPosition> getGroupNeighbors( GoBoardPosition stone, boolean friendPlayer1, boolean samePlayerOnly )
    {
        getProfiler().start(GoProfiler.GET_GROUP_NBRS);

        NeighborAnalyzer na = new NeighborAnalyzer(this);
        Set<GoBoardPosition> nbrStones = na.getGroupNeighbors(stone, friendPlayer1, samePlayerOnly);

        getProfiler().stop(GoProfiler.GET_GROUP_NBRS);
        return nbrStones;
    }

    /**
     * This version assumes that the stone is occupied.
     */
    public Set getGroupNeighbors( GoBoardPosition position, boolean samePlayerOnly )
    {
        assert (position != null);
        assert (position.getPiece() != null);
        return getGroupNeighbors( position, position.getPiece().isOwnedByPlayer1(), samePlayerOnly );
    }

    /**
     * determine a set of stones that are loosely connected to the specified stone.
     * This set of stones constitutes a group, but since stones cannot belong to more than
     * one group (or string) we must return a List.
     *
     * @param stone the stone to search from for group neighbors.
     * @return the list of stones in the group that was found.
     */
    public List<GoBoardPosition> findGroupFromInitialPosition( GoBoardPosition stone )
    {
        return findGroupFromInitialPosition( stone, true );
    }

    /**
     * determine a set of stones that have group connections to the specified stone.
     * This set of stones constitutes a group, but since stones cannot belong to more than
     * one group (or string) we must return a List.
     * Group connections include nobi, ikken tobi, and kogeima.
     *
     * @param stone the stone to search from for group neighbors.
     * @param returnToUnvisitedState if true, then mark everything unvisited when done.
     * @return the list of stones in the group that was found.
     */
    public List<GoBoardPosition> findGroupFromInitialPosition( GoBoardPosition stone, boolean returnToUnvisitedState )
    {
        getProfiler().start(GoProfiler.FIND_GROUPS);

        NeighborAnalyzer na = new NeighborAnalyzer(this);
        List<GoBoardPosition> stones = na.findGroupFromInitialPosition(stone, returnToUnvisitedState);

        getProfiler().stop(GoProfiler.FIND_GROUPS);
        return stones;
    }

    /**
     * clear all the eyes from all the stones on the board
     */
    private void clearEyes()
    {
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                GoBoardPosition space = (GoBoardPosition)positions_[i][j];
                if ( space.isInEye() )     {
                    // remove reference to the owning group so it can be garbage collected.
                    space.getEye().clear();
                    space.setEye(null);
                }
            }
        }
    }


    /**
     * @return either the number of black or white stones.
     */
    public int getNumStones(boolean forPlayer1)
    {
        int numStones = 0;

        // we should be able to just sum all the position scores now.
        for ( int i = 1; i <= getNumRows(); i++ )  {
           for ( int j = 1; j <= getNumCols(); j++ ) {
               GoBoardPosition pos = (GoBoardPosition)positions_[i][j];
               if (pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == forPlayer1)  {
                  numStones++;
               }
           }
        }
        return numStones;
    }

    /**
     * Num different states.
     * This is used primarily for the Zobrist hash. You do not need to override if you do not use it.
     * @return number of different states this position can have.
     */
    public int getNumPositionStates() {
        return 3;
    }

    public List getHandicapPositions() {
        return handicap_.getStarPoints();
    }

    @Override
    public String toString() {
        int rows = getNumRows();
        int cols = getNumCols();
        StringBuffer buf = new StringBuffer((rows + 2) * (cols + 2));

        buf.append("   ");
        for ( int j = 1; j <= rows; j++ ) {
            buf.append(j % 10);
        }
        buf.append(' ');
        buf.append("\n  ");
        for ( int j = 1; j <= cols + 2; j++ ) {
            buf.append('-');
        }
        buf.append('\n');

        for ( int i = 1; i <= rows; i++ ) {
            buf.append(i / 10);
            buf.append(i % 10);
            buf.append('|');
            for ( int j = 1; j <= cols; j++ ) {
                GoBoardPosition space = (GoBoardPosition) getPosition(i, j);
                if ( space.isOccupied() )     {
                    buf.append(space.getPiece().isOwnedByPlayer1()?'X':'O');
                }
                else {
                    buf.append(' ');
                }
            }
            buf.append('|');
            buf.append('\n');
        }
        return buf.toString();
    }



    /**
     * The number of star points used for handicap stones on the board
     * There may be none.
     */
    private static class HandicapStones {

        private static final float HANDICAP_STONE_HEALTH = 0.8f;

        // the number of initial handicap stones to use
        private int numHandicapStones_ = 0;

        // typically there are at most 9 handicap stones in an uneven game
        private List<GoBoardPosition> starPoints_ = null;


        /**
         * @param num number of handicap stones
         * @param boardSize on one side.
         */
        HandicapStones(int num, int boardSize) {
            initStarPoints(boardSize);
            numHandicapStones_ = num;
        }

        public int getNumber() {
            return numHandicapStones_;
        }

        public List getStarPoints() {
            return starPoints_;
        }

        /**
         * specify the number of handicap stones that will actually be used this game.
         * public since we might set if from the options dialog
         */
        public List getHandicapMoves()
        {
            assert numHandicapStones_ <= starPoints_.size();
            List<GoMove> handicapMoves = new ArrayList<GoMove>(numHandicapStones_);

            for ( int i = 0; i < numHandicapStones_; i++ ) {
                GoBoardPosition hpos = starPoints_.get( i );

                GoMove m = GoMove.createGoMove( hpos.getRow(), hpos.getCol(), 0, (GoStone)hpos.getPiece());
                                              new GoStone(hpos.getPiece().isOwnedByPlayer1(), GamePiece.REGULAR_PIECE );
                handicapMoves.add(m);
            }
            return handicapMoves;
        }

        /**
         * initialize a list of stones at the star points
         */
        private void initStarPoints(int boardSize)
        {
            // initialize the list of handicap stones.
            // The number of these that actually get placed on the board
            // depends on the handicap
            starPoints_ = new ArrayList<GoBoardPosition>(9);
            int nRows = boardSize;
            int min = 4;
            // on a really small board we put the corner star points at 3-3.
            if (nRows < 13)
                min = 3;
            int max = nRows - (min-1);
            int mid = (nRows >> 1) + 1;

            // add the star points
            GoStone handicapStone = new GoStone(true, HANDICAP_STONE_HEALTH);
            starPoints_.add( new GoBoardPosition( min, min, null, (GoStone)handicapStone.copy()) );
            starPoints_.add( new GoBoardPosition( max, max, null, (GoStone)handicapStone.copy()) );
            starPoints_.add( new GoBoardPosition( min, max, null, (GoStone)handicapStone.copy()) );
            starPoints_.add( new GoBoardPosition( max, min, null, (GoStone)handicapStone.copy()) );
            starPoints_.add( new GoBoardPosition( min, mid, null, (GoStone)handicapStone.copy()) );
            starPoints_.add( new GoBoardPosition( max, mid, null, (GoStone)handicapStone.copy()) );
            starPoints_.add( new GoBoardPosition( mid, min, null, (GoStone)handicapStone.copy()) );
            starPoints_.add( new GoBoardPosition( mid, max, null, (GoStone)handicapStone.copy()) );
            starPoints_.add( new GoBoardPosition( mid, mid, null, handicapStone) );
        }
    }

}
