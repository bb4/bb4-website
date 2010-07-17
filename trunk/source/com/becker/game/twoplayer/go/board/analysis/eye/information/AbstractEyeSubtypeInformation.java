package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Enum for the different possible Eye shapes.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 *
 * @author Barry Becker
 */
public abstract class AbstractEyeSubtypeInformation extends AbstractEyeInformation
{
    private boolean life;
    private byte size;
    private byte numPatterns;
    private float eyeValue;
    private float[] vitalPoints;
    private float[] endPoints;

    protected static final float[] EMPTY_POINTS = new float[] {};

    /**
     * Constructor
     */
    AbstractEyeSubtypeInformation() {
    }

    protected void initialize(boolean life, int eyeSize, int numPatterns, float eyeValue)  {
        initialize(life, eyeSize, numPatterns, eyeValue, EMPTY_POINTS, EMPTY_POINTS);
    }

    protected void initialize(boolean life, int eyeSize, int numPatterns, float eyeValue,
                                  float[] vitalPts)  {
        initialize(life, eyeSize, numPatterns, eyeValue, vitalPts, EMPTY_POINTS);
    }


    @SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter"})
    protected void initialize(boolean life, int eyeSize, int numPatterns, float eyeValue,
                              final float[] vitalPts, final float[] endPts)  {
        this.life = life;
        this.size = (byte)eyeSize;
        this.numPatterns = (byte)numPatterns;
        this.eyeValue = eyeValue;
        this.vitalPoints = vitalPts;
        this.endPoints = endPts;
    }


    /**
     * @return the number of spaces in they eye (maybe be filled with some enemy stones).
     */
    public byte getSize()  {
        return size;
    }


    @Override
    public boolean hasLifeProperty() {
        return life;
    }

    /**
     * @return The number of different ways this eye pattern can occur (independent of symmetries)
     */
    public byte getNumPatterns() {
        return numPatterns;
    }

    /**
     * @return score contribution for eye.   About 1 for single eye, 2 for 2 real eyes.
     */
    public float getEyeValue() {
        return eyeValue;
    }

    @Override
    public float[] getVitalPoints() {
        return vitalPoints;
    }

    @Override
    public float[] getEndPoints() {
        return endPoints;
    }

    /**
     * We only need to consider the non-life property status.
     * @param eye
     * @param board
     * @return status of the eye shape.
     */
    public EyeStatus determineStatus(GoEye eye, GoBoard board) {
        return EyeStatus.NAKADE;
    }

    /**
     * @return status of shape with numVitals vital points.
     */
    protected  EyeStatus handleVitalPointCases(EyeNeighborMap nbrMap, GoEye eye, final int numVitals)   {
        List<GoBoardPosition> vitalFilledSpaces = findSpecialFilledSpaces(nbrMap, getVitalPoints(), eye);
        int numFilledVitals = vitalFilledSpaces.size();
        assert numFilledVitals <= numVitals :
                "The number of filled vitals ("+ numFilledVitals +") " +
                "was greater than the total number of vitals ("+numVitals + ") vitals="
                + Arrays.toString(getVitalPoints()) + " eye="+ eye;
        
        if (numFilledVitals == numVitals) {
            return EyeStatus.NAKADE;
        }
        else if (numFilledVitals == numVitals-1) {
            return EyeStatus.UNSETTLED;
        }
        else return EyeStatus.ALIVE;
    }


    /**
     * I suppose, in very rare cases, there could be a same side stone among the enemy filled spaces in the eye.
     * @return the eye spaces that have enemy stones in them.
     */
    protected List<GoBoardPosition> findFilledSpaces(GoEye eye) {
        List<GoBoardPosition> filledSpaces = new ArrayList<GoBoardPosition>(6);
        for (GoBoardPosition space : eye.getMembers()) {
            if (space.isOccupied()) {
                assert eye.isOwnedByPlayer1() != space.getPiece().isOwnedByPlayer1();
                filledSpaces.add(space);
            }
        }
        return filledSpaces;
    }


    /**
     *
     * @return the set of special spaces (vital or end) that have enemy stones in them.
     */
    protected List<GoBoardPosition> findSpecialFilledSpaces(EyeNeighborMap nbrMap, float[] specialPoints, GoEye eye) {
        List<GoBoardPosition> specialFilledSpaces = new LinkedList<GoBoardPosition>();
        for (GoBoardPosition space : eye.getMembers()) {
            if (space.isOccupied()) {
                assert eye.isOwnedByPlayer1() != space.getPiece().isOwnedByPlayer1();
                if (nbrMap.isSpecialPoint(space, specialPoints)) {
                    specialFilledSpaces.add(space);
                }
            }
        }
        return specialFilledSpaces;
    }


    /**
     * When the eye type has the alive property, we can only be alive or alive in atari.
     * @return either alive or alive in atari (rare)
     */
    protected EyeStatus handleSubtypeWithLifeProperty(GoEye eye, GoBoard board) {
        List<GoBoardPosition> filledSpaces = findFilledSpaces(eye);
        if (eye.size() - filledSpaces.size() == 1 && eye.getGroup().getLiberties(board).size() == 1) {
            return EyeStatus.ALIVE_IN_ATARI;
        }
        return EyeStatus.ALIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEyeSubtypeInformation that = (AbstractEyeSubtypeInformation) o;

        if (!getTypeName().equals(that.getTypeName())) return false;
        if (Float.compare(that.eyeValue, eyeValue) != 0) return false;
        if (life != that.life) return false;
        if (numPatterns != that.numPatterns) return false;
        if (size != that.size) return false;
        if (!Arrays.equals(endPoints, that.endPoints)) return false;
        return Arrays.equals(vitalPoints, that.vitalPoints);
    }

    @Override
    public int hashCode() {
        int result = (life ? 1 : 0);
        result = 31 * result + (int) size;
        result = 31 * result + (int) numPatterns;
        result = (31 * result) + (eyeValue != +0.0f ? Float.floatToIntBits(eyeValue) : 0);
        result = 31 * result + (vitalPoints != null ? Arrays.hashCode(vitalPoints) : 0);
        result = 31 * result + (endPoints != null ? Arrays.hashCode(endPoints) : 0);
        result = 31 * result + getTypeName().hashCode();
        return result;
    }
    
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append(this.getTypeName());
        bldr.append(" lifeProp=").append(life);
        bldr.append(" size=").append(size);
        return bldr.toString();
    }
}