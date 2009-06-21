package com.becker.game.common;

import com.becker.common.*;

import java.util.*;


/**
 *  the Board describes the physical layout of the game.
 *  It is an abstract class that provides a common implementation for many of the
 *  methods in the BoardInterface.
 *  Assumes an M*N grid.
 *  Legal positions are [1, numRows_][1, numCols_]
 *
 *  Games like pente, go, chess, checkers, go-moku,
 *  shoji, othello, connect4, squares, Stratego, Blockade
 *  Other games like Risk, Galactic Empire, or Monopoly and might be supportable in the future.
 *  They are harder because they do not have perfect information (i.e. they use dice).
 *  and have multiple players.
 *
 *  @author Barry Becker
 */
public abstract class Board implements BoardInterface, Cloneable
{

    /** the internal data structures representing the game board and the positions on it. */
    protected BoardPosition positions_[][] = null;

    protected int numRows_;
    protected int numCols_;
    protected int rowsTimesCols_;

     /** a global profiler for recording timing stats. */
    private static GameProfiler profiler_;

    /**
     * We keep a list of the moves that have been made.
     * We can navigate forward or backward in time using this
     */
    protected LinkedList<Move> moveList_;// = new LinkedList<Move>();

   public Board() {
       System.out.println("creating board and movelist to go with it.");
       moveList_ = new LinkedList<Move>();
   }
    /**
     *  Reset the board to its initial state.
     */
    public void reset() {
        getMoveList().clear();
    }

    /**
     *  Change the dimensions of this game board.
     *  Note: we must call reset after changing the size, since the original game board will now be invalid.
     *  @param numRows the new number of rows for the board to have.
     *  @param numCols the new number of cols for the board to have.
     */
    public void setSize( int numRows, int numCols )
    {
        numRows_ = numRows;
        numCols_ = numCols;
        GameContext.log(3, "Board rows cols== " + numRows + ", " + numCols );
        rowsTimesCols_ = numRows_ * numCols_;
        reset();
    }

    /**
     * @return  retrieve the number of rows that the board has.
     */
    public final int getNumRows()
    {
        return numRows_;
    }

    /**
     * @return  retrieve the number of cols that the board has.
     */
    public final int getNumCols()
    {
        return numCols_;
    }

    public LinkedList<Move> getMoveList() {
        return moveList_;
    }

    public void initPlayers() {};

    /**
     * @return the most recent move played on the board. Returns null if there isn't one.
     */
    public final Move getLastMove()
    {
        if ( moveList_ == null || moveList_.isEmpty() ) {
            return null;
        }
        return moveList_.getLast();  //get(moveList_.size()-1);
    }


    /**
     * @return  the number of moves currently played.
     */
    public final int getNumMoves()
    {
        if ( moveList_ == null || moveList_.isEmpty() )
            return 0; // no moves yet
        return moveList_.size();
    }


    /**
     * returns null if there is no game piece at the position specified.
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final BoardPosition getPosition( int row, int col )
    {
        if ( row < 1 || row > numRows_ || col < 1 || col > numCols_) {
            return null;
        }
        return positions_[row][col];
    }

    /**
     * returns null if there is no game piece at the position specified.
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final BoardPosition getPosition( Location loc )
    {
        return getPosition(loc.getRow(), loc.getCol());
    }

    /**
     * @return a deep copy of the board.
     * @throws CloneNotSupportedException if this object should not be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
       Object clone = super.clone();
       BoardPosition[][] p = new BoardPosition[getNumRows() + 1][getNumCols() + 1];

       for ( int i = 1; i <= getNumRows(); i++ )   {
          for ( int j = 1; j <= getNumCols(); j++ ) {
             p[i][j] = this.getPosition(i,j).copy();
          }
       }
       ((Board)clone).positions_ = p;
       return clone;
    }

    /**
     * Two boards are considered equal if all the pieces are in the same spot and have like ownership.
     * @param b
     * @return true if all the pieces in board b in the same spot and have like ownership as this.
     */
    @Override
    public boolean equals(Object b)
    {
        Board board = (Board)b;
        for ( int i = 1; i <= getNumRows(); i++ )   {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                BoardPosition p1 = this.getPosition(i,j);
                BoardPosition p2 = board.getPosition(i,j);
                if (p1.isOccupied() != p2.isOccupied()) {
                    GameContext.log(2, "Inconsistent occupation status  p1="+p1+ " and p2="+p2 );
                    return false;
                }
                if (p1.isOccupied() && p2.isOccupied()) {
                    GamePiece piece1 = p1.getPiece();
                    GamePiece piece2 = p2.getPiece();
                    if (piece1.isOwnedByPlayer1() != piece2.isOwnedByPlayer1() ||
                        piece1.getType() != piece2.getType())    {
                        GameContext.log(2, "There was an inconsistency between p1="+p1+ " and "+p2 );
                        return false;
                    }
                }
            }
       }
       return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        int nRows = getNumRows();
        int nCols = getNumCols();
        for ( int i = 1; i <= nRows; i++ )   {
          int pos = (i-1) * nCols;
          for ( int j = 1; j <= nCols; j++ ) {
              BoardPosition p1 = this.getPosition(i,j);
              if (p1.isOccupied()) {
                  hash += 2 *(pos + j) + (p1.getPiece().isOwnedByPlayer1()? 1: 2);
              }
           }
        }
        return hash;
    }

    /**
     * @param move  to make
     * @return false if the move is illegal
     */
    public final boolean makeMove( Move move ) {
        boolean done = makeInternalMove(move);
        getMoveList().add( move );
        return done;
    }


    /**
     * undo the last move made.
     * @return  the move that got undone
     */
    public Move undoMove() {
        if ( !getMoveList().isEmpty() ) {
            Move move = getMoveList().removeLast();
            undoInternalMove( move );
            return move;
        }
        return null;
    }

    /**
     * @param move
     * @return  false if the move is illegal
     */
    protected abstract boolean makeInternalMove( Move move );

    /**
     * Allow reverting a move so we can step backwards in time.
     * Board is returned to the exact state it was in before the last move was made.
     */
    protected abstract void undoInternalMove( Move move );

    /**
     * @return true if the specified position is within the bounds of the board
     */
    public final boolean inBounds( int r, int c )
    {
        return !(r < 1 || r > getNumRows() || c < 1 || c > getNumCols());
    }


    public void initializeProfilingStats()
    {
        profiler_.initialize();
    }

    /**
      * For profiling output in a log
      * Record times for these operations so we get an accurate picture of where the bottlenecks are.
      */
    public GameProfiler getProfiler() {
        if (profiler_ == null)
        {
            profiler_ = createProfiler();
        }
        return profiler_;
    }

    /**
     * @return object to keep track of profiling statistics.
     */
    protected GameProfiler createProfiler() {
        return new GameProfiler();
    }

    /**
     * Explicitly clean things up to avoid memory leaks.
     * The most common way to accidentaly have memory leaks is to leave listeners on objects.
     */
    public void dispose()
    {
        positions_ = null;
    }


    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder(1000);
        bldr.append("\n");
        int nRows = getNumRows();
        int nCols = getNumCols();
        for ( int i = 1; i <= nRows; i++ )   {
          for ( int j = 1; j <= nCols; j++ ) {
              BoardPosition p1 = this.getPosition(i,j);
              if (p1.isOccupied()) {
                  bldr.append(p1.getPiece());
              }
              else {
                  bldr.append(" _ ");
              }
           }
           bldr.append("\n");
        }
        return bldr.toString();
    }

}
