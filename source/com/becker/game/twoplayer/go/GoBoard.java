package com.becker.game.twoplayer.go;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;

import java.util.*;

import static com.becker.game.twoplayer.go.GoControllerConstants.*;

/**
 * Representation of a Go Game Board
 * There are a lot of datastructures to organize the state of the pieces.
 * For example, we update strings, and groups (and eventually armies) after each move.
 * After updating we can use these structures to estimate territory for each side.
 *
 *
 * @author Barry Becker
 */
public final class GoBoard extends TwoPlayerBoard
{

    // this is a set of active groups. Groups are composed of strings.
    private Set groups_ = null;
    // this is a set of active armies. Armies are composed of groups.
    // armies not implemented yet.
    //private Set armies_;

    // The difference between the 2 player's territory.
    // It is computed as black-white = sum(health of stone i)
    private float territoryDelta_ = 0;

    private CandidateMoves candidateMoves_;

    private HandicapStones handicap_ = null;

    private int numWhiteStonesCaptured_ = 0;
    private int numBlackStonesCaptured_ = 0;

    // a global profiler for recording timing stats
    private static final GoProfiler profiler_ = new GoProfiler();



    /**
     *  constructor.
     *  @param numRows num rows
     *  @param numCols num cols
     *  @param numHandicapStones number of black handicap stones to initialize with.
     */
    public GoBoard( int numRows, int numCols, int numHandicapStones )
    {
        // need to synchronize this to avoid concurrent modification error during search.
        groups_ = Collections.synchronizedSet(new HashSet(10));
        //armies_ = new HashSet(0);
        setSize( numRows, numCols );
        setHandicap(numHandicapStones);
    }

    /**
     * start over from the beggining and reinitialize everything.
     */
    public void reset()
    {
        super.reset();
        groups_.clear();
        //armies_.clear();
        for ( int i = 1; i <= getNumRows(); i++ )  {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new GoBoardPosition(i,j, null, null);
            }
        }
        // first time through we need to initialize the starpoint positions
        setHandicap(getHandicap());

