package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;

/**
 * Describes an eye shape with 8 or more internal spaces.
 * It is almost certainly an unconditionally alive shape.
 * If its not, you have to play more to tell.
 *
 * @author Barry Becker
 */
public class TerritorialEyeInformation extends AbstractEyeInformation {

    /**
     * @return There are probably more than 127, but that is max-byte. Alternatively could return 0 or -1.
     */
    public byte getNumPatterns() {
        return 127;
    }

    public float getEyeValue() {
        return EyeShapeScores.TERRITORIAL_EYE;
    }

    public EyeStatus determineStatus(GoEye eye, GoBoard board) {
        return EyeStatus.ALIVE;
    }

    public String getTypeName() {
        return "Territorial";
    }
}