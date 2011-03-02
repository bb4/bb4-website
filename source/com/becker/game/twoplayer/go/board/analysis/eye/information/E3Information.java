package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;
import com.becker.game.twoplayer.go.board.elements.IGoEye;

/**
 * Three space eye *** - there is only one type.
 * The vital point may or may not be filled - determining its status.
 *
 * @author Barry Becker
 */
public class E3Information extends AbstractEyeSubtypeInformation
{
    public E3Information() {
        initialize(false, 3, new float[] {2.02f} );
    }

    /**
     * @return eye status for E3 type.
     */
    @Override
    public EyeStatus determineStatus(IGoEye eye, GoBoard board) {

        EyeNeighborMap nbrMap = new EyeNeighborMap(eye);
        return handleVitalPointCases(nbrMap, eye, 1);
    }

    public String getTypeName() {
       return "E112";
    }
}