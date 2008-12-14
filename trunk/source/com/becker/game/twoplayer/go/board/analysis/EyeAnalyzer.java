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
 * Determine properties about an eye on the board.
 * 
 * @author Barry Becker
 */
public class EyeAnalyzer {

    private GoEye eye_;
    
    public EyeAnalyzer(GoEye eye) {
        eye_ = eye;  
    }
    
      
    /**
     * @return the eye type determined based on the properties and nbrs of the positions in the spaces list.
     */
    public EyeType determineEyeType(GoBoard board)
    {
        Set<GoBoardPosition> spaces = eye_.getMembers();
        assert ( spaces != null): "spaces is null";
        int size = spaces.size();
        // iterate through. if any are determined to be a false-eye, then return false-eye for the eye type.
        for (GoBoardPosition space : spaces) {
            if ( isFalseEye( space, board ) ) {
                return EyeType.FALSE_EYE;
            }
        }
        if ( size <= 2 )
            return EyeType.TRUE_EYE;
        
        // For some eyes (like big eyes) there is a key point that will make a single eye if
        // the opponent plays first, or 2 eyes if you play first.
        GoBoardPosition keyPoint = null;
    
        if ( size > 2 && size < 8 ) {
            // check for a big-eye shape (also called a dead eye)
            // the keypoint is the space with the most nobi ngbors
            int max = 0;
            int sum = 0;
            for (GoBoardPosition space : spaces) {
                int numNobiNbrs = getNumEyeNobiNeighbors( space);
                sum += numNobiNbrs;
                if ( numNobiNbrs > max ) {
                    keyPoint = space;
                    max = numNobiNbrs;
                }
            }
            assert keyPoint != null : "There must be a space with at least 1 nobi nbr";
            // check for different cases of big eyes
            boolean farmersHatOrClump =  ((size == 4) && ((max == 3 && sum == 6) || (max == 2 && sum == 8)));
            boolean bulkyOrCrossedFive = ((size == 5) && ((max == 4 && sum == 8) || (max == 3 && sum == 10)));
            boolean rabbitySix = ((size == 6) && (max == 4 && sum == 12));
            boolean butterflySeven = ((size == 7) && (max == 4 && sum == 16));

            if ( (size == 3)
                    || farmersHatOrClump
                    || bulkyOrCrossedFive
                    || rabbitySix
                    || butterflySeven) {
                if ( keyPoint.isUnoccupied() ) {
                    // it has the potential to be 2 eyes depending on who plays the keypoint
                    return EyeType.BIG_EYE;
                }
                else {
                    return EyeType.TRUE_EYE;  // only one true eye
                }
            }
        }
        // if none of the above cases were hit, we assume its a very large internal space
        assert ( size > 3): "there must be at least 4 spaces for a territorial eye";
        return EyeType.TERRITORIAL_EYE;
    }

    /**
     * @return number of eye-space nobi neighbors.
     * these neighbors may either be blanks or dead stones of the opponent
     */
    private int getNumEyeNobiNeighbors( GoBoardPosition space)
    {
        int numNbrs = 0;
        for (GoBoardPosition ns : eye_.getMembers()) {
       
            if ( space.getDistanceFrom( ns ) == 1.0 )
                numNbrs++;
        }
        return numNbrs;
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
            else if (space.isOnEdge(board) && numOppDiag >=1) {
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