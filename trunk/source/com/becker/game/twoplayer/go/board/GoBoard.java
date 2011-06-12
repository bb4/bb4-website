package com.becker.game.twoplayer.go.board;

import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.go.board.analysis.CornerChecker;
import com.becker.game.twoplayer.go.board.analysis.TerritoryAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.elements.group.GoGroupSet;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoStone;
import com.becker.game.twoplayer.go.board.elements.string.GoString;
import com.becker.game.twoplayer.go.board.elements.string.GoStringSet;
import com.becker.game.twoplayer.go.board.elements.string.IGoString;
import com.becker.game.twoplayer.go.board.move.GoMove;
import com.becker.game.twoplayer.go.board.update.BoardUpdater;
import com.becker.game.twoplayer.go.board.update.CaptureCounts;

import java.util.List;


/**
 * Representation of a Go Game Board
 * There are a lot of data structures to organize the state of the pieces.
 * For example, we update strings, and groups (and eventually armies) after each move.
 * After updating we can use these structures to estimate territory for each side.
 *
 * Could move many methods to StringFinder and GroupFinder classes.
 * @author Barry Becker
 */
public final class GoBoard extends TwoPlayerBoard {

    /** This is a set of active groups. Groups are composed of strings. */
    private GoGroupSet groups_;

    /** Handicap stones are on the star points, unless the board is very small */
    private HandicapStones handicap_;

    private BoardUpdater boardUpdater_;

    private TerritoryAnalyzer territoryAnalyzer_;


    /**
     *  Constructor.
     *  @param numRows num rows
     *  @param numCols num cols
     *  @param numHandicapStones number of black handicap stones to initialize with.
     */
    public GoBoard( int numRows, int numCols, int numHandicapStones ) {

        setSize( numRows, numCols );
        setHandicap(numHandicapStones);

        init(new CaptureCounts());
    }

    /**
     * Copy constructor
     */
    public GoBoard(GoBoard board) {
        super(board);

        handicap_ = board.handicap_;
        NeighborAnalyzer analyzer = new NeighborAnalyzer(this);
        analyzer.determineAllStringsOnBoard();
        groups_ = analyzer.findAllGroupsOnBoard();

        init(board.boardUpdater_.getCaptureCounts());
    }

    public synchronized GoBoard copy() {

        getProfiler().startCopyBoard();
        GoBoard b = new GoBoard(this);
        getProfiler().stopCopyBoard();
        return b;
    }

    /**
     * Start over from the beginning and reinitialize everything.
     * The first time through we need to initialize the star-point positions.
     */
    @Override
    public void reset() {

        super.reset();
        groups_ = new GoGroupSet();

        setHandicap(getHandicap());
        init(new CaptureCounts());
    }

    private void init(CaptureCounts capCounts) {
        boardUpdater_ = new BoardUpdater(this, capCounts);
        territoryAnalyzer_ = new TerritoryAnalyzer(this);
    }

    @Override
    protected BoardPosition getPositionPrototype() {
        return new GoBoardPosition(1, 1, null, null);
    }

    public void setHandicap(int handicap) {
        handicap_ = new HandicapStones(handicap, getNumRows());
        makeMoves(handicap_.getHandicapMoves());
    }

    /**
     * get the number of handicap stones used in this game.
     * @return number of handicap stones
     */
    public int getHandicap() {
        if (handicap_ == null) {
            return 0;
        }
        return handicap_.getNumber();
    }

    /**
     * in go there is not really a theoretical limit to the number of moves,
     * but practically if we exceed this then we award the game to whoever is ahead.
     * @return the maximum number of moves ever expected for this game.
     */
    public int getMaxNumMoves()
    {
        return 2 * positions_.getNumBoardSpaces();
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
    @Override
    public int getTypicalNumMoves() {
        return positions_.getNumBoardSpaces() - getNumRows();
    }

    /**
     * get the current set of active groups. Should be read only. Do not modify.
     * @return all the valid groups on the board (for both sides)
     */
    public GoGroupSet getGroups() {
        return groups_;
    }


    public void setGroups(GoGroupSet groups) {
        groups_ = groups;
    }


    /**
     * Adjust the liberties on the strings (both black and white) that we touch.
     * @param liberty either occupied or not depending on if we are placing the stone or removing it.
     */
    public void adjustLiberties(GoBoardPosition liberty) {

         NeighborAnalyzer na = new NeighborAnalyzer(this);
         GoStringSet stringNbrs = na.findStringNeighbors( liberty );
         for (IGoString sn : stringNbrs) {
            ((GoString)sn).changedLiberty(liberty);
         }
    }

    /**
     * Make sure that all the positions on the board are reset to the unvisited state.
     */
    public void unvisitAll() {
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                GoBoardPosition pos = (GoBoardPosition) getPosition( i, j );
                pos.setVisited(false);
            }
        }
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
    protected boolean makeInternalMove( Move move ) {
        getProfiler().startMakeMove();

        GoMove m = (GoMove)move;

        // if its a passing move, there is nothing to do
        if ( m.isPassOrResignation() ) {
            GameContext.log( 2, m.isPassingMove() ? "Making passing move" : "Resigning");
            getProfiler().stopMakeMove();
            return true;
        }
        clearEyes();
        boolean valid = super.makeInternalMove( m );
        boardUpdater_.updateAfterMove(m);

        getProfiler().stopMakeMove();
        return valid;
    }

    /**
     * for Go, undoing a move means changing that space back to a blank, restoring captures, and updating groups.
     * @param move  the move to undo.
     */
    @Override
    protected void undoInternalMove( Move move ) {

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
    public float getTerritoryDelta() {
        return territoryAnalyzer_.getTerritoryDelta();
    }

    /**
     * Get estimate of territory for specified player.
     * @param forPlayer1 the player to get the estimate for
     * @param isEndOfGame then we need the estimate to be more accurate.
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
        clearScores();
        return territoryAnalyzer_.updateTerritory(isEndOfGame);
    }

    /**
     * Corner triples are the 3 points closest to a corner
     * @param position position to see if in corner of board.
     * @return true if the specified BoardPosition is on the order of the board
     */
    public boolean isCornerTriple(BoardPosition position) {
        return new CornerChecker(getNumRows(), getNumCols()).isCornerTriple(position);
    }

    /**
     * @return either the number of black or white stones.
     */
    public int getNumStones(boolean forPlayer1) {
        int numStones = 0;

        // we should be able to just sum all the position scores now.
        for ( int i = 1; i <= getNumRows(); i++ )  {
           for ( int j = 1; j <= getNumCols(); j++ ) {
               GoBoardPosition pos = (GoBoardPosition)getPosition(i, j);
               if (pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == forPlayer1)  {
                  numStones++;
               }
           }
        }
        return numStones;
    }

    /**
     * Clear whatever cached score state we might have before recomputing.
     */
    private void clearScores() {
        // we should be able to just sum all the position scores now.
        for ( int i = 1; i <= getNumRows(); i++ )  {
           for ( int j = 1; j <= getNumCols(); j++ ) {
               GoBoardPosition pos = (GoBoardPosition)getPosition(i, j);
               pos.setScoreContribution(0);

               if (pos.isOccupied()) {
                   GoStone stone = ((GoStone)pos.getPiece());
                   stone.setHealth(0);
               }
           }
        }
    }

    /**
     * clear all the eyes from all the stones on the board
     */
    private void clearEyes() {
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                GoBoardPosition space = (GoBoardPosition)getPosition(i, j);
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
        StringBuilder buf = new StringBuilder((rows + 2) * (cols + 2));

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
