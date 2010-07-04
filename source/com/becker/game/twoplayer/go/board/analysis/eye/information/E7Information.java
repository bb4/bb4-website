package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.common.Box;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;

import java.util.ArrayList;
import java.util.List;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.*;

/**
 * Subtype containing MetaData for the different possible Eye shapes of size 7.
 * There are 14 different subtypes to consider (if you count the 2 sub-subtypes of E1112234).
 *
 * @author Barry Becker
 */
public class E7Information extends AbstractEyeSubtypeInformation
{
    /** Different sorts of eye with 7 spaces. */
    enum Subtype {E1122222, E1112223, E1122233, E1111233, E1222223, E1111224, E1112333,
                  E1222333, E1112234, E1112234a, E1112234b, E1222234, E1122224, E2222224}
    private Subtype type;

    /**
     * Constructor
     * @param subTypeDesc description of the type - something like "E112223".
     */
    E7Information(String subTypeDesc) {
        type = Subtype.valueOf(subTypeDesc);
        switch(type) {
           case E1122222 : initialize(true, 7, 30, GUARANTEED_TWO_EYES);
               break;
           case E1112223 : initialize(true, 7, 40, GUARANTEED_TWO_EYES);
               break;
           case E1122233 : initialize(true, 7, 11, GUARANTEED_TWO_EYES);
               break;
           case E1111233 : initialize(true, 7, 8, GUARANTEED_TWO_EYES);
               break;
           case E1222223 : initialize(true, 7, 5, GUARANTEED_TWO_EYES);
               break;
           case E1111224 : initialize(true, 7, 4, GUARANTEED_TWO_EYES);
               break;
           case E1112333 : initialize(true, 7, 2, GUARANTEED_TWO_EYES);
               break;
           case E1222333 : initialize(true, 7, 2, GUARANTEED_TWO_EYES);
               break;
           case E1112234 : initialize(false, 7, 2, PROBABLE_TWO_EYES);
               break;
           case E1112234a : initialize(false, 7, 2, PROBABLE_TWO_EYES, new float[] {2.07f, 3.05f, 4.06f, 2.07f},
                                                                       new float[] {1.03f});
               break;
           case E1112234b : initialize(false, 7, 1, PROBABLE_TWO_EYES, new float[] {3.07f, 4.07f},
                                                                       new float[] {1.03f});
               break;
           case E1222234 : initialize(false, 7, 1, BIG_EYE, new float[] {4.08f},
                                                            new float[] {1.04f});
               break;
           case E1122224 : initialize(false, 7, 1, PROBABLE_TWO_EYES, new float[] {2.05f, 4.07f},
                                                                      new float[] {1.02f});
               break;
           case E2222224 : initialize(false, 7, 1, BIG_EYE, new float[] {4.10f},
                                                            new float[] {1.04f, 1.04f});
               break;
        }
    }

    /**
     * @return eye status for E6 types.
     */
    @Override
    public EyeStatus determineStatus(GoEye eye, GoBoard board) {
        EyeNeighborMap nbrMap = new EyeNeighborMap(eye);
        switch (type) {
            case E1122222 :
            case E1112223 :
            case E1122233 :
            case E1111233 :
            case E1222223 :
            case E1111224 :
            case E1112333 :
            case E1222333 :
                handleSubtypeWithLifeProperty(eye, board);
            case E1112234 :
                Subtype E112233Subtype = determineE1112234Subtype(nbrMap);
                if (E112233Subtype == Subtype.E1112234a) {
                   return handleVitalPointCases(nbrMap, eye, 2);
                }
                else {
                   return handleVitalPointCases(nbrMap, eye, 2);
                }
            case E1222234 :
                return handleVitalPointCases(nbrMap, eye, 1);
            case E1122224 :
                return handleVitalPointCases(nbrMap, eye, 2);
            case E2222224 :
                return handleVitalPointCases(nbrMap, eye, 1);
        }
        return EyeStatus.NAKADE; // never reached
    }

    /**
     * find the 2 spaces with only 1 nbr
     * if the box defined by those 2 positions contains the other 4 spaces, then case b, else a
     * @return the subtype E112233a or E112233b
     */
    private Subtype determineE1112234Subtype(EyeNeighborMap nbrMap) {

        List<GoBoardPosition> oneNbrPoints = new ArrayList<GoBoardPosition>(3);
        List<GoBoardPosition> otherPoints = new ArrayList<GoBoardPosition>(4);

        for (GoBoardPosition pos : nbrMap.keySet()) {
            if (nbrMap.getNumEyeNeighbors(pos) == 1)  {
               oneNbrPoints.add(pos);
            }
            else {
               otherPoints.add(pos);
            }
        }
        assert oneNbrPoints.size() == 3;  // hitting this
        Box bounds = new Box(oneNbrPoints.get(0).getLocation(), oneNbrPoints.get(1).getLocation());
        bounds.expandBy(oneNbrPoints.get(2).getLocation());

        for (GoBoardPosition otherPt : otherPoints) {
            if (!bounds.contains(otherPt.getLocation())) {
                return Subtype.E1112234a;
            }
        }
        return Subtype.E1112234b;
    }

    public String getTypeName() {
        return type.toString();
    }
}