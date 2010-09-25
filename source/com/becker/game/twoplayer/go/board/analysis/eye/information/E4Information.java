package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.*;

/**
 * Eye6Type containing MetaData for the different possible Eye shapes of size 4.
 *
 * @author Barry Becker
 */
public class E4Information extends AbstractEyeSubtypeInformation
{
    /** Different sorts of eye with 4 spaces. */
    public enum Eye4Type {E1122, E1113, E2222}
    private Eye4Type e4Type;

    /**
     * Constructor
     * @param subTypeDesc description of the type - something like "E1122".
     */
    E4Information(String subTypeDesc) {
        e4Type = Eye4Type.valueOf(subTypeDesc);
        switch(e4Type) {
           case E1122 : initialize(false, 4, new float[] {2.03f, 2.03f});
               break;
           case E1113 : initialize(false, 4, new float[] {3.03f});
               break;
           case E2222 : initialize(false, 4);
               break;
        }
    }

    /**
     * @return eye status for E4 types.
     */
    @Override
    public EyeStatus determineStatus(GoEye eye, GoBoard board) {
        EyeNeighborMap nbrMap = new EyeNeighborMap(eye);
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