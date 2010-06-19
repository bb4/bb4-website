package com.becker.game.twoplayer.go.board.analysis.eye.metadata;

import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.*;

/**
 * Subtype containing MetaData for the different possible Eye shapes of size 5.
 * There are 4 different subtypes to consider.
 *
 * @author Barry Becker
 */
public class E5Subtype extends AbstractEyeSubtype
{
    /** Different sorts of eye with 5 spaces. */
    enum Eye5Type {E11222, E11123, E11114,  E12223}
    private Eye5Type e5Type;

    /**
     * Constructor
     * @param subTypeDesc description of the type - something like "E11223".
     */
    E5Subtype(String subTypeDesc) {
        e5Type = Eye5Type.valueOf(subTypeDesc);
        switch(e5Type) {
           case E11222 : initialize(true, 5, 3, GUARANTEED_TWO_EYES);
               break;
           case E11123 : initialize(false, 5, 1, BIG_EYE, new float[] {3.04f, 2.04f}, new float[] {1.02f});
               break;
           case E11114 : initialize(false, 5, 1, BIG_EYE, new float[] {4.04f});
               break;
           case E12223 : initialize(false, 5, 1, SINGLE_EYE);
               break;
        }
    }

    /**
     * @return eye status for E5 types.
     */
    @Override
    public EyeStatus determineStatus(GoEye eye, EyeNeighborMap nbrMap) {
        switch (e5Type) {
            case E11222 :
                handleSubtypeWithLifeProperty();
            case E11123 :
                return handleVitalPointCases(nbrMap, eye, 2);
            case E11114 :
                return handleVitalPointCases(nbrMap, eye, 1);
            case E12223 :
                return EyeStatus.UNSETTLED;
        }
        return EyeStatus.NAKADE; // never reached
    }


    public String getTypeName() {
        return e5Type.toString();
    }
}