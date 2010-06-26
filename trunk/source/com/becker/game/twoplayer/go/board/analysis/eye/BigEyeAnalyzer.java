package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeType;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeInformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Determine properties about a big eye on the board.
 * This analyizer is only used by the EyeTypeAnalyzer.
 * It classifies eyes that are not false eye and have between 2 and 8 spaces.
 * Some of those spaces may have enemy stones in them.
 * See EyeTypeAnalyzer
 *
 * @author Barry Becker
 */
class BigEyeAnalyzer {

    /** the eye to classify. */
    private GoEye eye_;

    /** spaces in the eye */
    Set<GoBoardPosition> spaces;

    /**
     * The eye must have between 3 and 8 spaces.
     * @param eye the eye to analyze
     */
    BigEyeAnalyzer(GoEye eye) {
        eye_ = eye;
        spaces = eye_.getMembers();
        int size = spaces.size();
        assert ( size > 3 && size < 8 );
    }

    /**
     * For some eyes (like big eyes) there are one or more key points that will make a single eye if
     * the opponent plays first (or first and second if 2 key points), or 2 eyes if you play first.
     * We refer to the paper "When One Eye is Sufficient: A Static Classification"
     * to classify the different eye types based solely on eye-point neighbors.
     *
     * The pattern formed by the sorted list of neighbor counts uniquely determines the type.
     *
     * @return the eye type determined based on the properties and nbrs of the positions in the spaces list.
     */
    EyeInformation determineEyeInformation()
    {
        List<Integer> counts = new ArrayList<Integer>(7);

        for (GoBoardPosition space : spaces) {
            counts.add(getNumEyeNobiNeighbors(space));
        }
        Collections.sort(counts);

        return getEyeInformation(counts);
    }

    private EyeInformation getEyeInformation(List<Integer> counts) {
        StringBuilder bldr = new StringBuilder("E");
        for (int num : counts) {
            bldr.append(num);
        }
        EyeType type = EyeType.valueOf("E" + counts.size());
        return type.getInformation(bldr.toString());
    }

    /**
     * @param space eye space to check
     * @return number of eye-space nobi neighbors.
     * these neighbors may either be blanks or dead stones of the opponent
     */
    private int getNumEyeNobiNeighbors(GoBoardPosition space)
    {
        int numNbrs = 0;
        for (GoBoardPosition eyeSpace : eye_.getMembers()) {
       
            if ( space.isNeighbor( eyeSpace ))
                numNbrs++;
        }
        return numNbrs;
    }
}
