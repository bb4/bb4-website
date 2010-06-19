package com.becker.game.twoplayer.go.board.analysis.eye.metadata;

import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.*;

/**
 * Subtype containing MetaData for the different possible Eye shapes of size 4.
 *
 * @author Barry Becker
 */
public class E4Subtype extends AbstractEyeSubtype
{
    /** Different sorts of eye with 4 spaces. */
    enum Eye4Type {E1122, E1113, E2222}
    private Eye4Type e4Type;

    /**
     * Constructor
     * @param subTypeDesc description of the type - something like "E1122".
     */
    E4Subtype(String subTypeDesc) {
        e4Type = Eye4Type.valueOf(subTypeDesc);
        switch(e4Type) {
           case E1122 : initialize(false, 4, 3, PROBABLE_TWO_EYES, new float[] {2.03f, 2.03f});
               break;
           case E1113 : initialize(false, 4, 1, BIG_EYE, new float[] {1.03f});
               break;
           case E2222 : initialize(false, 4, 1, SINGLE_EYE);
               break;
        }
    }

    /**
     * @return eye status for E4 types.
     */
    @Override
    public EyeStatus determineStatus(GoEye eye, EyeNeighborMap nbrMap) {
        switch (e4Type) {
            case E1122 :
                return handleVitalPointCases(nbrMap, eye, 2);
            case E1113 :
                return handleVitalPointCases(nbrMap, eye, 1);
            case E2222 :
                return EyeStatus.UNSETTLED;
        }
        return EyeStatus.NAKADE; // never reached
    }

    public String getTypeName() {
        return e4Type.toString();
    }
}