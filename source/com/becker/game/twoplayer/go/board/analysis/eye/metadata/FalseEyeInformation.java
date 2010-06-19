package com.becker.game.twoplayer.go.board.analysis.eye.metadata;

import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;

/**
 * Info about a false eye
 *
 * @author Barry Becker
 */
public class FalseEyeInformation extends AbstractEyeInformation {
    
    public byte getNumPatterns() {
        return 0;
    }

    public float getEyeValue() {
        return EyeShapeScores.FALSE_EYE;
    }

    public EyeStatus determineStatus(GoEye eye, EyeNeighborMap nbrMap) {
        return EyeStatus.UNSETTLED;
    }

    public String getTypeName() {
        return "False";
    }
}
