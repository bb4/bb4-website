package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeStatus;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeTypeAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeInformation;

/**
 *  A GoEye is composed of a strongly connected set of empty spaces (and possible some dead enemy stones).
 *  By strongly connected I mean nobi connections only.
 *  A GoEye may be one of several different eye types enumerated below
 *  Some convenience operations for eyes are contained in this class
 *  A group needs 2 provably true eyes to live.
 *
 *  @author Barry Becker
 */
public class GoEye extends GoString implements IGoEye
{
    /** A set of positions that are in the eye space. Need not be empty. */
    private GoBoardPositionSet members_;
    
    /** The kind of eye that this is. */
    private final EyeInformation information_;

    /** In addition to the type, an eye can have a status like nakade, unsettled, or aliveInAtari. */
    private final EyeStatus status_;

    private int numCornerPoints_;
    private int numEdgePoints_;

    /**
     * Constructor.
     * Create a new eye shape containing the specified list of stones/spaces
     */
    public GoEye( GoBoardPositionList spaces, GoBoard board, IGoGroup g ) {
        super( spaces, board );
        group_ = g;
        ownedByPlayer1_ = g.isOwnedByPlayer1();

        EyeTypeAnalyzer eyeAnalyzer = new EyeTypeAnalyzer(this, board);
        information_ = eyeAnalyzer.determineEyeInformation();
        status_ = information_.determineStatus(this, board);
        initializePositionCounts(board);
    }

    public EyeInformation getInformation() {
        return information_;
    }

    public EyeStatus getStatus() {
        return status_;
    }

    public String getEyeTypeName() {
        if (information_ ==null)
            return "unknown eye type";
        return information_.getTypeName();
    }

    public int getNumCornerPoints() {
        return numCornerPoints_;
    }

    public int getNumEdgePoints() {
        return numEdgePoints_;
    }

    @Override
    protected void initializeMembers() {
        members_ = new GoBoardPositionSet();
    }

    private void initializePositionCounts(GoBoard board) {
        numCornerPoints_ = 0;
        numEdgePoints_ = 0;
        for (GoBoardPosition pos : getMembers()) {
            if (board.isCornerTriple(pos))  {
               numCornerPoints_++;
            }
            if (board.isOnEdge(pos))  {
               numEdgePoints_++;
            }
        }
    }
    
    /**
     * @return the hashSet containing the members
     */
    @Override
    public GoBoardPositionSet getMembers() {
        return members_;
    }

    /**
     *  @return true if the piece is an enemy of the string owner.
     *  If the difference in health between the stones is great, then they are not really enemies
     *  because one of them is dead.
     */
    @Override
    public boolean isEnemy(GoBoardPosition pos)
    {
        IGoGroup g = getGroup();
        assert (g != null): "group for "+ this +" is null";
        if (pos.isUnoccupied()) {
            return false;
        }
        GoStone stone = (GoStone)pos.getPiece();
        boolean weaker = g.isStoneMuchWeaker(stone);

        assert (g.isOwnedByPlayer1() == isOwnedByPlayer1()):
                 "Bad group ownership for eye="+ this +". Owning Group="+g;
        return (stone.isOwnedByPlayer1() != isOwnedByPlayer1() && !weaker);
    }
    
    /**
     * Add a space to the eye string.
     * The space is either blank or a dead enemy stone.
     */
    @Override
    protected void addMemberInternal(GoBoardPosition space, GoBoard board) {
        if ( getMembers().contains( space ) ) {
            GameContext.log( 1, "Warning: the eye, " + this + ", already contains " + space );
            assert ( (space.getString() == null) || (this == space.getString())):
                    "bad space or bad owning string" + space.getString();
        }
        space.setEye( this );
        getMembers().add( space );
    }

    /** {@inheritDoc} */
    public void clear() {
        for (GoBoardPosition pos : getMembers()) {
            pos.setEye(null);
            pos.setVisited(false);
        }
         setGroup(null);
    }

    @Override
    protected String getPrintPrefix() {
        return " Eye: " + getEyeTypeName() + ": ";
    }
}