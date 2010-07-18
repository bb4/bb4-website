package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.elements.GoEye;

import java.util.Arrays;

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

    public boolean isInCorner(GoEye eye) {
        return eye.getNumCornerPoints() == 3;
    }

    public boolean isOnEdge(GoEye eye){
         return eye.getNumEdgePoints() >= 3;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEyeInformation that = (AbstractEyeInformation) o;

        return hasLifeProperty() == that.hasLifeProperty()
                && Arrays.equals(getEndPoints(), that.getEndPoints())
                && Arrays.equals(getVitalPoints(), that.getVitalPoints());
    }

    @Override
    public int hashCode() {
        int result = (hasLifeProperty() ? 1 : 0);
        result = 31 * result + (getVitalPoints() != null ? Arrays.hashCode(getVitalPoints()) : 0);
        result = 31 * result + (getEndPoints() != null ? Arrays.hashCode(getEndPoints()) : 0);
        return result;
    }
}