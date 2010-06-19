package com.becker.game.twoplayer.go.board.analysis.eye.metadata;

/**
 * Enum for the different possible Eye shapes.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 *
 * @author Barry Becker
 */
public abstract class AbstractEyeInformation implements EyeInformation
{
    public boolean hasLifeProperty() {
        return false;
    }

    public float[] getVitalPoints() {
        return new float[0];
    }

    public float[] getEndPoints() {
        return new float[0];
    }
}