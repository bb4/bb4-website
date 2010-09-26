package com.becker.game.twoplayer.go.board.analysis.neighbor;

import com.becker.common.Box;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoBoardPositionList;
import com.becker.game.twoplayer.go.board.elements.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.elements.GoStringSet;

/**
 * Performs static analysis of a go board to determine strings and
 * other analysis involving neighbor locations.
 * Also acts as a facade to the other neighbor analyzers in this class.
 *
 * @author Barry Becker
 */
public class NeighborAnalyzer {

    private GoBoard board_;
    private GroupNeighborAnalyzer groupNbrAnalyzer_;
    private StringNeighborAnalyzer stringNbrAnalyzer_;
    private NobiNeighborAnalyzer nobiAnalyzer_;

    /**
     * Constructor
     * @param board needed to find neighbors.
     */
    public NeighborAnalyzer(GoBoard board) {
        board_ = board;
        groupNbrAnalyzer_ = new GroupNeighborAnalyzer(board);
        stringNbrAnalyzer_ = new StringNeighborAnalyzer(board);
        nobiAnalyzer_ = new NobiNeighborAnalyzer(board);
    }

    /**
     * @param empties a list of unoccupied positions.
     * @return a list of stones bordering the set of empty board positions.
     */
    public GoBoardPositionSet findOccupiedNobiNeighbors(GoBoardPositionList empties) {
        return nobiAnalyzer_.findOccupiedNobiNeighbors(empties);
    }

    /**
     * get neighboring stones of the specified stone.
     * @param stone the stone (or space) whose neighbors we are to find (it must contain a piece).
     * @param neighborType (EYE, NOT_FRIEND etc)
     * @return a set of stones that are immediate (nobi) neighbors.
     */
    public GoBoardPositionSet getNobiNeighbors( GoBoardPosition stone, NeighborType neighborType ) {
       return getNobiNeighbors( stone, stone.getPiece().isOwnedByPlayer1(), neighborType);
    }

    /**
     * get neighboring stones of the specified stone.
     * @param stone the stone (or space) whose neighbors we are to find.
     * @param friendOwnedByP1 need to specify this in the case that the stone is a blank space and has undefined ownership.
     * @param neighborType (EYE, NOT_FRIEND etc)
     * @return a set of stones that are immediate (nobi) neighbors.
     */
    public GoBoardPositionSet getNobiNeighbors( GoBoardPosition stone, boolean friendOwnedByP1,
                                                  NeighborType neighborType ) {
        return nobiAnalyzer_.getNobiNeighbors(stone, friendOwnedByP1, neighborType);
    }

    /**
     * determine a set of stones that are tightly connected to the specified stone.
     * This set of stones constitutes a string, but since stones cannot belong to more than
     * one string we must return a List.
     * @param stone he stone from which to begin searching for the string
     * @param returnToUnvisitedState if true then the stomes will all be marked unvisited when done searching
     * @return find string.
     */
    public GoBoardPositionList findStringFromInitialPosition(GoBoardPosition stone,
                                                               boolean returnToUnvisitedState) {
        return findStringFromInitialPosition(
                stone, stone.getPiece().isOwnedByPlayer1(), returnToUnvisitedState, NeighborType.OCCUPIED,
                new Box(1, 1, board_.getNumRows(), board_.getNumCols()) );
    }

    /**
     * Determines a string connected from a seed stone within a specified bounding area.
     * @return string from seed stone.
     */
    public GoBoardPositionList findStringFromInitialPosition( GoBoardPosition stone,  boolean friendOwnedByP1,
                                                     boolean returnToUnvisitedState, NeighborType type, Box box) {
        GoProfiler.getInstance().start(GoProfiler.FIND_STRINGS);
        GoBoardPositionList stones =
                stringNbrAnalyzer_.findStringFromInitialPosition(stone, friendOwnedByP1, returnToUnvisitedState,
                                                 type, box);
        GoProfiler.getInstance().stop(GoProfiler.FIND_STRINGS);

        return stones;
    }

    /**
     * @param position stone or space to find string neighbors of.
     * @return string neighbors
     */
    public GoStringSet findStringNeighbors(GoBoardPosition position ) {
        return stringNbrAnalyzer_.findStringNeighbors(position);
    }

    /**
     * This version assumes that the stone is occupied.
     * @return the list of stones in the group that was found.
     */
    public GoBoardPositionSet findGroupNeighbors( GoBoardPosition position, boolean samePlayerOnly ) {
        assert (position != null);
        assert (position.getPiece() != null);
        return findGroupNeighbors( position, position.getPiece().isOwnedByPlayer1(), samePlayerOnly );
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
     * @return group neighbors
     */
    public GoBoardPositionSet findGroupNeighbors( GoBoardPosition stone, boolean friendPlayer1,
                                                   boolean samePlayerOnly ) {
       return groupNbrAnalyzer_.findGroupNeighbors(stone, friendPlayer1, samePlayerOnly);

    }

    /**
     * determine a set of stones that are loosely connected to the specified stone.
     * This set of stones constitutes a group, but since stones cannot belong to more than
     * one group (or string) we must return a List.
     *
     * @param stone the stone to search from for group neighbors.
     * @return the list of stones in the group that was found.
     */
    public GoBoardPositionList findGroupFromInitialPosition( GoBoardPosition stone )
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
    public GoBoardPositionList findGroupFromInitialPosition(GoBoardPosition stone, boolean returnToUnvisitedState) {
        return groupNbrAnalyzer_.findGroupFromInitialPosition(stone, returnToUnvisitedState);
    }
}