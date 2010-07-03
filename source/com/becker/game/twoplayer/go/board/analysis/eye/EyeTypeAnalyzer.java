package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.common.BoardPosition;
import com.becker.game.twoplayer.go.board.*;
import com.becker.game.twoplayer.go.board.analysis.eye.information.*;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;

import java.util.Set;

/**
 * Determine the type of an eye on the board.
 * 
 * @author Barry Becker
 */
public class EyeTypeAnalyzer {

    private GoEye eye_;
    private GoBoard board_;
    private NeighborAnalyzer nbrAnalyzer_;

    
    public EyeTypeAnalyzer(GoEye eye, GoBoard board) {
        eye_ = eye;
        board_ = board;
        nbrAnalyzer_ = new NeighborAnalyzer(board);
    }
      
    /**
     * @return the eye type determined based on the properties and
     *     nbrs of the positions in the spaces list.
     */
    public EyeInformation determineEyeInformation()
    {
        Set<GoBoardPosition> spaces = eye_.getMembers();
        assert (spaces != null): "spaces is null";
        int size = spaces.size();

        if (isFalseEye()) {
            return new FalseEyeInformation();
        }
        if ( size == 1 ) {
            return new E1Information();
        }
        if ( size == 2 ) {
            return new E2Information();
        }
        if ( size == 3 ) {
            return new E3Information();
        }  
        if ( size > 3 && size < 8 ) {
            BigEyeAnalyzer bigEyeAnalyzer = new BigEyeAnalyzer(eye_);
            return bigEyeAnalyzer.determineEyeInformation();
        }
        return new TerritorialEyeInformation();
    }

    /**
     * Iterate through the spaces in the eye.
     * if any are determined to be a false-eye, then return false-eye for the eye type.
     * @return true if we are a false eye.
     */
    private boolean isFalseEye()  {
        Set<GoBoardPosition> spaces = eye_.getMembers();
        for (GoBoardPosition space : spaces) {
            if ( isFalseEye( space) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generally if 3 or more of the nobi neighbors are friendly,
     * and 2 or more of the diagonal nbrs are not, then it is a false eye.
     * However, if against the edge or in the corner, then 2 or more friendly nobi nbrs
     * and 1 or more enemy diagonal nbrs are needed in order to call a false eye.
     * Those enemy diagonal neighbors need to be stronger otherwise they
     * should be considered too weak to cause a false eye.
     *
     * @param space check to see if this space is part of a false eye.
     * @return true if htis is a false eye.
     */
    private boolean isFalseEye( GoBoardPosition space )
    {
        GoGroup ourGroup = eye_.getGroup();
        boolean groupP1 = ourGroup.isOwnedByPlayer1();
        Set nbrs = nbrAnalyzer_.getNobiNeighbors( space, groupP1, NeighborType.FRIEND );

        if ( nbrs.size() >= 2 ) {

            int numOppDiag = getNumOpponentDiagonals(space, groupP1);

            // now decide if false eye based on nbrs and proximity to edge.
            if ( numOppDiag >= 2  && (nbrs.size() >= 3))
                return true;
            else if (board_.isOnEdge(space) && numOppDiag >=1) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return  The number of diagonal points that are occupied by opponent stones (0, 1, or 2)
     */
    private int getNumOpponentDiagonals(GoBoardPosition space, boolean groupP1) {
        int numOppDiag = 0;
        int r = space.getRow();
        int c = space.getCol();
        // check the diagonals for > 2 of the opponents pieces.
        // there are 2 cases: both opponent pieces on the same vert or horiz, or
        // the opponents pieces are on the opposite diagonals
        if (qualifiedOpponentDiagonal(-1,-1, r,c, groupP1))
            numOppDiag++;
        if (qualifiedOpponentDiagonal(-1, 1, r,c, groupP1))
            numOppDiag++;
        if (qualifiedOpponentDiagonal( 1,-1, r,c, groupP1))
            numOppDiag++;
        if (qualifiedOpponentDiagonal( 1, 1, r,c, groupP1))
            numOppDiag++;
        return numOppDiag;
    }

    /**
     *
     * @return true of the enemy piece on the diagoanal is relatively strong and there are groups stones adjacent.
     */
    private boolean qualifiedOpponentDiagonal(int rowOffset, int colOffset, int r, int c, boolean groupP1)
    {
        GoBoardPosition diagPos = (GoBoardPosition)board_.getPosition( r + rowOffset, c + colOffset );
        if (diagPos == null || diagPos.isUnoccupied() || diagPos.getPiece().isOwnedByPlayer1() == groupP1 )
            return false;

        BoardPosition pos1 = board_.getPosition( r + rowOffset, c );
        BoardPosition pos2 = board_.getPosition( r, c + colOffset );

        return (pos1.isOccupied() && (pos1.getPiece().isOwnedByPlayer1() == groupP1) &&
                pos2.isOccupied() && (pos2.getPiece().isOwnedByPlayer1() == groupP1) &&
                eye_.isEnemy(diagPos));
    }
}
