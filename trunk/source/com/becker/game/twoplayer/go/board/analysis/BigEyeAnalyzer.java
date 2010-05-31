package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.board.EyeType;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.GoBoard;
import java.util.Set;

/**
 * Determine properties about a big eye on the board.
 * This analyizer is only used by the EyeAnalyzer.
 * See EyeAnalyzer
 *
 * @author Barry Becker
 */
class BigEyeAnalyzer {

    private GoEye eye_;

    Set<GoBoardPosition> spaces;

    /**
     * The eye must have between 3 and 8 spaces.
     * @param eye the eye to analyze
     */
    public BigEyeAnalyzer(GoEye eye) {
        eye_ = eye;
        spaces = eye_.getMembers();
        int size = spaces.size();
        assert ( size > 2 && size < 8 );
    }
    
      
    /**
     * For some eyes (like big eyes) there is a key point that will make a single eye if
     * the opponent plays first, or 2 eyes if you play first.
     * @return the eye type determined based on the properties and nbrs of the positions in the spaces list.
     */
    public EyeType determineEyeType()
    {
        GoBoardPosition keyPoint = null;
           
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
        int size = spaces.size();

        assert keyPoint != null : "There must be a space with at least 1 nobi nbr";
        return getEyeType(keyPoint, max, sum, size);

    }

    /**
     * @return  type of eye.
     */
    private EyeType getEyeType(GoBoardPosition keyPoint, int max, int sum, int size) {
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
                // only one true eye if the keypoint is occupied by opponent piece.
                return EyeType.TRUE_EYE;
            }
        }

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
}
