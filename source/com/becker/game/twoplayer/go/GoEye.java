package com.becker.game.twoplayer.go;

import com.becker.game.common.GameContext;

import java.util.*;

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
        super( spaces );
        group_ = g;
        ownedByPlayer1_ = g.isOwnedByPlayer1();
        type_ = determineEyeType( spaces, board );
    }

    public final EyeType getEyeType()
    {
        return type_;
    }

    public final String getEyeTypeName()
    {
        if (type_==null)
            return "unknown eye type";
        return type_.getName();
    }

    /**
     * Add a space to the eye string.
     * The space is either blank or a dead enemy stone.
     */
    public void addMember( GoBoardPosition space )
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
        // iterate through. if any are marked false-eye then return false-eye.
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
            if ( (size == 3)
                    || ((size == 4) && (max == 3 && sum == 6))
                    || ((size == 5) && ((max == 4 && sum == 8) || (max == 3 && sum == 10)))
                    || ((size == 6) && (max == 4 && sum == 12))
                    || ((size == 7) && (max == 4 && sum == 16)) ) {
                if ( keyPoint_.isUnoccupied() ) {
                    // it has the potential to be 2 eyes depending on who plays the keypoint
                    return EyeType.BIG_EYE;
                }
                else
                    return EyeType.TRUE_EYE;  // only one true eye
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
    private static final int getNumEyeNobiNeighbors( GoBoardPosition space, List spaces )
    {
        int numNbrs = 0;

        Iterator it = spaces.iterator();

        while ( it.hasNext() ) {
            GoBoardPosition ns = (GoBoardPosition) it.next();
            if ( space.getDistanceFrom( ns ) == 1 )
                numNbrs++;
        }
        return numNbrs;
    }

    /**
     * Generally if 3 or more of the nobi neighbors are friendly,
     * and 2 or more of the diagonal nbrs are not, then it is a false eye.
     * However, if against the edge or in the corner, then 2 or more friendly nobi nbrs
     * and 1 or more enemy diagonal nbrs are needed in order to call a false eye.
     *
     * @param space check to see if this space is part of a false eye.
     * @param board the go game board.
     */
    private boolean isFalseEye( GoBoardPosition space, GoBoard board )
    {
        HashSet nbrs = board.getNobiNeighbors( space, getGroup().isOwnedByPlayer1(), NeighborType.FRIEND );
        int r = space.getRow();
        int c = space.getCol();

        if ( nbrs.size() >= 2 ) {
            int numOppDiag = 0;
            // check the diagonals for >2 of the opponents pieces
            // there are 2 cases: both opponent peices on the same vert or horiz, or
            // the opponents pieces are on the diagonal diagonals
            if ( (r > 1 && c  > 1) &&
                 isEnemy( (GoBoardPosition)board.getPosition( r - 1, c - 1 ), board ) )
                numOppDiag++;
            if ( (r > 1 && c + 1 < board.getNumCols()) &&
                 isEnemy( (GoBoardPosition)board.getPosition( r - 1, c + 1 ), board ) )
                numOppDiag++;
            if ( (r + 1 < board.getNumRows() && c  > 1) &&
                 isEnemy( (GoBoardPosition)board.getPosition( r + 1, c - 1 ), board ) )
                numOppDiag++;
            if ( (r + 1 < board.getNumRows() && c + 1 < board.getNumCols()) &&
                 isEnemy( (GoBoardPosition)board.getPosition( r + 1, c + 1 ), board ) )
                numOppDiag++;

            // now decide if false eye based on ngbors and proximity to edge.
            if ( numOppDiag >= 2  && (nbrs.size() >= 3))
                return true;
            else if (board.isOnEdge(space) && numOppDiag >=1)
                return true;
        }
        return false;
    }

    /**
     *  @return true if the piece is an enemy of the string owner
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
    protected boolean isEnemy( GoBoardPosition pos, GoBoard board )
    {
        assert (group_!=null): "group for "+this+" is null";
        GoStone stone = (GoStone)pos.getPiece();
        boolean withinDifferenceThreshold = true;
        if (stone!=null)  {
            withinDifferenceThreshold =
                 (Math.abs(getGroup().getAbsoluteHealth() + stone.getHealth()) <= GoGroup.DIFFERENCE_THRESHOLD);
        }
        assert (getGroup().isOwnedByPlayer1() == this.isOwnedByPlayer1()):
                 getGroup()+"bad group ownership. eye="+this+". Group="+getGroup();
        return (pos.isOccupied() && (stone.isOwnedByPlayer1() != isOwnedByPlayer1() && withinDifferenceThreshold));
    }

    /**
     * empty the positions from the eye.
     */
    public final void clear()
    {
         Iterator it = members_.iterator();
         while (it.hasNext()) {
             GoBoardPosition p = (GoBoardPosition)it.next();
             p.setEye(null);
             //p.setString(null);
             p.setVisited(false);
         }
         // remove reference to the owning group so it can be garbage collected.
         setGroup(null);
    }

    protected final String getPrintPrefix()
    {
        return " Eye: " + getEyeTypeName() + ": ";
    }
}