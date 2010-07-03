package com.becker.game.twoplayer.go.board;

import com.becker.game.twoplayer.go.board.analysis.CornerChecker;
import com.becker.game.twoplayer.go.*;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;

import com.becker.game.twoplayer.go.board.analysis.TerritoryAnalyzer;
import com.becker.game.twoplayer.go.board.update.BoardUpdater;

import java.util.*;


/**
 * Representation of a Go Game Board
 * There are a lot of datastructures to organize the state of the pieces.
 * For example, we update strings, and groups (and eventually armies) after each move.
 * After updating we can use these structures to estimate territory for each side.
 *
 * Could move many methods to StringFinder and GroupFinder classes.
 * @author Barry Becker
 */
public final class GoBoard extends TwoPlayerBoard
{
    /** This is a set of active groups. Groups are composed of strings. */
    private volatile Set<GoGroup> groups_;

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
        groups_ = createGroupSet();
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
        // first time through we need to initialize the star-point positions
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

        // make copies of all the groups
        if (groups_ != null) {
            ((GoBoard)clone).groups_ = createGroupSet();

            Set<GoGroup> groupsCopy = ((GoBoard)clone).groups_;

            synchronized(groups_) {
                for (GoGroup g : groups_)  {
                    groupsCopy.add((GoGroup)g.clone());
                }
            }
        }
        return clone;
    }

    /**
     * @return  synchronized and ordered set of groups.
     */
    private Set<GoGroup> createGroupSet() {
        return Collections.synchronizedSet(new LinkedHashSet<GoGroup>(10));
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
     * Num different states.
     * This is used primarily for the Zobrist hash. You do not need to override if you do not use it.
     * The states are player1, player2, or empty (we may want to add ko).
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
        return 3;
    }

    public List getHandicapPositions() {
        return handicap_.getStarPoints();
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

    private GoProfiler getProfiler() {
        return GoProfiler.getInstance();
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
    @Override
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
     * @return change in territorial score
     */
    public float getTerritoryDelta()
    {
        return territoryAnalyzer_.getTerritoryDelta();
    }

    /**
     * @see TerritoryAnalyzer#getTerritoryEstimate
     * @return estimate of size of territory for specified player.
     */
    public int getTerritoryEstimate( boolean forPlayer1, boolean isEndOfGame) {
        return territoryAnalyzer_.getTerritoryEstimate(forPlayer1, isEndOfGame);
    }

    /**
     * @see TerritoryAnalyzer#updateTerritory
     * @return the estimated difference in territory between the 2 sides.
     */
    public float updateTerritory(boolean isEndOfGame) {
        return territoryAnalyzer_.updateTerritory(isEndOfGame);
    }

    /**
     * Corner triples are the 3 points closest to a corner
     * @param position position to see if in corner of board.
     * @return true if the specified BoardPosition is on the corder of the board
     */
    public boolean isCornerTriple(BoardPosition position) {
        return new CornerChecker(getNumRows(), getNumCols()).isCornerTriple(position);
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
}
