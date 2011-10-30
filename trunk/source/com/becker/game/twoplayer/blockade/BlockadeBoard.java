/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.blockade;

import com.becker.common.geometry.Location;
import com.becker.game.common.AbstractGameProfiler;
import com.becker.game.common.GameProfiler;
import com.becker.game.common.Move;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.blockade.analysis.BoardAnalyzer;
import com.becker.game.twoplayer.common.TwoPlayerBoard;

import java.util.List;
import java.util.Set;

/**
 * Defines the structure of the com.becker.game.twoplayer.blockade board and the pieces on it.
 * Each BlockadeBoardPosition can contain a piece and south and east walls.
 *
 * @author Barry Becker
 */
public class BlockadeBoard extends TwoPlayerBoard {

    /** The number of home bases for each player.  Traditional rules call for 2. */
    public static final int NUM_HOMES = 2;

    /** The percentage away from the players closest edge to place the bases. */
    private static final float HOME_BASE_POSITION_PERCENT = 0.3f;

    /** Home base positions for both players. */
    private BoardPosition[] p1Homes_ = null;
    private BoardPosition[] p2Homes_ = null;

    private BoardAnalyzer boardAnalyzer_;


    /**
     * Constructor.
     * @param numRows number of rows in the board grid.
     * @param numCols number of rows in the board grid.
     */
    public BlockadeBoard(int numRows, int numCols) {
        setSize(numRows, numCols);
        boardAnalyzer_ = new BoardAnalyzer(this);
    }

    /** copy constructor */
    protected BlockadeBoard(BlockadeBoard b) {
        super(b);
        p1Homes_ = b.p1Homes_.clone();
        p2Homes_ = b.p2Homes_.clone();
        boardAnalyzer_ = new BoardAnalyzer(this);
    }

    @Override
    public BlockadeBoard copy() {
        return new BlockadeBoard(this);
    }

    /**
     * reset the board to its initial state.
     */
    @Override
    public void reset() {
        super.reset();

        p1Homes_ = new BlockadeBoardPosition[NUM_HOMES];
        p2Homes_ = new BlockadeBoardPosition[NUM_HOMES];

        // determine the home base positions,
        // and place the players 2 pieces on their respective home bases initially.
        int homeRow1 = getNumRows() - (int) (HOME_BASE_POSITION_PERCENT * getNumRows()) + 1;
        int homeRow2 = (int) (HOME_BASE_POSITION_PERCENT * getNumRows());
        float increment = (float)(getNumCols())/(NUM_HOMES+1);
        int baseOffset = Math.round(increment);
        for (int i=0; i<NUM_HOMES; i++) {
            int c = baseOffset + Math.round(i*increment);
            setPosition(new BlockadeBoardPosition( homeRow1, c, null, null, null, true, false));
            setPosition(new BlockadeBoardPosition( homeRow2, c, null, null, null, false, true));

            p1Homes_[i] = positions_.getPosition(homeRow1, c);
            p1Homes_[i].setPiece(new GamePiece(true));
            p2Homes_[i] = positions_.getPosition(homeRow2, c);
            p2Homes_[i].setPiece(new GamePiece(false));
        }
    }

    @Override
    protected BoardPosition getPositionPrototype() {
        return new BlockadeBoardPosition(1, 1);
    }
        

    /**
     * If the Blockade game has more than this many moves, then we assume it is a draw.
     * We make this number big, because in com.becker.game.twoplayer.blockade it is impossible to have a draw.
     * I haven't proved it, but I think it is impossible for the number of moves to exceed
     * the rows*cols.
     * @return assumed maximum number of moves.
     */
    public int getMaxNumMoves()
    {
        return Integer.MAX_VALUE;
    }

    /**
     * @return player1's home bases.
     */
    public BoardPosition[] getPlayer1Homes()
    {
        return p1Homes_;
    }

    /**
     * 
     * @return player2's home bases.
     */
    public BoardPosition[] getPlayer2Homes()
    {
        return p2Homes_;
    }

    /**
      * Blockade pieces can move 1 or 2 spaces in any direction.
      * However, only in rare cases would you ever want to move only 1 space.
      * For example, move 1 space to land on a home base, or in preparation to jump an oppponent piece.
      * They may jump over opponent pieces that are in the way (but they do not capture it).
      * The wall is ignored for the purposes of this method.
      *     Moves are only allowed if the candidate position is unoccupied (unless a home base) and if
      * it has not been visited already. The visited part is only significant when we are doing a traversal
      * such as when we are finding the shortest paths to home bases.
      * <pre>
      *       #     There are at most 12 moves from this position
      *     #*#    (some of course may be blocked by walls)
      *   #*O*#    The most common being marked with #'s.
      *     #*#
      *       #
      * </pre>
      *
      * We only add the one space moves if 
      *   1. jumping 2 spaces in that direction would land on an opponent pawn,
      *   2. or moving one space moves lands on an opponent home base.
      *
      * @param position we are moving from
      * @param op1 true if opposing player is player1; false if player2.
      * @return a list of legal piece movements
      */
     public List<BlockadeMove> getPossibleMoveList(BoardPosition position, boolean op1)
     {
         return boardAnalyzer_.getPossibleMoveList(position, op1);
     }

     
    /**
     * @param player1 the last player to make a move.
     * @return all the opponent's shortest paths to your home bases.
     */
    public List<Path> findAllOpponentShortestPaths(boolean player1) {

        return boardAnalyzer_.findAllOpponentShortestPaths(player1);
    }

