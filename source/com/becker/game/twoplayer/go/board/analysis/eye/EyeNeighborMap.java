package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoEye;

import java.util.*;

/**
 * Maps eye positions to lists of neighboring eye spaces.
 *
 * @author Barry Becker
 */
public class EyeNeighborMap {

    private GoEye eye_;
    private Map<GoBoardPosition, List<GoBoardPosition>> nbrMap_;

    /**
     * Constructor
     */
    public EyeNeighborMap(GoEye eye) {
        eye_ = eye;
        nbrMap_ = createMap();
    }
    
    public List<GoBoardPosition> getEyeNeighbors(GoBoardPosition eyeSpace) {
        return nbrMap_.get(eyeSpace);
    }

    public int getNumEyeNeighbors(GoBoardPosition eyeSpace) {
        return getEyeNeighbors(eyeSpace).size();
    }

    public Set<GoBoardPosition> keySet() {
        return nbrMap_.keySet();
    }

    /**
     * @return true if identifying index for space is in array of specialPoints.
     */
    public boolean isSpecialPoint(GoBoardPosition space, float[] specialPoints)  {
        float index = getEyeNeighborIndex(space);
        for (float specialPtIndex : specialPoints) {
            if (index == specialPtIndex) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Number of eye neighbors + (the sum of all the neighbors neighbors)/100.
     */
    private float getEyeNeighborIndex(GoBoardPosition eyeSpace) {
        float nbrNbrSum = 0;
        for (GoBoardPosition pos : getEyeNeighbors(eyeSpace))  {
            nbrNbrSum += getNumEyeNeighbors(pos);
        }
        return getEyeNeighbors(eyeSpace).size() + nbrNbrSum / 100.0f;
    }

    /**
     * @return eye status
     */
    private Map<GoBoardPosition, List<GoBoardPosition>> createMap()
    {
        Map<GoBoardPosition, List<GoBoardPosition>> nbrMap = new HashMap<GoBoardPosition, List<GoBoardPosition>>();

        GoBoardPosition firstPos = eye_.getMembers().iterator().next();
        for (GoBoardPosition space : getEyeNeighbors(firstPos))  {
            nbrMap.put(space, getEyeNobiNeighbors(space));
        }
        return nbrMap;
    }

    /**
     * @param space eye space to check
     * @return number of eye-space nobi neighbors.
     * these neighbors may either be blanks or dead stones of the opponent
     */
    private List<GoBoardPosition> getEyeNobiNeighbors(GoBoardPosition space)
    {
        List<GoBoardPosition> nbrs = new ArrayList<GoBoardPosition>();
        for (GoBoardPosition eyeSpace : eye_.getMembers()) {

            if ( space.isNeighbor( eyeSpace ))
                nbrs.add(eyeSpace);
        }
        return nbrs;
    }
}