        candidateMoves_.reset();
    }

    public void setHandicap(int handicap) {
        handicap_ = new HandicapStones(handicap, getNumRows());
        makeMoves(handicap_.getHandicapMoves());
    }

    /**
      * @return a deep copy of the board.
      */
     public Object clone() throws CloneNotSupportedException
     {
        Object clone = super.clone();

        // make copies of all the groups and armies
        if (groups_!=null) {
            ((GoBoard)clone).groups_ = new HashSet(10);

            Set groupsCopy = ((GoBoard)clone).groups_;

            // new way to interate
            for (Object g : groups_)  {
                groupsCopy.add(((GoGroup)g).clone());
            }
        }

        /*
        if (armies_!=null)  {
            ((GoBoard)clone).armies_ = new HashSet(10);
            Set armiesCopy = ((GoBoard)clone).armies_;
            for (Object a : armies_) {
                armiesCopy.add(((GoArmy)a).clone());
            }
        }
        */
        return clone;
     }


    /**
     * set the dimensions of the game board (must be square).
     * must call reset() after changing the size.
     * @param numRows number of rows
     * @param numCols number of columns
     *
     * @@ just create new board instead of calling reset or resize
     */
    public void setSize( int numRows, int numCols )
    {
        numRows_ = numRows;
        numCols_ = numCols;
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

        candidateMoves_ = new CandidateMoves(numRows);
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

    public void determineCandidateMoves() {
        candidateMoves_.determineCandidateMoves(positions_);
    }

    /**
     * In theory all empties should be considered, but in practice we keep
     * a shorter list of reasonable moves lest things get intractable.
     *
     * @return true if this position is a reasonable next move
     */
    public boolean isCandidateMove( int row, int col )
    {
        return candidateMoves_.isCandidateMove(row,col) && positions_[row][col].isUnoccupied();
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
     * get the current set of active groups
     * @return all the valid groups on the board (for both sides)
     */
    public Set getGroups()
    {
        return groups_;
    }

    /**
     * record times for these operations so we get an accurate picture of where the bottlenecks are.
     */
    public void initializeGobalProfilingStats()
    {
        profiler_.initialize();
    }


    public static GoProfiler getProfiler() {
        return profiler_;
    }

    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move, and updates groups,
     * removes captures, and counts territory.
     *
     * @return false if the move is somehow invalid
     */
    protected boolean makeInternalMove( Move move )
    {
        profiler_.startMakeMove();

        GoMove m = (GoMove)move;

        // if its a passing move, there is nothing to do
        if ( m.isPassingMove() ) {
            GameContext.log( 2, "making passing move" );
            return true;
        }


        // we hit this all the time when mousing over the game tree.
        //assert (stone.isUnoccupied()):
        //        "Position "+stone+" is already occupied. move num ="+ this.getNumMoves() +" \nBoard:\n"+this.toString();

        // first make sure that there are no references to obsolete groups.
        clearEyes();    // I think this is important

        super.makeInternalMove( m );

        m.updateBoardAfterMoving(this);

        updateCaptures(m, true);

        //if ( m.isSuicidal(this) )  now checked beforehand
        //    return false;

        profiler_.stopMakeMove();
        return true;
    }



    /**
     * for Go, undoing a move means changing that space back to a blank, restoring captures, and updating groups.
     * @param move  the move to undo.
     */
    protected void undoInternalMove( Move move )
    {
        profiler_.startUndoMove();

        GoMove m = (GoMove) move;

        // there is nothing to do if it is a pass
        if ( m.isPassingMove() ) {
            return;
        }


        // first make sure that there are no references to obsolete groups.
        clearEyes();

        m.updateBoardAfterRemoving(this);

        updateCaptures(m, false);

        profiler_.stopUndoMove();
    }

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


    public int getNumCaptures(boolean player1StonesCaptured) {
        return player1StonesCaptured ? numBlackStonesCaptured_ : numWhiteStonesCaptured_ ;
    }


    public float getTerritoryDelta()
    {
        return territoryDelta_;
    }

    /**
     * loops through the groups and armies to determine the territorial
     * difference between the players.
     * Then loops through and determines a score for positions that are not part of groups.
     * If a position is part of an area that borders only a living group, then it is considered
     * territory for that group's side. If, however, the position borders living groups from
     * both sides, then the score is weighted according to what proportion of the perimeter
     * borders each living group and how alive those bordering groups are.
     * This is the primary factor in evaluating the board position for purposes of search.
     * This method and the methods it calls are the crux of this go playing program.
     *
     * @return the estimated difference in territory between the 2 sides.
     *  A large positive number indeicates black is winning, while a negative number indicates taht white has the edge.
     */
    float updateTerritory(GoBoardPosition lastMove)
    {
        profiler_.start(GoProfiler.UPDATE_TERRITORY);

        float delta = 0;
        Iterator it = groups_.iterator();
        // first calculate the absolute health of the groups so that measure can
        // be used in the more accurate relative health computation.

        while ( it.hasNext() ) {
            profiler_.start(GoProfiler.ABSOLUTE_TERRITORY);
            GoGroup g = (GoGroup) it.next();

            float health = g.calculateAbsoluteHealth( this, profiler_ );

            if (!USE_RELATIVE_GROUP_SCORING)  {
                g.updateTerritory( health );
                delta += health * g.getNumStones();
            }
            profiler_.stop(GoProfiler.ABSOLUTE_TERRITORY);
        }

        if (USE_RELATIVE_GROUP_SCORING)  {
            profiler_.start(GoProfiler.RELATIVE_TERRITORY);
            it = groups_.iterator();
            while ( it.hasNext() ) {
                GoGroup g = (GoGroup) it.next();
                float health = g.calculateRelativeHealth( this, lastMove, profiler_ );
                g.updateTerritory( health );
                delta += health * g.getNumStones();
            }
            profiler_.stop(GoProfiler.RELATIVE_TERRITORY);
        }
        // need to loop over the board and determine for each space if it is territory for the specified player.
        // We will first mark visited all the stones that are "controlled" by the specified player.
        // The unoccupied "controlled" positions will be territory.
        profiler_.start(GoProfiler.UPDATE_EMPTY);
        delta += updateEmptyRegions();
        profiler_.stop(GoProfiler.UPDATE_EMPTY);

        profiler_.stop(GoProfiler.UPDATE_TERRITORY);
        territoryDelta_ = delta;
        return delta;
    }


    /**
     * assign scores to empty positions that are not eyes in groups.
     *
     * @return the estimated difference in territory. A positive value means black is ahead.
     */
    private float updateEmptyRegions()
    {
        float diffScore = 0;
        //only do this when the midgame starts, since early on there is alwas only one connected empty region.
        int edgeOffset = 1;

        if (getNumMoves() <= 2 * this.getNumRows())
            return diffScore;
        if (getNumMoves() >= rowsTimesCols_ / 4.5)
            edgeOffset = 0;
        int min = 1+edgeOffset;
        int rMax = getNumRows()-edgeOffset;
        int cMax = getNumCols()-edgeOffset;

        List emptyLists = new LinkedList();
        for ( int i = min; i <= rMax; i++ )  {
           for ( int j = min; j <= cMax; j++ ) {
               GoBoardPosition pos = (GoBoardPosition)positions_[i][j];
               if (pos.getString() == null && !pos.isInEye()) {
                   assert pos.isUnoccupied();
                   if (!pos.isVisited()) {

                       // don't go all the way to the borders (until the end of the game),
                       // since otherwise we will likely get only one big empty region.
                       List empties = findStringFromInitialPosition(pos, false, false, NeighborType.UNOCCUPIED,
                                                                    min, rMax,  min, cMax);
                       emptyLists.add(empties);
                       Set nbrs = findOccupiedNeighbors(empties);
                       float avg = GoBoardUtil.calcAverageScore(nbrs);

                       float score = avg * (float)nbrs.size()/Math.max(1, Math.max(nbrs.size(), empties.size()));
                       assert (score <= 1.0 && score >= -1.0): "score="+score+" avg="+avg;
                       Iterator it = empties.iterator();
                       while (it.hasNext()) {
                           GoBoardPosition p = (GoBoardPosition)it.next();

                           p.setScoreContribution(score);
                           diffScore += score;
                       }
                   }
               }
           }
        }

        GoBoardUtil.unvisitPositionsInLists(emptyLists);
        return diffScore;
    }

    /**
     * @param empties a list of unoccupied positions.
     * @return a list of stones bordering the set of empty board positions.
     */
    public Set findOccupiedNeighbors(List empties)
    {
        Iterator it = empties.iterator();
        Set allNbrs = new HashSet();
        while (it.hasNext()) {
            GoBoardPosition empty = (GoBoardPosition)it.next();
            assert (empty.isUnoccupied());
            Set nbrs = getNobiNeighbors(empty, false, NeighborType.OCCUPIED);
            // add these nbrs to the set of all nbrs
            // (dupes automatically culled because HashSets only have unique members)
            allNbrs.addAll(nbrs);
        }
        return allNbrs;
    }


    /**
     * determine a set of stones that are tightly connected to the specified stone.
     * This set of stones constitutes a string, but since stones cannot belong to more than
     * one string we must return a List.
     * @param stone he stone from which to begin searching for the string
     * @param returnToUnvisitedState if true then the stomes will all be marked unvisited when done searching
     */
    public List findStringFromInitialPosition( GoBoardPosition stone, boolean returnToUnvisitedState )
    {
        return findStringFromInitialPosition(
                stone, stone.getPiece().isOwnedByPlayer1(), returnToUnvisitedState, NeighborType.OCCUPIED,
                1, numRows_, 1, numCols_ );
    }

    public List findStringFromInitialPosition( GoBoardPosition stone,  boolean friendOwnedByP1,
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
    public List findStringFromInitialPosition( GoBoardPosition stone,  boolean friendOwnedByP1,
                                                     boolean returnToUnvisitedState, NeighborType type,
                                                     int rMin, int rMax, int cMin, int cMax )
    {
        profiler_.start(GoProfiler.FIND_STRINGS);
        List stones = new ArrayList();
        // perform a breadth first search  until all found.
        // use the visited flag to indicate that a stone has been added to the string
        List stack = new LinkedList();
        assert ( stone.getRow() >= rMin && stone.getRow() <= rMax && stone.getCol() >= cMin && stone.getCol() <= cMax ):
                "rMin="+rMin +" rMax="+rMax+" cMin="+cMin+" cMax="+cMax+"   r="+stone.getRow()+" c="+stone.getCol();
        assert ( !stone.isVisited() ): "stone="+stone;
        stack.add( 0, stone );
        while ( !stack.isEmpty() ) {
            GoBoardPosition s = (GoBoardPosition) stack.remove( 0 );
            if ( !s.isVisited() ) {
                s.setVisited( true );
                stones.add( s );
                pushStringNeighbors(s, friendOwnedByP1, stack, true, type,  new Box(rMin, cMin, rMax, cMax));
            }
        }
        if ( returnToUnvisitedState )
            GoBoardUtil.unvisitPositions( stones );
        // GoBoardUtil.confirmNoDupes( stone, stones );
        profiler_.stop(GoProfiler.FIND_STRINGS);

        return stones;
    }




    /**
     * get an estimate of the territory for the specified player.
     * This estimate is computed by summing all spaces in eyes + dead opponent stones that are still on the board in eyes.
     * At the end of the game this + the number of pieces captured so far should give the true score.
     */
    public int getTerritoryEstimate( boolean forPlayer1, boolean estimate)
    {
        float territoryEstimate = 0;

        // we should be able to just sum all the position scores now.
        for ( int i = 1; i <= getNumRows(); i++ )  {
           for ( int j = 1; j <= getNumCols(); j++ ) {
               GoBoardPosition pos = (GoBoardPosition)positions_[i][j];

               if (pos.isUnoccupied()) {
                   double val = estimate? pos.getScoreContribution() : 1.0;
                   if (forPlayer1 && pos.getScoreContribution() > 0) {
                       territoryEstimate += val;
                   }
                   else if (!forPlayer1 && pos.getScoreContribution() < 0)  {
                       territoryEstimate -= val;  // will be positive
                   }
               }
           }
        }

        return (int)territoryEstimate;
    }


    /**
     * get neighboring stones of the specified stone.
     * @param stone the stone (or space) whose neighbors we are to find (it must contain a piece).
     * @param neighborType (EYE, NOT_FRIEND etc)
     * @return a set of stones that are immediate (nobi) neighbors.
     */
    public Set getNobiNeighbors( GoBoardPosition stone, NeighborType neighborType )
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
    public Set getNobiNeighbors( GoBoardPosition stone, boolean friendOwnedByP1, NeighborType neighborType )
    {
        Set nbrs = new HashSet();
        int row = stone.getRow();
        int col = stone.getCol();

        if ( row > 1 )
            GoBoardUtil.getNobiNeighbor( (GoBoardPosition) positions_[row - 1][col], friendOwnedByP1, nbrs, neighborType );
        if ( row + 1 <= numRows_ )
            GoBoardUtil.getNobiNeighbor( (GoBoardPosition) positions_[row + 1][col], friendOwnedByP1, nbrs, neighborType );
        if ( col > 1 )
            GoBoardUtil.getNobiNeighbor( (GoBoardPosition) positions_[row][col - 1], friendOwnedByP1, nbrs, neighborType );
        if ( col + 1 <= numCols_ )
            GoBoardUtil.getNobiNeighbor( (GoBoardPosition) positions_[row][col + 1], friendOwnedByP1, nbrs, neighborType );

        return nbrs;
    }

    public void printNobiNeighborsOf(GoBoardPosition stone)
    {
        int row = stone.getRow();
        int col = stone.getCol();
        GameContext.log(0,  "Nobi Neigbors of "+stone+" are : " );
        if ( row > 1 )
            System.out.println( positions_[row - 1][col] );
        if ( row + 1 <= numRows_ )
            System.out.println( positions_[row + 1][col] );
        if ( col > 1 )
            System.out.println( positions_[row][col-1] );
        if ( col + 1 <= numCols_ )
            System.out.println( positions_[row][col+1] );
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
    public Set getGroupNeighbors( GoBoardPosition stone, boolean friendPlayer1, boolean samePlayerOnly )
    {
        profiler_.start(GoProfiler.GET_GROUP_NBRS);
        List stack = new LinkedList();

        pushGroupNeighbors( stone, friendPlayer1, stack, samePlayerOnly );
        Set nbrStones = new HashSet();
        nbrStones.addAll( stack );

        profiler_.stop(GoProfiler.GET_GROUP_NBRS);
        return nbrStones;
    }

    // this version assumes that the stone is occupied.
    public Set getGroupNeighbors( GoBoardPosition position, boolean samePlayerOnly )
    {
        assert (position.getPiece() != null);
        return getGroupNeighbors( position, position.getPiece().isOwnedByPlayer1(), samePlayerOnly );
    }


    /**
     * Remove all the groups in groups_ corresponding to the specified list of stones.
     * @param stones
     */
    public void removeGroupsForListOfStones(List stones) {
        Iterator mIt = stones.iterator();
        while ( mIt.hasNext() ) {
            GoBoardPosition nbrStone = (GoBoardPosition) mIt.next();
            // In the case where the removed stone was causing an atari in a string in an enemy group,
            // there is a group that does not contain a nbrstone that also needs to be removed here.
            groups_.remove( nbrStone.getGroup() );
        }
    }


    public Set findStringNeighbors(GoBoardPosition stone ) {
        Set stringNbrs = new HashSet();
        List nobiNbrs = new LinkedList();
        pushStringNeighbors(stone, false, nobiNbrs, false);

        // add strings only once
        for (Object nn : nobiNbrs) {
            GoBoardPosition nbr = (GoBoardPosition)nn;
            stringNbrs.add(nbr.getString());
        }
        return stringNbrs;
    }

    /**
     * Check all nobi neighbors (at most 4).
     * @param s the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @return number of stones added to the stack
     */
    private int pushStringNeighbors( GoBoardPosition s, boolean friendPlayer1, List stack,
                                     boolean samePlayerOnly, NeighborType type,
                                     Box bbox )
    {
        int r = s.getRow();
        int c = s.getCol();
        int numPushed = 0;
        Location loc = new Location(r, c);

        if ( r > 1 )
            numPushed += checkNeighbor( loc, -1, 0, friendPlayer1, stack, samePlayerOnly, type, bbox );
        if ( c > 1 )
            numPushed += checkNeighbor( loc, 0, -1, friendPlayer1, stack, samePlayerOnly, type, bbox );
        if ( r + 1 <= numRows_ )
            numPushed += checkNeighbor( loc, 1, 0, friendPlayer1, stack, samePlayerOnly, type, bbox );
        if ( c + 1 <= numCols_ )
            numPushed += checkNeighbor( loc, 0, 1, friendPlayer1, stack, samePlayerOnly, type, bbox );

        return numPushed;
    }

    private int pushStringNeighbors( GoBoardPosition s, boolean friendPlayer1, List stack, boolean samePlayerOnly )
    {
        return pushStringNeighbors( s, friendPlayer1, stack, samePlayerOnly,
                                    NeighborType.OCCUPIED, new Box(1, 1, numRows_, numCols_));
    }

    /**
     * Check all diagonal neighbors (at most 4).
     * @param s the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @return number of stones added to the stack
     */
    private int pushEnemyDiagonalNeighbors( GoBoardPosition s, boolean friendPlayer1, List stack )
    {
        int r = s.getRow();
        int c = s.getCol();
        int numPushed = 0;
        if ( r > 1 && c > 1 )
            numPushed += checkDiagonalNeighbor( r, c, -1, -1, friendPlayer1, false, stack );
        if ( r + 1 <= numRows_ && c > 1 )
            numPushed += checkDiagonalNeighbor( r, c, 1, -1, friendPlayer1, false, stack );
        if ( r + 1 <= numRows_ && c + 1 <= numCols_ )
            numPushed += checkDiagonalNeighbor( r, c, 1, 1, friendPlayer1, false, stack );
        if ( r > 1 && c + 1 <= numCols_ )
            numPushed += checkDiagonalNeighbor( r, c, -1, 1, friendPlayer1, false, stack );

        return numPushed;
    }

    /**
     * Check all non-nobi group neighbors.
     * @param pos the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @param sameSideOnly if true push pure group nbrs of the same side only.
     * @return number of stones added to the stack
     */
    private int pushPureGroupNeighbors( GoBoardPosition pos, boolean friendPlayer1, boolean sameSideOnly, List stack )
    {
        int r = pos.getRow();
        int c = pos.getCol();
        int numPushed = 0;

        // if the stone of which we are checking nbrs is in atari, then there are no pure group nbrs because an
        // atari counts as a cut
        if (pos.isInAtari(this))
          return 0;


        // now check the diagonals
        if ( r > 1 && c > 1 )
            numPushed += checkDiagonalNeighbor( r, c, -1, -1, friendPlayer1, sameSideOnly, stack );
        if ( r > 1 && c + 1 <= numCols_ )
            numPushed += checkDiagonalNeighbor( r, c, -1, 1, friendPlayer1, sameSideOnly, stack );
        if ( r + 1 <= numRows_ && c + 1 <= numCols_ )
            numPushed += checkDiagonalNeighbor( r, c, 1, 1, friendPlayer1, sameSideOnly, stack );
        if ( r + 1 <= numRows_ && c > 1 )
            numPushed += checkDiagonalNeighbor( r, c, 1, -1, friendPlayer1, sameSideOnly, stack );

        // now check the 1-space jumps
        if ( r > 2 )
            numPushed += checkOneSpaceNeighbor( r, c, -2, 0, friendPlayer1, sameSideOnly, stack );
        if ( c > 2 )
            numPushed += checkOneSpaceNeighbor( r, c, 0, -2, friendPlayer1, sameSideOnly, stack );
        if ( r + 2 <= numRows_ )
            numPushed += checkOneSpaceNeighbor( r, c, 2, 0, friendPlayer1, sameSideOnly, stack );
        if ( c + 2 <= numCols_ )
            numPushed += checkOneSpaceNeighbor( r, c, 0, 2, friendPlayer1, sameSideOnly, stack );

        // now check knights move neighbors
        if ( (r > 2) && (c > 1) )
            numPushed += checkKogeimaNeighbor( r, c, -2, -1, friendPlayer1,  sameSideOnly, stack );
        if ( (r > 2) && (c + 1 <= numCols_) )
            numPushed += checkKogeimaNeighbor( r, c, -2, 1, friendPlayer1, sameSideOnly, stack );

        if ( (r + 2 <= numRows_) && (c > 1) )
            numPushed += checkKogeimaNeighbor( r, c, 2, -1, friendPlayer1, sameSideOnly, stack );
        if ( (r + 2 <= numRows_) && (c + 1 <= numCols_) )
            numPushed += checkKogeimaNeighbor( r, c, 2, 1, friendPlayer1, sameSideOnly, stack );

        if ( (r > 1) && (c > 2) )
            numPushed += checkKogeimaNeighbor( r, c, -1, -2, friendPlayer1, sameSideOnly, stack );
        if ( (r + 1 <= numRows_) && (c > 2) )
            numPushed += checkKogeimaNeighbor( r, c, 1, -2, friendPlayer1, sameSideOnly, stack );

        if ( (r > 1) && (c + 2 <= numCols_) )
            numPushed += checkKogeimaNeighbor( r, c, -1, 2, friendPlayer1, sameSideOnly, stack );
        if ( (r + 1 <= numRows_) && (c + 2 <= numCols_) )
            numPushed += checkKogeimaNeighbor( r, c, 1, 2, friendPlayer1, sameSideOnly, stack );

        return numPushed;
    }


    /**
     * Check all 20 neighbors (including diagonals, 1-space jumps, and knights moves).
     * Make sure diagonals are not cut nor 1-space jumps peeped.
     *
     * @param s the position containing a stone of which to check the neighbors of.
     * @param friendPlayer1 side to find groups stones for.
     * @param stack the stack to add unvisited neighbors.
     * @return number of stones added to the stack.
     */
    private int pushGroupNeighbors( GoBoardPosition s, boolean friendPlayer1, List stack )
    {
        return pushGroupNeighbors( s, friendPlayer1, stack, true );
    }

    /**
     * Check all 20 neighbors (including diagonals, 1-space jumps, and knights moves).
     * Make sure diagonals and 1-space jumps are not cut.
     * Don't push a group neighbor if it is part of a string which is in atari
     *
     * @param s the position of a stone of which to check the neighbors of.
     * @param friendPlayer1 side to find group stones for.
     * @param stack the stack on which we add unvisited neighbors.
     * @return number of stones added to the stack.
     */
    private int pushGroupNeighbors( GoBoardPosition s, boolean friendPlayer1, List stack, boolean samePlayerOnly )
    {
        // start with the nobi string nbrs
        int numPushed = pushStringNeighbors( s, friendPlayer1, stack, samePlayerOnly );

        // now push the non-nobi group neighbors
        if ( !samePlayerOnly )
            numPushed += pushEnemyDiagonalNeighbors( s, friendPlayer1, stack );

        // we only find pure group neighbors of the same color
        numPushed += pushPureGroupNeighbors( s, friendPlayer1, true, stack );

        return numPushed;
    }

    /**
     * determine a set of stones that are loosely connected to the specified stone.
     * This set of stones constitutes a group, but since stones cannot belong to more than
     * one group (or string) we must return a List.
     *
     * @param stone the stone to search from for group neighbors.
     * @return the list of stones in the group that was found.
     */
    public List findGroupFromInitialPosition( GoBoardPosition stone )
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
    public List findGroupFromInitialPosition( GoBoardPosition stone, boolean returnToUnvisitedState )
    {
        profiler_.start(GoProfiler.FIND_GROUPS);
        List stones = new ArrayList();
        // perform a breadth first search  until all found.
        // use the visited flag to indicate that a stone has been added to the group
        List stack = new LinkedList();
        stack.add( 0, stone );
        while ( !stack.isEmpty() ) {
            GoBoardPosition s = (GoBoardPosition) stack.remove( 0 );
            if ( !s.isVisited()) {
                s.setVisited( true );
                assert (s.getPiece().isOwnedByPlayer1()==stone.getPiece().isOwnedByPlayer1()):
                        s+" does not have same ownership as "+stone;
                stones.add( s );
                pushGroupNeighbors( s, s.getPiece().isOwnedByPlayer1(), stack );
            }
        }
        if ( returnToUnvisitedState ) {
            GoBoardUtil.unvisitPositions( stones );
            if (GameContext.getDebugMode() > 1)
                GoBoardUtil.confirmAllUnvisited(this);
        }
        profiler_.stop(GoProfiler.FIND_GROUPS);
        return stones;
    }

    /**
     * Check an immediately adjacent (nobi) nbr.
     *
     * @param r row
     * @param c column
     * @param rowOffset offset from row indicating position of ngbor to check
     * @param colOffset offset from column indicating position of ngbor to check
     * @param friendOwnedByPlayer1 need to specify this when the position checked, s, is empty and has undefined ownership.
     * @param stack if nbr fits criteria then add to stack
     * @param samePlayerOnly  mus the nbr be owned by the same player only
     * @param type one of REGULAR_PIECE, UNOCCUPIED, or NOT_FRIEND
     * @return  1 if this is a valid neighbor of the type that we want
     */
    private int checkNeighbor( int r, int c, int rowOffset, int colOffset,
                                    boolean friendOwnedByPlayer1, List stack, boolean samePlayerOnly, NeighborType type)
    {
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];

        switch (type) {
            case OCCUPIED:  // occupied black or white
                if ( !nbr.isVisited() && nbr.isOccupied() &&
                     (!samePlayerOnly || nbr.getPiece().isOwnedByPlayer1() == friendOwnedByPlayer1)) {
                    //if (samePlayerOnly && nbr.getPiece().isOwnedByPlayer1()!=s.getPiece().isOwnedByPlayer1())
                    //  System.out.println(nbr +" s="+s+" friendOwnedByPlayer="+friendOwnedByPlayer1 );
                    stack.add( 0, nbr );
                    return 1;
                }
                break;
           case UNOCCUPIED:  // empty space
                if ( !nbr.isVisited() && nbr.isUnoccupied() ) {
                    stack.add( 0, nbr );
                    return 1;
                }
                break;
           case NOT_FRIEND:   // blank or enemy
                if ( !nbr.isVisited() &&
                    ( nbr.isUnoccupied() ||
                       ( nbr.isOccupied() && (nbr.getPiece().isOwnedByPlayer1()!=friendOwnedByPlayer1))
                    ))  {
                    stack.add( 0, nbr );
                    return 1;
                }
                break;
           default : assert false: "unknown or unsupported neighbor type:"+type;
        }
        return 0;
    }


    /**
     * return 1 if this is a valid neighbor according to specification
     * these are the immediately adjacent (nobi) nbrs within the specified rectangular bounds
     */
    private int checkNeighbor( Location loc, int rowOffset, int colOffset,
                               boolean friendOwnedByPlayer1, List stack, boolean samePlayerOnly, NeighborType type,
                               Box bbox )
    {
        int r = loc.getRow();
        int c = loc.getCol();
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];
        if ( nbr.getRow() >= bbox.getMinRow() && nbr.getRow() <= bbox.getMaxRow()
          && nbr.getCol() >= bbox.getMinCol() && nbr.getCol() <= bbox.getMaxCol() ) {
            return checkNeighbor( r, c, rowOffset, colOffset, friendOwnedByPlayer1, stack, samePlayerOnly, type );
        }
        else
            return 0;
    }

    /**
     *  We allow these as long as the diagonal has not been fully cut
     *  i.e. not an opponent stone on both sides of the cut (or the diag stone is not in atari).
     *
     *  @param sameSideOnly if true then push nbrs on the same side, else push enemy nbrs
     */
    private int checkDiagonalNeighbor( int r, int c, int rowOffset, int colOffset,
                                       boolean friendPlayer1, boolean sameSideOnly, List stack )
    {
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];
        if (nbr.isUnoccupied()) {
            return 0;
        }
        // don't add it if it is in atari
        // but this leads to a problem in that ataried stones then don't belong to a group.
        if  (nbr.isInAtari(this)) {
            return 0;
        }
        // determine the side we are checking for (one or the other)
        boolean sideTest = sameSideOnly ? friendPlayer1 : !friendPlayer1;
        if ( (nbr.getPiece().isOwnedByPlayer1() == sideTest) && !nbr.isVisited()) {
            if (!((positions_[r + rowOffset][c].isOccupied() &&
                positions_[r + rowOffset][c].getPiece().isOwnedByPlayer1() != sideTest) &&
                (positions_[r][c + colOffset].isOccupied() &&
                positions_[r][c + colOffset].getPiece().isOwnedByPlayer1() != sideTest)) )  { // then not cut
                stack.add( 0, nbr );
                return 1;
            }
        }
        return 0;
    }

    // only add if not completely cut (there's no enemy stone in the middle).
    private int checkOneSpaceNeighbor( int r, int c, int rowOffset, int colOffset,
                                       boolean friendPlayer1, boolean samePlayerOnly, List stack )
    {
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];
        // don't add it if it is in atari
        if (nbr.isInAtari(this))
            return 0;
        if ( nbr.isOccupied() &&
                (!samePlayerOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited() ) {
            // we consider the link cut if there is an opponent piece between the 2 stones
            //     eg:          *|*
            boolean cut;
            if ( rowOffset == 0 ) {
                int col = c + (colOffset >> 1);
                cut =  (positions_[r][col].isOccupied() &&
                        (positions_[r][col].getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            else {
                int row = r + (rowOffset >> 1);
                cut =   (positions_[row][c].isOccupied() &&
                        (positions_[row][c].getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            if ( !cut ) {
                stack.add( 0, nbr );
                return 1;
            }
        }
        return 0;
    }

    /**
     * for the knight's move we consider it cut if there is an enemy stone at the base.
     */
    private int checkKogeimaNeighbor( int r, int c, int rowOffset, int colOffset,
                                      boolean friendPlayer1, boolean sameSideOnly, List stack )
    {
        if ( !inBounds( r + rowOffset, c + colOffset )) {
            return 0;
        }
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];
        // don't add it if it is in atari
        if (nbr.isInAtari(this)) {
            return 0;
        }

        if ( nbr.isOccupied() && (!sameSideOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited() ) {
            boolean cut;
            // consider it cut if there is an opponent stone in one of the 2 spaces between.
            if ( Math.abs( rowOffset ) == 2 ) {
                int rr = r + (rowOffset >> 1);
                cut = (positions_[rr][c].isOccupied()
                        && (positions_[rr][c].getPiece().isOwnedByPlayer1() != friendPlayer1)) ||
                        (positions_[rr][c + colOffset].isOccupied()
                        && (positions_[rr][c + colOffset].getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            else {
                int cc = c + (colOffset >> 1);
                cut = (positions_[r][cc].isOccupied()
                        && (positions_[r][cc].getPiece().isOwnedByPlayer1() != friendPlayer1)) ||
                        (positions_[r + rowOffset][cc].isOccupied()
                        && (positions_[r + rowOffset][cc].getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            if ( !cut ) {
                stack.add( 0, nbr );
                return 1;
            }
        }
        return 0;
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
                    space.getEye().getGroup().getEyes().remove(this);
                    space.getEye().clear();
                    space.setEye(null);
                }
            }
        }
    }

    /**
     * @return a number corresponding to the number of clumps of 4 or empty triangles that this stone is connected to.
     * returns 0 if does not form bad shape at all. Large numbers indicate worse shape.
     * Possible bad shapes are :
     *  SHAPE_EMPTY_TRIANGLE :  X -   ,   SHAPE_CLUMP_OF_4 :  X X
     *                          X X                           X X
     */
    public int formsBadShape(GoBoardPosition position)
    {
        GoStone stone = (GoStone)position.getPiece();
        int r = position.getRow();
        int c = position.getCol();

        int severity =
             checkBadShape(stone, r, c,  1,-1, 1) +
             checkBadShape(stone, r, c, -1,-1, 1) +
             checkBadShape(stone, r, c,  1, 1, 1) +
             checkBadShape(stone, r, c, -1, 1, 1) +

             checkBadShape(stone, r, c,  1,-1, 2) +
             checkBadShape(stone, r, c, -1,-1, 2) +
             checkBadShape(stone, r, c,  1, 1, 2) +
             checkBadShape(stone, r, c, -1, 1, 2) +

             checkBadShape(stone, r, c,  1,-1, 3) +
             checkBadShape(stone, r, c, -1,-1, 3) +
             checkBadShape(stone, r, c,  1, 1, 3) +
             checkBadShape(stone, r, c, -1, 1, 3);

        return severity;
    }

    private int checkBadShape(GoStone stone, int r, int c, int incr, int incc, int type) {
        boolean player1 = stone.isOwnedByPlayer1();
        if ( inBounds( r + incr, c + incc ) ) {
            BoardPosition adjacent1 = positions_[r+incr][c]; //getPosition( r + incr, c );
            BoardPosition adjacent2 = positions_[r][c+incc]; //getPosition( r , c + incc);
            BoardPosition diagonal = positions_[r+incr][c+incc]; //getPosition( r + incr, c + incc);
            // there are 3 cases:
            //       a1 diag    X     XX    X
            //        X a2      XX    X    XX
            switch (type) {
                case 1 :
                    if (adjacent1.isOccupied() && adjacent2.isOccupied())  {
                        if (   adjacent1.getPiece().isOwnedByPlayer1() == player1
                            && adjacent2.getPiece().isOwnedByPlayer1() == player1)
                            return GoBoardUtil.getBadShapeAux(diagonal, player1);
                    }  break;
                case 2 :
                    if (adjacent1.isOccupied() && diagonal.isOccupied())  {
                        if (   adjacent1.getPiece().isOwnedByPlayer1() == player1
                            && diagonal.getPiece().isOwnedByPlayer1() == player1)
                            return GoBoardUtil.getBadShapeAux(adjacent2, player1);
                    }  break;
                case 3 :
                    if (adjacent2.isOccupied() && diagonal.isOccupied())  {
                        if (   adjacent2.getPiece().isOwnedByPlayer1() == player1
                            && diagonal.getPiece().isOwnedByPlayer1() == player1)
                            return GoBoardUtil.getBadShapeAux(adjacent1, player1);
                    }  break;
               default : assert false;

            }
        }
        return 0;
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


    public List getHandicapPositions() {
        return handicap_.getStarPoints();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer((getNumRows()+2) * (getNumCols()+2));

        buf.append("   ");
        for ( int j = 1; j <= getNumCols(); j++ ) {
            buf.append(j % 10);
        }
        buf.append(' ');
        buf.append("\n  ");
        for ( int j = 1; j <= getNumCols()+2; j++ ) {
            buf.append('-');
        }
        buf.append('\n');

        for ( int i = 1; i <= getNumRows(); i++ ) {
            buf.append(i / 10);
            buf.append(i % 10);
            buf.append('|');
            for ( int j = 1; j <= getNumCols(); j++ ) {
                GoBoardPosition space = (GoBoardPosition) positions_[i][j];
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
     *
     * @author Barry Becker
     * Date: Aug 27, 2005
     */
    private static class HandicapStones {


        private static final float HANDICAP_STONE_HEALTH = 0.8f;

        // the number of initial handicap stones to use
        private int numHandicapStones_ = 0;

        // typically there are at most 9 handicap stones in an uneven game
        private List starPoints_ = null;


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
            List handicapMoves = new ArrayList(numHandicapStones_);

            for ( int i = 0; i < numHandicapStones_; i++ ) {
                GoBoardPosition hpos = (GoBoardPosition) starPoints_.get( i );

                GoMove m = GoMove.createGoMove( hpos.getRow(), hpos.getCol(), 0, (GoStone)hpos.getPiece());
                                              new GoStone(hpos.getPiece().isOwnedByPlayer1(), GamePiece.REGULAR_PIECE );
                //board.makeMove( m );
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
            starPoints_ = new ArrayList(9);
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
