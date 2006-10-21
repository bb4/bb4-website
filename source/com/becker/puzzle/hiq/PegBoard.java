package com.becker.puzzle.hiq;

import com.becker.game.common.*;
import com.becker.common.*;

import java.util.*;

/**
 * @author Barry Becker
 */
public class PegBoard {

    static final byte SIZE = 7;  // this must be odd
    private static final byte CENTER = 3;
    private static final byte CORNER_SIZE = 2;

    private boolean[][] positions_;
    private int numPegsLeft_;

    public PegBoard() {
        initializeBoard();
    }

    private void initializeBoard() {
        positions_ = new boolean[SIZE][SIZE];
        setToInitialState();
    }

    public void setToInitialState() {
       numPegsLeft_ = 0;
       for (int i = 0; i<SIZE; i++) {
           for (int j = 0; j<SIZE; j++) {
               if (isValidPosition(i, j)) {
                   positions_[i][j] = true;
                   numPegsLeft_++;
               }
           }
       }
       positions_[CENTER][CENTER] = false;
       numPegsLeft_--;
   }


    public void setToSolvedState() {
        for (int i = 0; i<SIZE; i++) {
            for (int j = 0; j<SIZE; j++) {
                positions_[i][j] = false;
            }
        }
        positions_[CENTER][CENTER] = true;
    }

    public int getSize() {
        return SIZE;
    }
    public int getCornerSize() {
        return CORNER_SIZE;
    }

    boolean getPosition(int row, int col) {
        return positions_[row][col];
    }

    boolean isValidPosition(int row , int col) {
        if (row < 0 || row  >= SIZE || col < 0 || col >= SIZE) {
            return false;
        }
        if (row >= CORNER_SIZE && row < SIZE - CORNER_SIZE) {
            return true;
        }
        else return col >= CORNER_SIZE && col < SIZE - CORNER_SIZE;
    }

    public PegBoard copy() {
        PegBoard newBoard = new PegBoard();

        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(positions_[i], 0, newBoard.positions_[i],  0, SIZE);
        }
        return newBoard;
    }

    public boolean isEmpty(int row, int col) {
        return !positions_[row][col];
    }

    /**
     * Because of symmetry, there is really only one first move not 4.
     * @return PegMove the first move.
     */
    public PegMove getFirstMove() {
       return new PegMove(CENTER, (byte)(CENTER-2), CENTER, CENTER);
    }

    public boolean isSolved() {
        //return  (getNumPegsLeft() < 2);
        return (getNumPegsLeft() == 1 && positions_[CENTER][CENTER]);
    }

    public int getNumPegsLeft() {
        return numPegsLeft_;
    }

    public void makeMove(PegMove move) {
        doMove(move, false);
    }

    public void undoMove(PegMove move) {
        doMove(move, true);
    }

    public void doMove(PegMove move, boolean undo) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        positions_[fromRow][fromCol] = undo;
        positions_[(fromRow + toRow) >> 1][(fromCol + toCol) >> 1] = undo;
        positions_[toRow][toCol] = !undo;

        numPegsLeft_ += undo? 1 : -1;
    }

    /**
     *
     * @param location Location empty or peg location based on undo
     * @param undo boolean find undo (peg) or redo (empty location) moves.
     * @return List
     */
    private List<PegMove> findMovesForLocation(Location location, boolean undo) {
        List<PegMove> moves = new LinkedList<PegMove>();
        byte r = (byte) location.getRow();
        byte c = (byte) location.getCol();
        Location destination = new Location(r, c);

        // 4 cases to consider: NEWS
        if (isValidPosition(r, c-2) && positions_[r][c-2]!=undo && positions_[r][c-1]!=undo) {
            Location from = new Location(r, c - 2);
            moves.add(new PegMove(from, destination));
        }
        if (isValidPosition(r, c+2) && positions_[r][c+2]!=undo && positions_[r][c+1]!=undo) {
            Location from = new Location(r, c + 2);
            moves.add(new PegMove(from, destination));
        }
        if (isValidPosition(r-2, c) && positions_[r-2][c]!=undo && positions_[r-1][c]!=undo) {
            Location from = new Location(r-2, c);
            moves.add(new PegMove(from, destination));
        }
        if (isValidPosition(r+2, c) && positions_[r+2][c]!=undo && positions_[r+1][c]!=undo) {
            Location from = new Location(r+2, c);
            moves.add(new PegMove(from, destination));
        }
        return moves;
    }

    public BoardHashKey hashKey() {
        return new BoardHashKey(this);
    }

    /**
     *
     * @param pegged boolean if true get pegged locations, else empty locations
     * @return List of pegged or empty locations
     */
    public List<Location> getLocations(boolean pegged) {

        List<Location> list = new LinkedList<Location>();
        for (int i = 0; i<SIZE; i++) {
            for (int j = 0; j<SIZE; j++) {
                if (isValidPosition(i, j) && positions_[i][j] == pegged) {
                    list.add(new Location(i, j));
                }
            }
        }
        return list;
    }

    /**
     * @return List of all valid jumps for the current board state
     */
    public List<PegMove> generateMoves() {
       List<PegMove> moves = new LinkedList<PegMove>();
       List<Location> emptyLocations = getLocations(false);
       if (emptyLocations.size() == 0) {
           moves.add(getFirstMove());
       } else {
           Iterator it = emptyLocations.iterator();
           while (it.hasNext()) {
               Location pos = (Location) it.next();
               moves.addAll(findMovesForLocation(pos, false));
           }
       }
       //Collections.shuffle(moves);
       return moves;
    }

      /**
       * @return List of possible undo moves
       */
      public List<PegMove> generateUndoMoves() {
         List<PegMove> moves = new LinkedList<PegMove>();
         List<Location> pegLocations = getLocations(true);

         Iterator it = pegLocations.iterator();
         while (it.hasNext()) {
             Location pos = (Location) it.next();
             moves.addAll(findMovesForLocation(pos, true));
         }
         return moves;
    }

}
