package com.becker.game.twoplayer.go;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.common.Assert;
import com.becker.ui.Profiler;

import java.util.*;

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
    private Set groups_ = null;
    // this is a set of active armies. Armies are composed of groups.
    // @@ armies not implemented yet.
    private Set armies_ = null;

    // The difference between the 2 player's territory.
    // It is computed as  black-white = sum(health of stone i)
    private float territoryDelta_ = 0;

    // this is an auxilliary structure to help determine candidate moves
    private boolean[][] candidateMoves_ = null;

    // These give all the positions for a cut knights move
    // check every pair of 2 in the sequence to check if the stone at 0,0 is a cutting stone.
    private static final int[] kogeimaCutR_ = {1, -1, 0, 1, -1, 1, 0, -1, 1};
    private static final int[] kogeimaCutC_ = {0, 1, -1, 1, 0, -1, 1, -1, 0};

    // the number of initial handicap stones to use
    private int numHandicapStones_ = 0;
    // typically there are at most 9 handicap stones in an uneven game
    private List starPoints_ = null;

    private static final int CANDIDATE_MOVE_OFFSET = 1;

    // a global profiler for recording timing stats
    private static Profiler profiler_;
    private static final String UNDO_MOVE = "undoing move";
    private static final String UPDATE_STRINGS_AFTER_REMOVE = "updating strings after remove";
    private static final String UPDATE_GROUPS_AFTER_REMOVE = "updating groups after remove";
    private static final String MAKE_MOVE = "making move";
    private static final String UPDATE_STRINGS_AFTER_MOVE = "updating strings after move";
    private static final String UPDATE_GROUPS_AFTER_MOVE = "updating groups after move";
    private static final String GET_NBR_GROUPS = "getting nbr groups";
    private static final String UPDATE_TERRITORY = "updating territory";
    private static final String ABSOLUTE_TERRITORY = "absolute territory";
    private static final String RELATIVE_TERRITORY = "relative territory";
    private static final String UPDATE_EMPTY = "updating empty regions";
    private static final String CHECK_FOR_CUTS = "checking for cuts";
    private static final String GET_GROUP_NBRS = "getting group nbrs";
    private static final String FIND_GROUPS = "finding groups";
    private static final String FIND_STRINGS = "finding strings";

    static {
        profiler_ = new Profiler();
        profiler_.add(UNDO_MOVE);
          profiler_.add(UPDATE_STRINGS_AFTER_REMOVE, UNDO_MOVE);
          profiler_.add(UPDATE_GROUPS_AFTER_REMOVE, UNDO_MOVE);
        profiler_.add(MAKE_MOVE);
          profiler_.add(UPDATE_STRINGS_AFTER_MOVE, MAKE_MOVE);
          profiler_.add(UPDATE_GROUPS_AFTER_MOVE, MAKE_MOVE);
            profiler_.add(GET_NBR_GROUPS, UPDATE_GROUPS_AFTER_MOVE);
            profiler_.add(CHECK_FOR_CUTS, UPDATE_GROUPS_AFTER_MOVE);
            profiler_.add(UPDATE_TERRITORY, UPDATE_GROUPS_AFTER_MOVE);
              profiler_.add(ABSOLUTE_TERRITORY, UPDATE_TERRITORY);
                profiler_.add(GoGroup.UPDATE_EYES, ABSOLUTE_TERRITORY);
              profiler_.add(RELATIVE_TERRITORY, UPDATE_TERRITORY);
                profiler_.add(GoGroup.GET_ENEMY_GROUPS_NBRS, RELATIVE_TERRITORY);
              profiler_.add(UPDATE_EMPTY, UPDATE_TERRITORY);
        profiler_.add(GET_GROUP_NBRS);
        profiler_.add(FIND_GROUPS);
        profiler_.add(FIND_STRINGS);
    }


    /**
     *  constructor.
     *  @param numRows num rows
     *  @param numCols num cols
     *  @param numHandicapStones number of black handicap stones to initialize with.
     */
    public GoBoard( int numRows, int numCols, int numHandicapStones )
    {
        // need to synchronize this to avoid concurrent modification error during search.
        groups_ = Collections.synchronizedSet(new HashSet());
        armies_ = new HashSet();
        setSize( numRows, numCols );
        setHandicap( numHandicapStones );
    }

    /**
     * start over from the beggining and reinitialize everything.
     */
    public final void reset()
    {
        groups_.clear();
        armies_.clear();
        for ( int i = 1; i <= getNumRows(); i++ )  {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new GoBoardPosition(i,j, null, null);
            }
        }
        // first time through we need to initialize the starpoint positions
        initStarPoints();
        setHandicap( numHandicapStones_ );
        initCandidateMoves();
    }

    /**
      * @return a deep copy of the board.
      */
     public Object clone() throws CloneNotSupportedException
     {
        Object clone = super.clone();

        // make copies of all the groups and armies
        if (groups_!=null) {
            ((GoBoard)clone).groups_ = new HashSet();
            Set groupsCopy = ((GoBoard)clone).groups_;
            Iterator it = groups_.iterator();
            while (it.hasNext()) {
                GoGroup g = (GoGroup)it.next();
                groupsCopy.add(g.clone());
            }
        }

        if (armies_!=null)  {
            ((GoBoard)clone).armies_ = new HashSet();
            Set armiesCopy = ((GoBoard)clone).armies_;
            Iterator it = armies_.iterator();
            while (it.hasNext()) {
                GoArmy a = (GoArmy)it.next();
                armiesCopy.add(a.clone());
            }
        }
        return clone;
     }


    /**
     * set the dimensions of the game board (must be square).
     * must call reset() after changing the size.
     * @param numRows number of rows
     * @param numCols number of columns
     */
    public final void setSize( int numRows, int numCols )
    {
        if ( numRows != numCols )
            GameContext.log(0,  "The board must be square and have an odd edge length" );
        if ( numRows % 2 == 0 ) numRows++;
        if ( numCols % 2 == 0 ) numCols++;
        if ( numRows > numCols )
            numCols = numRows;
        numRows_ = numRows;
        numCols_ = numCols;
        rowsTimesCols_ = numRows_ * numCols_;
        // we don't use the 0 edges of the board
        positions_ = new BoardPosition[numRows_ + 1][numCols_ + 1];
        candidateMoves_ = new boolean[numRows_ + 1][numCols_ + 1];
        reset();
    }

    /**
     * specify the number of handicap stones that will actually be used this game.
     * public since we might set if from the options dialog
     * @param numHandicapStones the number of handicap stones to start with
     */
    public final void setHandicap( int numHandicapStones )
    {
        assert ( numHandicapStones <= starPoints_.size() );

        numHandicapStones_ = numHandicapStones;
        for ( int i = 0; i < numHandicapStones_; i++ ) {
            GoBoardPosition hpos = (GoBoardPosition) starPoints_.get( i );
            GameContext.log( 3, "adding handicap stone:" + hpos );

            GoMove m = GoMove.createMove( hpos.getRow(), hpos.getCol(), null, 0, i, (GoStone)hpos.getPiece());
                                          new GoStone(hpos.getPiece().isOwnedByPlayer1(), GamePiece.REGULAR_PIECE );
            this.makeMove( m );
        }
    }

    /**
     * get the number of handicap stones used in this game.
     * @return number of handicap stones
     */
    public final int getHandicap()
    {
        return numHandicapStones_;
    }

    /**
     * initialize a list of stones at the star points
     */
    private void initStarPoints()
    {
        // initialize the list of handicap stones.
        // The number of these that actually get placed on the board
        // depends on the handicap
        starPoints_ = new ArrayList();
        int nRows = getNumRows();
        int min = 4;
        // on a really small board we put the corner star points at 3-3.
        if (nRows<13)
            min = 3;
        int max = getNumRows() - (min-1);
        int mid = getNumRows() / 2 + 1;

        // add the star points
        starPoints_.add( new GoBoardPosition( min, min, null, new GoStone(true, 0.8f)) );
        starPoints_.add( new GoBoardPosition( max, max, null, new GoStone(true, 0.8f)) );
        starPoints_.add( new GoBoardPosition( min, max, null, new GoStone(true, 0.8f)) );
        starPoints_.add( new GoBoardPosition( max, min, null, new GoStone(true, 0.8f)) );
        starPoints_.add( new GoBoardPosition( min, mid, null, new GoStone(true, 0.8f)) );
        starPoints_.add( new GoBoardPosition( max, mid, null, new GoStone(true, 0.8f)) );
        starPoints_.add( new GoBoardPosition( mid, min, null, new GoStone(true, 0.8f)) );
        starPoints_.add( new GoBoardPosition( mid, max, null, new GoStone(true, 0.8f)) );
        starPoints_.add( new GoBoardPosition( mid, mid, null, new GoStone(true, 0.8f)) );
    }

    public List getStarPointPositions()
    {
        return starPoints_;
    }

    /**
     * we start with a default list of good starting moves, and
     * add to it all moves within 2 spaces of those that are played.
     */
    private void initCandidateMoves()
    {
        int numRows = getNumRows();
        int numCols = getNumCols();
        int i,j;

        // this will fill a 2 stone wide strip on the 3rd and 4rth lines of the board.
        // this includes the star points and many others as candidates to consider
        for ( i = 3; i <= numRows - 2; i++ ) {
            candidateMoves_[i][3] = true;
            candidateMoves_[i][4] = true;
            candidateMoves_[i][numCols - 2] = true;
            candidateMoves_[i][numCols - 3] = true;
        }
        for ( j = 5; j <= numCols - 4; j++ ) {
            candidateMoves_[3][j] = true;
            candidateMoves_[4][j] = true;
            candidateMoves_[numRows - 2][j] = true;
            candidateMoves_[numRows - 3][j] = true;
        }
        // also make the center space a candidate move
        candidateMoves_[(numRows + 1) / 2][(numCols + 1) / 2] = true;
    }

    /**
     * this method splats a footprint of trues around the current moves.
     * later we look for empty spots that are true for candidate moves
     */
    public final void determineCandidateMoves()
    {
        //  set the footprints
        int i,j;
        for ( i = 1; i <= getNumRows(); i++ )
            for ( j = 1; j <= getNumCols(); j++ )
                if ( !positions_[i][j].isUnoccupied() )
                    addCandidateMoves( positions_[i][j] );
    }

    /**
     * this method splats a footprint of trues around the specified move.
     * @param stone
     */
    private final void addCandidateMoves( BoardPosition stone )
    {
        int i,j;
        boolean[][] b = candidateMoves_;

        int startrow = Math.max( stone.getRow() - CANDIDATE_MOVE_OFFSET, 1 );
        int stoprow = Math.min( stone.getRow() + CANDIDATE_MOVE_OFFSET, getNumRows() );
        int startcol = Math.max( stone.getCol() - CANDIDATE_MOVE_OFFSET, 1 );
        int stopcol = Math.min( stone.getCol() + CANDIDATE_MOVE_OFFSET, getNumCols() );
        // set the footprint
        for ( i = startrow; i <= stoprow; i++ )
            for ( j = startcol; j <= stopcol; j++ )
                if ( positions_[i][j].isUnoccupied() ) {
                    b[i][j] = true;
                }
    }

    /**
     * In theory all empties should be considered, but in practice we keep
     * a shorter list of reasonable moves lest things get intractable.
     *
     * @return true if this position is a reasonable next move
     */
    public final boolean isCandidateMove( int row, int col )
    {
        if ( candidateMoves_[row][col] && positions_[row][col].isUnoccupied() ) {
            return true;
        }
        else
            return false;
    }

    /**
     *in go there is not really a theoretical limit to the number of moves,
     * but practically if we exceed this then we award the game to whoever is ahead.
     * @return the maximum number of moves ever expected for this game.
     */
    public final int getMaxNumMoves()
    {
        return 3 * rowsTimesCols_;
    }

    /**
     * get the current set of active groups
     * @return all the valid groups on the board (for both sides)
     */
    public final Set getGroups()
    {
        return groups_;
    }

    /**
     * record times for these operations so we get an accurate picture of where the bottlenecks are.
     */
    public final void initializeGobalProfilingStats()
    {
        profiler_.resetAll();
        profiler_.setEnabled(GameContext.isProfiling());
        profiler_.setLogger(GameContext.getLogger());
    }

    /**
     * show useful performance profiling statistics so we know where the bottleknecks are.
     * @param totalTime the total time taken to compute the next move
     */
    public final void showProfileStats( long totalTime )
    {
        profiler_.print();
    }

    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move, and updates groups,
     * removes captures, and counts territory.
     *
     * @return false if the move is suicidal
     */
    public final boolean makeMove( Move move )
    {
        profiler_.start(MAKE_MOVE);

        GoMove m = (GoMove)move;
        // if its a passing move, there is nothing to do
        if ( m.isPassingMove() ) {
            GameContext.log( 2, "making passing move" );
            return true;
        }
        // first make sure that there are no references to obsolete groups.
        clearEyes();    // I think this is important

        super.makeMove( m );

        GoBoardPosition stone = (GoBoardPosition) (positions_[m.getToRow()][m.getToCol()]);

        m.captureList = findCaptures( stone );

        updateStringsAfterMoving( stone );


        if ( m.captureList != null ) {
            removeCaptures( m.captureList );
            updateAfterRemovingCaptures( stone );
            GameContext.log( 2, "GoBoard: makeMove: " + m + "  groups after removing captures" );
            debugPrintGroups( 2 );
        }

        updateGroupsAfterMoving( stone, move.moveNumber );

        if ( isSuicidal( m ) )
            return false;

        if ( GameContext.getDebugMode() > 1 ) {
            GoString string = stone.getString();
            assert ( string.getLiberties( this ).size() > 0): "ERROR: string owned by placed stone has no liberties: " + string;
            assert ( string.size() > 0): "stone has bad string: " + stone;
        }
        profiler_.stop(MAKE_MOVE);

        return true;
    }


    /**
     * for Go, undoing a move means changing that space back to a blank, restoring captures, and updating groups.
     * @param move  the move to undo.
     */
    public final void undoMove( Move move )
    {
        profiler_.start(UNDO_MOVE);

        GoMove m = (GoMove) move;

        // there is nothing to do if it is a pass
        if ( m.isPassingMove() ) return;

        GoBoardPosition stone = (GoBoardPosition) positions_[m.getToRow()][m.getToCol()];
        GoString stringThatItBelongedTo = stone.getString();
        clear( stone );   // clearing a stone may cause a string to split into smaller strings

        // first make sure that there are no references to obsolete groups.
        clearEyes();

        updateStringsAfterRemoving( stone, stringThatItBelongedTo );
        if ( m.captureList != null ) {
            restoreCaptures( m.captureList );
            updateAfterRestoringCaptures( m.captureList );
            GameContext.log( 3, "GoBoard: undoMove: " + move + "  groups after restoring captures:" );
            debugPrintGroups( 3 );
        }
        updateGroupsAfterRemoving( stone, stringThatItBelongedTo );
        profiler_.stop(UNDO_MOVE);
    }


    /**
     * Determine a list of enemy stones that are captured when this stone is played on the board.
     * In other words determine all opponent strings (at most 4) whose last liberty is at the new stone location.
     */
    private CaptureList findCaptures( GoBoardPosition stone )
    {
        assert ( stone!=null );
        HashSet nbrs = getNobiNeighbors( stone, NeighborType.ENEMY );
        CaptureList captureList = null;
        Iterator it = nbrs.iterator();
        // keep track of the strings captured so we don't capture the same one twice
        HashSet capturedStrings = new HashSet();

        while ( it.hasNext() ) {
            GoBoardPosition enbr = (GoBoardPosition) it.next();
            assert (enbr.isOccupied()): "enbr="+enbr;

            GoString str = enbr.getString();
            assert ( str.isOwnedByPlayer1() != stone.getPiece().isOwnedByPlayer1()): "The "+str+" is not an enemy of "+stone;
            if ( str.getLiberties( this ).size() == 0 && str.size() > 0 ) {
                capturedStrings.add( str );
                // we need to add copies so that when the original stones on the board are
                // changed we don't change the captures
                if ( captureList == null )
                    captureList = new CaptureList();
                captureList.addAllCopied( str.getMembers() );
            }
        }
        return captureList;
    }

    /**
     * make the positions on the board represented be the captureList show up empty.
     * @param captureList list of stones to remove
     */
    public final void removeCaptures( CaptureList captureList )
    {
        if ( captureList == null )
            return;

        // remove the captured strings from the owning group (there could be up to 4)
        GoString capString = ((GoBoardPosition) captureList.get( 0 )).getString();
        GoGroup group = capString.getGroup();
        HashSet capStrings = new HashSet();

        Iterator it = captureList.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition capStone = (GoBoardPosition) it.next();
            capString = capStone.getString();
            // remove the captured strings from the group
            if ( !capStrings.contains( capString ) ) {
                capStrings.add( capString );
                group.remove( capString );
            }
            GoBoardPosition stoneOnBoard = (GoBoardPosition) getPosition( capStone.getRow(), capStone.getCol() );
            //stoneOnBoard.getString().remove(stoneOnBoard);
            stoneOnBoard.setString(null);
            clear( stoneOnBoard );
            // ?? restore disconnected groups?
        }

        // if there are no more stones in the group, remove it.
        if ( group.getNumStones() == 0 ) {
            getGroups().remove( group );
        }

        //GameContext.log( 2, "removed these captures (num stones remaining in group="
        //        + group.getNumStones() + ") : " + captureList );
        //group.confirmNoNullMembers();
    }

    /**
     * put the captures back on the board.
     */
    public final void restoreCaptures( CaptureList captureList )
    {
        if ( captureList == null )
            return;
        captureList.restoreOnBoard( this );
        //GameContext.log( 2, "GoMove: restoring these captures: " + captureList );
        Iterator it = captureList.iterator();
        List restoredList = new LinkedList();
        while ( it.hasNext() ) {
            GoBoardPosition capStone = (GoBoardPosition) it.next();
            GoBoardPosition stoneOnBoard = (GoBoardPosition) getPosition( capStone.getRow(), capStone.getCol() );
            stoneOnBoard.setVisited(false);    // make sure in virgin unvisited state
            restoredList.add( stoneOnBoard );
        }

        // there may have been more than one string in the capturelist
        LinkedList strings = new LinkedList();
        it = restoredList.iterator();

        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            if ( !s.isVisited() ) {
                List string1 = findStringFromInitialPosition( s, false );
                strings.add( string1 );
            }
        }

        // GoString restoredString = new GoString(restoredList, b); // old way
        // ?? form new group or check group nbrs to see if we can add to an existing one
        boolean firstString = true;
        it = strings.iterator();
        GoGroup group = null;
        while ( it.hasNext() ) {
            List stringList = (List) it.next();
            GoString string = new GoString( stringList );
            if ( firstString ) {
                group = new GoGroup( string );
                firstString = false;
            }
            else {
                group.addMember( string, this );
                GameContext.log( 2, "GoMove: restoring ----------------" + string );
            }
            string.unvisit();
        }

        assert ( group!=null): "no group was formed when restoring "+restoredList+" the list of strings was "+strings;
        getGroups().add( group );
    }


    /**
     * Examine the neighbors of this added stone and determine how the strings have changed.
     * For strings: examine the strongly connected neighbors. If more than one string borders then
     * we merge the strings. If only one borders, then we add this stone to that string. If no strings
     * touch the added stone, then we create a new string containing only this stone.
     * @param stone the stone that was just placed on the board.
     */
    private void updateStringsAfterMoving( GoBoardPosition stone )
    {
        profiler_.start(UPDATE_STRINGS_AFTER_MOVE);
        HashSet nbrs = getNobiNeighbors( stone, NeighborType.FRIEND );

        GoString str = null;
        if ( nbrs.size() == 0 ) {
            // there are no strongly connected nbrs, create a new string
            str = new GoString( stone );
        }
        else {
            Iterator nbrIt = nbrs.iterator();
            GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
            str = nbrStone.getString();
            str.addMember( stone );
            GameContext.log( 3, "groups before merging:" );
            debugPrintGroups( 3 );

            if ( nbrs.size() > 1 ) {
                // then we probably need to merge the strings
                // we would not, for example, if we are completing a clump of four
                while ( nbrIt.hasNext() ) {
                    // if its the same string then there is nothing to merge
                    nbrStone = (GoBoardPosition) nbrIt.next();
                    GoString nbrString = nbrStone.getString();
                    if ( str != nbrString )
                        str.merge( nbrString );
                }
            }
        }
        //cleanupGroups();
        profiler_.stop(UPDATE_STRINGS_AFTER_MOVE);
    }

    /**
     * Examine the neighbors of this added stone and determine how the groups have changed.
     * For groups: examine neighbors. If more than one group borders then
     * we merge the groups. If only one borders, then we add this stone to that group. If no groups
     * touch the added stone, then we create a new group containing only this stone.
     */
    private void updateGroupsAfterMoving( GoBoardPosition pos, int moveNum )
    {
        profiler_.start(UPDATE_GROUPS_AFTER_MOVE);
        GoGroup group = null;
        GoString str = pos.getString();

        // check diagonals, ikken tobi, knights moves (unless they are completely cut) for groups
        profiler_.start(GET_NBR_GROUPS);
        HashSet nbrGroups = getNeighborGroups( pos, true );
        profiler_.stop(GET_NBR_GROUPS);

        if ( nbrGroups.size() == 0 ) {

            // there are no adjacent groups, we need to create a new one.
            // we can use str because if there are no adjacent groups, we are
            // guaranteed that there were no adjacent strings either
            if ( str.getGroup() == null ) {
                group = new GoGroup( str );
                groups_.add( group );
                /*
                if ( GameContext.getDebugMode() > 1 ) {
                    assert ( group == str.getGroup() );
                    confirmNoEmptyStrings();
                    confirmStonesInValidGroups();
                    confirmStonesInOneGroup( group );
                }*/
            }
        }
        else {
            // merge together the groups which are joined by this stone
            group = str.getGroup();
            Iterator nbrIt = nbrGroups.iterator();
            if ( group == null ) {
                // then this stone is a loner. add it to the 1st group
                group = (GoGroup) nbrIt.next();
                GoGroup oldGroup = str.getGroup();
                /*
                if ( GameContext.getDebugMode() > 1 ) {
                    assert (group.isOwnedByPlayer1()==pos.getPiece().isOwnedByPlayer1()):
                            group+" must have same ownership as "+pos;
                    assert (group.isOwnedByPlayer1()==str.isOwnedByPlayer1()): group+" must have same ownership as "+str;
                }    */
                group.addMember( str, this );
                /*
                if ( GameContext.getDebugMode() > 1 ) {
                    assert ( oldGroup==null): "oldGroup must be null if it got here";
                    assert ( str.getGroup() == group );
                    confirmNoEmptyStrings();
                    confirmStonesInValidGroups();
                    confirmStonesInOneGroup( group );
                }   */
            }
            group = str.getGroup();  // it might have changed since above
            while ( nbrIt.hasNext() ) {
                GoGroup g = (GoGroup) nbrIt.next();
                if ( g != group ) {
                    group.merge( g, this );
                    groups_.remove( g );
                }
            }
        }
        /*
        if ( GameContext.getDebugMode() > 1 ) {
            confirmNoEmptyStrings();
            confirmStonesInValidGroups();
            confirmAllUnvisited();
        } */

        // also check groups of the opponent which have been cut by this move
        // check the 20 ways that we could have cut an existing opponent group.

        profiler_.start(CHECK_FOR_CUTS);
        // cut diagonals
        checkForDiagonalCut( pos, -1, -1 );
        checkForDiagonalCut( pos, 1, -1 );
        checkForDiagonalCut( pos, -1, 1 );
        checkForDiagonalCut( pos, 1, 1 );

        // cut 1 space jumps (ikken tobi)
        checkForIkkenTobiCut( pos, 0, 1 );
        checkForIkkenTobiCut( pos, 1, 0 );

        // cut knights moves (kogeima)
        checkForKogeimaCuts( pos );

        // @@ we also treat artari's like cuts.
        // if a neighboring oponent string was put in atari by this move,
        // then it needs to be split off into its own group.
        checkForAtariCuts( pos );

        profiler_.stop(CHECK_FOR_CUTS);

        cleanupGroups();

        // this gets used when calculating the worth of the board
        territoryDelta_ = updateTerritory(pos, moveNum);

        //GameContext.log( 3, "The territory Delta after placing " + pos + " = " + territoryDelta_ );
        profiler_.stop(UPDATE_GROUPS_AFTER_MOVE);

    }

    public float getTerritoryDelta()
    {
        return territoryDelta_;
    }

    /**
     * loops through the groups and armies to determine the territorial
     * difference between the players.
     * Then loop through and determine a score for positions that are not part of groups.
     * If a position is part of an area that borders only a living group, then it is considered
     * territory for that group's side. If, however, the position borders living groups from
     * both sides, then the score is weighted according to what proportion of the perimeter
     * borders each living group.
     * This is the primary factor in evaluating the board position for purposes of search.
     *
     * @return the estimated difference in territory
     */
    private float updateTerritory(GoBoardPosition lastMove, int moveNum)
    {
        profiler_.start(UPDATE_TERRITORY);
        float delta = 0;
        Iterator it = groups_.iterator();
        // first calculate the absolute health of the groups so that measure can
        // be used in the more accurate relative health computation.

        while ( it.hasNext() ) {
            profiler_.start(ABSOLUTE_TERRITORY);
            GoGroup g = (GoGroup) it.next();

            float health = g.calculateAbsoluteHealth( this, profiler_ );

            if (!GoController.USE_RELATIVE_GROUP_SCORING)  {
                g.updateTerritory( health );
                delta += health * g.getNumStones();
            }
            profiler_.stop(ABSOLUTE_TERRITORY);
        }
        if (GoController.USE_RELATIVE_GROUP_SCORING)  {
            profiler_.start(RELATIVE_TERRITORY);
            it = groups_.iterator();
            while ( it.hasNext() ) {
                GoGroup g = (GoGroup) it.next();
                float health = g.calculateRelativeHealth( this, lastMove, profiler_ );
                g.updateTerritory( health );
                delta += health * g.getNumStones();
            }
            profiler_.stop(RELATIVE_TERRITORY);
        }
        // need to loop over the board and determine for each space if it is territory for the specified player.
        // We will first mark visited all the stones that are "controlled" by the specified player.
        // The unoccupied "controlled" positions will be territory.
        profiler_.start(UPDATE_EMPTY);
        delta += updateEmptyRegions(moveNum);
        profiler_.stop(UPDATE_EMPTY);

        profiler_.stop(UPDATE_TERRITORY);
        return delta;
    }


    /**
     * assign scores to empty positions that are not eyes in groups.
     *
     * @return the estimated difference in territory. A positive value means black is ahead.
     */
    private float updateEmptyRegions(int moveNum)
    {
        float diffScore = 0;
        //only do this when the midgame starts, since early on there is alwas only one connected empty region.
        int edgeOffset = 1;
        if (moveNum <= 2.0*this.getNumRows())
            return diffScore;
        if (moveNum > rowsTimesCols_/3)
            edgeOffset = 0;
        int min = 1+edgeOffset;
        int rMax = getNumRows()-edgeOffset;
        int cMax = getNumCols()-edgeOffset;

        List emptyLists = new LinkedList();
        for ( int i = min; i <= rMax; i++ )  {
           for ( int j = min; j <= cMax; j++ ) {
               GoBoardPosition pos = (GoBoardPosition)getPosition(i,j);
               if (pos.getString()==null && pos.getEye()==null) {
                   assert pos.isUnoccupied();
                   if (!pos.isVisited()) {

                       // don't go all the way to the borders (until the end of the game),
                       // since otherwise we will likely get only one big empty region.
                       List empties = findStringFromInitialPosition(pos, false, false, NeighborType.UNOCCUPIED,
                                                                    min, rMax,  min, cMax);
                       emptyLists.add(empties);
                       Set nbrs = findOccupiedNeighbors(empties);
                       float avg = calcAverageScore(nbrs);
                       float score = avg * (Math.min(1.0f, (float)nbrs.size()/empties.size()));

                       assert (score<=1.0 && score >=-1.0): "score="+score+" avg="+avg;
                       Iterator it = empties.iterator();
                       while (it.hasNext()) {
                           GoBoardPosition p = (GoBoardPosition)it.next();

                           p.scoreContribution = score;
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
    private final Set findOccupiedNeighbors(List empties)
    {
        Iterator it = empties.iterator();
        Set allNbrs = new HashSet();
        while (it.hasNext()) {
            GoBoardPosition empty = (GoBoardPosition)it.next();
            HashSet nbrs = getNobiNeighbors(empty, false, NeighborType.OCCUPIED);
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
     * @param stone the stone from which to begin searching for the string
     */
    public final List findStringFromInitialPosition( GoBoardPosition stone )
    {
        return findStringFromInitialPosition( stone, true );
    }

    /**
     * determine a set of stones that are tightly connected to the specified stone.
     * @param stone he stone from which to begin searching for the string
     * @param returnToUnvisitedState if true then the stomes will all be marked unvisited when done searching
     */
    public final List findStringFromInitialPosition( GoBoardPosition stone, boolean returnToUnvisitedState )
    {
        return findStringFromInitialPosition(
                stone, stone.getPiece().isOwnedByPlayer1(), returnToUnvisitedState, NeighborType.OCCUPIED,
                1, numRows_, 1, numCols_ );
    }

    /**
     * determines a string connected from a seed stone within a specified bounding area
     * @return string from seed stone
     */
    public final List findStringFromInitialPosition( GoBoardPosition stone,  boolean friendOwnedByP1,
                                                     boolean returnToUnvisitedState, NeighborType type,
                                                     int rMin, int rMax, int cMin, int cMax )
    {
        profiler_.start(FIND_STRINGS);
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
                pushStringNeighbors( s, friendOwnedByP1, stack, true, type, rMin, rMax, cMin, cMax );
            }
        }
        if ( returnToUnvisitedState )
            GoBoardUtil.unvisitPositionsInList( stones );
        // GoBoardUtil.confirmNoDupes( stone, stones );
        profiler_.stop(FIND_STRINGS);

        return stones;
    }



    /**
     * @param stones actually the positions containing the stones.
     * @return the average scores of the stones in the list.
     */
    private float calcAverageScore(Set stones)
    {
        float totalScore = 0;
        Iterator it = stones.iterator();
        while (it.hasNext()) {
            GoBoardPosition p = (GoBoardPosition)it.next();
            if (GoController.USE_RELATIVE_GROUP_SCORING)
                totalScore += p.getString().getGroup().getRelativeHealth();
            else
                totalScore += p.getString().getGroup().getAbsoluteHealth();
        }
        return totalScore/stones.size();
    }


    /**
     * get an estimate of the territory for the specified player.
     * This estimate is computed by summing all spaces in eyes + dead opponent stones that are still on the board in eyes.
     * At the end of the game this + the number of pieces captured so far should give the true score.
     */
    public int getTerritoryEstimate( boolean forPlayer1 )
    {
        float territoryEstimate = 0;

        // we should be able to just sum all the position scores now.
        for ( int i = 1; i <= getNumRows(); i++ )  {
           for ( int j = 1; j <= getNumCols(); j++ ) {
               GoBoardPosition pos = (GoBoardPosition)getPosition(i,j);
               if (forPlayer1 && pos.scoreContribution > 0) //GoController.LIFE_THRESHOLD)
                   territoryEstimate += pos.scoreContribution;
               else if (!forPlayer1 && pos.scoreContribution < 0) //-GoController.LIFE_THRESHOLD)
                   territoryEstimate -= pos.scoreContribution;  // will be positive
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
    public final HashSet getNobiNeighbors( GoBoardPosition stone, NeighborType neighborType )
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
    public final HashSet getNobiNeighbors( GoBoardPosition stone, boolean friendOwnedByP1, NeighborType neighborType )
    {
        HashSet nbrs = new HashSet();
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

    public final void printNobiNeighborsOf(GoBoardPosition stone)
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
     * get the groups that are now adjacent to this stone (not including the group
     * to which the stone belongs (if any)
     *
     * @param stone find the groups that neighbor this stone
     * @param samePlayerOnly only include groups that are owned by the same player as this stone
     */
    private HashSet getNeighborGroups( GoBoardPosition stone, boolean samePlayerOnly )
    {
        HashSet nbrGroups = new HashSet();
        HashSet nbrStones = getGroupNeighbors( stone, stone.getPiece().isOwnedByPlayer1(), samePlayerOnly );
        GoGroup g = stone.getGroup(); // might be null
        Iterator nbrIt = nbrStones.iterator();
        while ( nbrIt.hasNext() ) {
            GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
            GoString string = nbrStone.getString();
            assert ( nbrStone!=null): "the neighboring group stone is null";
            assert (string!=null): string+" was null";
            if ( (string != null)
                  && (g != string.getGroup()))
            {
                nbrGroups.add( string.getGroup() );
            }
        }

        return nbrGroups;
    }

    /**
     * return a set of stones which are loosely connected.
     * Check the 16 purely group neighbors and 4 string neighbors
     *         ***
     *        **S**
     *        *SXS*
     *        **S**
     *         ***
     * @param stone (not necessarily occupied)
     * @param friendPlayer1 typically the isOwnedByPlayer1 value of stone unless it is blank.
     * @param samePlayerOnly if true then find group nbrs that are have same ownership as friend (Player1)
     */
    private HashSet getGroupNeighbors( GoBoardPosition stone, boolean friendPlayer1, boolean samePlayerOnly )
    {
        profiler_.start(GET_GROUP_NBRS);
        List stack = new LinkedList();

        pushGroupNeighbors( stone, friendPlayer1, stack, samePlayerOnly );
        HashSet nbrStones = new HashSet();
        nbrStones.addAll( stack );

        profiler_.stop(GET_GROUP_NBRS);
        return nbrStones;
    }

    // this version assumes that the stone is occupied.
    public final HashSet getGroupNeighbors( GoBoardPosition position, boolean samePlayerOnly )
    {
        assert (position.getPiece()!=null);
        return getGroupNeighbors( position, position.getPiece().isOwnedByPlayer1(), samePlayerOnly );
    }

    /**
     *  @return true if there was a diagonal cut of the opponents group
     */
    private final boolean checkForDiagonalCut( GoBoardPosition stone, int rowOffset, int colOffset )
    {
        int r = stone.getRow();
        int c = stone.getCol();
        boolean p1 = stone.getPiece().isOwnedByPlayer1();
        int rdiag = r + rowOffset;
        int cdiag = c + colOffset;
        if ( !inBounds( rdiag, cdiag ) )
            return false; // out of bounds so irrelevant
        GoBoardPosition diagSpace = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];
        boolean cut = false;
        if ( diagSpace.isOccupied() && diagSpace.getPiece().isOwnedByPlayer1() == p1 ) {
            GoBoardPosition cutStone1 = (GoBoardPosition) positions_[rdiag][c];
            GoBoardPosition cutStone2 = (GoBoardPosition) positions_[r][cdiag];
            if ( cutStone1.isOccupied() && cutStone2.isOccupied()
                    && (cutStone1.getPiece().isOwnedByPlayer1() != p1)
                    && (cutStone2.getPiece().isOwnedByPlayer1() != p1) ) {
                // then there is a cut
                makeCut( stone, cutStone1, cutStone2 );
                cut = true;
            }
        }
        return cut;
    }

    /**
     * Check for cuts of one-spaced (ikken tobi) group connections.
     * We confirm that there is not a circuitous route still
     * connecting the 2 stones in makeCut
     * @return true if there was a cut of the opponents group
     */
    private final boolean checkForIkkenTobiCut( GoBoardPosition stone, int rowOffset, int colOffset )
    {
        int r = stone.getRow();
        int c = stone.getCol();
        boolean p1 = stone.getPiece().isOwnedByPlayer1();
        boolean cut = false;
        GoBoardPosition cutStone1 = null;
        GoBoardPosition cutStone2 = null;
        if ( (colOffset == 0) && (r + rowOffset <= numRows_) && (r - rowOffset > 0) ) {
            cutStone1 = (GoBoardPosition) positions_[r + rowOffset][c];
            cutStone2 = (GoBoardPosition) positions_[r - rowOffset][c];
            if ( cutStone1.isOccupied() && cutStone2.isOccupied() )
                cut = true;
        }
        else if ( (rowOffset == 0) && (c + colOffset <= numCols_) && (c - colOffset > 0) ) {
            cutStone1 = (GoBoardPosition) positions_[r][c + colOffset];
            cutStone2 = (GoBoardPosition) positions_[r][c - colOffset];
            if ( cutStone1.isOccupied() && cutStone2.isOccupied() )
                cut = true;
        }

        if ( cut
                && (cutStone1.getPiece().isOwnedByPlayer1() != p1)
                && (cutStone2.getPiece().isOwnedByPlayer1() != p1)
                && (cutStone1.getGroup() == cutStone2.getGroup())  // confirm not cut already
        ) {
            if ( rowOffset == 1 && colOffset == -1 ) {
                GameContext.log( 0, "Checking for ikkenTobi " + stone );
                debugPrintGroups( 0 );
            }
            //System.out.println("\n IkkenTobiCut: cutStone1 = "+cutStone1+" group = "+cutStone1.getGroup());
            //System.out.println(" IkkenTobiCut: cutStone2 = "+cutStone2+" group = "+cutStone2.getGroup());
            return makeCut( stone, cutStone1, cutStone2 );
        }
        else
            return false;
    }

    /**
     * Check all the possible knight's moves which might have been cut by the specified stone.
     * There are 8. See also makeCut.
     * @return true if there was a cut of the opponents group
     */
    private final boolean checkForKogeimaCuts( GoBoardPosition stone )
    {
        int r = stone.getRow();
        int c = stone.getCol();

        boolean p1 = stone.getPiece().isOwnedByPlayer1();
        for ( int i = 0; i < 8; i++ ) {
            // if this and the next one are the same color then there is a cut
            int newRow1 = r + kogeimaCutR_[i];
            int newRow2 = r + kogeimaCutR_[i + 1];
            int newCol1 = c + kogeimaCutC_[i];
            int newCol2 = c + kogeimaCutC_[i + 1];
            if ( inBounds( newRow1, newCol1 ) && inBounds( newRow2, newCol2 ) ) {
                GoBoardPosition cutStone1 = (GoBoardPosition) positions_[newRow1][newCol1];
                GoBoardPosition cutStone2 = (GoBoardPosition) positions_[newRow2][newCol2];
                if ( cutStone1.isOccupied() && cutStone2.isOccupied()
                        && (cutStone1.getPiece().isOwnedByPlayer1() != p1)
                        && (cutStone2.getPiece().isOwnedByPlayer1() != p1) ) {
                    // and if only 2 of the 6 spaces defined by the 2 knight stones are !=p1's owner
                    int pct = 0;
                    for ( int j = Math.min( newRow1, newRow2 ); j <= Math.max( newRow1, newRow2 ); j++ )
                        for ( int k = Math.min( newCol1, newCol2 ); k <= Math.max( newCol1, newCol2 ); k++ )
                            if ( positions_[j][k].isOccupied() && positions_[j][k].getPiece().isOwnedByPlayer1() != p1 )
                                pct++;
                    // and if the 2 stones are already not split??
                    if ( pct >= 2 ) {
                        if (makeCut( stone, cutStone1, cutStone2 ))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param stone which is putting enemy group in atari
     * @return true if an atari cut
     */
    private final boolean checkForAtariCuts(GoBoardPosition stone )
    {
        System.out.println("start atri check");
        Set enemyNbrs = this.getNobiNeighbors(stone, NeighborType.ENEMY);
        // if any of the enymy nbr strings are put into atari by this stone, then
        // they should be split off from their owning group.
        Iterator it = enemyNbrs.iterator();
        while (it.hasNext()) {
            GoBoardPosition eStone = (GoBoardPosition)it.next();
            GoString str = eStone.getString();
            if (str.getLiberties(this).size()==1) {
                GoGroup cutGroup = str.getGroup();
                // find a member of cutGroup not in str.
                Iterator git = cutGroup.members_.iterator();
                // there are no cuts if only one string in group.
                if (cutGroup.getMembers().size()==1)
                    return false;
                boolean found = false;
                GoString differentString = null;
                while (git.hasNext() && !found) {
                    differentString = (GoString)git.next();
                    if (differentString != str)
                        found = true;
                }
                assert (found);
                GoBoardPosition cutStone2 = (GoBoardPosition)differentString.getMembers().iterator().next();
                makeCut(stone, eStone, cutStone2);
            }
        }
        System.out.println("done atari check");
        return false;
    }

    /**
     * Cut the enemy group into 2.
     * @param stone the cutting stone
     * @param cutStone1 one of the stones that is cut
     * @param cutStone2 the other stone that is cut
     * @return true if the cut was actually made
     */
    private boolean makeCut( GoBoardPosition stone, GoBoardPosition cutStone1, GoBoardPosition cutStone2 )
    {
        GoString str = cutStone1.getString();
        GoGroup wholeGroup = str.getGroup();

        if ( cutStone1.getString() == null || cutStone2.getString() == null )
            return false;
        if ( cutStone1.getGroup() != cutStone2.getGroup() ) {
            //String trace = GUIUtil.getStackTrace(new Exception());
            GameContext.log( 3, "Warning: makeCut: the 2 stones that are cut by " + stone + " should be from the same group" );
            GameContext.log( 3, "  MakeCut: The first(" + cutStone1 + ") \n" + cutStone1.getGroup() );
            GameContext.log( 3, "  MakeCut: The second(" + cutStone2 + ")  \n" + cutStone2.getGroup() );
            return false;
        }
        // now cut the group
        // determine the 2 groups by traversing the 2 cut stones. (there may be more than 2!)
        // If they still actually linked together via an independent link, then
        // we do not need to split the group.
        List splitStones = findGroupFromInitialPosition( cutStone1 );
        //GameContext.log(0, "cutting: splitstones: "+splitStones+ " off of "+wholeGroup);
        assert ( splitStones.size() <= wholeGroup.getNumStones()):
                "error: " + splitStones + " is greater in size than \n" + wholeGroup + "!";
        if ( splitStones.size() == wholeGroup.getNumStones() ) {
            // then the group was not actually split
            return false;
        }
        else {
            // split the group
            // we have to create a new group to represent the one split off
            //(but there may be more than 1 group split off!)
            GoGroup g = new GoGroup( splitStones );
            groups_.add( g );
            wholeGroup.remove( splitStones );
            //GameContext.log(2, "split groups\n 1:"+g+" 2:"+wholeGroup);
            if ( GameContext.getDebugMode() > 1 ) {
                confirmNoEmptyStrings();
                confirmStonesInOneGroup( g );
            }
            return true;
        }
    }

    /**
     * update the strings after a stone has been removed.
     * Some friendly strings may have been split by the removal.
     * @param stone that was removed.
     */
    private void updateStringsAfterRemoving( GoBoardPosition stone, GoString string )
    {
        profiler_.start(UPDATE_STRINGS_AFTER_REMOVE);

        // avoid error when calling from treeDlg
        if (string==null) return;
        //assert notNull(string, "null string after removing stone.");

        GoGroup group = string.getGroup();
        HashSet nbrs = getNobiNeighbors( stone, group.isOwnedByPlayer1(), NeighborType.FRIEND );
        if ( string.size() == 0 ) {
            GameContext.log( 3, "ERROR: string size = 0" );
            return;
        }
        // make new string(s) if removing the stone has caused a larger string to be split.
        if ( nbrs.size() > 1 ) {
            Iterator nbrIt = nbrs.iterator();
            List lists = new ArrayList();
            GoBoardPosition firstNbr = (GoBoardPosition) nbrIt.next();
            List stones = findStringFromInitialPosition( firstNbr, false );
            //GameContext.log( 3, firstNbr + " yields this string:" + stones );
            lists.add( stones );
            while ( nbrIt.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
                if ( !nbrStone.isVisited() ) {
                    List stones1 = findStringFromInitialPosition( nbrStone, false );
                    GoString newString = new GoString( stones1 );
                    group.addMember( newString, this );
                    //GameContext.log( 3, "mainString after removing (" + stone + ") =" + string );
                    //GameContext.log( 3, "subString =" + newString );
                    string.remove( stones1, this );
                    lists.add( stones1 );
                }
            }
            GoBoardUtil.unvisitPositionsInLists( lists );
        }
        //cleanupGroups();
        if ( GameContext.getDebugMode() > 2 ) {
            confirmAllUnvisited();
            confirmNoEmptyStrings();
            confirmStonesInValidGroups();
            confirmStonesInOneGroup( group );
        }
        profiler_.stop(UPDATE_STRINGS_AFTER_REMOVE);
    }

    /**
     * update the groups after a stone has been removed (and captures replaced perhaps).
     * Some friendly groups may have been split by the removal while
     * some enemy groups may need to be rejoined.
     * @param stone that was removed
     * @param string the string that the stone was removed from
     */
    private void updateGroupsAfterRemoving( GoBoardPosition stone, GoString string )
    {
        profiler_.start(UPDATE_GROUPS_AFTER_REMOVE);

        if ( string == null ) {
            if ( GameContext.getDebugMode() > 1 ) confirmStonesInValidGroups();
            return;
        }
        GoGroup group = string.getGroup();
        //GameContext.log( 3, "updateGroupsAfterRemoving: " + stone + " string=" + string + " original group=" + group );
        HashSet nbrs = getGroupNeighbors( stone, group.isOwnedByPlayer1(), false );

        // create a set of friendly group nbrs and a separate set of enemy ones.
        HashSet friendlyNbrs = new HashSet();
        HashSet enemyNbrs = new HashSet();
        Iterator nbrIt = nbrs.iterator();
        while ( nbrIt.hasNext() ) {
            GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
            if ( nbrStone.getPiece().isOwnedByPlayer1() == group.isOwnedByPlayer1() )
                friendlyNbrs.add( nbrStone );
            else
                enemyNbrs.add( nbrStone );
        }

        // check for friendly groups that have been split by the removal
        if ( friendlyNbrs.size() <= 1 ) {
            //no search needed, just remove this stone
            group.remove( stone, this );
        }
        else {
            Iterator friendIt = friendlyNbrs.iterator();
            GoBoardPosition firstStone = (GoBoardPosition) friendIt.next();
            List stones = findGroupFromInitialPosition( firstStone, false );
            //GameContext.log( 3, "result of find group from stone=" + stones );
            List lists = new ArrayList();
            lists.add( stones );

            while ( friendIt.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) friendIt.next();
                //System.out.println("checking next nbr="+nbrStone);
                if ( !nbrStone.isVisited() ) {
                    List stones1 = findGroupFromInitialPosition( nbrStone, false );
                    //GameContext.log( 3, "updateGroupsAfterRemoving: result of find group from stone1= " + stones1 );
                    if ( !groupAlreadyExists( stones1 ) ) {
                        GoGroup newGroup = new GoGroup( stones1 );
                        groups_.add( newGroup );
                        //GameContext.log( 3, nbrStone + "(" + ct + "): removing " + stones1 + " from " + group +
                        //        " to form own group (split off by removal of " + stone + ")" );
                        group.remove( stones1 );   // hitting error in group.remove
                        if ( GameContext.getDebugMode() > 1 )
                            confirmStonesInOneGroup( newGroup );
                    }
                    lists.add( stones1 );
                    if ( GameContext.getDebugMode() > 1 )
                        confirmStonesInValidGroups();
                }
            }
            GoBoardUtil.unvisitPositionsInLists( lists );
        }

        // now check for enemy groups that have been rejoined
        if ( enemyNbrs.size() > 1 ) {
            Iterator enemyIt = enemyNbrs.iterator();
            GoBoardPosition firstStone = (GoBoardPosition) enemyIt.next();
            GoGroup firstGroup = firstStone.getGroup();
            List mergedStones = findGroupFromInitialPosition( firstStone ); // the restored merged group
            //GameContext.log( 2, "The merged enemy group from ("+firstStone+") is " + mergedStones );
            // remove all the old groups and replace by one big one
            //GameContext.log( 3, "removing first group :" + firstGroup );
            groups_.remove( firstGroup );

            while ( enemyIt.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) enemyIt.next();
                // if the neighbor is part of the now merged group, remove its group
                if ( mergedStones.contains( nbrStone ) ) {
                    //GameContext.log( 3, "removing :" + nbrStone.getGroup() );
                    groups_.remove( nbrStone.getGroup() );
                }
            }
            GoGroup restoredGroup = null;
            if (mergedStones.size()>0)  {
                restoredGroup = new GoGroup( mergedStones );
                //GameContext.log( 3, "updateGroupsAfterRemoving: the restored group is :" + restoredGroup );
                if ( GameContext.getDebugMode() > 1 ) {
                    confirmNoEmptyStrings();
                }

                groups_.add( restoredGroup );
                if ( GameContext.getDebugMode() > 1 ) {
                    confirmNoEmptyStrings();
                    try {
                        confirmStonesInValidGroups();
                    } catch (RuntimeException rte) {
                        System.out.println( "Update after removing : " + stone );
                        System.out.println( " friendly nbrs = " + friendlyNbrs );
                        System.out.println( " enemy nbrs = " + enemyNbrs );
                        System.out.println( " firstStone = " + firstStone );
                        System.out.println( " the restored group is = " + restoredGroup );
                        throw rte;
                    }
                    confirmStonesInOneGroup( restoredGroup );
                }
            }
        }
        if ( GameContext.getDebugMode() > 1 ) confirmAllUnvisited();
        cleanupGroups();
        profiler_.stop(UPDATE_GROUPS_AFTER_REMOVE);
    }

    /**
     * After removing the captures the stones surrounding the captures will form 1 cohesive group rather than disparate ones.
     */
    private void updateAfterRemovingCaptures( GoBoardPosition finalStone )
    {
        if ( finalStone == null )
            return;

        List bigGroup = findGroupFromInitialPosition( finalStone );
        // @@ something below may be causing an empty position to be added to a group

        assert ( bigGroup.size() > 0 );
        Iterator allGroupsIt = groups_.iterator();

        boolean mergedGroup = false;
        // iterate through all the groups on the board
        while ( allGroupsIt.hasNext() ) {
            mergedGroup = false;
            GoGroup g = (GoGroup) allGroupsIt.next();
            Iterator stringIt = g.getMembers().iterator();
            GoString string = (GoString) stringIt.next();
            Iterator stoneIt = string.getMembers().iterator();
            // iterate through all the strings for every group
            if ( stoneIt.hasNext() ) {
                GoBoardPosition firstStone = (GoBoardPosition) stoneIt.next();
                if ( bigGroup.contains( firstStone ) ) {
                    mergedGroup = true;
                    // assert that all the stones in g are part of the big group
                    GoBoardPosition stone;
                    while ( stringIt.hasNext() ) {
                        string = (GoString) stringIt.next();
                        stoneIt = string.getMembers().iterator();
                        while ( stoneIt.hasNext() ) {
                            stone = (GoBoardPosition) stoneIt.next();
                            assert  bigGroup.contains( stone ):
                                    "we expected " +stone+ " to be part of " +bigGroup+ " because " +firstStone
                                    + " was. \nInstead " + stone + " belongs to " + stone.getGroup() ;
                        }
                    }
                }
            }
            //remove this group g from the set of all groups_.
            //this is the safe way to remove it.
            //We cannot say groups_.remove without gettting a concurrentModificationException
            if ( mergedGroup )
                allGroupsIt.remove();
        }
        // add the combined group after removing all the little ones.
        GoGroup newGroup = new GoGroup( bigGroup);
        //newGroup.confirmNoNullMembers();
        groups_.add( newGroup );
    }

    /**
     * After restoring the captures the stones surrounding the captures will probably
     * form disparate groups rather than 1 cohesive one.
     */
    private void updateAfterRestoringCaptures( CaptureList captures )
    {
        if ( captures == null )
            return;
        LinkedList nobiNbrs = new LinkedList();
        Iterator captureIt = captures.iterator();
        while ( captureIt.hasNext() ) {
            GoBoardPosition capture = (GoBoardPosition) captureIt.next();
            HashSet enns = getNobiNeighbors( capture, NeighborType.ENEMY );
            nobiNbrs.addAll( enns );
        }
        // in some bizarre cases there might actually be no enemy nobi nbrs
        // (such as when when restoring the captures kills all the other stones on the board?)
        if (nobiNbrs.size() == 0) {
            System.out.println( "The restored captures ("+captures+") have no enemy neighbors (very strange!)" );
            return;
        }
        GoBoardPosition firstEnemyStone = (GoBoardPosition) nobiNbrs.get( 0 );
        GoGroup bigEnemyGroup = firstEnemyStone.getGroup();
        //GameContext.log( 3, "updateAfterRestoringCaptures: The big enemy group :" + bigEnemyGroup );

        // assert that the bigEnemyGroup contains all the enemy nobi neighbors
        Iterator ennIt = nobiNbrs.iterator();
        if ( GameContext.getDebugMode() > 1 ) {
            while ( ennIt.hasNext() ) {
                GoBoardPosition enn = (GoBoardPosition) ennIt.next();
                assert ( bigEnemyGroup.containsStone( enn )): "enn " + enn + " not contained by " + bigEnemyGroup;
            }
        }
        // now replace the bigEnemyGroup by the potentially disparate smaller ones
        List listsToUnvisit = new ArrayList();
        List gStones = bigEnemyGroup.getStones();
        groups_.remove( bigEnemyGroup );
        ennIt = gStones.iterator();
        while ( ennIt.hasNext() ) {
            GoBoardPosition enn = (GoBoardPosition) ennIt.next();
            if ( !enn.isVisited() ) {
                List list = findGroupFromInitialPosition( enn, false );
                listsToUnvisit.add( list );
            }
        }

        Iterator it = listsToUnvisit.iterator();
        while ( it.hasNext() ) {
            List list = (List) it.next();
            GoBoardUtil.unvisitPositionsInList( list );
            GoGroup group = new GoGroup( list );
            //GameContext.log( 3, "updateAfterRestoringCaptures: adding sub group :" + group );
            groups_.add( group );
        }
    }


    /**
     * Check all nobi neighbors (at most 4).
     * @param s the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @return number of stones added to the stack
     */
    private int pushStringNeighbors( GoBoardPosition s, boolean friendPlayer1, List stack,
                                     boolean samePlayerOnly, NeighborType type,
                                     int rMin, int rMax, int cMin, int cMax )
    {
        int r = s.getRow();
        int c = s.getCol();
        int numPushed = 0;

        if ( r > 1 )
            numPushed += checkNeighbor( r, c, -1, 0, friendPlayer1, stack, samePlayerOnly, type, rMin, rMax, cMin, cMax );
        if ( c > 1 )
            numPushed += checkNeighbor( r, c, 0, -1, friendPlayer1, stack, samePlayerOnly, type, rMin, rMax, cMin, cMax );
        if ( r + 1 <= numRows_ )
            numPushed += checkNeighbor( r, c, 1, 0, friendPlayer1, stack, samePlayerOnly, type, rMin, rMax, cMin, cMax );
        if ( c + 1 <= numCols_ )
            numPushed += checkNeighbor( r, c, 0, 1, friendPlayer1, stack, samePlayerOnly, type, rMin, rMax, cMin, cMax );

        return numPushed;
    }

    private int pushStringNeighbors( GoBoardPosition s, boolean friendPlayer1, List stack, boolean samePlayerOnly )
    {
        return pushStringNeighbors( s, friendPlayer1, stack, samePlayerOnly,
                                    NeighborType.OCCUPIED, 1, numRows_, 1, numCols_ );
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
     * @param s the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @param sameSideOnly if true push pure group nbrs of the same side only.
     * @return number of stones added to the stack
     */
    private int pushPureGroupNeighbors( GoBoardPosition s, boolean friendPlayer1, boolean sameSideOnly, List stack )
    {
        int r = s.getRow();
        int c = s.getCol();
        int numPushed = 0;

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
     * Don't push a group neighbor if it is part of a string which is in ataryi
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
    private List findGroupFromInitialPosition( GoBoardPosition stone )
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
    private List findGroupFromInitialPosition( GoBoardPosition stone, boolean returnToUnvisitedState )
    {
        profiler_.start(FIND_GROUPS);
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
        //GameContext.log( 3, "findGroupFromInitialPosition = " + stones );
        if ( returnToUnvisitedState ) {
            GoBoardUtil.unvisitPositionsInList( stones );
        }
        profiler_.stop(FIND_GROUPS);
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
    private final int checkNeighbor( int r, int c, int rowOffset, int colOffset,
                                    boolean friendOwnedByPlayer1, List stack, boolean samePlayerOnly, NeighborType type)
    {
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];

        switch (type.getOrdinal()) {
            case NeighborType.OCCUPIED_CODE:  // occupied black or white
                if ( !nbr.isVisited() && nbr.isOccupied() &&
                     (!samePlayerOnly || nbr.getPiece().isOwnedByPlayer1() == friendOwnedByPlayer1)) {
                    //if (samePlayerOnly && nbr.getPiece().isOwnedByPlayer1()!=s.getPiece().isOwnedByPlayer1())
                    //  System.out.println(nbr +" s="+s+" friendOwnedByPlayer="+friendOwnedByPlayer1 );
                    stack.add( 0, nbr );
                    return 1;
                }
                break;
           case NeighborType.UNOCCUPIED_CODE :  // empty space
                if ( !nbr.isVisited() && nbr.isUnoccupied() ) {
                    stack.add( 0, nbr );
                    return 1;
                }
                break;
           case NeighborType.NOT_FRIEND_CODE :   // blank or enemy
                if ( !nbr.isVisited() &&
                    ( nbr.isUnoccupied() ||
                       ( nbr.isOccupied() && (nbr.getPiece().isOwnedByPlayer1()!=friendOwnedByPlayer1))
                    ))  {
                    stack.add( 0, nbr );
                    return 1;
                }
                break;
           default : assert false: "unknown or unsupported neighbor type:"+type.getName();
        }
        return 0;
    }


    /**
     * return 1 if this is a valid neighbor according to specification
     * these are the immediately adjacent (nobi) nbrs within the specified rectangular bounds
     */
    private final int checkNeighbor( int r, int c, int rowOffset, int colOffset,
                               boolean friendOwnedByPlayer1, List stack, boolean samePlayerOnly, NeighborType type,
                               int rMin, int rMax, int cMin, int cMax )
    {
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];
        if ( nbr.getRow() >= rMin && nbr.getRow() <= rMax && nbr.getCol() >= cMin && nbr.getCol() <= cMax ) {
            return checkNeighbor( r, c, rowOffset, colOffset, friendOwnedByPlayer1, stack, samePlayerOnly, type );
        }
        else
            return 0;
    }

    /**
     *  we allow these as long as the diagonal has not been fully cut
     *  i.e. not an opponent stone on both sides of the cut.
     *
     *  @param sameSideOnly if true then push nbrs on the same side, else push enemy nbrs
     */
    private int checkDiagonalNeighbor( int r, int c, int rowOffset, int colOffset,
                                       boolean friendPlayer1, boolean sameSideOnly, List stack )
    {
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];
        // determine the side we are checking for (one or the other)
        if (nbr.isUnoccupied())
            return 0;
        // don't add it if it is in atari
        if (nbr.getString()!=null && nbr.getString().getLiberties(this).size()==1)
            return 0;
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
    private final int checkOneSpaceNeighbor( int r, int c, int rowOffset, int colOffset,
                                       boolean friendPlayer1, boolean samePlayerOnly, List stack )
    {
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];
        // don't add it if it is in atari
        if (nbr.getString()!=null && nbr.getString().getLiberties(this).size()==1)
            return 0;
        if ( nbr.isOccupied() &&
                (!samePlayerOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited() ) {
            // we consider the link cut if there is an opponent piece between the 2 stones
            //     eg:          *|*
            boolean cut = false;
            if ( rowOffset == 0 ) {
                int col = c + colOffset / 2;
                cut =  (positions_[r][col].isOccupied() &&
                        (positions_[r][col].getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            else {
                int row = r + rowOffset / 2;
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
    private final int checkKogeimaNeighbor( int r, int c, int rowOffset, int colOffset,
                                      boolean friendPlayer1, boolean sameSideOnly, List stack )
    {
        if ( !inBounds( r + rowOffset, c + colOffset ) )
            return 0;
        GoBoardPosition nbr = (GoBoardPosition) positions_[r + rowOffset][c + colOffset];
        // don't add it if it is in atari
        if (nbr.getString()!=null && nbr.getString().getLiberties(this).size()==1)
            return 0;

        if ( nbr.isOccupied() && (!sameSideOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited() ) {
            boolean cut = false;
            // consider it cut if there is an opponent stone in one of the 2 spaces between.
            if ( Math.abs( rowOffset ) == 2 ) {
                int rr = r + rowOffset / 2;
                cut = (positions_[rr][c].isOccupied()
                        && (positions_[rr][c].getPiece().isOwnedByPlayer1() != friendPlayer1)) ||
                        (positions_[rr][c + colOffset].isOccupied()
                        && (positions_[rr][c + colOffset].getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            else {
                int cc = c + colOffset / 2;
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
     * return true if the stones in this list exactly match those in an existing group
     */
    private boolean groupAlreadyExists( List stones )
    {
        Iterator gIt = groups_.iterator();
        GoGroup foundGroup = null;
        // first find the group that contains the stones
        while ( gIt.hasNext() && foundGroup == null ) {
            GoGroup g = (GoGroup) gIt.next();
            if ( g.exactlyContains( stones ) )
                return true;
        }
        return false;
    }

    /**
     * check if the current move is suicidal.
     * suicidal moves (ones that kill your own pieces) are illegal.
     * Usually a move is suicidal if you play on your last liberty.
     * However, if you kill an enemy string by playing on your last liberty,
     * then it is legal.
     */
    public final boolean isSuicidal( GoMove m )
    {
        GoBoardPosition stone = (GoBoardPosition) this.getPosition( m.getToRow(), m.getToCol() );
        GoString string = stone.getString();
        if ( string == null )
            GameContext.log( 0, "Warning: GoMove.isSuicidal: the string is null" );
        if ( string != null && string.size() > 0 && string.getLiberties( this ).size() == 0 ) {
            GameContext.log( 2,
                    "GoMove.isSuicidal: your are playing on the last liberty for this string=" + string.toString() +
                    " captures=" + m.captureList );
            // if we do not have captures, then it is a suicide move and should not be allowed.
            if ( m.captureList == null )
                return true;
        }
        return false;
    }


    /**
     * returns true if the specified move caused one or more opponent groups to be in atari
     *
     * @param m the move to check.
     * @return true if the move m caused an atari
     */
    public final boolean causedAtari( GoMove m )
    {
        if ( m.isPassingMove() )
            return false; // a pass cannot cause an atari
        GoBoardPosition stone = (GoBoardPosition)this.getPosition( m.getToRow(), m.getToCol() );
        HashSet enemyNbrs =
                this.getNobiNeighbors( stone, NeighborType.ENEMY );
        Iterator it = enemyNbrs.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            GoGroup g = s.getGroup();
            if ( g.getLiberties( this ).size() == 1 ) {
                GameContext.log( 2, "ATARI:" + g + " is atari'd as a result of move " + this );
                return true;
            }
        }
        return false;
    }

    /**
     * make it show an empty board position.
     * @param pos the position to clear.
     */
    public final void clear( GoBoardPosition pos )
    {
        GoString string = pos.getString();
        if (string != null)  {
            GoGroup g = string.getGroup();
            g.remove( pos, this );   // may cause a string to break into smaller strings
        }
        super.clear(pos);
        pos.setString(null); // no longer belongs to any string.
        pos.setVisited(false);
    }

    /**
     * we must recalculate the number of liberties every time because it changes often.
     * @param pos position on the the board to get the number of liberties for
     * @return the number of liperties the specified position has.
     */
    public final int getNumLiberties( GoBoardPosition pos )
    {
        int numLiberties = 0;
        int row = pos.getRow();
        int col = pos.getCol();
        if ( row > 1 && getPosition( row - 1, col ).isUnoccupied() )
            numLiberties++;
        if ( row < getNumRows() && getPosition( row + 1, col ).isUnoccupied() )
            numLiberties++;
        if ( col > 1 && getPosition( row, col - 1 ).isUnoccupied() )
            numLiberties++;
        if ( col < getNumCols() && getPosition( row, col + 1 ).isUnoccupied() )
            numLiberties++;

        return numLiberties;
    }

    /**
     * remove groups that have no stones in them.
     */
    private final  void cleanupGroups()
    {
        Iterator it = groups_.iterator();
        while ( it.hasNext() ) {
            GoGroup group = (GoGroup) it.next();
            //group.confirmNoNullMembers();
            if ( group.getNumStones() == 0 )  {
                assert (group.getEyes().isEmpty()): group+ " has eyes! It was assumed not to";
                it.remove();
            }
        }
    }

    /**
     * create a nice list of all the current groups (and the strings they contain)
     * @return String containing the current groups
     */
    public final String getGroupsText()
    {
        StringBuffer groupText = new StringBuffer( "The black groups are :\n" );
        StringBuffer whiteGroupsText = new StringBuffer( "\nThe white groups are :\n" );

        Iterator it = groups_.iterator();
        while ( it.hasNext() ) {
            GoGroup group = (GoGroup) it.next();
            if ( group.isOwnedByPlayer1() )
                groupText.append( group.toString() );
            else
                whiteGroupsText.append( group.toString() );
        }
        groupText.append( whiteGroupsText.toString() );

        return groupText.toString();
    }


    /**
     * clear all the eyes from all the stones on the board
     */
    private void clearEyes()
    {
        for ( int i = 1; i <= getNumRows(); i++ )
            for ( int j = 1; j <= getNumCols(); j++ ) {
                GoBoardPosition space = (GoBoardPosition) this.getPosition( i, j );
                if ( space.isInEye() )     {
                    space.getEye().clear();
                    space.setEye(null);
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
    public final int formsBadShape( GoStone stone, int r, int c )
    {
        int severity =
                checkForBadShape( stone, r, c,  1) +
                checkForBadShape( stone, r, c,  -1);
        return severity;
    }

    /**
     * @param stone the go stone.
     * @param inc direction to increment (+/-1
     * @return an integer value indicating how bad the shape is (0 being not bad at all).
     */
    private int checkForBadShape(GoStone stone,  int r, int c, int inc )
    {

        int severity = 0;
        boolean player1 = stone.isOwnedByPlayer1();

        if ( inBounds( r + inc, c ) ) {
            BoardPosition adjacent = getPosition( r + inc, c );
            if ( adjacent.isOccupied() && adjacent.getPiece().isOwnedByPlayer1() == player1 ) {
                BoardPosition diagonal1 = getPosition( r + inc, c - 1 );
                if ( inBounds( r + inc, c - 1 ) &&
                     diagonal1.isOccupied() && diagonal1.getPiece().isOwnedByPlayer1() == player1) {
                    severity += getBadShapeAux( getPosition( r, c - 1 ), player1 );
                }
                BoardPosition diagonal2 = getPosition( r + inc, c + 1 );
                if ( inBounds( r + inc, c + 1 ) &&
                     diagonal2.isOccupied() && diagonal2.getPiece().isOwnedByPlayer1() == player1 ) {
                    severity += getBadShapeAux( getPosition( r, c + 1 ), player1 );
                }
            }
        }
        return severity;
    }

    private static final int getBadShapeAux( BoardPosition adjacent1, boolean player1 )
    {
        if ( adjacent1.isUnoccupied() || adjacent1.getPiece().isOwnedByPlayer1() == player1 )
            return 1;
        return 0;
    }
    //------------- debugging routines below this point ----------------------------------
    /**
     * pretty print a list of all the current groups (and the strings they contain)
     */
    public final void debugPrintGroups( int logLevel )
    {
        if (logLevel < GameContext.getDebugMode())  {
            GameContext.log( logLevel, "---The groups currently on the board are:" );
            GameContext.log( logLevel, getGroupsText() );
            GameContext.log( logLevel, "----" );
        }
    }

    private void confirmNoEmptyStrings()
    {
        Iterator it = groups_.iterator();
        while ( it.hasNext() ) {
            GoGroup group = (GoGroup) it.next();
            Iterator sit = group.getMembers().iterator();
            while ( sit.hasNext() ) {
                GoString string = (GoString) sit.next();
                Assert.isTrue((string.size() > 0), "There is an empty string in " + string.getGroup());
            }
        }
    }

    /**
     *  confirm that all the strings in a group have nobi connections.
     */
    public final void confirmGroupsHaveValidStrings()
    {
        Iterator it = groups_.iterator();
        while ( it.hasNext() ) {
            GoGroup group = (GoGroup) it.next();
            group.confirmValidStrings( this );
        }
    }

    /**
     * verify that all the stones on the board are in the boards member list of groups.
     */
    private void confirmStonesInValidGroups()
    {
        for ( int i = 1; i <= getNumRows(); i++ )
            for ( int j = 1; j <= getNumCols(); j++ ) {
                GoBoardPosition space = (GoBoardPosition) this.getPosition( i, j );
                if ( space.isOccupied() )
                    confirmStoneInValidGroup( space );
            }
    }

    /**
     * @param stone verify that this stone has a valid string and a group in the boards member list.
     */
    private void confirmStoneInValidGroup( GoBoardPosition stone )
    {
        GoString str = stone.getString();
        //boolean b = stone.getPiece().isOwnedByPlayer1();
        Assert.notNull( str, stone + " does not belong to any string!") ;
        GoGroup g = str.getGroup();
        boolean valid = false;
        Iterator gIt = groups_.iterator();
        GoGroup g1 = null;
        while ( !valid && gIt.hasNext() ) {
            g1 = (GoGroup) gIt.next();
            valid = (g == g1);
        }
        if ( !valid ) {
            debugPrintGroups( 0 );
            Assert.exception(
                   "Error: This " + stone + " does not belong to a valid group: " + g + " \nThe valid groups are:" + groups_);
        }
    }

    /**
     * verify that all the stones are marked unvisited.
     */
    public final void confirmAllUnvisited()
    {
        GoBoardPosition stone = areAllUnvisited();
        if (stone != null)
           Assert.exception(stone + " is marked visited" );
    }

    /**
     * verify that all the stones are marked unvisited.
     */
    private final GoBoardPosition areAllUnvisited()
    {
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                GoBoardPosition stone = (GoBoardPosition) this.getPosition( i, j );
                if (stone.isVisited())
                    return stone;
            }
        }
        return null;
    }

    public List getVisitedSpaces()
     {
        List list = new ArrayList();
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                GoBoardPosition stone = (GoBoardPosition) this.getPosition( i, j );
                if (stone.isVisited())
                    list.add(stone);
            }
        }
         return list;
     }


    /**
     * confirm that the stones in this group are not contained in any other group.
     */
    private void confirmStonesInOneGroup( GoGroup group )
    {
        Iterator strIt = group.getMembers().iterator();
        while ( strIt.hasNext() ) {
            GoString string1 = (GoString) strIt.next();
            Iterator grIt = groups_.iterator();
            while ( grIt.hasNext() ) {
                GoGroup g = (GoGroup) grIt.next();
                if ( g != group ) {
                    Iterator it = g.getMembers().iterator();
                    while ( it.hasNext() ) {
                        GoString s = (GoString) it.next();
                        if ( string1 == s ) {
                            debugPrintGroups( 0 );
                            assert false: "ERROR: " + s + " contained by 2 groups" ;
                        }
                        //make sure that every stone in the string belongs in this group
                        Iterator stoneIt = s.getMembers().iterator();
                        while ( stoneIt.hasNext() ) {
                            GoBoardPosition st1 = (GoBoardPosition) stoneIt.next();
                            if ( g != st1.getGroup() ) {
                                debugPrintGroups( 0 );
                                assert false:
                                       st1 + " does not just belong to " + st1.getGroup()
                                        + " as its ancestry indicates. It also belongs to " + g;
                            }
                        }
                    }
                }
            }
        }
    }
}