    /**
     * Find the shortest paths from the specified position to opponent homes.
     * We use DefaultMutableTreeNodes to represent nodes in the path.
     * If the number of paths returned by this method is less than NUM_HOMES,
     * then there has been an illegal wall pacement, since according to the rules
     * of the game there must always be paths from all pieces to all opponent homes.
     * If a pawn has reached an opponent home then the path magnitude is 0 and that player won.
     * 
     * @param position position to check shortest paths for.
     * @return the NUM_HOMES shortest paths from toPosition.
     */
    public List<Path> findShortestPaths( BlockadeBoardPosition position ) 
    {
        return boardAnalyzer_.findShortestPaths(position);
    }

    /*
     * It is illegal to place a wall at a position that overlaps
     * or intersects another wall, or if the wall prevents one of the pawns from reaching an
     * opponent home.
     * @param wall to place. has not been placed yet.
     * @param location where the wall is to be placed.
     * @return an error string if the wall is not a legal placement on the board.
     */
    public String checkLegalWallPlacement(BlockadeWall wall, Location location)
    {
        return boardAnalyzer_.checkLegalWallPlacement(wall, location);
    }
    
    /**
     * find all the paths from each player's pawn to each opponent base.
     * @param lastMove last move made
     * @return the lengths of all the paths from each player's pawn to each opponent base.
     */
    public PlayerPathLengths findPlayerPathLengths(BlockadeMove lastMove) {

        return boardAnalyzer_.findPlayerPathLengths(lastMove);
    }

    
    void addWall(BlockadeWall wall)
    {
        showWall(wall, true);
    }

    void removeWall(BlockadeWall wall)
    {
        showWall(wall, false);
    }

    /**
     * shows or hides this wall on the game board.
     * @param show whether to show or hide the wall.
     */
    private void showWall(BlockadeWall wall, boolean show)
    {
        Set positions  = wall.getPositions();
        if (!positions.isEmpty())
           assert (positions.size()==2): "positions="+positions;
        for (Object position : positions) {
            // since p may be from a different board, we need to make sure that we set the
            // wall for this board.
            BlockadeBoardPosition p = (BlockadeBoardPosition) position;
            BlockadeBoardPosition pp = (BlockadeBoardPosition) getPosition(p.getRow(), p.getCol());
            if (wall.isVertical()) {
                pp.setEastWall(show ? wall : null);
            } else {
                pp.setSouthWall(show ? wall : null);
            }
        }
    }

    private AbstractGameProfiler getProfiler() {
        return GameProfiler.getInstance();
    }

    /**
     * Given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move,
     * and then also places a wall somewhere.
     * @return true if the move was made successfully
     */
    @Override
    protected boolean makeInternalMove( Move move )
    {
        getProfiler().startMakeMove();
        BlockadeMove m = (BlockadeMove) move;
        getPosition(m.getToRow(), m.getToCol()).setPiece(m.getPiece());

        // we also need to place a wall.
        if (m.getWall() != null) {
            addWall(m.getWall());
        }
        getPosition(m.getFromRow(), m.getFromCol()).clear();
        getProfiler().stopMakeMove();
        return true;
    }
    
 
    /**
     * for Blockade, undoing a move means moving the piece back and
     * restoring any captures.
     */
    @Override
    protected void undoInternalMove( Move move ) {
        getProfiler().startUndoMove();
        BlockadeMove m = (BlockadeMove) move;
        BoardPosition startPos = getPosition(m.getFromRow(), m.getFromCol());
        startPos.setPiece( m.getPiece() );
        getPosition(m.getToRow(), m.getToCol()).clear();

        // remove the wall that was placed by this move.
        if (m.getWall()!=null) {
            removeWall(m.getWall());
        }
        getProfiler().stopUndoMove();
    }


    /**
     * Num different states.
     * There are 12 unique states for a position. 4 ways the walls can be arranged around the position.
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
         return 12;
    }

    /**
     * The index of the state for this position.
     * @return The index of the state for tihs position.
     */
    @Override
    public  int getStateIndex(BoardPosition pos) {
        return ((BlockadeBoardPosition) pos).getStateIndex();
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(50);
        // print just the walls
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
               BlockadeBoardPosition pos = ((BlockadeBoardPosition)getPosition(i, j));
               if (pos.getEastWall()!=null)
                   buf.append("East wall at: ").append(i).append(' ').append(j).append('\n');
               if (pos.getSouthWall()!=null)
                   buf.append("South wall at: ").append(i).append(' ').append(j).append('\n');

            }
        }
        return buf.toString();
    }
}
