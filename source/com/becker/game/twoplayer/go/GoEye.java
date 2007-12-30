package com.becker.game.twoplayer.go;

import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *  A GoEye is composed of a strongly connected set of empty spaces (and possible some dead enemy stones).
 *  By strongly connected I mean nobi connections only.
 *  A GoEye may be one of several different eye types enumerated below
 *  Some convenience operations for eyes are contained in this class
 *  A group needs 2 provably true eyes to live.
 *
 *  @see GoGroup
 *  @see GoBoard
 *  @see EyeType for the the possible types of eyes that can occur
 *  @author Barry Becker
 */
public final class GoEye extends GoString implements GoMember
{
    // For some eyes (like big eyes) there is a key point that will make a single eye if
    // the opponent plays first, or 2 eyes if you play first
    private GoBoardPosition keyPoint_ = null;

    // the kind of eye that this is
    private final EyeType type_;

    /**
     * constructor. Create a new eye shape containing the specified list of stones/spaces
     */
    public GoEye( List spaces, GoBoard board, GoGroup g )
    {
        super( spaces, board );
        group_ = g;
        ownedByPlayer1_ = g.isOwnedByPlayer1();
        type_ = determineEyeType( spaces, board );
    }

    public EyeType getEyeType()
    {
        return type_;
    }

    public String getEyeTypeName()
    {
        if (type_==null)
            return "unknown eye type";
        return type_.toString();
    }

    /**
     * Add a space to the eye string.
     * The space is either blank or a dead enemy stone.
     */
    @Override protected void addMemberInternal(GoBoardPosition space, GoBoard board)
    {
        if ( members_.contains( space ) ) {
            GameContext.log( 1, "Warning: the eye, " + this + ", already contains " + space );
            assert ( (space.getString() == null) || (this == space.getString())):
                    "bad space or bad owning string" + space.getString();
        }
        space.setEye( this );
        members_.add( space );
    }

    /**
     * @return the eye type determined based on the properties and nbrs of the positions in the spaces list.
     */
    private EyeType determineEyeType( List spaces, GoBoard board )
    {
        assert ( spaces != null): "spaces is null";
        int size = spaces.size();
        // iterate through. if any are determined to be a false-eye, then return false-eye for the eye type.
        Iterator it = spaces.iterator();
        while ( it.hasNext() ) {
            GoBoardPosition space = (GoBoardPosition) it.next();
            if ( isFalseEye( space, board ) ) {
                return EyeType.FALSE_EYE;
            }
        }
        if ( size <= 2 )
            return EyeType.TRUE_EYE;
        if ( size > 2 && size < 8 ) {
            // check for a big-eye shape (also called a dead eye)
            // the keypoint is the space with the most nobi ngbors
            int max = 0;
            int sum = 0;
            it = spaces.iterator();
            while ( it.hasNext() ) {
                GoBoardPosition space = (GoBoardPosition) it.next();
                int numNobiNbrs = getNumEyeNobiNeighbors( space, spaces );
                sum += numNobiNbrs;
                if ( numNobiNbrs > max ) {
                    keyPoint_ = space;
                    max = numNobiNbrs;
                }
            }
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
                if ( keyPoint_.isUnoccupied() ) {
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
     * get number of eye-space nobi neighbors.
     * these neighbors may either be blanks or dead stones of the opponent
     */
    private static int getNumEyeNobiNeighbors( GoBoardPosition space, List spaces )
    {
        int numNbrs = 0;

        Iterator it = spaces.iterator();

        while ( it.hasNext() ) {
            GoBoardPosition ns = (GoBoardPosition) it.next();
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
        GoGroup ourGroup = getGroup();
        boolean groupP1 = ourGroup.isOwnedByPlayer1();
        Set nbrs = board.getNobiNeighbors( space, groupP1, NeighborType.FRIEND );
        int r = space.getRow();
        int c = space.getCol();

        if ( nbrs.size() >= 2 ) {

            int numOppDiag = 0;
            // check the diagonals for > 2 of the opponents pieces.
            // there are 2 cases: both opponent pieces on the same vert or horiz, or
            // the opponents pieces are on the opposite diagonals
            if (qualifiedOpponentDiag(-1,-1, r,c, board, groupP1))
                numOppDiag++;
            if (qualifiedOpponentDiag(-1, 1, r,c, board, groupP1))
                numOppDiag++;
            if (qualifiedOpponentDiag( 1,-1, r,c, board, groupP1))
                numOppDiag++;
            if (qualifiedOpponentDiag( 1, 1, r,c, board, groupP1))
                numOppDiag++;

            /* it is also a requirement that one of the friendly nbrs be in atari - No!
            boolean nbrInAtari = false;
            Iterator it = nbrs.iterator();
            while (it.hasNext() && !nbrInAtari) {
                GoBoardPosition nbr = (GoBoardPosition)it.next();
                if (nbr.isInAtari(board))
                    nbrInAtari = true;
            }  */

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
     * @return true of the enemy piece on the diagoanl is relatively strong and there are groups stones adjacent
     */
    private boolean qualifiedOpponentDiag(int rowOffset, int colOffset, int r, int c,
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
     *  @return true if the piece is an enemy of the string owner
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
    protected boolean isEnemy( GoBoardPosition pos)
    {
        assert (group_ != null): "group for "+this+" is null";
        GoStone stone = (GoStone)pos.getPiece();
        boolean weaker = false;
        if (stone != null)  {
            weaker = GoBoardUtil.isStoneMuchWeaker(getGroup(), stone);
        }
        assert (getGroup().isOwnedByPlayer1() == this.isOwnedByPlayer1()):
                 getGroup()+"bad group ownership. eye="+this+". Group="+getGroup();
        return (pos.isOccupied() && (stone.isOwnedByPlayer1() != isOwnedByPlayer1() && !weaker));
    }


    /**
     * @return true if all the empty spaces in this eye are touching the specified string.
     */
    public boolean allUnocupiedAdjacentToString(GoString string, GoBoard b)   {
        for (Object p : this.getMembers()) {
            GoBoardPosition pos = (GoBoardPosition) p;
            if (pos.isUnoccupied()) {
                Set nbrs = b.getNobiNeighbors(pos, this.isOwnedByPlayer1(), NeighborType.FRIEND);
                // verify that at least one of the nbrs is in this string
                boolean thereIsANbr = false;
                for  (Object nbr : nbrs) {
                    GoBoardPosition nbrPos = (GoBoardPosition) nbr;
                    if (string.getMembers().contains(nbrPos)) {
                        thereIsANbr = true;
                        break;
                    }
                }
                if (!thereIsANbr) {
                    //GameContext.log(2, "pos:"+pos+" was found to not be adjacent to the bordering string : "+this);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * empty the positions from the eye.
     */
    public void clear()
    {
         Iterator it = members_.iterator();
         while (it.hasNext()) {
             GoBoardPosition pos = (GoBoardPosition)it.next();
             pos.setEye(null);
             pos.setVisited(false);
         }

         setGroup(null);
    }

    protected String getPrintPrefix()
    {
        return " Eye: " + getEyeTypeName() + ": ";
    }

}