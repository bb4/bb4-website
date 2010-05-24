package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.common.BoardPosition;
import com.becker.game.twoplayer.go.board.EyeType;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.GoString;
import com.becker.game.twoplayer.go.board.NeighborType;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoGroup;
import com.becker.game.twoplayer.go.board.GoStone;
import java.util.Set;

/**
 * Determine the type of an eye on the board.
 * 
 * @author Barry Becker
 */
public class EyeAnalyzer {

    private GoEye eye_;
    
    public EyeAnalyzer(GoEye eye) {
        eye_ = eye;  
    }
      
    /**
     * @return the eye type determined based on the properties and
     *     nbrs of the positions in the spaces list.
     */
    public EyeType determineEyeType(GoBoard board)
    {
        Set<GoBoardPosition> spaces = eye_.getMembers();
        assert (spaces != null): "spaces is null";
        int size = spaces.size();

        if (isFalseEye( board )) {
            return EyeType.FALSE_EYE;
        }
        if ( size <= 2 ) {
            return EyeType.TRUE_EYE;
        }
    
        if ( size > 2 && size < 8 ) {
            BigEyeAnalyzer bigEyeAnalyzer = new BigEyeAnalyzer(eye_);
            return bigEyeAnalyzer.determineEyeType(board);
        }
        return EyeType.TERRITORIAL_EYE;
    }

    /**
     * Iterate through the spaces in the eye.
     * if any are determined to be a false-eye, then return false-eye for the eye type.
     * @return true if we are a false eye.
     */
    private boolean isFalseEye(GoBoard board)  {
        Set<GoBoardPosition> spaces = eye_.getMembers();
        for (GoBoardPosition space : spaces) {
            if ( isFalseEye( space, board ) ) {
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
     * @param board the go game board.
     * @return true if htis is a false eye.
     */
    private boolean isFalseEye( GoBoardPosition space, GoBoard board )
    {
        GoGroup ourGroup = eye_.getGroup();
        boolean groupP1 = ourGroup.isOwnedByPlayer1();
        Set nbrs = board.getNobiNeighbors( space, groupP1, NeighborType.FRIEND );
        int r = space.getRow();
        int c = space.getCol();

        if ( nbrs.size() >= 2 ) {

            int numOppDiag = 0;
            // check the diagonals for > 2 of the opponents pieces.
            // there are 2 cases: both opponent pieces on the same vert or horiz, or
            // the opponents pieces are on the opposite diagonals
            if (qualifiedOpponentDiagonal(-1,-1, r,c, board, groupP1))
                numOppDiag++;
            if (qualifiedOpponentDiagonal(-1, 1, r,c, board, groupP1))
                numOppDiag++;
            if (qualifiedOpponentDiagonal( 1,-1, r,c, board, groupP1))
                numOppDiag++;
            if (qualifiedOpponentDiagonal( 1, 1, r,c, board, groupP1))
                numOppDiag++;

            // now decide if false eye based on nbrs and proximity to edge.
            if ( numOppDiag >= 2  && (nbrs.size() >= 3))
                return true;
            else if (board.isOnEdge(space) && numOppDiag >=1) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return true of the enemy piece on the diagoanal is relatively strong and there are groups stones adjacent.
     */
    private boolean qualifiedOpponentDiagonal(int rowOffset, int colOffset, int r, int c,
                                          GoBoard board, boolean groupP1)
    {
        GoBoardPosition diagPos = (GoBoardPosition)board.getPosition( r + rowOffset, c + colOffset );
        if (diagPos == null || diagPos.isUnoccupied() || diagPos.getPiece().isOwnedByPlayer1() == groupP1 )
            return false;

        BoardPosition pos1 = board.getPosition( r + rowOffset, c );
        BoardPosition pos2 = board.getPosition( r, c + colOffset );

        return (pos1.isOccupied() && pos1.getPiece().isOwnedByPlayer1() == groupP1 &&
                pos2.isOccupied() && pos2.getPiece().isOwnedByPlayer1() == groupP1 &&
                isEnemy( diagPos ));
    }

    /**
     *  @return true if the piece is an enemy of the string owner.
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
    public boolean isEnemy( GoBoardPosition pos)
    {
        GoGroup g = eye_.getGroup();
        assert (g != null): "group for "+ eye_ +" is null";
        if (pos.isUnoccupied()) {
            return false;
        }
        GoStone stone = (GoStone)pos.getPiece();
        boolean weaker = GoBoardUtil.isStoneMuchWeaker(g, stone);

        assert (g.isOwnedByPlayer1() == eye_.isOwnedByPlayer1()):
                 "Bad group ownership for eye="+ eye_ +". Owning Group="+g;
        return (stone.isOwnedByPlayer1() != eye_.isOwnedByPlayer1() && !weaker);
    }


    /**
     * @return true if all the empty spaces in this eye are touching the specified string.
     */
    public boolean allUnocupiedAdjacentToString(GoString string, GoBoard b)   {
        for (GoBoardPosition pos : eye_.getMembers()) {
            if (pos.isUnoccupied()) {
                Set<GoBoardPosition> nbrs = b.getNobiNeighbors(pos, eye_.isOwnedByPlayer1(), NeighborType.FRIEND);
                // verify that at least one of the nbrs is in this string
                boolean thereIsaNbr = false;
                for  (GoBoardPosition nbr : nbrs) { 
                    if (string.getMembers().contains(nbr)) {
                        thereIsaNbr = true;
                        break;
                    }
                }
                if (!thereIsaNbr) {
                    //GameContext.log(2, "pos:"+pos+" was found to not be adjacent to the bordering string : "+this);
                    return false;
                }
            }
        }
        return true;
    }
}
