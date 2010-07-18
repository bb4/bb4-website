package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.IGoString;

import java.util.*;

/**
 * Maps eye positions to lists of neighboring eye spaces_.
 *
 * @author Barry Becker
 */
public class EyeNeighborMap {

    private IGoString eye_;
    private Map<GoBoardPosition, List<GoBoardPosition>> nbrMap_;

    /**
     * Constructor
     * @param eye we take an IGoString because that is all we need, but typically you want to pass in a GoEye.
     */
    public EyeNeighborMap(IGoString eye) {
        eye_ = eye;
        nbrMap_ = createMap();
    }

    /**
     * @return list of eye neighbors for the specified eyeSpace position.
     */
    public List<GoBoardPosition> getEyeNeighbors(GoBoardPosition eyeSpace) {
        return nbrMap_.get(eyeSpace);
    }

    /**
     * @return number of eye neighbors for the specified eyeSpace position.
     */
    public int getNumEyeNeighbors(GoBoardPosition eyeSpace) {
        return getEyeNeighbors(eyeSpace).size();
    }

    public GoBoardPositionSet keySet() {
        return new GoBoardPositionSet(nbrMap_.keySet());
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
     * Do a breadth first search of all the positions in the eye, adding their nbr set to the map as we go.
     * @return the new neighbor map
     */
    private Map<GoBoardPosition, List<GoBoardPosition>> createMap()
    {
        Map<GoBoardPosition, List<GoBoardPosition>> nbrMap = new HashMap<GoBoardPosition, List<GoBoardPosition>>();
        // we should probably be able to assume that the eye spaces_ are unvisted, but apparently not. assert instead?
        eye_.unvisit();
        
        List<GoBoardPosition> queue = new LinkedList<GoBoardPosition>();
        GoBoardPosition firstPos = eye_.getMembers().iterator().next();
        firstPos.setVisited(true);
        queue.add(firstPos);

        int count = proecessSearchQueue(queue, nbrMap);

        if (count != eye_.getMembers().size()) {
            throw new IllegalArgumentException("The eye string must not have been nobi connected because " +
                    "not all memebers were searched. " + eye_);
        }
        eye_.unvisit();
        return nbrMap;
    }

    /**
     * Do a breadth first search of all the positions in the eye, adding their nbr set to the map as we go.
     * @return the number of element that were searched.
     */
    private int proecessSearchQueue(List<GoBoardPosition> queue, Map<GoBoardPosition, List<GoBoardPosition>> nbrMap) {
        int count = 0;
        while (!queue.isEmpty())
        {
            GoBoardPosition current = queue.remove(0);
            List<GoBoardPosition> nbrs = getEyeNobiNeighbors(current);
            nbrMap.put(current, nbrs);
            count++;
            for (GoBoardPosition space : nbrs)  {
                if (!space.isVisited()) {
                    space.setVisited(true);
                    queue.add(space);
                }
            }
        }
        return count;
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

    public String toString() {
        return nbrMap_.toString();
    }
}